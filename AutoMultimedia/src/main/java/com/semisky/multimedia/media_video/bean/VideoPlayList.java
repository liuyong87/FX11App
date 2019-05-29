package com.semisky.multimedia.media_video.bean;


import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 视频播放列表
 * Created by LiuYong on 2018/8/7.
 */

public class VideoPlayList {
    private static final String TAG = Logutil.makeTagLog(VideoPlayList.class);
    // 下一曲事件处理标识
    public static final int CHANGE_NEXT_WITH_USER_OPERATION = 1;// 用户意图切换下一曲标识
    public static final int CHANGE_NEXT_WITH_PLAY_COMPLETED = 2;// 播放完成切换下一曲标识
    public static final int CHANGE_NEXT_WITH_PLAY_EXCEPTION = 3;// 播放异常切换下一曲标识

    private List<VideoInfo> mVideoInfoList;// 视频播放列表
    public static final int NO_POSITION = -1;
    private int mPlayingIndex = -1;// 当前播放视频下标
    private String mCurrentPlayingUrl = null;// 当前播放媒体URL
    private List<String> mDamageMediaList = null;


    public VideoPlayList() {
        mVideoInfoList = new ArrayList<VideoInfo>();
    }

    // 是否禁止下个节目
    public boolean isStopPlayNextPrgrogram() {
        if (null != mDamageMediaList) {
            if (getSize() == mDamageMediaList.size()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加损坏多媒体URL
     *
     * @param damageUrl
     */
    public void addDamageUrlToList(String damageUrl) {
        if (null == mDamageMediaList) {
            this.mDamageMediaList = new ArrayList<String>();
        }
        synchronized (mDamageMediaList) {
            if (null != damageUrl && !mDamageMediaList.contains(damageUrl)) {
                this.mDamageMediaList.add(damageUrl);
            }
        }
    }

    // 清除损坏URL集合
    private void removeDamageMediaList() {
        if (null != mDamageMediaList) {
            synchronized (mDamageMediaList) {
                mDamageMediaList.clear();
            }
        }
    }

    public String getmCurrentPlayingUrl() {
        if (mPlayingIndex != NO_POSITION) {
            return getVideoInfos().get(mPlayingIndex).getFileUrl();
        }
        return mCurrentPlayingUrl;
    }

    public void setmCurrentPlayingUrl(String currentPlayingUrl) {
        this.mCurrentPlayingUrl = currentPlayingUrl;
    }

    /**
     * 刷新当前播放URL在列表中的位置
     */
    public void refreshCurPlayUrlPos() {
        if(null == mVideoInfoList){
            Logutil.i(TAG, "refreshCurPlayUrlPos() fail !!!");
            return;
        }
        Logutil.i(TAG, "========");
        Logutil.i(TAG, "refreshCurPlayUrlPos() mCurrentPlayingUrl=" + mCurrentPlayingUrl);
        Logutil.i(TAG, "refreshCurPlayUrlPos() mPlayingIndex=" + mPlayingIndex);
        Logutil.i(TAG, "refreshCurPlayUrlPos() hasData=" + hasData());
        if (null != mCurrentPlayingUrl && hasData()) {
            for (int i = 0; i < mVideoInfoList.size(); i++) {
                String url = mVideoInfoList.get(i).getFileUrl();
                Logutil.i(TAG,"url="+url);
                if (url.equals(mCurrentPlayingUrl)) {
                    mPlayingIndex = i;
                    break;
                }
            }
        }
        Logutil.i(TAG, "refreshCurPlayUrlPos() update mPlayingIndex=" + mPlayingIndex);
        Logutil.i(TAG, "========\n");
    }

    /**
     * 添加视频列表
     *
     * @param videoInfoList
     */
    public void addVideoList(List<VideoInfo> videoInfoList) {
        Logutil.i(TAG, "addVideoList() ..." + (null != videoInfoList ? videoInfoList.size() : 0));
        if (null == videoInfoList) {
            return;
        }
        mVideoInfoList.clear();
        mVideoInfoList.addAll(videoInfoList);
    }

    /**
     * 获取视频列表
     *
     * @return
     */
    public List<VideoInfo> getVideoInfos() {
        if (null != this.mVideoInfoList) {
            return this.mVideoInfoList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 准备播放视频下标
     */
    public boolean prepare() {
        if (getVideoInfos().isEmpty()) {
            return false;
        }

        if (mPlayingIndex == NO_POSITION) {
            mPlayingIndex = 0;
        }
        return true;
    }

    /**
     * 获取当前播放歌曲下标
     *
     * @return
     */
    public int getmPlayingIndex() {
        return mPlayingIndex;
    }

    /**
     * 设置当前播放歌曲下标
     *
     * @param mPlayingIndex
     */
    public void setmPlayingIndex(int mPlayingIndex) {
        this.mPlayingIndex = mPlayingIndex;
    }

    /**
     * 获得当前播放视频信息 {@link #mPlayingIndex}
     */
    public VideoInfo getCurrentVideoInfo() {
        if (mPlayingIndex != NO_POSITION) {
            return getVideoInfos().get(mPlayingIndex);
        }
        return null;
    }

    /**
     * 是否有媒体视频数据
     *
     * @return
     */
    public boolean hasData() {
        boolean hasData = (getVideoInfos() != null && getVideoInfos().size() > 0);
        Logutil.i(TAG, "hasData() ..." + hasData);
        return hasData;
    }

    /**
     * 获取上一个节目信息
     *
     * @return
     */
    public VideoInfo prev() {
        int newIndex = this.mPlayingIndex - 1;
        if (newIndex < 0) {
            newIndex = this.getVideoInfos().size() - 1;
        }
        this.mPlayingIndex = newIndex;
        return getVideoInfos().get(this.mPlayingIndex);
    }

    /**
     * 是否有下一个节目
     *
     * @param from
     * @return
     */
    public boolean hasNext(int from) {
        boolean canNext = false;
        switch (from) {
            case CHANGE_NEXT_WITH_USER_OPERATION:
            case CHANGE_NEXT_WITH_PLAY_COMPLETED:
                canNext = hasData();
                break;
            case CHANGE_NEXT_WITH_PLAY_EXCEPTION:
                canNext = (hasData() && !isStopPlayNextPrgrogram());
                break;
        }
        return canNext;
    }

    /**
     * 获取下一个节目信息
     *
     * @return
     */
    public VideoInfo next() {
        int newIndex = mPlayingIndex + 1;
        if (newIndex >= getVideoInfos().size()) {
            newIndex = 0;
        }
        this.mPlayingIndex = newIndex;
        return this.getVideoInfos().get(mPlayingIndex);
    }

    /**
     * 媒体清单尺寸
     *
     * @return
     */
    public int getSize() {
        return (null != mVideoInfoList ? mVideoInfoList.size() : 0);
    }

    public void onDestory() {
        this.getVideoInfos().clear();
        this.mPlayingIndex = NO_POSITION;
        this.mCurrentPlayingUrl = null;
        removeDamageMediaList();
    }
}
