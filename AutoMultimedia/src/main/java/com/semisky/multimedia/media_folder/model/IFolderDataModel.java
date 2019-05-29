package com.semisky.multimedia.media_folder.model;

import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.common.interfaces.OnLoadDataListener;
import com.semisky.multimedia.common.interfaces.OnRefreshDataListener;

/**
 * Created by LiuYong on 2018/8/29.
 */

public interface IFolderDataModel {

    /**
     * 加载媒体文件夹数据
     *
     * @param l
     * @param dir
     */
    void onLoadData(OnLoadDataListener<FolderInfo> l, String dir);

    /**
     * 刷新媒体文件夹数据
     *
     * @param l
     * @param dir
     */
    void onRefreshData(OnRefreshDataListener<FolderInfo> l, String dir);

}
