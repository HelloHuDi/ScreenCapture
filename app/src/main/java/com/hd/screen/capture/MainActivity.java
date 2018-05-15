package com.hd.screen.capture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.ScreenCaptureConfig;

public class MainActivity extends AppCompatActivity {

    private ScreenCapture screenCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int dpi = metrics.densityDpi;
        final int width = metrics.widthPixels;
        final int height = metrics.heightPixels;
        final int bitrate = 6000000;
        Log.d("tag", "current device +" + dpi + "==" + width + "==" + height);
        ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()//
                                                                             .setAudio(false)//
                                                                             .setDpi(dpi)//
                                                                             .setWidth(width)//
                                                                             .setHeight(height)//
                                                                             .setBitrate(bitrate)//
                                                                             .setFrameRate(60)//
                                                                             .setIFrameInterval(10)//
                                                                             .create();//
        screenCapture = ScreenCapture.with(this).setConfig(captureConfig);
    }

    public void startCapture(View view) {
        screenCapture.startCapture();
    }

    public void stopCapture(View view) {
        screenCapture.stopCapture();
    }
}
