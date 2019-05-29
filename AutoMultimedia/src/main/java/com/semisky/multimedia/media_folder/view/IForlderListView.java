package com.semisky.multimedia.media_folder.view;

import android.app.Activity;
import android.content.Context;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IForlderListView<E> {

    void onRefreshData(List<E> list);
    /**
     * 当集合空数据时警示提示
     *
     * @param enable
     */
    void onAlertEmptyListTextVisible(boolean enable);

    Activity getmActivity();
}
