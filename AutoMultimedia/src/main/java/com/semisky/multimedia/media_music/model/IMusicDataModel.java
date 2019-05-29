package com.semisky.multimedia.media_music.model;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/8.
 */

public interface IMusicDataModel {
    void onLoadMusicInfoList(OnLoadDataListener listener,int usbFlag);


    void registerOnRefreshDataListener(OnRefreshDataListener listener);

    void unregisterOnRefreshDataListener();

    void onLoadMusicFolder(OnLoadDataListener loadDataListener,int usbFlag,String path);





    interface OnLoadDataListener<E> {
        void onLoadData(List<E> dataList);
    }

    interface OnRefreshDataListener {
        void onUpdateData(List<MusicInfo> dataList,boolean isScanEnd);
    }

    /**

    /**
     * 获取是否扫描完成
     */
    boolean isMediaScanFinished();
    /**
     * 删除所有音乐
     */
    void deleteAllMusic(int usbFlag);

}
