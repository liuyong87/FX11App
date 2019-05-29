package com.semisky.multimedia.media_music.model;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;

/**
 * Created by LiuYong on 2018/8/25.
 */

public class MusicAudioFocusModel implements OnAudioFocusChangeListener {
    private static final String TAG = Logutil.makeTagLog(MusicAudioFocusModel.class);
    private static MusicAudioFocusModel _INSTANCE;
    private Context mCtx;
    private AudioManager mAudioManager;
    private OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private boolean mIsAudioFocus;// 是否有音频焦点

    private MusicAudioFocusModel() {

    }

    public MusicAudioFocusModel init(Context ctx) {
        this.mCtx = ctx;
        if (null == mAudioManager) {
            this.mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        }
        return this;
    }

    public static MusicAudioFocusModel getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new MusicAudioFocusModel();
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
        if (null == mAudioManager) {
            Logutil.e(TAG, "null == mAudioManager");
            return;
        }
        if (!mIsAudioFocus) {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            switch (result) {
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    Logutil.i(TAG, "onRequestAudioFocus() AUDIOFOCUS_REQUEST_GRANTED ...");
                    SemiskyIVIManager.getInstance().openAndroidStreamVolume();
                    this.mIsAudioFocus = true;
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
        if (null == mAudioManager) {
            Logutil.e(TAG, "null == mAudioManager");
            return;
        }
        if (mIsAudioFocus) {
            mAudioManager.abandonAudioFocus(this);
            unregisterAudioFocus();
            this.mIsAudioFocus = false;
            _INSTANCE = null;
            mCtx = null;
            this.mAudioManager = null;
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
