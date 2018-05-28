package com.hd.screencapture.observer;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hd.screencapture.ScreenCapture;


/**
 * Created by hd on 2018/5/14 .
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class ScreenCaptureObserver extends CaptureObserver implements LifecycleObserver {

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
        stopCapture();
    }
}
