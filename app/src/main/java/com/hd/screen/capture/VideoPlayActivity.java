package com.hd.screen.capture;

import android.os.Bundle;

import com.hd.splashscreen.video.BackgroundVideoView;

public class VideoPlayActivity extends BaseActivity {

    private BackgroundVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        String path=getIntent().getStringExtra("video_path");
        videoView=findViewById(R.id.videoView);
        videoView.setLoopPlay(false);
        videoView.setPlayPath(path);
        videoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stop();
    }
}
