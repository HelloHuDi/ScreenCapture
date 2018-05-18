package com.hd.screencapture;

/**
 * Created by hd on 2018/5/15 .
 */
public interface ScreenCaptureCallback {

    /**
     * report capture state{@link ScreenCaptureState}
     */
    void captureState(ScreenCaptureState state);
}
