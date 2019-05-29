package com.semisky.multimedia.media_music.service;

import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;

/**
 * 本地音乐服务模板接口
 * Created by LiuYong on 2018/8/22.
 */

public interface ILocalMusicService {

    /**
     * 注册曲目改变监听接口
     **/
    void registerCallback(IProxyProgramChangeCallback callback);

    /**
     * 反注册曲目改变监听接口
     **/
    void unregisterCallback(IProxyProgramChangeCallback callback);

    /**
     * 获取歌曲名
     **/
    String getSongName();

    /**
     * 获取歌手名
     **/
    String getArtistName();

    /**
     * 获取专辑名
     **/
    String getAlbumName();

    /**
     * 获取媒体总进度
     **/
    int getTotalPorgress();

    /**
     * 获取媒体当前进度
     **/
    int getCurrentProgress();

    /**
     * 获取当前播放曲目位置
     **/
    String getCurProgramPos();

    /**
     * 是否收藏
     **/
    boolean isFavorite();

    /**
     * 获取播放模式
     **/
    int getPlayMode();

    /**
     * 是否播放
     **/
    boolean isPlaying();
    /*----------- play control -----------*/

    /**
     * 切换下一曲目
     **/
    void onSwitchNext();

    /**
     * 切换上一曲目
     **/
    void onSwitchPrev();

    /**
     * 切换快进
     **/
    void onSwitchFastForward();

    /**
     * 停止快进
     **/
    void onSwitchStopFastForward();

    /**
     * 切换快退
     **/
    void onSwitchFastBackward();

    /**
     * 停止快退
     **/
    void onSwitchStopFastBackward();

    /**
     * 切换播放或暂停
     **/
    void onSwitchPlayOrPause();

    /**
     * 媒体音乐播放
     */
    void start();

    /**
     * 媒体音乐暂停
     */
    void pause();

    /**
     * 恢复播放操作（前置条件：之前是播放状态）
     **/
    void onSwitchResumePlay();

    /**
     * 切换播放模式
     **/
    void onSwitchPlayMode();

    /**
     * 切换收藏
     **/
    void onSwitchFavorite();

    /**
     * 设置播放器进度
     **/
    void onSeekTo(int progress);

    /**
     * 设置更新进度线程使能状态
     *
     * @param enabled true:开启 ;false:关闭
     **/
    void onUpdateProgressWithThreadEnabled(boolean enabled);

    /**
     * 删除收藏歌曲（来自收藏列表收藏，需同时通知到播放界面）
     */
    void onDeleteFromFavorite();

    /**
     * 申请音频焦点
     */
    void RequestAudio();

    /**
     * 停止快进快退线程
     */
    void stopFastBackOrFastF(boolean isH);

    /**
     * 获取当前歌词URL
     *
     * @return
     */
    String getCurrentLyricUrl();

}
