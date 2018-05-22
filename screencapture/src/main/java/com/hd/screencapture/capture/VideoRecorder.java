package com.hd.screencapture.capture;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.hd.screencapture.callback.RecorderCallback;
import com.hd.screencapture.config.ScreenCaptureConfig;
import com.hd.screencapture.observer.CaptureObserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Created by hd on 2018/5/20 .
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VideoRecorder extends Recorder {

    private final String TAG = "VideoRecorder";

    private Surface surface;

    private MediaCodec mEncoder;

    public VideoRecorder(@NonNull CaptureObserver observer, @NonNull ScreenCaptureConfig config,//
                         @NonNull RecorderCallback callback) {
        super(observer, config, callback);
    }

    @Override
    public boolean prepare() {
        try {
            MediaFormat format = createMediaFormat();
            String mimeType = format.getString(MediaFormat.KEY_MIME);
            mEncoder = createEncoder(mimeType);
            mEncoder.setCallback(mCodecCallback);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            surface = mEncoder.createInputSurface();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean record() {
        mEncoder.start();
        return true;
    }

    @Override
    public void release() {
        if (surface != null) {
            surface.release();
            surface = null;
        }
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.reset();
            mEncoder.release();
            mEncoder = null;
        }
    }

    public Surface getSurface() {
        return surface;
    }

    private MediaFormat createMediaFormat() {
        final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, config.getVideoConfig().getWidth(), config.getVideoConfig().getHeight());
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, config.getVideoConfig().getBitrate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, config.getVideoConfig().getFrameRate());
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, config.getVideoConfig().getIFrameInterval());
        MediaCodecInfo.CodecProfileLevel codecProfileLevel=config.getVideoConfig().getLevel();
        if (codecProfileLevel != null && codecProfileLevel.profile != 0 && codecProfileLevel.level != 0) {
            format.setInteger(MediaFormat.KEY_PROFILE, codecProfileLevel.profile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                format.setInteger(MediaFormat.KEY_LEVEL, codecProfileLevel.level);
            }else{
                format.setInteger("level", codecProfileLevel.level);
            }
        }
        if (config.allowLog())
            Log.d(TAG, "created video format: " + format);
        return format;
    }

    private MediaCodec createEncoder(String mimeType) throws IOException {
        String mCodecName = config.getVideoConfig().getCodecName();
        try {
            if (!TextUtils.isEmpty(mCodecName)) {
                return MediaCodec.createByCodecName(mCodecName);
            }
        } catch (IOException e) {
            Log.w(TAG, "Create MediaCodec by name '" + mCodecName + "' failure!", e);
        }
        return MediaCodec.createEncoderByType(mimeType);
    }

    private MediaCodec.Callback mCodecCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            callback.onInputBufferAvailable(index);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            callback.onOutputBufferAvailable(index, info);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            callback.onError(e);
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            callback.onOutputFormatChanged(format);
        }
    };

    public final ByteBuffer getOutputBuffer(int index) {
        return getEncoder().getOutputBuffer(index);
    }

    public final ByteBuffer getInputBuffer(int index) {
        return getEncoder().getInputBuffer(index);
    }

    public final void queueInputBuffer(int index, int offset, int size, long pstTs, int flags) {
        getEncoder().queueInputBuffer(index, offset, size, pstTs, flags);
    }

    public final void releaseOutputBuffer(int index) {
        getEncoder().releaseOutputBuffer(index, false);
    }

    public final MediaCodec getEncoder() {
        return Objects.requireNonNull(mEncoder, "doesn't prepare()");
    }
}
