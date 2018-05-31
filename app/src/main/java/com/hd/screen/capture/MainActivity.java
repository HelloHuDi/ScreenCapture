package com.hd.screen.capture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.help.ScreenCaptureState;

import java.util.Objects;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends BaseActivity implements //
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
        addSlide();
    }

    private void resetView() {
        setTime("00:00");
        setVideoHeaderSize("0", "0");
        setVideoDataSize("0");
        setAudioDataSize("0");
    }

    public void captureConfig(View view) {
        Intent intent = new Intent(this, CaptureConfigActivity.class);
        startActivity(intent);
    }

    public void startCapture(View view) {
        resetView();
        screenCapturePresenter.startCapture();
    }

    public void stopCapture(View view) {
        screenCapturePresenter.stopCapture();
    }

    public void playVideo(View view) {
        if (!screenCapturePresenter.isCapturing()) {
            if (screenCapturePresenter.getFile() != null) {
                Intent intent = new Intent(this, VideoPlayActivity.class);
                intent.putExtra("video_path", screenCapturePresenter.getFile().getAbsolutePath());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "do not play ,the video file is null !!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "capturing", Toast.LENGTH_SHORT).show();
        }
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


    //==============extra==============

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void addSlide() {
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final LinearLayout linContent = findViewById(R.id.linContent);
        final NavigationView mNavigationView = findViewById(R.id.navigation_view);
        final DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.draw_open, R.string.draw_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display;
                if (windowManager != null) {
                    display = windowManager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    linContent.layout(mNavigationView.getRight(), 0, point.x + mNavigationView.getRight(), point.y);
                    super.onDrawerSlide(drawerView, slideOffset);
                }
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            captureConfig(null);
            mDrawerLayout.closeDrawers();
            return true;
        });
    }

}
