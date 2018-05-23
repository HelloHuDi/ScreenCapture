package com.hd.screencapture.capture;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

import java.nio.ByteBuffer;

/**
 * Created by hd on 2018/5/20 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AudioRecorder extends Recorder {

    public AudioRecorder(@NonNull CaptureObserver observer, @NonNull ScreenCaptureConfig config,//
                         @NonNull RecorderCallback callback) {
        super(observer, config, callback);
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public boolean record() {
        return true;
    }

    @Override
    public void release() {

    }

    public void releaseOutputBuffer(int index) {
    }

    public ByteBuffer getOutputBuffer(int index) {
        return null;
    }

}
