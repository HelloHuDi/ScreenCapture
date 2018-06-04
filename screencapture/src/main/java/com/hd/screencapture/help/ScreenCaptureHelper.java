package com.hd.screencapture.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hd.screencapture.capture.ScreenCaptureRecorder;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

/**
 * Created by hd on 2018/6/4 .
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public final class ScreenCaptureHelper implements ScreenCaptureFragment.CapturePrepareCallback {

    private final String TAG = ScreenCaptureHelper.class.getSimpleName();

    private Context appContext;

    private CaptureObserver observer;

    private ScreenCaptureFragment screenCaptureFragment;

    private ScreenCaptureConfig config;

    private ScreenCaptureRecorder screenCaptureRecorder;

    public ScreenCaptureHelper(@NonNull Context appContext, @NonNull CaptureObserver observer,//
                               @NonNull ScreenCaptureFragment screenCaptureFragment) {
        this.appContext = appContext;
        this.observer = observer;
        this.screenCaptureFragment = screenCaptureFragment;
        screenCaptureFragment.addCallback(this);
    }

    public void addConfig(ScreenCaptureConfig config) {
        this.config = config;
        screenCaptureFragment.addConfig(config);
        observer.addConfig(config);
    }

    public void startCapture() {
        try {
            screenCaptureFragment.startCapture();
        } catch (Exception e) {
            e.printStackTrace();
            observer.notAllowEnterNextStep();
        }
    }

    public void stopCapture() {
        cancelRecord();
        //notification system refresh video list
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).addCategory(Intent.CATEGORY_DEFAULT)//
                                                                         .setData(Uri.fromFile(config.getFile()));
        appContext.sendBroadcast(intent);
        observer.reportState(ScreenCaptureState.COMPLETED);
        observer.reset();
    }

    @Override
    public void startRecord(Activity activity, @NonNull MediaProjection mediaProjection) {
        if (observer.isAlive()) {
            observer.reportState(ScreenCaptureState.START);
            screenCaptureRecorder = new ScreenCaptureRecorder(mediaProjection, config);
            screenCaptureRecorder.addObserver(observer);
            screenCaptureRecorder.startCapture();
            if (activity != null && !activity.isFinishing() &&//
                    !activity.isDestroyed() && config.isAutoMoveTaskToBack())
                activity.moveTaskToBack(true);
        } else {
            if (config.allowLog())
                Log.e(TAG, "start recorder failed ,current activity is not alive state !!!");
            observer.notAllowEnterNextStep();
        }
    }

    @Override
    public void cancelRecord() {
        if (screenCaptureRecorder != null) {
            screenCaptureRecorder.stopCapture();
        }
    }

    @Override
    public void specialPeriodHandler() {
        if (config.allowLog() && screenCaptureRecorder != null)
            Log.d(TAG, "wait screenCaptureRecorder die:" + screenCaptureRecorder.isAlive());
        if (screenCaptureRecorder != null && screenCaptureRecorder.isAlive()) {
            try {
                cancelRecord();
                screenCaptureRecorder.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            screenCaptureRecorder = null;
        }
    }

}
