package com.semisky.multimedia.media_folder.model;

import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.common.interfaces.OnLoadDataListener;
import com.semisky.multimedia.common.interfaces.OnRefreshDataListener;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;

import java.util.List;

/**
 * 文件夹数据模型
 * Created by LiuYong on 2018/8/29.
 */

public class FolderDataModel implements IFolderDataModel {
    private MediaStorageAccessProxyModel mMediaStorageAccessProxyModel;

    public FolderDataModel() {
        mMediaStorageAccessProxyModel = MediaStorageAccessProxyModel.getInstance();
    }

    @Override
    public void onLoadData(final OnLoadDataListener<FolderInfo> l, final String dir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != l) {
                    l.onLoadData(querySpecifyDirectoryUnder(dir));
                }
            }
        }).start();
    }

    @Override
    public void onRefreshData(final OnRefreshDataListener<FolderInfo> l, final String dir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != l) {
                    l.onUpdateData(querySpecifyDirectoryUnder(dir));
                }
            }
        }).start();
    }

    private List<FolderInfo> querySpecifyDirectoryUnder(String dir) {
        return mMediaStorageAccessProxyModel.querySpecifyDirectoryUnder(dir);
    }
}
