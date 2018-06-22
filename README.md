<p align="center">
	<img width="72" height="72" src="art/ic_launcher-web.png"/>
</p>
<h3 align="center">ScreenCapture</h3>
<p align="center">
<a href="" target="_blank"><img src="https://img.shields.io/badge/release-v2.4-blue.svg"></img></a>
</p>

## Implement screen capture without root on Android 5.0+ by using MediaProjectionManager, VirtualDisplay, AudioRecord, MediaCodec and MediaMuxer APIs

## According to [ScreenRecorder][1] adaptation,thanks [Yrom Wang][2]

## [provide screen record tool , and the MP4 files that support the recording are converted to GIF][3]

## screenshot

<img src="art/screen.gif" width="300px" height="500px"/>

## Usage:

### dependencies :

```
dependencies {
    //...
    implementation 'com.hd:screencapture:2.4'
}
```

### code

```
//use default video config
ScreenCapture.with(activity).startCapture();
```

### or

```
ScreenCaptureConfig captureConfig = new ScreenCaptureConfig.Builder()
                                                           .setFile("your file")//
                                                           .setAllowLog(BuildConfig.DEBUG)
                                                           //init default video config
                                                           .setVideoConfig(VideoConfig.initDefaultConfig(activity))
                                                           //if it is not set, then the voice will not be supported  
                                                           .setAudioConfig(AudioConfig.initDefaultConfig())
                                                           .setCaptureCallback((ScreenCaptureStreamCallback) activity)
                                                           //relevance the activity lifecycle ,if false not auto stop
                                                           .setRelevanceLifecycle(true)
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

### License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://github.com/yrom/ScreenRecorder
[2]: https://github.com/yrom
[3]: https://github.com/HelloHuDi/ScreenRecordTool
