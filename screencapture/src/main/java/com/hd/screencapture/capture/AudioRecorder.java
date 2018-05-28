package com.hd.screencapture.capture;

import android.annotation.TargetApi;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

import java.io.IOException;
import java.util.Locale;


/**
 * Created by hd on 2018/5/20 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class AudioRecorder extends Recorder {

    private AudioRecord audioRecord;
    
    private AudioConfig audioConfig;

    public AudioRecorder(@NonNull CaptureObserver observer, @NonNull ScreenCaptureConfig config,//
                         @NonNull RecorderCallback callback) {
        super(AUDIO_RECORDER, observer, config, callback);
        TAG = "AudioRecorder";
        audioConfig=config.getAudioConfig();
    }

    @Override
    public boolean prepare() {
        try {
            if (initAudioRecord()) {
                initMediaCodec(createMediaFormat());
                startReadData();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.e(TAG, "Invalid parameter ! "+String.format(Locale.US, "Bad arguments: getMinBufferSize(%d, %d, %d)",//
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
    void initMediaCodec(MediaFormat format)throws IOException {
        super.initMediaCodec(format);
        mEncoder = createEncoder(format.getString(MediaFormat.KEY_MIME));
        mEncoder.setCallback(mCodecCallback);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    private MediaCodec.Callback mCodecCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
//            callback.onInputBufferAvailable(index);
            Log.d(TAG,"onInputBufferAvailable :"+codec+"="+index);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
//            callback.onOutputBufferAvailable(index, info);
            Log.d(TAG,"onOutputBufferAvailable :"+codec+"="+index+"=="+info);

        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
//            callback.onError(e);
            Log.d(TAG,"onError :"+codec+"="+e);

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
//            callback.onOutputFormatChanged(format);
            Log.d(TAG,"onOutputFormatChanged :"+codec+"="+format);
        }
    };

    private void startReadData() {

    }
}
