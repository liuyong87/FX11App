package com.semisky.multimedia.common.interfaces;

import java.util.List;

/**
 * 数据加载监听接口
 * Created by LiuYong on 2018/8/29.
 */

public interface OnLoadDataListener<E> {
    void onLoadData(List<E> dataList);
}
