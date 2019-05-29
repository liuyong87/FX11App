package com.semisky.multimedia.media_video.view;

import java.util.List;

/**
 * 视频列表视图接口
 * Created by LiuYong on 2018/8/9.
 */

public interface IVideoListView<E> {
    /**
     * 首次刷新数据
     *
     * @param dataList
     */
    void onFirstRefreshData(List<E> dataList);

    /**
     * 动态刷新数据
     *
     * @param dataList
     */
    void onRefreshData(List<E> dataList);

    /**
     * 当集合空数据时警示提示
     *
     * @param enable
     */
    void onAlertEmptyListTextVisible(boolean enable);
}
