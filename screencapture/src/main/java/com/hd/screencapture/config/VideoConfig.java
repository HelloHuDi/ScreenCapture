package com.hd.screencapture.config;

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
     * video bitrate {800,1200,1600,2000,2500,5000,10000,12000,16000,20000,25000}
     */
    private int bitrate = 10000;

    /**
     * video frame rate {15,25,30,60,90,120}
     */
    private int frameRate = 60;

    /**
     * time between I-frames {1,5,10,20,30}
     */
    private int iFrameInterval = 10;

    public static VideoConfig initDefaultConfig() {
        return new VideoConfig();
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
}
