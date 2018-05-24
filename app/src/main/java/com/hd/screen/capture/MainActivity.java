package com.hd.screen.capture;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.help.ExecutorUtil;
import com.hd.screencapture.help.ScreenCaptureState;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends AppCompatActivity implements ScreenCaptureStreamCallback {

    private TextView tvState, tvTime, tvVideoHeaderData, tvVideoData, tvAudioData;

    private ExecutorUtil executorUtil;

    private ScreenCapturePresenter screenCapturePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        LogConfig.log();
        init();
    }

    private void init() {
        tvState = findViewById(R.id.tvState);
        tvTime = findViewById(R.id.tvTime);
        tvVideoHeaderData = findViewById(R.id.tvVideoHeaderData);
        tvVideoData = findViewById(R.id.tvVideoData);
        tvAudioData = findViewById(R.id.tvAudioData);
        executorUtil = new ExecutorUtil();
        screenCapturePresenter=new ScreenCapturePresenter(this);
    }

    public void startCapture(View view) {
        screenCapturePresenter.startCapture();
    }

    public void stopCapture(View view) {
       screenCapturePresenter.stopCapture();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void captureState(ScreenCaptureState state) {
        executorUtil.mainThread().execute(() ->{
            tvState.setText("capture state ==>" + state);
            Toast.makeText(MainActivity.this, "capture state ==>" + state, Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void captureTime(long time) {
        executorUtil.mainThread().execute(() -> tvTime.setText("capture time ==>" + DateUtils.formatElapsedTime(time)));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void videoHeaderByte(@NonNull byte[] sps, @NonNull byte[] pps) {
        executorUtil.mainThread().execute(() -> tvVideoHeaderData.setText("video header byte length ==> sps len: " + sps.length + ",  pps len : " + pps.length));
        //executorUtil.networkIO().execute();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void videoContentByte(@NonNull byte[] content) {
        executorUtil.mainThread().execute(() -> tvVideoData.setText("video content byte len ==> " + content.length));
        //executorUtil.networkIO().execute();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void audioContentByte(@NonNull byte[] content) {
        executorUtil.mainThread().execute(() -> tvAudioData.setText("audio content byte len ==> " + content.length));
        //executorUtil.networkIO().execute();
    }

}
