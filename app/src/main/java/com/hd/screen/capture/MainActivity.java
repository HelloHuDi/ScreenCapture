package com.hd.screen.capture;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.config.VideoConfig;
import com.hd.screencapture.help.ScreenCaptureState;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends AppCompatActivity implements ScreenCaptureStreamCallback {

    private ScreenCapture screenCapture;

    private boolean isRunning;

    private TextView tvTime,tvVideoHeaderData,tvVideoData,tvAudioData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime=findViewById(R.id.tvTime);
        tvVideoHeaderData=findViewById(R.id.tvVideoHeaderData);
        tvVideoData=findViewById(R.id.tvVideoData);
        tvAudioData=findViewById(R.id.tvAudioData);
        init();
    }

    private void init() {
        ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()//
                                                  .setAllowLog(BuildConfig.DEBUG)
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

    @SuppressLint("SetTextI18n")
    @Override
    public void captureTime(long time) {
        runOnUiThread(() -> tvTime.setText("capture time ==>"+ DateUtils.formatElapsedTime(time)));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void videoHeaderByte(@NonNull byte[] sps, @NonNull byte[] pps) {
        runOnUiThread(() -> tvVideoHeaderData.setText("video header byte length ==> sps len: " + sps.length +",  pps len : "+ pps.length));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void videoContentByte(@NonNull byte[] content) {
        runOnUiThread(() -> tvVideoData.setText("video content byte len ==> " + content.length));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void audioContentByte(@NonNull byte[] content) {
        runOnUiThread(() -> tvAudioData.setText("audio content byte len ==> " + content.length));
    }
}
