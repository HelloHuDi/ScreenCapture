package com.hd.screencapture.config;

import android.media.AudioFormat;

/**
 * Created by hd on 2018/5/18 .
 */
public class AudioConfig extends CaptureConfig{

    private int samplingRate = 44100;

    private int channelCount = 1;

    private int bitrate = AudioFormat.ENCODING_PCM_16BIT;

    public static AudioConfig initDefaultConfig() {
        return new AudioConfig();
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }
}
