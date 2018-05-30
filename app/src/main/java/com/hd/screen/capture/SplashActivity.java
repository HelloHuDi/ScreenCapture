package com.hd.screen.capture;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hd.splashscreen.text.SimpleConfig;
import com.hd.splashscreen.text.SimpleSplashFinishCallback;
import com.hd.splashscreen.text.SimpleSplashScreen;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by hd on 2018/5/30 .
 * request permission
 */
public class SplashActivity extends AppCompatActivity implements SimpleSplashFinishCallback,//
        EasyPermissions.PermissionCallbacks {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        SimpleSplashScreen screen = findViewById(R.id.screen);
        SimpleConfig simpleConfig = new SimpleConfig(this);
        simpleConfig.setCallback(this);
        screen.addConfig(simpleConfig);
        screen.start();
    }

    @Override
    public void loadFinish() {
        if (!isDestroyed()) {
            requestPermission();
        }
    }

    private final int RESULT_CODE = 100;

    private final String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String recordAudio = Manifest.permission.RECORD_AUDIO;

    private void requestPermission() {
        if (EasyPermissions.hasPermissions(this, writeExternalStorage, recordAudio)) {
            goMain();
        } else {
            EasyPermissions.requestPermissions(this, "You need to save the video to the local", RESULT_CODE, writeExternalStorage, recordAudio);
        }
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RESULT_CODE) {
            requestPermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (EasyPermissions.hasPermissions(this, writeExternalStorage, recordAudio)) {
            goMain();
        }
    }
}
