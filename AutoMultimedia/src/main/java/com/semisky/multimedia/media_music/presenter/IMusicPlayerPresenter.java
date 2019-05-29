package com.semisky.multimedia.media_music.presenter;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by Anter on 2018/8/3.
 */

public interface IMusicPlayerPresenter {
    /**
     * 获取进度监听接口
     *
     * @return
     */
    OnSeekBarChangeListener getOnSeekBarChangeListener();

    /**
     * 切换收藏
     */
    void onSwitchFavorite();

    /**
     * 快进
     */
    void onFastForwardProgram();

    /**
     * 停止快进
     */
    void onStopFastForwardProgram();


    /**
     * 快退
     */
    void onFastBackwardProgram();

    /**
     * 停止快退
     */
    void onStopFastBackwardProgram();

    /**
     * 切换播放模式
     */
    void onSwitchPlayMode();

    /**
     * 切换播放与暂停
     */
    void onSwitchPlayOrPause();

    /**
     * 上一个节目
     */
    void onPrevProgram();

    /**
     * 下一个节目
     */
    void onNextProgram();

    /**
     * 进入列表
     */
    void onEnterList();

    /**
     * 处理意图
     *
     * @param intent
     */
    void onHandlerIntent(Intent intent);

    /**
     * 绑定音乐服务
     */
    void bindService();

    /**
     * 解绑音乐服务
     */
    void unbindService();

    /**
     * 设置应用标题到状态栏
     */
    void setTitleToStatusBar(String clz, String title, int state);

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
     * onResume 状态 申请音频焦点
     */
    void onResumeRequestAudio();

    /**
     * 音效设置
     */
    void onChangeSoundSetting();

    /**
     * 加载数据
     */
    void loadData();

    /**
     * 更新数据
     */
    void refreshData();

    /**
     * 获取音乐数据
     */
    List<MusicInfo> getMusicData();

    /**
     * 正在播放列表选取一项播放
     */
    void onPlayingListUrl(String url);

    /**
     * 根据专辑图播放指定的音乐
     */
    void onPlayPosition(int position);

    /**
     * 音效设置
     */
    void setSound();

    /**
     * 隐藏显示播放按钮
     */
    void playButtonGoneAndVisible(int state);

    /**
     * 请求显示歌词
     */
    void reqShowLrc();
}
