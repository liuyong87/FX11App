package com.semisky.multimedia.media_music.presenter;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IMusicListPresenter {
    // 列表状态
    int LIST_STATE_USB_UNMOUNTED = 1;// 列表状态：U盘未挂载
    int LIST_STATE_USB_SCANNING = 2;// 列表状态：正在扫描媒体
    int LIST_STATE_NO_DATA = 3;// 列表状态：无媒体数据
    int LIST_STATE_HAVE_DATA = 4;// 列表状态：有媒体数据

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
    /**
     * 列表抬头信息隐藏与显示
     *
     */
    void onMusicInfoChange(int change);

    /**
     * 文件，专辑，歌手 抬头信息
     */
    void onMusicOtherInfo(int change);

    void setAllMusicInfo();

    void setArtistInfo();

    void setAlbumInfo();

    void setFolder();


}
