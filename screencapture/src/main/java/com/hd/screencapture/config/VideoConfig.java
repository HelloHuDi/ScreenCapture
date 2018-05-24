package com.hd.screencapture.config;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.hd.screencapture.help.Utils;

/**
 * Created by hd on 2018/5/18 .
 */
public class VideoConfig extends CaptureConfig {

    /**
     * video width and height
     */
    private int width = 1080, height = 1920;

    /**
     * device dpi
     */
    private int dpi = 440;

    /**
     * video bitrate
     */
    private int bitrate = 12000000;

    /**
     * video frame rate
     */
    private int frameRate = 60;

    /**
     * time between I-frames {1,5,10,20,30}
     */
    private int iFrameInterval = 10;

    /**
     * {@link Utils#findAllVideoCodecName()}
     * {@link MediaCodec#createByCodecName(String)}
     */
    private String codecName;

    /**
     * {@link Utils#findVideoProfileLevel}
     * {@link MediaFormat#KEY_PROFILE}
     * {@link MediaFormat#KEY_LEVEL}
     */
    private MediaCodecInfo.CodecProfileLevel level;

    @Override
    public String toString() {
        return "VideoConfig{" + "width=" + width + ", height=" + height + ", dpi=" + dpi +//
                ", bitrate=" + bitrate + ", frameRate=" + frameRate + ", iFrameInterval=" + iFrameInterval +//
                ", codecName='" + codecName + '\'' + ", level=" + level + '}';
    }

    public static VideoConfig initDefaultConfig() {
        return new VideoConfig();
    }

    public static VideoConfig initDefaultConfig(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.setDpi(metrics.densityDpi);
        videoConfig.setWidth(metrics.widthPixels);
        videoConfig.setHeight(metrics.heightPixels);
        return videoConfig;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getIFrameInterval() {
        return iFrameInterval;
    }

    public void setIFrameInterval(int iFrameInterval) {
        this.iFrameInterval = iFrameInterval;
    }

    public String getCodecName() {
        return codecName;
    }

    public void setCodecName(String codecName) {
        this.codecName = codecName;
    }

    public MediaCodecInfo.CodecProfileLevel getLevel() {
        return level;
    }

    public void setLevel(MediaCodecInfo.CodecProfileLevel level) {
        this.level = level;
    }
}
