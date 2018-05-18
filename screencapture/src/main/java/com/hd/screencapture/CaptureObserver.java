package com.hd.screencapture;

import com.hd.screencapture.config.ScreenCaptureConfig;

/**
 * Created by hd on 2018/5/15 .
 */
public abstract class CaptureObserver {

    private ScreenCapture screenCapture;

    private ScreenCaptureConfig config;

    volatile boolean alive;

    CaptureObserver(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
        alive = true;
    }

    public void initConfig(ScreenCaptureConfig config) {
        this.config = config;
    }

    public boolean isAlive() {
        return alive;
    }

    public void stopCapture() {
        screenCapture.stopCapture();
    }

    public void reportState(ScreenCaptureState state) {
        if (isAlive() && config != null) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null) {
                callback.captureState(state);
            }
        }
    }
}
