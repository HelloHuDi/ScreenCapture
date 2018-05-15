package com.hd.screen.capture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hd.screencapture.ScreenCapture;

public class MainActivity extends AppCompatActivity {

    private ScreenCapture screenCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenCapture = ScreenCapture.with(this);
    }

    public void startCapture(View view) {
        screenCapture.startCapture();
    }

    public void stopCapture(View view) {
        screenCapture.stopCapture();
    }
}
