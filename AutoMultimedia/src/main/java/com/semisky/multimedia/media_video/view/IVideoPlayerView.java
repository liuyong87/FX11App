package com.semisky.multimedia.media_video.view;

import android.app.Activity;

/**
 * Created by Anter on 2018/8/7.
 */

public interface IVideoPlayerView {

    /**
     * 播放媒体资源
     *
     * @param path       资源路径
     * @param progress   进度
     * @param isAutoPlay 是否自动播放
     */
    void onPlayVideo(String path, int progress, boolean isAutoPlay);

    void onChangeSeekbarMaxProgress(int progress);

    /**
     * 当前播放时间进度
     *
     * @param curTime
     */
    void onShowProgramCurrentTime(String curTime);

    /**
     * 总播放时间进度
     *
     * @param totalTime
     */
    void onShowProgramTotalTime(String totalTime);

    /**
     * 更新播放进度
     *
     * @param progress
     */
    void onShowProgramProgress(int progress);

    /**
     * 显示节目名字
     *
     * @param programName
     */
    void onShowProgramName(String programName);

    /**
     * 切换过渡黑色视图（防止切换节目时屏闪问题）
     *
     * @param enable true:显示视图 ,false:隐藏视图
     */
    void onSwitchTransitionBlackView(boolean enable);

    /**
     * 切换观看视频警示视频
     *
     * @param enable true:显示视图 ,false:隐藏视图
     */
    void onSwitchWatchVideoWarningView(boolean enable);

    /**
     * 视频异常时的警示信息视图
     *
     * @param enable true:显示视图 ,false:隐藏视图
     */
    void onSwitchPlayVideoExceptionWarningView(boolean enable);

    /**
     * 更新播放开关状态视图
     *
     * @param enable
     */
    void onChangePlaySwitchStateView(boolean enable);

    /**
     * 屏幕显示模式改变
     *
     * @param isFullScreen
     */
    void onChangeScreenMode(boolean isFullScreen);

    /**
     * 是否正在播放
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 是否准备完成
     *
     * @return
     */
    boolean isPrepared();

    /**
     * 暂停视频
     */
    void onPauseVideo();

    /**
     * 播放视频
     */
    void onStartVideo();

    /**
     * 媒体总进度
     *
     * @return
     */
    int getDuration();

    /**
     * 媒体当前进度
     *
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取当前播放的媒体URI
     *
     * @return 媒体URI
     */
    String getVideoPath();

    /**
     * 设置指定位置媒体
     *
     * @param progress 进度
     */
    void onSeekTo(int progress);

    /**
     * 设置静音状态
     *
     * @param isMute
     */
    void onMuteVolumeEnable(boolean isMute);

    /**
     * 获取上下文
     *
     * @return
     */
    Activity getContext();
    /**
     * 手动暂停 ，播放状态
     */
    void setIsNeedPlay(boolean isNeedPlay);

    /**
     * 关闭视频异常显示的dialog
     */
    void closeDialog();
    /**
     * 按back 退出视频界面
     */
    void backKey();
    /**
     * 设置标记位是否存在音频焦点
     */
    void setIsHasAudioFocus(boolean isHasAudioFocus);


}
