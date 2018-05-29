package com.hd.screencapture.help;

import android.Manifest;
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

import com.hd.screencapture.capture.ScreenCaptureRecorder;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;


/**
 * Created by hd on 2018/5/14 .
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public final class ScreenCaptureFragment extends Fragment {

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

    public void addConfig(@NonNull ScreenCaptureConfig config) {
        this.config = config;
    }

    public void startCapture() {
        if (observer.isAlive()) {
            if (hasPermissions()) {
                if (Utils.checkFile(config.getFile())) {
                    observer.reportState(ScreenCaptureState.PREPARE);
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
                } else {
                    if (config.allowLog())
                        Log.e(TAG, "current video file parent folder not exists !");
                    observer.notAllowEnterNextStep();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                } else {
                    if (config.allowLog())
                        Log.e(TAG, "fuck,how can this happened !");
                    observer.notAllowEnterNextStep();
                }
            }
        } else {
            if (config.allowLog())
                Log.e(TAG, "current activity is not alive state !!!");
            observer.notAllowEnterNextStep();
        }
    }

    public void stopCapture() {
        cancelRecorder();
        //notification system refresh video list
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)//
                                         .addCategory(Intent.CATEGORY_DEFAULT)//
                                         .setData(Uri.fromFile(config.getFile()));
        appContext.sendBroadcast(intent);
        observer.reportState(ScreenCaptureState.COMPLETED);
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
                if (config.allowLog())
                    Log.e(TAG, "no Permission!");
                observer.notAllowEnterNextStep();
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
                    if (config.allowLog())
                        Log.e(TAG, "No Permission!");
                    observer.notAllowEnterNextStep();
                }
            } else {
                if (config.allowLog())
                    Log.e(TAG, "media projection is null,maybe you cancel recorder");
                observer.reportState(ScreenCaptureState.CANCEL);
                observer.notAllowEnterNextStep();
            }
        }
    }

    @Override
    public void onDestroyView() {
        specialPeriodHandler();
        super.onDestroyView();
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
            if (config.allowLog())
                Log.e(TAG, "start recorder failed ,current activity is not alive state !!!");
            observer.notAllowEnterNextStep();
        }
    }

    private void cancelRecorder() {
        if(screenCaptureRecorder!=null) {
            screenCaptureRecorder.stopCapture();
        }
    }

    private void specialPeriodHandler() {
        if (config.allowLog() && screenCaptureRecorder!=null)
            Log.d(TAG, "wait screenCaptureRecorder die:"+screenCaptureRecorder.isAlive());
        if(screenCaptureRecorder!=null && screenCaptureRecorder.isAlive()) {
            try {
                cancelRecorder();
                screenCaptureRecorder.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            screenCaptureRecorder = null;
        }
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
                       .setMessage("using your mic to record audio and your sd card to save video file")//
                       .setCancelable(false)//
                       .setPositiveButton(android.R.string.ok, (dialog, which) -> //
                               requestPermissions(permissions, PERMISSIONS_REQUEST_CODE))//
                       .setNegativeButton(android.R.string.cancel, (dialog, which) -> //
                               observer.notAllowEnterNextStep())//
                       .create()//
                       .show();
    }

    private boolean hasPermissions() {
        return Utils.isPermissionGranted(appContext, config.hasAudio());
    }
}
