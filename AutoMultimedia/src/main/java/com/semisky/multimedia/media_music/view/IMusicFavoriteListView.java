package com.semisky.multimedia.media_music.view;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IMusicFavoriteListView<E> {

    void onRefreshData(List<E> dataList);
    /**
     * 当集合空数据时警示提示
     *
     * @param enable
     */
    void onAlertEmptyListTextVisible(boolean enable);

    void onRefreshCancelFavoriteItemView();

}
