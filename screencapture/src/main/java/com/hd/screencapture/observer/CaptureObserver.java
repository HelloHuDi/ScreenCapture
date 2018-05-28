package com.hd.screencapture.observer;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureCallback;
import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.help.ScreenCaptureState;

/**
 * Created by hd on 2018/5/15 .
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class CaptureObserver {

    final String TAG = CaptureObserver.class.getSimpleName();

    volatile boolean alive;

    private ScreenCapture screenCapture;

    private ScreenCaptureConfig config;

    private boolean continueReportState = true;

    CaptureObserver(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
        alive = true;
    }

    void stopCapture() {
        if (screenCapture.isRunning())
            screenCapture.stopCapture();
    }

    public void addConfig(ScreenCaptureConfig config) {
        this.config = config;
    }

    public boolean isAlive() {
        return alive;
    }

    public void notAllowEnterNextStep() {
        reportState(ScreenCaptureState.FAILED);
        stopCapture();
    }

    public void reportState(ScreenCaptureState state) {
        if (config.allowLog())
            Log.d(TAG, "report State:" + state);
        if (checkAliveAndConfig() && continueReportState) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null) {
                callback.captureState(state);
            }
            continueReportState = continueState(state);
        }
    }

    public void reportTime(long time) {
        if (checkAliveAndConfig()) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null) {
                callback.captureTime(time);
            }
        }
    }

    public void reportVideoHeaderByte(byte[] sps, byte[] pps) {
        if (checkAliveAndConfig() && checkByte(sps) && checkByte(pps)) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null && callback instanceof ScreenCaptureStreamCallback) {
                ((ScreenCaptureStreamCallback) callback).videoHeaderByte(sps, pps);
            }
        }
    }

    public void reportVideoContentByte(byte[] content) {
        if (checkAliveAndConfig() && checkByte(content)) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null && callback instanceof ScreenCaptureStreamCallback) {
                ((ScreenCaptureStreamCallback) callback).videoContentByte(content);
            }
        }
    }

    public void reportAudioContentByte(byte[] content) {
        if (checkAliveAndConfig() && checkByte(content)) {
            ScreenCaptureCallback callback = config.getCaptureCallback();
            if (callback != null && callback instanceof ScreenCaptureStreamCallback) {
                ((ScreenCaptureStreamCallback) callback).audioContentByte(content);
            }
        }
    }

    private boolean checkAliveAndConfig() {
        return isAlive() && config != null;
    }

    private boolean checkByte(byte[] data) {
        return data != null && data.length > 0;
    }

    private boolean continueState(ScreenCaptureState state) {
        return !(state == ScreenCaptureState.CANCEL || //
                state == ScreenCaptureState.FAILED || //
                state == ScreenCaptureState.COMPLETED);
    }
}
