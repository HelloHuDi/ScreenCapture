package com.hd.screen.capture;

import android.content.Intent;
import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Range;

import com.hd.screencapture.help.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hd on 2018/5/31 .
 */
public class CaptureConfigFragment extends PreferenceFragment {

    private PreferenceHelp help;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.capture_config);
        help = new PreferenceHelp(getActivity());
        setContent(findPref(PreferenceHelp.VIDEO_ENCODER), Utils.findAllVideoCodecName(), help.getVideoEncoder());
        setContent(findPref(PreferenceHelp.VIDEO_BITRATE), getResources().getStringArray(R.array.video_bitrates), help.getVideoBitrate());
        setContent(findPref(PreferenceHelp.FPS), getResources().getStringArray(R.array.video_framerates), help.getFPS());
        setContent(findPref(PreferenceHelp.IFRAME_INTERVAL), getResources().getStringArray(R.array.iframeintervals), help.getIFrameInterval());
        setVideoProfile();
        setContent(findPref(PreferenceHelp.AUDIO_ENCODER), Utils.findAllAudioCodecName(), help.getAudioEncoder());
        setContent(findPref(PreferenceHelp.CHANNELS), getResources().getStringArray(R.array.audio_channels), help.getChannels());
        setAudioPar();
        findPref("capture").setOnPreferenceClickListener(preference1 -> {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
            return false;
        });
    }

    private void setAudioPar() {
        MediaCodecInfo.CodecCapabilities capabilities = Utils.findAudioCodecCapabilities(help.getAudioEncoder());

        int[] audioSampleRates = capabilities.getAudioCapabilities().getSupportedSampleRates();
        String[] sampleRatesArray = new String[audioSampleRates.length];
        for (int i = 0; i < audioSampleRates.length; i++) {
            sampleRatesArray[i] = String.valueOf(audioSampleRates[i]);
        }
        setContent(findPref(PreferenceHelp.SAMPLE_RATE), sampleRatesArray, help.getSampleRate());

        //audio bitrate
        Range<Integer> audioBitrateRange = capabilities.getAudioCapabilities().getBitrateRange();
        int lower = Math.max(audioBitrateRange.getLower() / 1000, 80);
        int upper = audioBitrateRange.getUpper() / 1000;
        List<String> rates = new ArrayList<>();
        for (int rate = lower; rate < upper; rate += lower) {
            rates.add(String.valueOf(rate));
        }
        rates.add(String.valueOf(upper));
        String[] bitrateArray = new String[rates.size()];
        setContent(findPref(PreferenceHelp.AUDIO_BITRATE), rates.toArray(bitrateArray), help.getAudioBitrate());

        //audio profile
        setContent(findPref(PreferenceHelp.AAC_PROFILE), Utils.aacProfiles(), help.getAacProfile());
    }

    private void setVideoProfile() {
        List<String> levels = new ArrayList<>();
        for (MediaCodecInfo.CodecProfileLevel level : Utils.findVideoProfileLevel(help.getVideoEncoder())) {
            levels.add(Utils.avcProfileLevelToString(level));
        }
        String[] levelsArray = new String[levels.size()];
        setContent(findPref(PreferenceHelp.AVC_PROFILE), levels.toArray(levelsArray), help.getAvcProfile());
    }

    private void setContent(ListPreference listPreference, String[] entries, Object content) {
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entries);
        if (content instanceof String) {
            if (TextUtils.isEmpty((CharSequence) content)) {
                content = entries[0];
                listPreference.setValue((String) content);
            }
        }
        listPreference.setSummary(String.valueOf(content));
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                listPreference.setSummary((CharSequence) newValue);
                return true;
            } finally {
                if (PreferenceHelp.VIDEO_ENCODER.equals(preference.getKey())) {
                    setVideoProfile();
                } else if (PreferenceHelp.AUDIO_ENCODER.equals(preference.getKey())) {
                    setAudioPar();
                }
            }
        });
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    protected <T extends Preference> T findPref(CharSequence key) {
        return (T) findPreference(key);
    }
}
