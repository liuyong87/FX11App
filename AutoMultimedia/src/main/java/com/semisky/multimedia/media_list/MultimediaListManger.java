package com.semisky.multimedia.media_list;

import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.ArrayList;
import java.util.List;

/**
 * 多媒体列表管理
 * 作用范围：
 * 1.记录当前播放媒体文件
 * Created by LiuYong on 2018/9/19.
 */

public class MultimediaListManger {
    private static final String TAG = Logutil.makeTagLog(MultimediaListManger.class);
    private static MultimediaListManger _INSTANCE;
    private List<OnItemHighLightChangeCallback> mCallbacks;

    private String mPlayingUrlWithMusic = null;// 当前播放媒体音乐资源
    private String mPlayingUrlWithVideo = null;// 当前播放媒体视频资源
    private String mPlayingUrlWithPhoto = null;// 当前播放媒体图片资源

    private MultimediaListManger() {
        this.mCallbacks = new ArrayList<OnItemHighLightChangeCallback>();
    }

    public static MultimediaListManger getInstance() {
        if (null == _INSTANCE) {
            synchronized (MultimediaListManger.class) {
                if (null == _INSTANCE) {
                    _INSTANCE = new MultimediaListManger();
                }
            }
        }
        return _INSTANCE;
    }


    public void addCallback(OnItemHighLightChangeCallback callback) {
        if (null != mCallbacks && !mCallbacks.contains(callback)) {
            Logutil.i(TAG, "addCallback() ..." + (null != callback ? callback.getClass().getName() : "null"));
            synchronized (mCallbacks) {
                mCallbacks.add(callback);
            }
        }
    }

    public void unRegisterCallback(OnItemHighLightChangeCallback callback) {
        if (null != mCallbacks && mCallbacks.contains(callback)) {
            Logutil.i(TAG, "unRegisterCallback() ..." + (null != callback ? callback.getClass().getName() : "null"));
            synchronized (mCallbacks) {
                mCallbacks.remove(callback);
            }
        }
    }

    public void notifyItemHighLightChange() {
        if (null != mCallbacks) {
            synchronized (mCallbacks) {
                if (hasCallbacks()) {
                    for (OnItemHighLightChangeCallback callback : mCallbacks) {
                        callback.onItemHighLightChange();
                    }
                }
            }
        }
    }

    private boolean hasCallbacks() {
        return (null != mCallbacks && mCallbacks.size() > 0);
    }

    // Setter/Getter

    public String getmPlayingUrlWithMusic() {
        return mPlayingUrlWithMusic;
    }

    public void setmPlayingUrlWithMusic(String playingUrlWithMusic) {
        this.mPlayingUrlWithMusic = playingUrlWithMusic;
        this.mPlayingUrlWithVideo = null;
    }

    public String getmPlayingUrlWithVideo() {
        return mPlayingUrlWithVideo;
    }

    public void setmPlayingUrlWithVideo(String playingUrlWithVideo) {
        this.mPlayingUrlWithVideo = playingUrlWithVideo;
        this.mPlayingUrlWithMusic = null;
    }

    public String getmPlayingUrlWithPhoto() {
        return mPlayingUrlWithPhoto;
    }

    public void setmPlayingUrlWithPhoto(String playingUrlWithPhoto) {
        this.mPlayingUrlWithPhoto = playingUrlWithPhoto;
    }
}
