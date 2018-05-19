package com.hd.screencapture.callback;

import com.hd.screencapture.help.ScreenCaptureState;

/**
 * Created by hd on 2018/5/15 .
 */
public interface ScreenCaptureCallback {

    /**
     * report capture state{@link ScreenCaptureState}
     */
    void captureState(ScreenCaptureState state);

    /**
     * report capture time,unit seconds
     */
    void captureTime(long time);
}
