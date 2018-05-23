package com.hd.screencapture.capture;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

/**
 * Created by hd on 2018/5/20 .
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public abstract class Recorder {

    CaptureObserver observer;

    ScreenCaptureConfig config;

    RecorderCallback callback;

    Recorder(@NonNull CaptureObserver observer,@NonNull ScreenCaptureConfig config,//
                    @NonNull RecorderCallback callback) {
        this.observer = observer;
        this.config = config;
        this.callback=callback;
    }

    public abstract boolean prepare();

    public abstract boolean record();

    public abstract void release();

}
