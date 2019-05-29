package com.semisky.multimedia.media_photo.presenter;

import android.content.Intent;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;

import com.semisky.multimedia.common.interfaces.IBackModeChange;

/**
 * Created by Anter on 2018/8/4.
 */

public interface IPhotoPlayerPresenter {

    void setUsbFlag(int usbFlag);
    int getUsbFlag();
    /**
     * 获取ViewPage监听接口
     *
     * @return
     */
    OnPageChangeListener getOnPageChangeListener();
    /**
     * 获取ViewPage监听接口 onTouch事件
     */
    View.OnTouchListener getOnTouchListener();

    /**
     * 处理意图
     *
     * @param intent
     */
    void onHandlerIntent(Intent intent);

    /**
     * 加载数据
     */
    void onLoadData();

    /**
     * 上一个图片
     */
    void onPrevProgram();

    /**
     * 下一个图片
     */
    void onNextProgram();

    /**
     * 图片播放或暂停
     */
    void onSwitchPlayOrPause();

    /**
     * 图片暂停播放
     */
    void onPhotoPause();

    /**
     * 旋转图片
     */
    void onRotate();

    /**
     * 自动播放图片
     */
    void onAutoPlayPhoto();

    /**
     * 进入列表界面
     */
    void onEnterList();

    /**
     * 切换屏幕显示模式
     */
    void onSwitchScreentMode();

    /**
     * 复位当前图片
     */
    void onResetCurrentPhoto();

    /**
     * 播放列表图片
     *
     * @param url
     */
    void onPlayListPhoto(String url);

    /**
     * 设置应用标题到状态栏
     */
    void setTitleToStatusBar(String clz, String title);

    /**
     * 当正常屏点击底部播放控制导航任意控件，清除超时全屏消息重新计时
     */
    void onReTimingWhenNormalScreentTouchPlayWidget();

    /**
     * 当图处于播放状态，接收到车事件后图片是否暂停处理
     *
     */
    IBackModeChange getIBackModeChanged();
    /**
     * 手动暂停
     */
    void touchPausePlay();



}
