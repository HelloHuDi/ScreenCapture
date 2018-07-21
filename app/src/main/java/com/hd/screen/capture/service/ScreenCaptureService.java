package com.hd.screen.capture.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by hd on 2018/7/21 .
 * 演示通过service 来实现调用录制
 */
public class ScreenCaptureService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        new Handler().postDelayed(() -> {
            Intent i = new Intent(this,ServiceCaptureActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }, 3000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
