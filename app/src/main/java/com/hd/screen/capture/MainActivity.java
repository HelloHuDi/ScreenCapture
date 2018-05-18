package com.hd.screen.capture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.ScreenCaptureCallback;
import com.hd.screencapture.ScreenCaptureState;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends AppCompatActivity implements ScreenCaptureCallback {

    private ScreenCapture screenCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()//
                                                                    .setAudioConfig(AudioConfig.initDefaultConfig())//
                                                                    .setCaptureCallback(this)//
                                                                    .setAutoMoveTaskToBack(true)//
                                                                    .create();//
        screenCapture = ScreenCapture.with(this).setConfig(captureConfig);
    }

    public void startCapture(View view) {
        screenCapture.startCapture();
    }

    public void stopCapture(View view) {
        screenCapture.stopCapture();
    }

    @Override
    public void captureState(ScreenCaptureState state) {
        Log.d("tag", "capture state ==>" + state);
        switch (state) {
            case PREPARE:
                break;
            case START:
                break;
            case CAPTURING:
                break;
            case FAILED:
                break;
            case COMPLETED:
                break;
        }
    }
}
