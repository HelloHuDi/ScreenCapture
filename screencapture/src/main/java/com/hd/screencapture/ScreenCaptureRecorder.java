package com.hd.screencapture;

import android.annotation.TargetApi;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hd on 2018/5/14 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureRecorder extends Thread {

    private final String TAG = ScreenCaptureRecorder.class.getSimpleName();

    private CaptureObserver observer;

    private MediaProjection mediaProjection;

    private ScreenCaptureConfig config;

    private AtomicBoolean recorder = new AtomicBoolean(false);

    private boolean mMuxerStarted = false;

    private int mVideoTrackIndex = -1;

    private MediaMuxer mMuxer;

    private MediaCodec mEncoder;

    private VirtualDisplay mVirtualDisplay;

    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private Surface mSurface;

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
    }

    @Override
    public void run() {
        super.run();
        boolean error = false;
        try {
            observer.reportState(ScreenCaptureState.CAPTURING);
            prepareEncoder();
            mMuxer = new MediaMuxer(config.getFile().getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVirtualDisplay = mediaProjection.createVirtualDisplay(TAG + "-display",//
                                                                   config.getWidth(), config.getHeight(), config.getDpi(), //
                                                                   DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR/*VIRTUAL_DISPLAY_FLAG_PUBLIC*/,//
                                                                   mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            recordVirtualDisplay();
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
            observer.reportState(ScreenCaptureState.FAILED);
        } finally {
            release();
            if (!error) {
                observer.reportState(ScreenCaptureState.COMPLETED);
            }
        }
    }

    private void prepareEncoder() throws IOException {
        final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC; // H.264 Advanced Video Coding
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, config.getWidth(), config.getHeight());
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, config.getBitrate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, config.getFrameRate());
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, config.getIFrameInterval());
        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
    }

    private void recordVirtualDisplay() {
        final long timeoutUs = 10000;
        while (recorder.get() && observer.isAlive()) {
            int index = mEncoder.dequeueOutputBuffer(mBufferInfo, timeoutUs);
            Log.i(TAG, "dequeue output buffer index=" + index + "=" + recorder.get());
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();
            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (index >= 0) {
                if (!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                }
                encodeToVideoTrack(index);
                mEncoder.releaseOutputBuffer(index, false);
            }
        }
    }

    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mEncoder.getOutputBuffer(index);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);
            Log.i(TAG, "sent " + mBufferInfo.size + " bytes to muxer...");
        }
    }

    private void resetOutputFormat() {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();
        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
        mVideoTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }

    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.reset();
            mEncoder.release();
            mEncoder = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
        Log.d("tag","recorder release complete");
    }
}
