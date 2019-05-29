package com.semisky.multimedia.media_usb.model;

import android.content.Intent;

public abstract class AbstractAppStartPolicy implements IAppStartPolicyModel{
    private OnAutoStartAppStateListener mOnAutoStartAppStateListener;




    @Override
    public void setOnAutoStartAppStateListener(OnAutoStartAppStateListener l) {
        this.mOnAutoStartAppStateListener = l;
    }

    @Override
    public void unRegisterOnAutoStartAppStateListener() {
        this.mOnAutoStartAppStateListener = null;
    }

    @Override
    public void onAutoStartApp(boolean fromScanDoneEvent) {

    }

    @Override
    public void onUserStartApp() {

    }

    @Override
    public void onRelease() {

    }

    public abstract void handlerAppStartEvent(Intent intent);
}
