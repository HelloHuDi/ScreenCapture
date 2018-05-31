<p align="center">
	<img width="72" height="72" src="art/ic_launcher-web.png"/>
</p>
<h3 align="center">ScreenCapture</h3>
<p align="center">
<a href="" target="_blank"><img src="https://img.shields.io/badge/release-v2.3-blue.svg"></img></a>
</p>

## Implement screen capture without root on Android 5.0+ by using MediaProjectionManager, VirtualDisplay, AudioRecord, MediaCodec and MediaMuxer APIs

## According to [ScreenRecorder][1] adaptation,thanks [Yrom Wang][2]

## Usage:

### dependencies :

```
dependencies {
    //...
    implementation 'com.hd:screencapture:2.3'
}
```

### code

```
ScreenCapture.with(activity).startCapture();
```

### or

```
ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()
                                                           .setAllowLog(BuildConfig.DEBUG)
                                                           //init default video config
                                                           .setVideoConfig(VideoConfig.initDefaultConfig(activity))
                                                           //if it is not set, then the voice will not be supported  
                                                           .setAudioConfig(AudioConfig.initDefaultConfig())
                                                           .setCaptureCallback((ScreenCaptureStreamCallback) activity)
                                                           //default false
                                                           .setAutoMoveTaskToBack(true)
                                                           .create();
ScreenCapture screenCapture = ScreenCapture.with(activity).setConfig(captureConfig);

screenCapture.startCapture();
```

### auto stop according to the activity lifecycle by default, of course, you can manual stop

```
//...
screenCapture.stopCapture();
```

[1]: https://github.com/yrom/ScreenRecorder
[2]: https://github.com/yrom
