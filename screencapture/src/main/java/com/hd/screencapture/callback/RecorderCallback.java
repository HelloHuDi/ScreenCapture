package com.hd.screencapture.callback;

import android.media.MediaCodec;
import android.media.MediaFormat;

/**
 * Created by hd on 2018/5/20 .
 */
public interface RecorderCallback {

    void onInputBufferAvailable(int index);

    void onOutputFormatChanged(MediaFormat format);

    void onOutputBufferAvailable(int index, MediaCodec.BufferInfo info);

    void onError(Exception exception);
}
