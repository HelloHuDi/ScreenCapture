package com.hd.screen.capture;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.config.VideoConfig;

/**
 * Created by hd on 2018/5/23 .
 */
public class ScreenCapturePresenter {

    private ScreenCapture screenCapture;

    private Context context;

    ScreenCapturePresenter(@NonNull AppCompatActivity activity) {
        this.context=activity.getApplicationContext();
        initCapture(activity);
    }

    private void initCapture(AppCompatActivity activity) {
        ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()//
                                                                   .setAllowLog(BuildConfig.DEBUG)//
                                                                   .setVideoConfig(VideoConfig.initDefaultConfig(activity))//
                                                                   //not completed
                                                                   //.setAudioConfig(AudioConfig.initDefaultConfig())//
                                                                   .setCaptureCallback((ScreenCaptureStreamCallback) activity)//
                                                                   .setAutoMoveTaskToBack(true)//
                                                                   .create();//
        screenCapture = ScreenCapture.with(activity).setConfig(captureConfig);
    }

    public void startCapture() {
        if (!screenCapture.isRunning()) {
            screenCapture.startCapture();
        } else {
            Toast.makeText(context, "current is capturing state", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopCapture() {
        if (screenCapture.isRunning()) {
            screenCapture.stopCapture();
        } else {
            Toast.makeText(context, "current is stopped state", Toast.LENGTH_SHORT).show();
        }
    }

}
