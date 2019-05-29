package com.semisky.multimedia.common.interfaces;

import java.util.List;

/**
 * 媒体数据动态刷新监听接口
 * Created by LiuYong on 2018/8/29.
 */

public interface  OnRefreshDataListener<E> {
    void onUpdateData(List<E> dataList);
}
