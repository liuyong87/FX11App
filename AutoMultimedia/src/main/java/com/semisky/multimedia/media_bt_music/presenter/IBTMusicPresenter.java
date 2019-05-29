package com.semisky.multimedia.media_bt_music.presenter;

public interface IBTMusicPresenter {

    /**
     * 检查蓝牙音乐连接
     */
    void checkBtMusicConnect();

    /**
     * 请求蓝牙音乐连接
     */
    void reqBtMusicConnect();


    void onViewResume();
    void onViewStop();

    /**
     * 销毁资源
     */
    void destory();

}
