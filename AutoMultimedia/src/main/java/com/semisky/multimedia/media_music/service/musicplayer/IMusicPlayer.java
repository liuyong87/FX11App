package com.semisky.multimedia.media_music.service.musicplayer;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;

import com.semisky.multimedia.media_music.LrcView.LrcEntity;

import java.util.List;

/**
 * 音乐媒体播放器模板接口
 * Created by LiuYong on 2018/8/22.
 */

public interface IMusicPlayer<E> {

    /**
     * 媒体播放状态监听接口
     */
    interface OnMediaPlayStateListener {
        void onChangePlayState(boolean isPlay);
    }

    interface OnMediaInfoChangerListener<E> {
        void onChangeMediaInfo(E info);
    }

    /**
     * 媒体进度改变监听接口定义
     */
    interface OnProgressChangeListener {
        void onChangeProgress(int progress);
    }

    /**
     * 播放曲目改变接口定义
     */
    interface OnPlayProgramChangeListener {
        void onChangePlayProgram(String pos);
    }

    /**
     * 设置指定进度完成监听
     *
     * @param l
     */
    void setmOnSeekCompleteListener(OnSeekCompleteListener l);

    /**
     * 播放曲目改变
     *
     * @param l
     */
    void setOnPlayProgramChangeListener(OnPlayProgramChangeListener l);

    /**
     * 注册播放状态监听
     *
     * @param l
     */
    void setOnMediaPlayStateListener(OnMediaPlayStateListener l);

    void setOnMediaInfoChangerListener(OnMediaInfoChangerListener l);

    /**
     * 注册媒体进度改变监听
     *
     * @param l
     */
    void setOnProgressChangeListener(OnProgressChangeListener l);

    /**
     * 注册媒体准备完成监听
     *
     * @param l
     */
    void setOnPreparedListener(OnPreparedListener l);

    /**
     * 注册媒体资源播放异常监听
     *
     * @param l
     */
    void setOnErrorListener(OnErrorListener l);

    /**
     * 注册媒体播放完成监听
     *
     * @param l
     */
    void setOnCompletionListener(OnCompletionListener l);

    /**
     * 设置播放列表
     *
     * @param playList
     */
    void setPlayList(List<E> playList);

    /**
     * 是否有媒体数据
     *
     * @return
     */
    boolean hasData();

    /**
     * 媒体是否准备完成
     *
     * @return
     */
    boolean isPrepared();

    /**
     * 播放指定位置媒体
     *
     * @param msc
     */
    IMusicPlayer seekTo(int msc);

    /**
     * 设置自动播放标识
     *
     * @param isAutoPlay
     */
    IMusicPlayer setAutoPlay(boolean isAutoPlay);

    /**
     * 设置音乐资源路径
     *
     * @param url
     * @return
     */
    IMusicPlayer setMusicPath(String url);

    /**
     * 准备播放
     *
     * @return
     */
    IMusicPlayer onPreparePlay();

    /**
     * 获取媒体ID3信息
     *
     * @return
     */
    E getCurrentID3Info();

    /**
     * 获取总进度
     *
     * @return
     */
    int getDuration();

    /**
     * 获取当前进度
     *
     * @return
     */
    int getCurrentProgress();

    /**
     * 获取播放状态
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 获取当前歌曲序号（包括当前歌曲序号与总歌曲数量）
     *
     * @return
     */
    String getmNumOfCurrentAndTotalProgram();

    // play control

    /**
     * 下一曲
     */
    void next();

    /**
     * 停止下一曲
     */
    void stopNext();

    /**
     * 上一曲
     */
    void prev();

    /**
     * 停止上一曲
     */
    void stopPrev();

    /**
     * 快退
     */
    void fastBackward();

    /**
     * 停止快退
     */
    void stopFastBackward(boolean fromAudioFocusLossEvent);

    /**
     * 快进
     */
    void fastForward();

    /**
     * 停止快进
     */
    void stopFastForward(boolean fromAudioFocusLossEvent);

    /**
     * 播放或暂停
     */
    void playOrPause();

    /**
     * 保存断点记忆
     */
    void saveLastMediaInfo();

    /**
     * 停止播放器
     */
    void stop();

    /**
     * 暂停播放
     *
     * @return
     */
    boolean pause();

    /**
     * 开始播放
     *
     * @return
     */
    boolean start();

    /**
     * 设置播放模式
     *
     * @param playMode
     */
    void setPlayMode(int playMode);

    /**
     * 获取当前播放媒体资源
     *
     * @return
     */
    String getCurrentPlayingUrl();

    /**
     * 清除播放列表
     */
    void removePlayList();

    /**
     * 设置更新进度线程使能状态
     **/
    void onUpdateProgressWithThreadEnabled(boolean enabled);

    /**
     * 是否mute声音
     *
     * @param enable
     */
    void onMuteVolumeEnable(boolean enable);

    /**
     * 设置当前歌曲收藏状态
     *
     * @param isFavorite
     */
    void setFavoriteStatusWithCurrentMusic(boolean isFavorite);

    /**
     * 当前歌曲是否已收藏
     *
     * @return
     */
    boolean isFavoriteWithCurrentMusic();

    /**
     * 释放相关资源
     */
    void onRelease();

    /**
     * 根据index 播放指定的歌曲
     */
    String playIndexMusic(int index);

    /**
     * 清空资源
     */
    void clean();

    /**
     * 设置音频焦点状态
     */
    void setAudioFocusState(boolean audioFocusState);

    /**
     * 设置是否允许播放
     */
    void setIsAllowPlaying(boolean isAllowPlaying);

    /**
     * 停止快进线程，不恢复播放
     */
    void stopFastRunnable();

    /**
     * 获取播放下标 （中间件使用）
     */
    int getPlayIndex();

    /**
     * 当前USB源标识
     *
     * @return
     */
    int getCurUsbSourceFlag();


}
