package com.hd.screen.capture;

import android.media.MediaCodecInfo;
import android.util.Log;
import android.util.Range;

import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.VideoConfig;
import com.hd.screencapture.help.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hd on 2018/5/23 .
 */
public final class LogConfig {

    /**
     * when you don't understand how to use config{@link VideoConfig}{@link AudioConfig},
     * maybe these parameters can help you.
     */
    public static void log() {
        MediaCodecInfo[] videoInfos = Utils.findAllVideoEncoder();
        MediaCodecInfo[] audioInfos = Utils.findAllAudioEncoder();
        String[] videoCodecName = Utils.findAllVideoCodecName();
        String[] audioCodecName = Utils.findAllAudioCodecName();
        Log.d("tag", "\nall ========" + Arrays.toString(videoInfos)//
                + "\n========" + Arrays.toString(audioInfos)//
                + "\n========" + Arrays.toString(videoCodecName)//
                + "\n========" + Arrays.toString(audioCodecName)//
                + "\n========" + Arrays.toString(Utils.aacProfiles()));
        for (String codecName : videoCodecName) {
            MediaCodecInfo.CodecProfileLevel[] levels = Utils.findVideoProfileLevel(codecName);
            MediaCodecInfo.CodecCapabilities capabilities = Utils.findVideoCodecCapabilities(codecName);
            for (MediaCodecInfo.CodecProfileLevel level : levels) {
                Log.d("tag", "\nvideoCodecName : " + codecName + " ,level========" + level.level //
                        + "\n========" + level.profile    //
                        + "\n========" + Utils.avcProfileLevelToString(level)//
                        + "\n========" + Utils.toProfileLevel(Utils.avcProfileLevelToString(level)));
            }
            Range<Integer> videoBitrateRange = capabilities.getVideoCapabilities().getBitrateRange();
            Range<Integer> videoFrameRatesRange = capabilities.getVideoCapabilities().getSupportedFrameRates();

            Log.d("tag", "\nvideoCodecName : " + codecName + "=======" + videoBitrateRange//
                    + "\n========" + videoFrameRatesRange);

            int[] videoColorFormats = capabilities.colorFormats;
            Log.d("tag", "\nvideoCodecName : " + codecName + "==videoColorFormats:" + Arrays.toString(videoColorFormats));
            for (int colorFormat : videoColorFormats) {
                Log.d("tag", "\nvideoCodecName : " + codecName //
                        + "\n=======colorFormat :" + Utils.toHumanReadable(colorFormat)//
                        + "\n=======" + Utils.toColorFormat(Utils.toHumanReadable(colorFormat)));
            }
        }
        for (String codecName : audioCodecName) {
            MediaCodecInfo.CodecProfileLevel[] levels = Utils.findAudioProfileLevel(codecName);
            MediaCodecInfo.CodecCapabilities capabilities = Utils.findAudioCodecCapabilities(codecName);
            for (MediaCodecInfo.CodecProfileLevel level : levels) {
                Log.d("tag", "\naudioCodecName : " + codecName + " ,level========" + level.level //
                        + "\n========" + level.profile);
            }
            Range<Integer> audioBitrateRange = capabilities.getAudioCapabilities().getBitrateRange();
            Range<Integer>[] audioSampleRateRanges = capabilities.getAudioCapabilities().getSupportedSampleRateRanges();
            int[] audioSampleRates = capabilities.getAudioCapabilities().getSupportedSampleRates();

            //
            int lower = Math.max(audioBitrateRange.getLower() / 1000, 80);
            int upper = audioBitrateRange.getUpper() / 1000;
            List<Integer> rates = new ArrayList<>();
            for (int rate = lower; rate < upper; rate += lower) {
                rates.add(rate);
            }
            rates.add(upper);

            Log.d("tag", "\naudioCodecName : " + codecName + "=======" + audioBitrateRange //
                    + "\n========" + rates//
                    + "\n========" + Arrays.toString(audioSampleRateRanges) //
                    + "\n========" + Arrays.toString(audioSampleRates));


            int[] audioColorFormats = capabilities.colorFormats;
            Log.d("tag", "\naudioCodecName : " + codecName + "==audioColorFormats:" + Arrays.toString(audioColorFormats));
            for (int colorFormat : audioColorFormats) {
                Log.d("tag", "\naudioCodecName : " + codecName//
                        + "\n=======colorFormat :" + Utils.toHumanReadable(colorFormat)//
                        + "\n=======" + Utils.toColorFormat(Utils.toHumanReadable(colorFormat)));
            }
        }
    }
}
