package com.semisky.multimedia.media_music.presenter;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IMusicFavoriteListPresenter {
    void onLoadData();

    /**
     * 列表播放
     *
     * @param url
     */
    void onListPlay(String url);

    /**
     * 取消收藏资源URL
     *
     * @param url
     */
    void onCancelFavoriteWith(String url);

}
