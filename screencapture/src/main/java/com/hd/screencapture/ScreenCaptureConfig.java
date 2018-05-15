package com.hd.screencapture;

import android.os.Environment;

import java.io.File;

/**
 * Created by hd on 2018/5/14 .
 */
public class ScreenCaptureConfig {

    /**
     * whether contain audio
     */
    private boolean audio;

    /**
     * video file
     */
    private File file;

    /**
     * video width and height
     */
    private int width = 1280, height = 1920;

    /**
     * device dpi
     */
    private int dpi=480;

    /**
     * video bitrate
     */
    private int bitrate = 60000;

    /**
     * video frame rate
     */
    private int frameRate = 60;

    /**
     * time between I-frames
     */
    private int iFrameInterval = 10;


    public static ScreenCaptureConfig initDefaultConfig() {
        return new ScreenCaptureConfig(true);
    }

    public ScreenCaptureConfig() {
        this(false);
    }

    private ScreenCaptureConfig(boolean dfConfig) {
        if (dfConfig) {
            setFile(new File(Environment.getExternalStorageDirectory(), "screen_capture_" + System.currentTimeMillis() + ".mp4"));
        }
    }

    public boolean hasAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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

    public int getiFrameInterval() {
        return iFrameInterval;
    }

    public void setiFrameInterval(int iFrameInterval) {
        this.iFrameInterval = iFrameInterval;
    }
}
