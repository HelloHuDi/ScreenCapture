package com.hd.screencapture.capture;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hd on 2018/5/20 .
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public abstract class Recorder {

    static final int VIDEO_RECORDER = 0;

    static final int AUDIO_RECORDER = 1;

    private int type;

    AtomicBoolean record = new AtomicBoolean(false);

    String TAG = Recorder.class.getSimpleName();

    CaptureObserver observer;

    ScreenCaptureConfig config;

    RecorderCallback callback;

    MediaCodec mEncoder;

    Recorder(int type, @NonNull CaptureObserver observer, @NonNull ScreenCaptureConfig config,//
             @NonNull RecorderCallback callback) {
        this.type = type;
        this.observer = observer;
        this.config = config;
        this.callback = callback;
    }

    public abstract boolean prepare();

    public boolean record() {
        try {
            if (mEncoder != null) {
                record.set(true);
                mEncoder.start();
                return true;
            }
            return false;
        }catch (Exception ignored){
            ignored.printStackTrace();
        }
        return false;
    }

    public void release() {
        record.set(false);
    }

    MediaFormat createMediaFormat() {
        return null;
    }

    void initMediaCodec(MediaFormat format) throws IOException {
    }

    MediaCodec createEncoder(String mimeType) throws IOException {
        String mCodecName = type == 0 ? config.getVideoConfig().getCodecName()//
                : config.getAudioConfig().getCodecName();
        try {
            if (!TextUtils.isEmpty(mCodecName)) {
                return MediaCodec.createByCodecName(mCodecName);
            }
        } catch (IOException e) {
            Log.w(TAG, "Create MediaCodec by name '" + mCodecName + "' failure!", e);
        }
        return MediaCodec.createEncoderByType(mimeType);
    }

    public final ByteBuffer getOutputBuffer(int index) {
        return getEncoder().getOutputBuffer(index);
    }

    public final ByteBuffer getInputBuffer(int index) {
        return getEncoder().getInputBuffer(index);
    }

    public final void queueInputBuffer(int index, int offset, int size, long pstTs, int flags) {
        getEncoder().queueInputBuffer(index, offset, size, pstTs, flags);
    }

    public final void releaseOutputBuffer(int index) {
        getEncoder().releaseOutputBuffer(index, false);
    }

    public final MediaCodec getEncoder() {
        return Objects.requireNonNull(mEncoder, "doesn't prepare()");
    }

}
