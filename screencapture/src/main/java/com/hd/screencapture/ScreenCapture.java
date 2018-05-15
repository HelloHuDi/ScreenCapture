package com.hd.screencapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hd on 2018/5/14 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapture {

    private final String TAG = "Screen-Capture";

    public static ScreenCapture with(@NonNull AppCompatActivity activity) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            throw new RuntimeException("current activity is not running state !");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new RuntimeException("the sdk version less than 21 equipment does not provide this function");
        }
        return new ScreenCapture(activity);
    }

    private ScreenCaptureFragment screenCaptureFragment;

    private ScreenCapture(@NonNull AppCompatActivity activity) {
        //add lifecycle observer
        ScreenCaptureObserver observer = new ScreenCaptureObserver(this);
        activity.getLifecycle().addObserver(observer);
        //init the main capture work fragment7
        screenCaptureFragment = getScreenCaptureFragment(activity);
        screenCaptureFragment.addObserver(observer);
        //init default config
        setConfig(ScreenCaptureConfig.initDefaultConfig());
    }

    private ScreenCaptureFragment getScreenCaptureFragment(Activity activity) {
        ScreenCaptureFragment screenCaptureFragment = findScreenCaptureFragment(activity);
        boolean isNewInstance = screenCaptureFragment == null;
        if (isNewInstance) {
            screenCaptureFragment = new ScreenCaptureFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction().add(screenCaptureFragment, TAG).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return screenCaptureFragment;
    }

    private ScreenCaptureFragment findScreenCaptureFragment(Activity activity) {
        return (ScreenCaptureFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public ScreenCapture setConfig(ScreenCaptureConfig config) {
        if (screenCaptureFragment != null) {
            screenCaptureFragment.setConfig(config);
        }
        return this;
    }

    public void startCapture() {
        startCapture(-1);
    }

    public void startCapture(long duration) {
        if (screenCaptureFragment != null) {
            screenCaptureFragment.startCapture();
            if (duration > 0) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopCapture();
                    }
                }, duration);
            }
        }
    }

    public void stopCapture() {
        if (screenCaptureFragment != null) {
            screenCaptureFragment.stopCapture();
        }
    }

}
