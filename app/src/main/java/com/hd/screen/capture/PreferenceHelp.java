package com.hd.screen.capture;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hd on 2018/5/31 .
 */
public final class PreferenceHelp {

    private SharedPreferences sp;

    public static final String HAS_AUDIO="has_audio";
    public static final String VIDEO_ENCODER="video_encoder";
    public static final String VIDEO_BITRATE="video_bitrate";
    public static final String FPS="fps";
    public static final String IFRAME_INTERVAL="iFrame_interval";
    public static final String AVC_PROFILE="avc_profile";
    public static final String AUDIO_ENCODER="audio_encoder";
    public static final String CHANNELS="channels";
    public static final String SAMPLE_RATE="sample_rate";
    public static final String AUDIO_BITRATE="audio_bitrate";
    public static final String AAC_PROFILE="aac_profile";

    public PreferenceHelp(Context context) {
        sp= PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean useDefaultAudioConfig(){
        return sp.getBoolean("defaultAudioConfig",true);
    }

    public boolean useDefaultVideoConfig(){
        return sp.getBoolean("defaultVideoConfig",true);
    }

    public boolean hasAudio(){
        return sp.getBoolean(HAS_AUDIO,true);
    }

    public String getVideoEncoder(){
        return sp.getString(VIDEO_ENCODER,"");
    }

    public int getVideoBitrate(){
        return Integer.parseInt(sp.getString(VIDEO_BITRATE,"12000000"));
    }

    public int getFPS(){
        return Integer.parseInt(sp.getString(FPS,"60"));
    }

    public int getIFrameInterval(){
        return Integer.parseInt(sp.getString(IFRAME_INTERVAL,"1"));
    }

    public String getAvcProfile(){
        return sp.getString(AVC_PROFILE,"");
    }

    public String getAudioEncoder(){
        return sp.getString(AUDIO_ENCODER,"");
    }

    public int getChannels(){
        return Integer.parseInt(sp.getString(CHANNELS,"1"));
    }

    public int getSampleRate(){
        return Integer.parseInt(sp.getString(SAMPLE_RATE,"44100"));
    }

    public int getAudioBitrate(){
        return Integer.parseInt(sp.getString(AUDIO_BITRATE, "2"));
    }

    public String getAacProfile(){
        return sp.getString(AAC_PROFILE,"");
    }
}
