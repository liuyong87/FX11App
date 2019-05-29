package com.semisky.multimedia.media_photo.view;

import android.app.Activity;

import com.semisky.multimedia.aidl.photo.PhotoInfo;

import java.util.List;

/**
 * Created by Anter on 2018/8/4.
 */

public interface IPhotoPlayerView<E> {
    /**
     * 上下文
     *
     * @return
     */
    Activity getContext();

    /**
     * 加载媒体数据
     */
    void onLoadData();

    /**
     * 刷新媒体数据列表
     *
     * @param list
     */

    void onRefreshPhotoList(List<E> list);

    /**
     * 当前播放图片条目位置
     *
     * @return
     */
    int getCurrentItem();

    /**
     * 显示指定图片
     *
     * @param pos
     */

    void onShowSpecifyPhoto(int pos);

    /**
     * 显示单条信息
     */
    void onShowSingleMessage(int resId);

    /**
     * 屏幕显示模式改变
     *
     * @param enable
     */
    void onScreenShowModeChange(boolean enable);

    /**
     * 旋转当前图片
     *
     * @param curItem
     */
    void onChangeRotatePhoto(int curItem);

    /**
     * 复位当前图片
     */
    void onResetCurrentPhoto(int curPosition);

    /**
     * UI播放状态更新
     *
     * @param isPlay
     */
    void onChangeSwitchView(boolean isPlay);

    /**
     * 获取“列表结束，回到第一张”字符
     *
     * @return
     */
    int getResIdByChangeFirstPhotoText();

    /**
     * 获取“列表结束，回到最后一张”字符
     *
     * @return
     */
    int getResIdByChangeLastPhotoText();

    /**
     * 获取是否全屏状态显示
     */
    int getIsFullShow();

    /**
     * 图片名字改变
     *
     * @param photoName
     */
    void onChangePhotoNameText(String photoName);

}
