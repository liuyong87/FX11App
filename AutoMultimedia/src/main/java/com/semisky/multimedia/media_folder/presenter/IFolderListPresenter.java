package com.semisky.multimedia.media_folder.presenter;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IFolderListPresenter {

    void onLoadData();

    /**
     * 打开目录
     *
     * @param dir
     */
    void onOpenDir(String dir);

    /**
     * 列表播放
     *
     * @param url 媒体资源路径
     */
    void onPlayList(int appFlag, String url);
    /**
     * 设置title
     */
    void setTitleToUI(String name,String title);
}
