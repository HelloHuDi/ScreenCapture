package com.hd.screencapture;

import android.os.Environment;

import java.io.File;

/**
 * Created by hd on 2018/5/14 .
 */
public class ScreenCaptureConfig {

    /**
     * report capture state
     */
    private ScreenCaptureCallback captureCallback;

    /**
     * whether contain audio
     */
    private boolean audio;

    /**
     * whether auto move task to back
     */
    private boolean autoMoveTaskToBack = true;

    /**
     * video file
     */
    private File file = new File(
            Environment.getExternalStorageDirectory(), //
            "screen_capture_" + System.currentTimeMillis() + ".mp4");

    /**
     * video width and height
     */
    private int width = 1080, height = 1920;

    /**
     * device dpi
     */
    private int dpi = 480;

    /**
     * video bitrate
     */
    private int bitrate = 25000;

    /**
     * video frame rate
     */
    private int frameRate = 60;

    /**
     * time between I-frames
     */
    private int iFrameInterval = 10;

    public static ScreenCaptureConfig initDefaultConfig() {
        return new ScreenCaptureConfig();
    }

    public ScreenCaptureCallback getCaptureCallback() {
        return captureCallback;
    }

    public void setCaptureCallback(ScreenCaptureCallback captureCallback) {
        this.captureCallback = captureCallback;
    }

    public boolean hasAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isAutoMoveTaskToBack() {
        return autoMoveTaskToBack;
    }

    public void setAutoMoveTaskToBack(boolean autoMoveTaskToBack) {
        this.autoMoveTaskToBack = autoMoveTaskToBack;
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

    public int getIFrameInterval() {
        return iFrameInterval;
    }

    public void setIFrameInterval(int iFrameInterval) {
        this.iFrameInterval = iFrameInterval;
    }

    public static class Builder {

        private ScreenCaptureConfig captureConfig;

        public Builder() {
            this.captureConfig = new ScreenCaptureConfig();
        }

        public Builder setCaptureCallback(ScreenCaptureCallback captureCallback) {
            captureConfig.setCaptureCallback(captureCallback);
            return this;
        }

        public Builder setAudio(boolean audio) {
            captureConfig.setAudio(audio);
            return this;
        }

        public Builder setAutoMoveTaskToBack(boolean autoMoveTaskToBack) {
            captureConfig.setAutoMoveTaskToBack(autoMoveTaskToBack);
            return this;
        }

        public Builder setFile(File file) {
            captureConfig.setFile(file);
            return this;
        }

        public Builder setWidth(int width) {
            captureConfig.setWidth(width);
            return this;
        }

        public Builder setHeight(int height) {
            captureConfig.setHeight(height);
            return this;
        }

        public Builder setDpi(int dpi) {
            captureConfig.setDpi(dpi);
            return this;
        }

        public Builder setBitrate(int bitrate) {
            captureConfig.setBitrate(bitrate);
            return this;
        }

        public Builder setFrameRate(int frameRate) {
            captureConfig.setFrameRate(frameRate);
            return this;
        }

        public Builder setIFrameInterval(int iFrameInterval) {
            captureConfig.setIFrameInterval(iFrameInterval);
            return this;
        }

        public ScreenCaptureConfig create() {
            return captureConfig;
        }
    }
}
