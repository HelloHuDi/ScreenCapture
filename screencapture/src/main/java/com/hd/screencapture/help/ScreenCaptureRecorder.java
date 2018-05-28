package com.hd.screencapture.help;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.capture.AudioRecorder;
import com.hd.screencapture.capture.VideoRecorder;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hd on 2018/5/14 .
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public final class ScreenCaptureRecorder extends Thread {

    private final String TAG = ScreenCaptureRecorder.class.getSimpleName();

    private CaptureObserver observer;

    private MediaProjection mediaProjection;

    private ScreenCaptureConfig config;

    private AtomicBoolean recorder = new AtomicBoolean(false);

    private boolean mMuxerStarted = false;

    private MediaMuxer mMuxer;

    private VirtualDisplay mVirtualDisplay;

    private VideoRecorder videoRecorder;

    private AudioRecorder audioRecorder;

    ScreenCaptureRecorder(@NonNull MediaProjection mediaProjection, @NonNull ScreenCaptureConfig config) {
        this.mediaProjection = mediaProjection;
        this.config = config;
    }

    public void addObserver(CaptureObserver observer) {
        this.observer = observer;
    }

    public void startCapture() {
        recorder.set(true);
        start();
    }

    public void stopCapture() {
        recorder.set(false);
        release();
    }

    @Override
    public void run() {
        super.run();
        try {
            if (prepareEncoder() && startEncoder()) {
                observer.reportState(ScreenCaptureState.CAPTURING);
                mMuxer = new MediaMuxer(config.getFile().getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                mVirtualDisplay = mediaProjection.createVirtualDisplay(TAG + "-display",//
                                                 config.getVideoConfig().getWidth(), config.getVideoConfig().getHeight(), config.getVideoConfig().getDpi(), //
                                                 DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,//
                                                 videoRecorder.getSurface(), null, null);
            } else {
                throw new RuntimeException("prepare encoder failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            observer.notAllowEnterNextStep();
        }
    }

    private boolean prepareEncoder() {
        return config.hasAudio() ? (prepareVideoEncoder() && prepareAudioEncoder()) : prepareVideoEncoder();
    }

    private boolean startEncoder() {
        return config.hasAudio() ? (videoRecorder.record() && audioRecorder.record()) : videoRecorder.record();
    }

    private boolean prepareVideoEncoder() {
        videoRecorder = new VideoRecorder(observer, config, new RecorderCallback() {
            @Override
            public void onInputBufferAvailable(int index) {
                if (config.allowLog())
                    Log.d(TAG, "VideoRecorder onInputBufferAvailable :" + index);
            }

            @Override
            public void onOutputFormatChanged(MediaFormat format) {
                if (config.allowLog())
                    Log.d(TAG, "VideoRecorder onOutputFormatChanged:" + format);
                resetVideoOutputFormat(format);
                startMuxerIfReady();
            }

            @Override
            public void onOutputBufferAvailable(int index, MediaCodec.BufferInfo info) {
                if (config.allowLog())
                    Log.i(TAG, "VideoRecorder onOutputBufferAvailable :" + index + "==" + info.size);
                try {
                    muxVideo(index, info);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (config.allowLog())
                        Log.e(TAG, "Muxer encountered an error! ", e);
                }
            }

            @Override
            public void onError(Exception exception) {
                if (config.allowLog())
                    Log.d(TAG, "VideoRecorder onError :" + exception);
                observer.notAllowEnterNextStep();
            }
        });
        return videoRecorder.prepare();
    }

    private boolean prepareAudioEncoder() {
        audioRecorder = new AudioRecorder(observer, config, new RecorderCallback() {
            @Override
            public void onInputBufferAvailable(int index) {
                if (config.allowLog())
                    Log.d(TAG, "AudioRecorder onInputBufferAvailable :" + index);
            }

            @Override
            public void onOutputFormatChanged(MediaFormat format) {
                if (config.allowLog())
                    Log.d(TAG, "AudioRecorder onOutputFormatChanged :" + format);
                resetAudioOutputFormat(format);
                startMuxerIfReady();
            }

            @Override
            public void onOutputBufferAvailable(int index, MediaCodec.BufferInfo info) {
                if (config.allowLog())
                    Log.i(TAG, "AudioRecorder onOutputBufferAvailable: " + index + "==" + info.size);
                try {
                    muxAudio(index, info);
                } catch (Exception e) {
                    if (config.allowLog())
                        Log.e(TAG, "Muxer encountered an error! ", e);
                }
            }

            @Override
            public void onError(Exception exception) {
                if (config.allowLog())
                    Log.d(TAG, "AudioRecorder onError :" + "==" + exception);
                observer.notAllowEnterNextStep();
            }
        });
        return audioRecorder.prepare();
    }

    private final int INVALID_INDEX = -1;

    private byte[] sps, pps;

    private MediaFormat mVideoOutputFormat = null, mAudioOutputFormat = null;

    private int mVideoTrackIndex = INVALID_INDEX, mAudioTrackIndex = INVALID_INDEX;

    private LinkedList<Integer> mPendingVideoEncoderBufferIndices = new LinkedList<>();

    private LinkedList<Integer> mPendingAudioEncoderBufferIndices = new LinkedList<>();

    private LinkedList<MediaCodec.BufferInfo> mPendingAudioEncoderBufferInfos = new LinkedList<>();

    private LinkedList<MediaCodec.BufferInfo> mPendingVideoEncoderBufferInfos = new LinkedList<>();

    private void resetVideoOutputFormat(MediaFormat newFormat) {
        if (mVideoTrackIndex >= 0 || mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        mVideoOutputFormat = newFormat;
        sps = newFormat.getByteBuffer("csd-0").array();
        pps = newFormat.getByteBuffer("csd-1").array();
        if (config.allowLog())
            Log.i(TAG, "Video output format changed.\n New format: " + newFormat.toString() +//
                    "\nvideo sps :" + Arrays.toString(sps) + "\nvideo pps :" + Arrays.toString(pps));
        // video sps :[0, 0, 0, 1, 103, 66, -128, 42, -38, 1, 16, 15, 30, 94, 82, 10, 12, 12, 13, -95, 66, 106]
        // video pps :[0, 0, 0, 1, 104, -50, 6, -30]
        observer.reportVideoHeaderByte(sps, pps);
    }

    private void resetAudioOutputFormat(MediaFormat newFormat) {
        if (mAudioTrackIndex >= 0 || mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        mAudioOutputFormat = newFormat;
        if (config.allowLog())
            Log.i(TAG, "Audio output format changed.\n New format: " + newFormat.toString());
    }

    private void startMuxerIfReady() {
        if (mMuxerStarted || mVideoOutputFormat == null || //
                (config.hasAudio() && (audioRecorder != null || mAudioOutputFormat == null))) {
             return;
        }
        mVideoTrackIndex = mMuxer.addTrack(mVideoOutputFormat);
        mAudioTrackIndex = !config.hasAudio() && audioRecorder == null ? INVALID_INDEX : mMuxer.addTrack(mAudioOutputFormat);
        mMuxer.start();
        mMuxerStarted = true;
        if (config.allowLog())
            Log.i(TAG, "Started media muxer, videoIndex=" + mVideoTrackIndex);
        if (mPendingVideoEncoderBufferIndices.isEmpty() && mPendingAudioEncoderBufferIndices.isEmpty()) {
            return;
        }
        if (config.allowLog())
            Log.i(TAG, "Mux pending video output buffers...");
        MediaCodec.BufferInfo info;
        while ((info = mPendingVideoEncoderBufferInfos.poll()) != null) {
            int index = mPendingVideoEncoderBufferIndices.poll();
            muxVideo(index, info);
        }
        if (config.hasAudio() && audioRecorder != null) {
            while ((info = mPendingAudioEncoderBufferInfos.poll()) != null) {
                int index = mPendingAudioEncoderBufferIndices.poll();
                muxAudio(index, info);
            }
        }
        if (config.allowLog())
            Log.i(TAG, "Mux pending video output buffers done.");
    }

    private void muxVideo(int index, MediaCodec.BufferInfo info) {
        if (!recorder.get()) {
            if (config.allowLog())
                Log.w(TAG, "muxVideo: Already stopped!");
            return;
        }
        if (!mMuxerStarted || mVideoTrackIndex == INVALID_INDEX) {
            mPendingVideoEncoderBufferIndices.add(index);
            mPendingVideoEncoderBufferInfos.add(info);
            return;
        }
        ByteBuffer encodedData = videoRecorder.getOutputBuffer(index);
        writeSampleData(mVideoTrackIndex, info, encodedData);
        videoRecorder.releaseOutputBuffer(index);
        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            if (config.allowLog())
                Log.d(TAG, "Stop encoder and muxer, since the buffer has been marked with EOS");
            mVideoTrackIndex = INVALID_INDEX;
        }
    }

    private void muxAudio(int index, MediaCodec.BufferInfo info) {
        if (!recorder.get()) {
            if (config.allowLog())
                Log.w(TAG, "muxAudio: Already stopped!");
            return;
        }
        if (!mMuxerStarted || mAudioTrackIndex == INVALID_INDEX) {
            mPendingAudioEncoderBufferIndices.add(index);
            mPendingAudioEncoderBufferInfos.add(info);
            return;
        }
        ByteBuffer encodedData = audioRecorder.getOutputBuffer(index);
        writeSampleData(mAudioTrackIndex, info, encodedData);
        audioRecorder.releaseOutputBuffer(index);
        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            if (config.allowLog())
                Log.d(TAG, "Stop encoder and muxer, since the buffer has been marked with EOS");
            mAudioTrackIndex = INVALID_INDEX;
        }
    }

    private void writeSampleData(int track, MediaCodec.BufferInfo info, ByteBuffer encodedData) {
        if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            if (config.allowLog())
                Log.d(TAG, "Ignoring BUFFER_FLAG_CODEC_CONFIG");
            info.size = 0;
        }
        boolean eos = (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
        if (info.size == 0 && !eos) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            if (info.presentationTimeUs != 0) { // maybe 0 if eos
                if (track == mVideoTrackIndex) {
                    setCaptureTime(true, info);
                } else if (track == mAudioTrackIndex) {
                    setCaptureTime(false, info);
                }
            }
            if (config.allowLog())
                Log.d(TAG, "Got buffer, track=" + track + ", info: size=" + info.size + ", presentationTimeUs=" + info.presentationTimeUs);
        }
        if (encodedData != null) {
            encodedData.position(info.offset);
            encodedData.limit(info.offset + info.size);
            mMuxer.writeSampleData(track, encodedData, info);
            if (config.allowLog())
                Log.i(TAG, "Sent " + info.size + " bytes to MediaMuxer on track " + track);
            reportData(track, info, encodedData);
        }
    }

    private void reportData(int track, MediaCodec.BufferInfo info, ByteBuffer encodedData) {
        byte[] bytes = null;
        if (track == mVideoTrackIndex) {//send video data
            if (info.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                bytes = new byte[info.size + sps.length + pps.length];
                System.arraycopy(sps, 0, bytes, 0, sps.length);
                System.arraycopy(pps, 0, bytes, sps.length, pps.length);
                encodedData.get(bytes, sps.length + pps.length, info.size);
            } else {
                bytes = new byte[info.size];
                encodedData.get(bytes, 0, info.size);
            }
            observer.reportVideoContentByte(bytes);
        } else if (track == mAudioTrackIndex) {//send audio data
            bytes = new byte[info.size];
            encodedData.get(bytes, 0, info.size);
            observer.reportAudioContentByte(bytes);
        }
        if (config.allowLog() && bytes != null)
            Log.i(TAG, "report video data :" + bytes.length + "==" + Arrays.toString(bytes));
    }

    private void setCaptureTime(boolean isVideo, MediaCodec.BufferInfo info) {
        if (info.presentationTimeUs != 0) {
            if (isVideo) {
                resetVideoPts(info);
            } else {
                resetAudioPts(info);
            }
        }
        if (startTime <= 0) {
            startTime = info.presentationTimeUs;
        }
        long time = (info.presentationTimeUs - startTime) / 1000 / 1000;
        //no need to report when time less than one second
        if (SystemClock.elapsedRealtime() - mLastFiredTime < 1000) {
            return;
        }
        observer.reportTime(time);
        mLastFiredTime = SystemClock.elapsedRealtime();
    }

    private long mVideoPtsOffset, mAudioPtsOffset, startTime, mLastFiredTime;

    private void resetAudioPts(MediaCodec.BufferInfo buffer) {
        if (mAudioPtsOffset == 0) {
            mAudioPtsOffset = buffer.presentationTimeUs;
            buffer.presentationTimeUs = 0;
        } else {
            buffer.presentationTimeUs -= mAudioPtsOffset;
        }
    }

    private void resetVideoPts(MediaCodec.BufferInfo buffer) {
        if (mVideoPtsOffset == 0) {
            mVideoPtsOffset = buffer.presentationTimeUs;
            buffer.presentationTimeUs = 0;
        } else {
            buffer.presentationTimeUs -= mVideoPtsOffset;
        }
    }

    private void release() {
        stopEncoders();
        signalStop();
        mVideoOutputFormat = mAudioOutputFormat = null;
        mVideoTrackIndex = mAudioTrackIndex = INVALID_INDEX;
        mMuxerStarted = false;
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        if (mMuxer != null) {
            try {
                mMuxer.stop();
                mMuxer.release();
            } catch (Exception e) {
                // ignored
            }
            mMuxer = null;
        }
        if (config.allowLog())
            Log.d(TAG, "recorder release complete");
    }

    private void stopEncoders() {
        mPendingAudioEncoderBufferInfos.clear();
        mPendingAudioEncoderBufferIndices.clear();
        mPendingVideoEncoderBufferInfos.clear();
        mPendingVideoEncoderBufferIndices.clear();
        // maybe called on an error has been occurred
        if (videoRecorder != null) {
            videoRecorder.release();
            videoRecorder = null;
        }
        if (audioRecorder != null) {
            audioRecorder.release();
            audioRecorder = null;
        }
    }

    private void signalStop() {
        MediaCodec.BufferInfo eos = new MediaCodec.BufferInfo();
        ByteBuffer buffer = ByteBuffer.allocate(0);
        eos.set(0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
        if (config.allowLog())
            Log.i(TAG, "Signal EOS to muxer ");
        if (mVideoTrackIndex != INVALID_INDEX) {
            writeSampleData(mVideoTrackIndex, eos, buffer);
        }
        if (mAudioTrackIndex != INVALID_INDEX) {
            writeSampleData(mAudioTrackIndex, eos, buffer);
        }
        mVideoTrackIndex = INVALID_INDEX;
        mAudioTrackIndex = INVALID_INDEX;
    }

}
