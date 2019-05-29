package com.semisky.multimedia.media_video.model;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;

/**
 * Created by LiuYong on 2018/8/25.
 */

public class VideoAudioFocusModel implements OnAudioFocusChangeListener {
    private static final String TAG = Logutil.makeTagLog(VideoAudioFocusModel.class);
    private static VideoAudioFocusModel _INSTANCE;
    private Context mCtx;
    private AudioManager mAudioManager;
    private OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private boolean mIsAudioFocus;// 是否有音频焦点

    private VideoAudioFocusModel(Context ctx) {
        this.mCtx = ctx;
        this.mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    }

    public static VideoAudioFocusModel getInstance(Context ctx) {
        if (null == _INSTANCE) {
            _INSTANCE = new VideoAudioFocusModel(ctx);
        }
        return _INSTANCE;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (null != this.mOnAudioFocusChangeListener) {
            this.mOnAudioFocusChangeListener.onAudioFocusChange(focusChange);
        }
    }

    /**
     * 注册监听音频焦点变化接口
     *
     * @param l
     */
    public void registerAudioFocus(OnAudioFocusChangeListener l) {
        this.mOnAudioFocusChangeListener = l;
        Logutil.i(TAG, "registerAudioFocusListener ..." + (null != l ? l.toString() : "NULL"));
    }

    /**
     * 反注册监听音频焦点变化接口
     */
    public void unregisterAudioFocus() {
        this.mOnAudioFocusChangeListener = null;
    }

    /**
     * 申请音频焦点
     */
    public void onRequestAudioFocus() {
        Logutil.i(TAG, "onRequestAudioFocus() mIsAudioFocus=" + mIsAudioFocus);
        if (!mIsAudioFocus) {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            switch (result) {
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    this.mIsAudioFocus = true;
                    SemiskyIVIManager.getInstance().openAndroidStreamVolumeByVideo();
                    Logutil.i(TAG, "onRequestAudioFocus() AUDIOFOCUS_REQUEST_GRANTED ...");
                    break;
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    Logutil.i(TAG, "onRequestAudioFocus() AUDIOFOCUS_REQUEST_FAILED ...");
                    break;
            }
        }
    }

    /**
     * 注销音频焦点
     */
    public void onAbandonAudioFocus() {
        if (mIsAudioFocus) {
            this.mAudioManager.abandonAudioFocus(this);
            unregisterAudioFocus();
            this.mIsAudioFocus = false;
            Logutil.i(TAG, "onAbondanAudioFocus() ...");
        }
    }

    /**
     * 是否有音频焦点
     *
     * @return
     */
    public boolean hasAudioFocus() {
        return this.mIsAudioFocus;
    }


}
