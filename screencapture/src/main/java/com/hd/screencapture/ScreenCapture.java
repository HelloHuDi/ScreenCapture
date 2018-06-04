package com.hd.screencapture;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.help.ScreenCaptureFragment;
import com.hd.screencapture.help.ScreenCaptureHelper;
import com.hd.screencapture.help.Utils;
import com.hd.screencapture.observer.CaptureObserver;
import com.hd.screencapture.observer.ScreenCaptureObserver;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by hd on 2018/5/14 .
 * Note permissions. By default, we will request permission for you
 * {@link Manifest.permission#WRITE_EXTERNAL_STORAGE},{@link Manifest.permission#RECORD_AUDIO}
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public final class ScreenCapture {

    private static final String TAG = "Screen-Capture";

    public static ScreenCapture with(@NonNull AppCompatActivity activity) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            throw new RuntimeException("current activity is not running state !");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new RuntimeException("the sdk version less than 21 equipment does not provide this function !");
        }
        if (!Utils.isExternalStorageReady()) {
            Log.e(TAG, "current no storage space");
        }
        if (!Utils.isPermissionGranted(activity, false)) {
            Log.e(TAG, "no permission !!!");
        }
        return new ScreenCapture(activity);
    }

    private ScreenCaptureHelper screenCaptureHelper;

    private AtomicBoolean capture = new AtomicBoolean(false);

    private ScreenCapture(@NonNull AppCompatActivity activity) {
        capture.set(false);
        CaptureObserver observer = new ScreenCaptureObserver(this);
        //add lifecycle observer
        activity.getLifecycle().addObserver((ScreenCaptureObserver) observer);
        //init the main capture work fragment
        ScreenCaptureFragment screenCaptureFragment = getScreenCaptureFragment(activity);
        //add capture callback observer
        screenCaptureFragment.addObserver(observer);
        //init capture helper
        screenCaptureHelper = new ScreenCaptureHelper(activity.getApplicationContext(), observer, screenCaptureFragment);
        //init default config
        setConfig(ScreenCaptureConfig.initDefaultConfig(activity));
    }

    private ScreenCaptureFragment getScreenCaptureFragment(@NonNull Activity activity) {
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

    private ScreenCaptureFragment findScreenCaptureFragment(@NonNull Activity activity) {
        return (ScreenCaptureFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public ScreenCapture setConfig(@NonNull ScreenCaptureConfig config) {
        if (config.getVideoConfig() == null)
            throw new RuntimeException("you must set the capture video config if you call this method," +//
                                               "if you do not call this method, we will provide a default video config ");
        if (config.getAudioConfig() == null)
            Log.w(TAG, "note that if you do not set the audio config, your video will have not voice ");
        screenCaptureHelper.addConfig(config);
        return this;
    }

    public boolean isRunning() {
        return capture.get();
    }

    public void startCapture() {
        startCapture(-1);
    }

    public void startCapture(long duration) {
        if (!isRunning()) {
            capture.set(true);
            screenCaptureHelper.startCapture();
            if (duration > 0) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopCapture();
                    }
                }, duration);
            }
        } else {
            Log.e(TAG, "capturing !!!");
        }
    }

    public void stopCapture() {
        if (isRunning()) {
            capture.set(false);
            screenCaptureHelper.stopCapture();
            System.gc();
            System.runFinalization();
        } else {
            Log.e(TAG, "stop capture always");
        }
    }

}
