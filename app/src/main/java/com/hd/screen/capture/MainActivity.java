package com.hd.screen.capture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureCallback;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.config.VideoConfig;
import com.hd.screencapture.help.ScreenCaptureState;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends AppCompatActivity implements ScreenCaptureCallback {

    private ScreenCapture screenCapture;

    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()//
                                                  .setVideoConfig(VideoConfig.initDefaultConfig(this))//
                                                  .setAudioConfig(AudioConfig.initDefaultConfig())//
                                                  .setCaptureCallback(this)//
                                                  .setAutoMoveTaskToBack(true)//
                                                  .create();//
        screenCapture = ScreenCapture.with(this).setConfig(captureConfig);
    }

    public void startCapture(View view) {
        if (!isRunning) {
            screenCapture.startCapture();
        } else {
            Toast.makeText(MainActivity.this, "current is capturing state", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopCapture(View view) {
        if (isRunning) {
            screenCapture.stopCapture();
        } else {
            Toast.makeText(MainActivity.this, "current is stopped state", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void captureState(ScreenCaptureState state) {
        isRunning = !(ScreenCaptureState.FAILED == state || ScreenCaptureState.COMPLETED == state);
        Log.d("tag", "capture state ==>" + state + "==" + isRunning);
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "capture state ==>" + state, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void captureTime(long time) {
        Log.d("tag", "capture time ==>" + time + "===" + DateUtils.formatElapsedTime(time));
    }
}
