package com.hd.screen.capture;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hd.screencapture.ScreenCapture;
import com.hd.screencapture.callback.ScreenCaptureStreamCallback;
import com.hd.screencapture.config.AudioConfig;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.config.VideoConfig;
import com.hd.screencapture.help.ExecutorUtil;
import com.hd.screencapture.help.ScreenCaptureState;
import com.hd.screencapture.help.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by hd on 2018/5/14 .
 */
public class MainActivity extends AppCompatActivity implements ScreenCaptureStreamCallback {

    private ScreenCapture screenCapture;

    private boolean isRunning;

    private TextView tvTime, tvVideoHeaderData, tvVideoData, tvAudioData;

    private ExecutorUtil executorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime = findViewById(R.id.tvTime);
        tvVideoHeaderData = findViewById(R.id.tvVideoHeaderData);
        tvVideoData = findViewById(R.id.tvVideoData);
        tvAudioData = findViewById(R.id.tvAudioData);
        log();
        init();
    }

    private void init() {
        executorUtil = new ExecutorUtil();
        ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()//
                                                .setAllowLog(false/*BuildConfig.DEBUG*/)//
                                                .setVideoConfig(VideoConfig.initDefaultConfig(this))//
                                                .setAudioConfig(AudioConfig.initDefaultConfig())//
                                                .setCaptureCallback(this)//
                                                .setAutoMoveTaskToBack(true)//
                                                .create();//
        screenCapture = ScreenCapture.with(this).setConfig(captureConfig);
    }

    public void startCapture(View view) {
        if (!isRunning) {
            screenCapture.startCapture();
        } else {
            Toast.makeText(MainActivity.this, "current is capturing state", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopCapture(View view) {
        if (isRunning) {
            screenCapture.stopCapture();
        } else {
            Toast.makeText(MainActivity.this, "current is stopped state", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void captureState(ScreenCaptureState state) {
        isRunning = !(ScreenCaptureState.FAILED == state || ScreenCaptureState.COMPLETED == state);
        Log.d("tag", "capture state ==>" + state + "==" + isRunning);
        executorUtil.mainThread().execute(() -> Toast.makeText(MainActivity.this, "capture state ==>" + state, Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void captureTime(long time) {
        executorUtil.mainThread().execute(() -> tvTime.setText("capture time ==>" + DateUtils.formatElapsedTime(time)));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void videoHeaderByte(@NonNull byte[] sps, @NonNull byte[] pps) {
        executorUtil.mainThread().execute(() -> tvVideoHeaderData.setText("video header byte length ==> sps len: " + sps.length + ",  pps len : " + pps.length));
        //executorUtil.networkIO().execute();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void videoContentByte(@NonNull byte[] content) {
        executorUtil.mainThread().execute(() -> tvVideoData.setText("video content byte len ==> " + content.length));
        //executorUtil.networkIO().execute();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void audioContentByte(@NonNull byte[] content) {
        executorUtil.mainThread().execute(() -> tvAudioData.setText("audio content byte len ==> " + content.length));
        //executorUtil.networkIO().execute();
    }

    private void log() {
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
