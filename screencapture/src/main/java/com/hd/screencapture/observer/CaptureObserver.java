package com.hd.screencapture.observer;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureCallback;
import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.help.ScreenCaptureState;

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
        if (screenCapture.isRunning()) screenCapture.stopCapture();
    }

    public void notAllowEnterNextStep() {
        reportState(ScreenCaptureState.FAILED);
        stopCapture();
    }

    public void reportState(ScreenCaptureState state) {
        if (isAlive() && config != null) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null) {
                callback.captureState(state);
            }
        }
    }

    public void reportTime(long time) {
        if (isAlive() && config != null) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null) {
                callback.captureTime(time);
            }
        }
    }

    public void reportVideoHeaderByte(byte[] sps, byte[] pps) {
        if (isAlive() && config != null && sps != null && sps.length > 0 && pps != null && pps.length > 0) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null && callback instanceof ScreenCaptureStreamCallback) {
                ((ScreenCaptureStreamCallback) callback).videoHeaderByte(sps, pps);
            }
        }
    }

    public void reportVideoContentByte(byte[] content) {
        if (isAlive() && config != null && content != null && content.length > 0) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null && callback instanceof ScreenCaptureStreamCallback) {
                ((ScreenCaptureStreamCallback) callback).videoContentByte(content);
            }
        }
    }

    public void reportAudioContentByte(byte[] content) {
        if (isAlive() && config != null && content != null && content.length > 0) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null && callback instanceof ScreenCaptureStreamCallback) {
                ((ScreenCaptureStreamCallback) callback).audioContentByte(content);
            }
        }
    }

}
