package com.semisky.multimedia.media_photo.presenter;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IPhotoListPresenter {

    void setUsbFlag(int usbFlag);

    int getUsbFlag();

    /**
     * 加载媒体图片数据
     */
    void onLoadData();

    /**
     * 列表播放
     *
     * @param url
     */
    void onListPlay(String url);
    /**
     * 设置title
     */
    void setTitleToUI(String name,String title);
}
