package com.semisky.multimedia.media_music.service;

import android.os.RemoteCallbackList;

import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;

/**
 * Created by LiuYong on 2018/8/27.
 */

public class MediaRemoteCallbackList extends RemoteCallbackList<IProxyProgramChangeCallback> {
    private OnCallbackDiedListener mOnCallbackDiedListener;
    interface OnCallbackDiedListener{
        void onCallbackDied(IProxyProgramChangeCallback callback);
    }


    public void registerListener(OnCallbackDiedListener listener){
        this.mOnCallbackDiedListener = listener;
    }

    public void unregisterListener(){
        this.mOnCallbackDiedListener = null;
    }


    @Override
    public void onCallbackDied(IProxyProgramChangeCallback callback) {
        super.onCallbackDied(callback);
        if(null != this.mOnCallbackDiedListener){
            mOnCallbackDiedListener.onCallbackDied(callback);
        }
    }
}
