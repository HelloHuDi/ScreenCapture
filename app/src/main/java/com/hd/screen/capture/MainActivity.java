package com.hd.screen.capture;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.help.ScreenCaptureState;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends AppCompatActivity implements //
        ScreenCaptureStreamCallback/*,ScreenCaptureCallback*/ {

    private TextView tvState, tvTime, tvVideoHeaderData, tvVideoData, tvAudioData;

    private ScreenCapturePresenter screenCapturePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //log default config of the current device
        //LogConfig.log();
        init();
    }

    @Override
    public void onBackPressed() {
        if (screenCapturePresenter.isCapturing()) {
            new AlertDialog.Builder(this)//
                                         .setMessage("Screen currently is recording! Confirm the stop?")//
                                         .setCancelable(false)//
                                         .setPositiveButton(android.R.string.ok, (dialog, which) -> //
                                                 super.onBackPressed())//
                                         .setNegativeButton(android.R.string.cancel, null)//
                                         .create()//
                                         .show();
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        tvState = findViewById(R.id.tvState);
        tvTime = findViewById(R.id.tvTime);
        tvVideoHeaderData = findViewById(R.id.tvVideoHeaderData);
        tvVideoData = findViewById(R.id.tvVideoData);
        tvAudioData = findViewById(R.id.tvAudioData);
        screenCapturePresenter = new ScreenCapturePresenter(this);
        resetView();
    }

    private void resetView() {
        setTime("00:00");
        setVideoHeaderSize("0", "0");
        setVideoDataSize("0");
        setAudioDataSize("0");
    }

    public void startCapture(View view) {
        resetView();
        screenCapturePresenter.startCapture();
    }

    public void stopCapture(View view) {
        screenCapturePresenter.stopCapture();
    }

    @Override
    public void captureState(ScreenCaptureState state) {
        runOnUiThread(() -> {
            setState(state.toString());
            Toast.makeText(MainActivity.this, "capture state ==>" + state, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void captureTime(long time) {
        runOnUiThread(() -> setTime(DateUtils.formatElapsedTime(time)));
    }

    @Override
    public void videoHeaderByte(@NonNull byte[] sps, @NonNull byte[] pps) {
        runOnUiThread(() -> setVideoHeaderSize(String.valueOf(sps.length), String.valueOf(pps.length)));
    }

    @Override
    public void videoContentByte(@NonNull byte[] content) {
        runOnUiThread(() -> setVideoDataSize(String.valueOf(content.length)));
    }

    @Override
    public void audioContentByte(@NonNull byte[] content) {
        runOnUiThread(() -> setAudioDataSize(String.valueOf(content.length)));
    }

    private void setState(String text) {
        setText(tvState, String.format("capture state ==> %s", text));
    }

    private void setTime(String text) {
        setText(tvTime, String.format("capture time ==> %s", text));
    }

    private void setVideoHeaderSize(String text1, String text2) {
        setText(tvVideoHeaderData, String.format("video header byte length ==> sps len:  %s ,  pps len :  %s", text1, text2));
    }

    private void setVideoDataSize(String text) {
        setText(tvVideoData, String.format("video content byte len ==> %s", text));
    }

    private void setAudioDataSize(String text) {
        setText(tvAudioData, String.format("audio content byte len ==> %s", text));
    }

    private void setText(TextView textView, String text) {
        textView.setText(text);
    }

}
