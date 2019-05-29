package com.semisky.multimedia.media_photo.model;

import com.semisky.multimedia.aidl.photo.PhotoInfo;

import java.util.List;

/**
 * Created by Anter on 2018/8/4.
 */

public interface IPhotoDataModel {
    void setUsbFlag(int usbFlag);
    void onLoadPhotoInfoList(int usbFlag,OnLoadDataListener listener);

    void registerOnRefreshDataListener(OnRefreshDataListener listener);

    void unregisterOnRefreshDataListener();


    interface OnLoadDataListener<E> {
        void onLoadData(List<E> photoInfoList);
    }

    interface OnRefreshDataListener {
        void onUpdateData(List<PhotoInfo> dataList);
    }

}
