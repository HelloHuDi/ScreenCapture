package com.hd.screencapture.callback;

import android.support.annotation.NonNull;

/**
 * Created by hd on 2018/5/21 .
 */
public interface ScreenCaptureStreamCallback extends ScreenCaptureCallback {

    void videoHeaderByte(@NonNull byte[] sps,@NonNull byte[] pps);

    void videoContentByte(@NonNull byte[] content);

    void audioContentByte(@NonNull byte[] content);
}
