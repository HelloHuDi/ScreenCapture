package com.hd.screencapture.config;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.hd.screencapture.help.Utils;

/**
 * Created by hd on 2018/5/18 .
 */
public final class AudioConfig extends CaptureConfig{

    private int samplingRate = 44100;

    private int channelCount = 1;

    private int bitrate = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * {@link Utils#findAllAudioCodecName()}
     * {@link MediaCodec#createByCodecName(String)}
     */
    private String codecName;

    /**
     * {@link Utils#findAudioProfileLevel(String)}
     * {@link MediaFormat#KEY_AAC_PROFILE}
     */
    private MediaCodecInfo.CodecProfileLevel level;

    @Override
    public String toString() {
        return "AudioConfig{" + "samplingRate=" + samplingRate + ", channelCount=" + channelCount +//
                ", bitrate=" + bitrate + ", codecName='" + codecName + '\'' + ", level=" + level + '}';
    }

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
