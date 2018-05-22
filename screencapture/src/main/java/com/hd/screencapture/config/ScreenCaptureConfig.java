package com.hd.screencapture.config;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.hd.screencapture.callback.ScreenCaptureCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hd on 2018/5/14 .
 */
public class ScreenCaptureConfig extends CaptureConfig {

    /**
     * about logcat
     */
    private boolean allowLog;

    /**
     * about video config
     */
    private VideoConfig videoConfig;

    /**
     * about audio config
     */
    private AudioConfig audioConfig;

    /**
     * report capture state
     */
    private ScreenCaptureCallback captureCallback;

    /**
     * whether auto move task to back
     */
    private boolean autoMoveTaskToBack = true;

    /**
     * screen capture file
     */
    private File file = new File(
            Environment.getExternalStorageDirectory(), "screen_capture_" //
            + new SimpleDateFormat("yyyyMMdd-HH-mm-ss", Locale.US).format(new Date()) + ".mp4");

    /**
     *  not provided voice recording by default
     */
    private static ScreenCaptureConfig initDefaultConfig(@NonNull VideoConfig videoConfig) {
        ScreenCaptureConfig config= new ScreenCaptureConfig();
        config.setVideoConfig(videoConfig);
        return config;
    }

    public static ScreenCaptureConfig initDefaultConfig() {
        return initDefaultConfig(VideoConfig.initDefaultConfig());
    }

    public static ScreenCaptureConfig initDefaultConfig(@NonNull Activity activity) {
        return initDefaultConfig(VideoConfig.initDefaultConfig(activity));
    }

    public boolean allowLog() {
        return allowLog;
    }

    public void setAllowLog(boolean allowLog) {
        this.allowLog = allowLog;
    }

    public VideoConfig getVideoConfig() {
        return videoConfig;
    }

    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }

    public AudioConfig getAudioConfig() {
        return audioConfig;
    }

    public void setAudioConfig(AudioConfig audioConfig) {
        this.audioConfig = audioConfig;
    }

    public ScreenCaptureCallback getCaptureCallback() {
        return captureCallback;
    }

    public void setCaptureCallback(ScreenCaptureCallback captureCallback) {
        this.captureCallback = captureCallback;
    }

    public boolean hasAudio() {
        return getAudioConfig() == null;
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

    public static class Builder {

        private ScreenCaptureConfig captureConfig;

        public Builder() {
            this.captureConfig = new ScreenCaptureConfig();
        }

        public Builder setAllowLog(boolean allowLog) {
            captureConfig.setAllowLog(allowLog);
            return this;
        }

        public Builder setVideoConfig(VideoConfig videoConfig) {
            captureConfig.setVideoConfig(videoConfig);
            return this;
        }

        public Builder setAudioConfig(AudioConfig audioConfig) {
            captureConfig.setAudioConfig(audioConfig);
            return this;
        }

        public Builder setCaptureCallback(ScreenCaptureCallback captureCallback) {
            captureConfig.setCaptureCallback(captureCallback);
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

        public ScreenCaptureConfig create() {
            return captureConfig;
        }
    }
}
