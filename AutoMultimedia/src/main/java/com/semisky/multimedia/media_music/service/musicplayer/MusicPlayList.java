package com.semisky.multimedia.media_music.service.musicplayer;


import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 音乐播放媒体列表
 *
 * @author liuyong
 */
public class MusicPlayList {

    private static final String TAG = Logutil.makeTagLog(MusicPlayList.class);
    private List<MusicInfo> mMusicInfoList = new ArrayList<MusicInfo>();// 播放列表集合
    private int mPlayMode = PlayMode.LOOP;// 使用单例播放模式

    // 下一曲事件处理标识
    public static final int CHANGE_NEXT_WITH_USER_OPERATION = 1;// 用户意图切换下一曲标识
    public static final int CHANGE_NEXT_WITH_PLAY_COMPLETED = 2;// 播放完成切换下一曲标识
    public static final int CHANGE_NEXT_WITH_PLAY_EXCEPTION = 3;// 播放异常切换下一曲标识

    public static final int NO_POSITION = -1;
    private int mPlayingIndex = -1;// 当前播放视频下标
    private String mCurrentPlayingUrl = null;// 当前播放媒体URL
    private MusicInfo mCurrentID3Info = null;
    private List<String> mDamageMediaList = null;
    private boolean mIsFavoriteWithCurrentMusic = false;
    private boolean mStopSinglePlay = false; //是否跳过异常节目的单曲循环

    public void setmStopSinglePlay(boolean mStopSinglePlay){
        Logutil.i(TAG,"stopSinglePlay: "+mStopSinglePlay);
        this.mStopSinglePlay = mStopSinglePlay;
    }

    /**
     * 设置当前歌曲收藏状态
     *
     * @param isFavorite
     */
    public void setFavoriteStatusWithCurrentMusic(boolean isFavorite) {
        this.mIsFavoriteWithCurrentMusic = isFavorite;
        Logutil.i(TAG, "setFavoriteStatusWithCurrentMusic() ..." + isFavorite);
    }

    /**
     * 当前歌曲是否已收藏
     *
     * @return
     */
    public boolean isFavoriteWithCurrentMusic() {
        return this.mIsFavoriteWithCurrentMusic;
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
     * 获取损坏ID3信息
     *
     * @return
     */
    public MusicInfo getCurrentDamageID3Info() {
        if (hasData() && mPlayingIndex != NO_POSITION) {
            return mMusicInfoList.get(mPlayingIndex);
        }
        return null;
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

    /**
     * 获取当前媒体ID3信息
     *
     * @return
     */
    public MusicInfo getmCurrentID3Info() {
        return this.mCurrentID3Info;
    }

    /**
     * 设置当前媒体ID3信息
     *
     * @param currentID3Info
     */
    public void setmCurrentID3Info(MusicInfo currentID3Info) {
        this.mCurrentID3Info = currentID3Info;
    }

    /**
     * 刷新并加载列表中正在播放URL位置
     *
     * @return
     */
    public int getRefreshCurPlayingUrlPos() {
        refreshCurPlayUrlPos();
        return this.mPlayingIndex;
    }

    /**
     * 刷新当前播放URL在列表中的位置
     */
    public void refreshCurPlayUrlPos() {
        Logutil.i(TAG, "========");
        Logutil.i(TAG, "refreshCurPlayUrlPos() mCurrentPlayingUrl=" + mCurrentPlayingUrl);
        Logutil.i(TAG, "refreshCurPlayUrlPos() mPlayingIndex=" + mPlayingIndex);
        Logutil.i(TAG, "refreshCurPlayUrlPos() hasData=" + hasData());
        if (null != mCurrentPlayingUrl && hasData()) {
            for (int i = 0; i < mMusicInfoList.size(); i++) {
                String url = mMusicInfoList.get(i).getUrl();
                if (mCurrentPlayingUrl.equals(url)) {
                    mPlayingIndex = i;
                    break;
                }
            }
        }
        Logutil.i(TAG, "refreshCurPlayUrlPos() update mPlayingIndex=" + mPlayingIndex);
        Logutil.i(TAG, "========\n");
    }

    /**
     * 获取当前播放媒体资源URL
     *
     * @return
     */
    public String getmCurrentPlayingUrl() {
        if (mPlayingIndex != NO_POSITION && hasData()) {
            mCurrentPlayingUrl = getMusicInfos().get(mPlayingIndex).getUrl();
        }
        Logutil.i(TAG, "getmCurrentPlayingUrl() ..." + mCurrentPlayingUrl);
        return mCurrentPlayingUrl;
    }

    /**
     * 设置当前播放媒体资源URL
     *
     * @param mCurrentPlayingUrl
     */
    public void setmCurrentPlayingUrl(String mCurrentPlayingUrl) {
        this.mCurrentPlayingUrl = mCurrentPlayingUrl;
    }

    /**
     * 获取播放模式
     *
     * @return
     */
    public int getmPlayMode() {
        return mPlayMode;
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    public void setmPlayMode(int playmode) {
        this.mPlayMode = playmode;
    }

    /**
     * 添加列表
     *
     * @param infos
     */
    public void addMusicInfos(List<MusicInfo> infos) {
        if (null != infos) {
            synchronized (mMusicInfoList) {
                mMusicInfoList.clear();
                mMusicInfoList.addAll(infos);
            }
        }
    }

    /**
     * 获取列表
     *
     * @return
     */
    public List<MusicInfo> getMusicInfos() {
        synchronized (mMusicInfoList) {
            if (mMusicInfoList == null) {
                mMusicInfoList = new ArrayList<MusicInfo>();
            }
            return mMusicInfoList;
        }
    }

    /**
     * 准备播放资源下标
     */
    public boolean prepare() {
        if (mMusicInfoList.isEmpty()) {
            Logutil.i(TAG, "prepare() fail !!!");
            return false;
        }
        if (mPlayingIndex == NO_POSITION) {
            mPlayingIndex = 0;
        }
        Logutil.i(TAG, "prepare() suc !!!");
        return true;
    }

    /**
     * 获取当前播放歌曲下标
     *
     * @return
     */
    public int getPlayingIndex() {
        return mPlayingIndex;
    }

    /**
     * 设置当前播放歌曲下标
     *
     * @param playingIndex
     */
    public void setPlayingIndex(int playingIndex) {
        this.mPlayingIndex = playingIndex;
    }

    /**
     * 获得当前播放资源信息 {@link #mPlayingIndex}
     */
    public MusicInfo getCurrentVideoInfo() {
        synchronized (mMusicInfoList) {
            if (mPlayingIndex != NO_POSITION) {
                return mMusicInfoList.get(mPlayingIndex);
            }
            return null;
        }
    }

    /**
     * 获取上一个节目信息
     *
     * @return
     */
    public MusicInfo prev() {
        synchronized (mMusicInfoList) {
            switch (mPlayMode) {
                case PlayMode.LOOP:
                    loopPrevious();
                    break;
                case PlayMode.SHUFFLE:
                    mPlayingIndex = randomPlayIndex();
                    break;
                case PlayMode.SINGLE:
                    if (mStopSinglePlay){
                       loopPrevious();
                    }
                    break;
            }
            return this.mMusicInfoList.get(mPlayingIndex);
        }
    }

    /**
     * 是否有媒体数据
     *
     * @return
     */
    public boolean hasData() {
        return this.mMusicInfoList != null && this.mMusicInfoList.size() > 0;
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
                canNext = (this.mMusicInfoList != null && this.mMusicInfoList.size() > 0);
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
    public MusicInfo next() {
        synchronized (mMusicInfoList) {
            switch (mPlayMode) {

                case PlayMode.LOOP:
                    loopNext();
                    break;
                case PlayMode.SHUFFLE:
                    mPlayingIndex = randomPlayIndex();
                    break;
                case PlayMode.SINGLE:
                    if (mStopSinglePlay){
                        loopNext();
                    }
                    break;
            }
            return this.mMusicInfoList.get(mPlayingIndex);
        }
    }

    /**
     * 获取随机下标
     *
     * @return
     */
    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(this.mMusicInfoList.size());
        if (this.mMusicInfoList.size() > 1 && randomIndex == this.mPlayingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }

    /**
     * 播放列表总长度
     *
     * @return
     */
    public int getSize() {
        return (null != mMusicInfoList ? mMusicInfoList.size() : 0);
    }

    public void onDestory() {
        synchronized (mMusicInfoList) {
            this.mMusicInfoList.clear();
            this.mPlayingIndex = NO_POSITION;
            this.mCurrentPlayingUrl = null;
            this.mCurrentID3Info = null;
            removeDamageMediaList();
        }
    }
    /**
     * 返回指定Index歌曲的path
     */
    public String getIndexPaht(int index) {
        if (index > -1 && index <= mMusicInfoList.size()) {
            return mMusicInfoList.get(index).getUrl();
        }
        return null;
    }

    private void loopNext(){
        int newIndex = mPlayingIndex + 1;
        if (newIndex >= this.mMusicInfoList.size()) {
            newIndex = 0;
        }
        mPlayingIndex = newIndex;
    }
    private void loopPrevious(){
        int prevIndex = mPlayingIndex - 1;
        if (prevIndex < 0) {
            prevIndex = this.mMusicInfoList.size() - 1;
        }
        mPlayingIndex = prevIndex;
    }

}
