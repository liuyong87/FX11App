package com.semisky.multimedia.media_video.presenter;

import android.content.Intent;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IVideoListPresenter {
    /**
     * 加载媒体数据集合
     */
    void onLoadData();

    /**
     * 列表播放
     *
     * @param url 媒体资源路径
     */
    void onPlayList(String url);

    /**
     * 设置title
     */
    void setTitleToUI(String name,String title);

    void setUsbFlag(int usbFlag);

    int getUsbFlag();

}
