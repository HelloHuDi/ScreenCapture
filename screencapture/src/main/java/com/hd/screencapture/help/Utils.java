package com.hd.screencapture.help;

import android.os.Environment;

/**
 * Created by hd on 2018/5/14 .
 */
public class Utils {

    public static boolean isExternalStorageReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}
