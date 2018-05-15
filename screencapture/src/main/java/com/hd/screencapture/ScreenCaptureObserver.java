package com.hd.screencapture;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;


/**
 * Created by hd on 2018/5/14 .
 */
public class ScreenCaptureObserver implements LifecycleObserver {

    private final String TAG = ScreenCaptureObserver.class.getSimpleName();

    private ScreenCapture screenCapture;

    public ScreenCaptureObserver(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        Log.d(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        Log.d(TAG, "onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {
        Log.d(TAG, "onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

}
