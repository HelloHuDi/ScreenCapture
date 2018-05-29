package com.hd.screencapture.capture;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseLongArray;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.help.ExecutorUtil;
import com.hd.screencapture.observer.CaptureObserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;


/**
 * Created by hd on 2018/5/20 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class AudioRecorder extends Recorder {

    private AudioRecord audioRecord;

    private AudioConfig audioConfig;

    private ExecutorUtil executorUtil;

    public AudioRecorder(@NonNull CaptureObserver observer, @NonNull ScreenCaptureConfig config,//
                         @NonNull RecorderCallback callback) {
        super(AUDIO_RECORDER, observer, config, callback);
        TAG = "AudioRecorder";
        audioConfig = config.getAudioConfig();
        executorUtil = new ExecutorUtil();
        mChannelsSampleRate = audioConfig.getSamplingRate() * audioConfig.getChannelCount();
        if (config.allowLog())
            Log.i(TAG, "in bitrate " + (mChannelsSampleRate << getBit()));
        mPollRate = 2048_000 / audioConfig.getSamplingRate(); // poll per 2048 samples
    }

    @Override
    public boolean prepare() {
        try {
            if (initAudioRecord()) {
                initMediaCodec(createMediaFormat());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean record() {
        try {
            if (mEncoder != null) {
                record.set(true);
                mEncoder.start();
                startReadData();
                return true;
            }
            return false;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    @Override
    public void release() {
        super.release();
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.reset();
            mEncoder.release();
            mEncoder = null;
        }
    }

    private boolean initAudioRecord() {
        int minBufferSize = AudioRecord.getMinBufferSize(audioConfig.getSamplingRate(), //
                                                         audioConfig.getChannelCount(), audioConfig.getBitrate());
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            if (config.allowLog())
                Log.e(TAG, "Invalid parameter ! " + String.format(Locale.US, "Bad arguments: getMinBufferSize(%d, %d, %d)",//
                                                                  audioConfig.getSamplingRate(), audioConfig.getChannelCount(), audioConfig.getBitrate()));
            return false;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, audioConfig.getSamplingRate(),//
                                      audioConfig.getChannelCount(), audioConfig.getBitrate(), minBufferSize * 4);
        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            if (config.allowLog())
                Log.e(TAG, "AudioRecord initialize fail !");
            return false;
        }
        try {
            if (config.allowLog() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, " size in frame " + audioRecord.getBufferSizeInFrames());
            }
            audioRecord.startRecording();
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                if (config.allowLog())
                    Log.e(TAG, "unable to recordings,recording equipment may be occupied");
                release();
                return false;
            }
            return true;
        } catch (Exception e) {
            if (config.allowLog())
                Log.e(TAG, "please check audio permission");
            release();
            return false;
        }
    }

    @Override
    MediaFormat createMediaFormat() {
        String type = MediaFormat.MIMETYPE_AUDIO_AAC;
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, type);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, audioConfig.getChannelCount());
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, audioConfig.getSamplingRate());
        format.setInteger(MediaFormat.KEY_BIT_RATE, audioConfig.getBitrate());
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, audioConfig.getLevel() == null ?//
                MediaCodecInfo.CodecProfileLevel.AACObjectMain : audioConfig.getLevel().profile);
        if (config.allowLog())
            Log.d(TAG, "created audio format: " + format);
        return format;
    }

    @Override
    void initMediaCodec(MediaFormat format) throws IOException {
        super.initMediaCodec(format);
        mEncoder = createEncoder(format.getString(MediaFormat.KEY_MIME));
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    private void startReadData() {
        executorUtil.diskIO().execute(() -> {
            while (record.get()) {
                try {
                    feedInput();
                    feedOutput();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }
        });
    }

    private void feedInput() {
        int inputBufferIndex = dequeueInputBuffer(0);
//        if (config.allowLog())
//            Log.d(TAG, "audio encoder returned input buffer index=" + inputBufferIndex);
        final boolean eos = audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED;
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = getInputBuffer(inputBufferIndex);
            int offset = inputBuffer.position();
            int limit = inputBuffer.limit();
            int size = 0;
            int flags;
            if (!eos) {
                flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                size = audioRecord.read(inputBuffer, limit);
                if (config.allowLog())
                    Log.d(TAG, "Read frame data size " + size //
                            + " for index " + inputBufferIndex + " buffer : " + offset + ", " + limit);
                if (size < 0) {
                    size = 0;
                }
            } else {
                flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            }
            long presentationTimeUs = calculateFrameTimestamp(size << 3);
            queueInputBuffer(inputBufferIndex, offset, size, presentationTimeUs, flags);
            callback.onInputBufferAvailable(inputBufferIndex);
        } else {
//            if (config.allowLog())
//                Log.i(TAG, "try later to poll input buffer:"+inputBufferIndex);
            sleepSomeTime();
            if (!record.get())
                return;
            feedInput();
        }
    }

    private MediaCodec.BufferInfo bufferInfo = null;

    private void feedOutput() {
        while (record.get()) {
            if (null == bufferInfo) {
                bufferInfo = new MediaCodec.BufferInfo();
            }
            int outputBufferIndex = dequeueOutputBuffer(bufferInfo, 1);
            if (config.allowLog())
                Log.d(TAG, "audio encoder returned output buffer index=" + outputBufferIndex);
            if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                callback.onOutputFormatChanged(getEncoder().getOutputFormat());
            }
            if (outputBufferIndex < 0) {
                bufferInfo.set(0, 0, 0, 0);
                break;
            } else {
                callback.onOutputBufferAvailable(outputBufferIndex, bufferInfo);
            }
        }
    }

    private static final int LAST_FRAME_ID = -1;

    private SparseLongArray mFramesUsCache = new SparseLongArray(2);

    private int mChannelsSampleRate;

    private long calculateFrameTimestamp(int totalBits) {
        int samples = totalBits >> getBit();
        long frameUs = mFramesUsCache.get(samples, -1);
        if (frameUs == -1) {
            frameUs = samples * 1000_000 / mChannelsSampleRate;
            mFramesUsCache.put(samples, frameUs);
        }
        long timeUs = SystemClock.elapsedRealtimeNanos() / 1000;
        // accounts the delay of polling the audio sample data
        timeUs -= frameUs;
        long currentUs;
        long lastFrameUs = mFramesUsCache.get(LAST_FRAME_ID, -1);
        if (lastFrameUs == -1) { // it's the first frame
            currentUs = timeUs;
        } else {
            currentUs = lastFrameUs;
        }
        if (config.allowLog())
            Log.i(TAG, "count samples pts: " + currentUs + ", time pts: " + timeUs + ", samples: " + samples);
        // maybe too late to acquire sample data
        if (timeUs - currentUs >= (frameUs << 1)) {
            // reset
            currentUs = timeUs;
        }
        mFramesUsCache.put(LAST_FRAME_ID, currentUs + frameUs);
        return currentUs;
    }

    private int getBit() {
        if (audioConfig.getBitrate() == AudioFormat.ENCODING_PCM_16BIT)
            return 4;
        else if (audioConfig.getBitrate() == AudioFormat.ENCODING_PCM_8BIT)
            return 3;
        return 1;
    }

    private int mPollRate;

    private void sleepSomeTime() {
        try {
            Thread.sleep(mPollRate);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
