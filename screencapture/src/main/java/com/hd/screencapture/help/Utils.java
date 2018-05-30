package com.hd.screencapture.help;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by hd on 2018/5/14 .
 */
public final class Utils {

    public static boolean isExternalStorageReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isPermissionGranted(Context context, boolean checkAudio) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int granted = (checkAudio ? pm.checkPermission(RECORD_AUDIO, packageName) : //
                PackageManager.PERMISSION_GRANTED) | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkFile(File file) {
        boolean su = true;
        if (!file.getParentFile().exists()) {
            su = file.getParentFile().mkdirs();
        }
       return su;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo.CodecProfileLevel[] findVideoProfileLevel(String codecName) {
        return findVideoCodecCapabilities(codecName).profileLevels;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo.CodecProfileLevel[] findAudioProfileLevel(String codecName) {
        return findAudioCodecCapabilities(codecName).profileLevels;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo.CodecCapabilities findVideoCodecCapabilities(String codecName) {
        return findProfileLevel(true, codecName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo.CodecCapabilities findAudioCodecCapabilities(String codecName) {
        return findProfileLevel(false, codecName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static MediaCodecInfo.CodecCapabilities findProfileLevel(boolean isVideo, String codecName) {
        MediaCodecInfo codec = isVideo ? matchVideoCodecInfo(codecName) : matchAudioCodecInfo(codecName);
        return codec.getCapabilitiesForType(isVideo ? MediaFormat.MIMETYPE_VIDEO_AVC : MediaFormat.MIMETYPE_AUDIO_AAC);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo matchAudioCodecInfo(String codecName) {
        return matchCodecInfo(false, codecName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo matchVideoCodecInfo(String codecName) {
        return matchCodecInfo(true, codecName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static MediaCodecInfo matchCodecInfo(boolean isVideo, String codecName) {
        MediaCodecInfo[] infos = isVideo ? findAllVideoEncoder() : findAllAudioEncoder();
        MediaCodecInfo codec = null;
        for (MediaCodecInfo info : infos) {
            if (info.getName().equals(codecName)) {
                codec = info;
                break;
            }
        }
        return codec;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String[] findAllVideoCodecName() {
        return findAllCodecName(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String[] findAllAudioCodecName() {
        return findAllCodecName(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String[] findAllCodecName(boolean isVideo) {
        MediaCodecInfo[] infos = isVideo ? findAllVideoEncoder() : findAllAudioEncoder();
        String[] names = new String[infos.length];
        for (int i = 0; i < infos.length; i++) {
            names[i] = infos[i].getName();
        }
        return names;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo[] findAllVideoEncoder() {
        return findEncodersByType(MediaFormat.MIMETYPE_VIDEO_AVC);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo[] findAllAudioEncoder() {
        return findEncodersByType(MediaFormat.MIMETYPE_AUDIO_AAC);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static MediaCodecInfo[] findEncodersByType(String mimeType) {
        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        List<MediaCodecInfo> infos = new ArrayList<>();
        for (MediaCodecInfo info : codecList.getCodecInfos()) {
            if (!info.isEncoder()) {
                continue;
            }
            try {
                MediaCodecInfo.CodecCapabilities cap = info.getCapabilitiesForType(mimeType);
                if (cap == null)
                    continue;
            } catch (IllegalArgumentException e) {
                // unsupported
                continue;
            }
            infos.add(info);
        }
        return infos.toArray(new MediaCodecInfo[infos.size()]);
    }


    //=====level====

    private static SparseArray<String> sAACProfiles = new SparseArray<>();
    private static SparseArray<String> sAVCProfiles = new SparseArray<>();
    private static SparseArray<String> sAVCLevels = new SparseArray<>();


    /**
     * @param avcProfileLevel AVC CodecProfileLevel
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static String avcProfileLevelToString(MediaCodecInfo.CodecProfileLevel avcProfileLevel) {
        if (sAVCProfiles.size() == 0 || sAVCLevels.size() == 0) {
            initProfileLevels();
        }
        String profile = null, level = null;
        int i = sAVCProfiles.indexOfKey(avcProfileLevel.profile);
        if (i >= 0) {
            profile = sAVCProfiles.valueAt(i);
        }
        i = sAVCLevels.indexOfKey(avcProfileLevel.level);
        if (i >= 0) {
            level = sAVCLevels.valueAt(i);
        }
        if (profile == null) {
            profile = String.valueOf(avcProfileLevel.profile);
        }
        if (level == null) {
            level = String.valueOf(avcProfileLevel.level);
        }
        return profile + '-' + level;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static String[] aacProfiles() {
        if (sAACProfiles.size() == 0) {
            initProfileLevels();
        }
        String[] profiles = new String[sAACProfiles.size()];
        for (int i = 0; i < sAACProfiles.size(); i++) {
            profiles[i] = sAACProfiles.valueAt(i);
        }
        return profiles;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static MediaCodecInfo.CodecProfileLevel toProfileLevel(String str) {
        if (sAVCProfiles.size() == 0 || sAVCLevels.size() == 0 || sAACProfiles.size() == 0) {
            initProfileLevels();
        }
        String profile = str;
        String level = null;
        int i = str.indexOf('-');
        if (i > 0) { // AVC profile has level
            profile = str.substring(0, i);
            level = str.substring(i + 1);
        }
        MediaCodecInfo.CodecProfileLevel res = new MediaCodecInfo.CodecProfileLevel();
        if (profile.startsWith("AVC")) {
            res.profile = keyOfValue(sAVCProfiles, profile);
        } else if (profile.startsWith("AAC")) {
            res.profile = keyOfValue(sAACProfiles, profile);
        } else {
            try {
                res.profile = Integer.parseInt(profile);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (level != null) {
            if (level.startsWith("AVC")) {
                res.level = keyOfValue(sAVCLevels, level);
            } else {
                try {
                    res.level = Integer.parseInt(level);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        return res.profile > 0 && res.level >= 0 ? res : null;
    }

    private static <T> int keyOfValue(SparseArray<T> array, T value) {
        int size = array.size();
        for (int i = 0; i < size; i++) {
            T t = array.valueAt(i);
            if (t == value || t.equals(value)) {
                return array.keyAt(i);
            }
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void initProfileLevels() {
        Field[] fields = MediaCodecInfo.CodecProfileLevel.class.getFields();
        for (Field f : fields) {
            if ((f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                continue;
            }
            String name = f.getName();
            SparseArray<String> target;
            if (name.startsWith("AVCProfile")) {
                target = sAVCProfiles;
            } else if (name.startsWith("AVCLevel")) {
                target = sAVCLevels;
            } else if (name.startsWith("AACObject")) {
                target = sAACProfiles;
            } else {
                continue;
            }
            try {
                target.put(f.getInt(null), name);
            } catch (IllegalAccessException e) {
                //ignored
            }
        }
    }

    //======color format======

    private static SparseArray<String> sColorFormats = new SparseArray<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static String toHumanReadable(int colorFormat) {
        if (sColorFormats.size() == 0) {
            initColorFormatFields();
        }
        int i = sColorFormats.indexOfKey(colorFormat);
        if (i >= 0)
            return sColorFormats.valueAt(i);
        return "0x" + Integer.toHexString(colorFormat);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static int toColorFormat(String str) {
        if (sColorFormats.size() == 0) {
            initColorFormatFields();
        }
        int color = keyOfValue(sColorFormats, str);
        if (color > 0)
            return color;
        if (str.startsWith("0x")) {
            return Integer.parseInt(str.substring(2), 16);
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void initColorFormatFields() {
        // COLOR_
        Field[] fields = MediaCodecInfo.CodecCapabilities.class.getFields();
        for (Field f : fields) {
            if ((f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                continue;
            }
            String name = f.getName();
            if (name.startsWith("COLOR_")) {
                try {
                    int value = f.getInt(null);
                    sColorFormats.put(value, name);
                } catch (IllegalAccessException e) {
                    // ignored
                }
            }
        }
    }
}
