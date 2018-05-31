package com.hd.screen.capture;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.config.VideoConfig;
import com.hd.screencapture.help.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hd on 2018/5/23 .
 */
public class ScreenCapturePresenter {

    private ScreenCapture screenCapture;

    private AppCompatActivity activity;

    private PreferenceHelp help;

    ScreenCapturePresenter(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        help = new PreferenceHelp(activity);
        screenCapture = ScreenCapture.with(activity);
    }

    public boolean isCapturing() {
        return screenCapture.isRunning();
    }

    public File getFile(){
        if (captureConfig != null)
            return captureConfig.getFile();
        return null;
    }

    public void startCapture() {
        if (!isCapturing()) {
            initConfig();
            screenCapture.startCapture();
        } else {
            Toast.makeText(activity, "current is capturing state", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopCapture() {
        if (isCapturing()) {
            screenCapture.stopCapture();
        } else {
            Toast.makeText(activity, "current is stopped state", Toast.LENGTH_SHORT).show();
        }
    }

    private ScreenCaptureConfig captureConfig;

    private void initConfig() {
        VideoConfig videoConfig = help.useDefaultVideoConfig() ? VideoConfig.initDefaultConfig(activity) : initVideoConfig(activity);
        AudioConfig audioConfig = null;
        if (help.hasAudio()) {
            audioConfig = help.useDefaultAudioConfig() ? AudioConfig.initDefaultConfig() : initAudioConfig();
        }
        captureConfig = new ScreenCaptureConfig.Builder()//
                                                         .setAllowLog(BuildConfig.DEBUG)//
                                                         .setFile(setSelfFile()).setVideoConfig(videoConfig)//
                                                         //if it is not set, then the voice will not be supported
                                                         .setAudioConfig(audioConfig)//
                                                         .setCaptureCallback((ScreenCaptureStreamCallback) activity)//
                                                         .setAutoMoveTaskToBack(true)//
                                                         .create();//
        Log.i("tag", "current using config ===>video config :" + videoConfig.toString()//
                + (audioConfig != null ? ("\n=====audio config :" + audioConfig.toString()) : "")+
             "\n======ScreenCaptureConfig :"+captureConfig.toString());
        screenCapture.setConfig(captureConfig);
    }

    private VideoConfig initVideoConfig(@NonNull AppCompatActivity activity) {
        VideoConfig videoConfig = new VideoConfig(activity);
        videoConfig.setBitrate(help.getVideoBitrate());
        videoConfig.setCodecName(help.getVideoEncoder());
        videoConfig.setFrameRate(help.getFPS());
        videoConfig.setIFrameInterval(help.getIFrameInterval());
        videoConfig.setLevel(Utils.toProfileLevel(help.getAvcProfile()));
        return videoConfig;
    }

    private AudioConfig initAudioConfig() {
        AudioConfig audioConfig = new AudioConfig();
        audioConfig.setBitrate(help.getAudioBitrate());
        audioConfig.setChannelCount(help.getChannels());
        audioConfig.setCodecName(help.getAudioEncoder());
        audioConfig.setSamplingRate(help.getSampleRate());
        audioConfig.setLevel(Utils.toProfileLevel(help.getAacProfile()));
        return audioConfig;
    }

    private File setSelfFile() {
        File file= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "screen_capture");
        if(!file.exists() && !file.mkdir())
            return null;
        file=new File(file,"screen_capture_"+ new SimpleDateFormat("yyyyMMdd-HH-mm-ss", //
                                                                   Locale.US).format(new Date()) + ".mp4");
        return file;
    }

}
