package com.hd.screencapture.help;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.capture.ScreenCaptureRecorder;
import com.hd.screencapture.observer.CaptureObserver;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * Created by hd on 2018/5/14 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureFragment extends Fragment {

    private final String TAG = ScreenCaptureFragment.class.getSimpleName();

    private final int REQUEST_MEDIA_PROJECTION = 441;

    private final int PERMISSIONS_REQUEST_CODE = 442;

    private static Context appContext;

    private CaptureObserver observer;

    private String[] permissions1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private String[] permissions2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    private MediaProjectionManager mMediaProjectionManager;

    private MediaProjection mediaProjection;

    private ScreenCaptureRecorder screenCaptureRecorder;

    private ScreenCaptureConfig config;

    public void addObserver(CaptureObserver observer) {
        this.observer = observer;
    }

    public void setConfig(@NonNull ScreenCaptureConfig config) {
        this.config = config;
    }

    public void startCapture() {
        if (observer.isAlive()) {
            if (hasPermissions()) {
                observer.reportState(ScreenCaptureState.PREPARE);
                Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                } else {
                    Log.e(TAG, "fuck,how can this happened !");
                }
            }
        } else {
            Log.e(TAG, "current activity is not alive state !!!");
            stopCapture();
        }
    }

    public void stopCapture() {
        cancelRecorder();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)//
                        .addCategory(Intent.CATEGORY_DEFAULT).setData(Uri.fromFile(config.getFile()));
        appContext.sendBroadcast(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        appContext = getActivity().getApplicationContext();
        mMediaProjectionManager = (MediaProjectionManager) appContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            int granted = PackageManager.PERMISSION_GRANTED;
            for (int r : grantResults) {
                granted |= r;
            }
            if (granted == PackageManager.PERMISSION_GRANTED) {
                startCapture();
            } else {
                Log.e(TAG, "No Permission!");
                observer.reportState(ScreenCaptureState.FAILED);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection != null) {
                if (hasPermissions()) {
                    startRecorder();
                } else {
                    cancelRecorder();
                }
            } else {
                Log.e(TAG, "media projection is null");
                observer.reportState(ScreenCaptureState.FAILED);
            }
        }
    }

    private void startRecorder() {
        if (observer.isAlive()) {
            observer.reportState(ScreenCaptureState.START);
            screenCaptureRecorder = new ScreenCaptureRecorder(mediaProjection, config);
            screenCaptureRecorder.addObserver(observer);
            screenCaptureRecorder.startCapture();
            if (config.isAutoMoveTaskToBack())
                getActivity().moveTaskToBack(true);
        } else {
            Log.e(TAG, "start recorder failed ,current activity is not alive state !!!");
            stopCapture();
        }
    }

    private void cancelRecorder() {
        if (screenCaptureRecorder != null)
            screenCaptureRecorder.stopCapture();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        String[] permissions = config.hasAudio() ? permissions2 : permissions1;
        boolean showRationale = false;
        for (String perm : permissions) {
            showRationale |= shouldShowRequestPermissionRationale(perm);
        }
        if (!showRationale) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
            return;
        }
        new AlertDialog.Builder(getActivity())//
                                              .setMessage("Using your mic to record audio and your sd card to save video file")//
                                              .setCancelable(false)//
                                              .setPositiveButton(android.R.string.ok, (dialog, which) -> requestPermissions(permissions, PERMISSIONS_REQUEST_CODE))//
                                              .setNegativeButton(android.R.string.cancel, null)//
                                              .create()//
                                              .show();
    }

    private boolean hasPermissions() {
        PackageManager pm = appContext.getPackageManager();
        String packageName = appContext.getPackageName();
        int granted = (config.hasAudio() ? pm.checkPermission(RECORD_AUDIO, packageName) : //
                PackageManager.PERMISSION_GRANTED) | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

}
