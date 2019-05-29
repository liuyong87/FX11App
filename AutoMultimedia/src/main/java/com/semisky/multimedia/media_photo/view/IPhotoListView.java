package com.semisky.multimedia.media_photo.view;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IPhotoListView<E> {


    /**
     * 刷新媒体数据集合
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
