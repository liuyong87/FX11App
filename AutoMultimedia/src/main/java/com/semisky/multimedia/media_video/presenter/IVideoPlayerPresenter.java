package com.semisky.multimedia.media_video.presenter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Created by Anter on 2018/8/7.
 */

public interface IVideoPlayerPresenter {
    /**
     * 获取媒体进度设置完成监听接口
     *
     * @return
     */
    OnSeekCompleteListener getOnSeekCompleteListener();

    /**
     * 获取媒体准备完成监听接口
     *
     * @return
     */
    OnPreparedListener getOnPreparedListener();

    /**
     * 获取媒体播放信息监听接口
     *
     * @return
     */
    OnInfoListener getOnInfoListener();

    /**
     * 获取媒体播放完成监听接口
     *
     * @return
     */
    OnCompletionListener getOnCompletionListener();

    /**
     * 获取媒体播放异常监听接口
     *
     * @return
     */
    OnErrorListener getOnErrorListener();

    /**
     * 获取播放进度条监听接口
     *
     * @return
     */
    OnSeekBarChangeListener getOnSeekBarChangeListener();

    /**
     * 加载媒体数据
     */
    void onLoadData();

    /**
     * 恢复媒体断点播放
     */
    void onRestorePlayVideo();

    /**
     * 上一个节目
     */
    void onPrevProgram();

    /**
     * 下一个节目
     */
    void onNextProgram();

    /**
     * 节目快退
     */
    void onFastBackward();

    /**
     * 停止节目快退
     */
    void onStopFastBackward();

    /**
     * 节目快进
     */
    void onFastForward();

    /**
     * 停止节目快进
     */
    void onStopFastForward();

    /**
     * 节目播放或暂停
     */
    void onSwitchPlayOrPause();

    /**
     * 进入媒体列表
     */
    void onEnterList();

    /**
     * 处理意图
     *
     * @param intent 意图
     */
    void onHandlerIntent(Intent intent);

    /**
     * 保存断点记忆媒体资源
     */
    void onSaveLastMediaInfos();

    /**
     * 暂停进度更新
     */
    void onPauseUpdateProgress();

    /**
     * 注册音频焦点变化监听
     */
    void registerAudioFocusChange();

    /**
     * 反注册音频焦点变化监听
     */
    void unRegisterAudioFocusChange();

    /**
     * 申请音频焦点
     */
    void onRequestAudioFocus();

    /**
     * 注销音频焦点
     */
    void onAbandonAudioFocus();

    /**
     * 切换屏幕模式
     */
    void onSwitchScreentMode();

    /**
     * 禁止按键事件状态
     *
     * @param enable
     */
    void onStopKeyEventEnable(boolean enable);

    /**
     * 手刹状态改变
     *
     * @param isDriving
     */
    void onHandBrakeChange(boolean isDriving);

    /**
     * 禁止手刹事件状态
     *
     * @param enable
     */
    void onStopHandBrakeEventEnable(boolean enable);

    /**
     * 设置应用标题到状态栏
     */
    void setTitleToStatusBar(String clz, String title);

    /**
     * 当下一个节目时，禁止下一个按键控件的触摸事件
     *
     * @param enable
     */
    void setStopTouchEventEnableWhenNextProgram(boolean enable);

    /**
     * 获取是否禁止下一个按键控件的触摸事件状态
     *
     * @return
     */
    boolean getStopTouchEventEnableWhenNextProgram();

    /**
     * 当上一个节目时，禁止上一个按键控件的触摸事件
     *
     * @param enable
     */
    void setStopTouchEventEnableWhenPrevProgram(boolean enable);

    /**
     * 获取是否禁止上一个按键控件的触摸事件状态
     *
     * @return
     */
    boolean getStopTouchEventEnableWhenPrevProgram();
    /**
     * 视频警告界面的返回key
     */
    void btnBack();
    /**
     * 暂停播放
     */
    void stopPlayVideo();
    /**
     * 按 back键时延迟退出视频界面。频繁的操作可能造成back键无效
     */
    void finishDelayed();
    /**
     * 停止快进或者快退线程，但不恢复播放
     */
    void stopFastOrBackTask(boolean isH);

    void removeFullScreenModeTimeoutRunnable();



}
