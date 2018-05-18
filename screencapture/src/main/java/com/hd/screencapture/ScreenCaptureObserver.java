package com.hd.screencapture;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;


/**
 * Created by hd on 2018/5/14 .
 */
public class ScreenCaptureObserver extends CaptureObserver implements LifecycleObserver {

    private final String TAG = ScreenCaptureObserver.class.getSimpleName();

    public ScreenCaptureObserver(ScreenCapture screenCapture) {
        super(screenCapture);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        Log.d(TAG, "onResume");
        alive = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        Log.d(TAG, "onDestroy");
        alive = false;
    }
}
