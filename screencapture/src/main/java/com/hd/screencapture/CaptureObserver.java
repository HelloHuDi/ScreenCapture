package com.hd.screencapture;

/**
 * Created by hd on 2018/5/15 .
 */
public abstract class CaptureObserver {

    private ScreenCapture screenCapture;

    volatile boolean alive;

    CaptureObserver(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
        alive=true;
    }

    public boolean isAlive(){
        return alive;
    }

    public void stopCapture(){
        screenCapture.stopCapture();
        alive=false;
        screenCapture=null;
    }

}
