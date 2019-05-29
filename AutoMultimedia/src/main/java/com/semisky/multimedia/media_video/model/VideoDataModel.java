package com.semisky.multimedia.media_video.model;

import android.os.RemoteException;

import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel.OnServiceConnectionCompletedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/7.
 */

public class VideoDataModel implements IVideoDataModel {
    private MediaStorageAccessProxyModel mMediaStorageAccessProxyModel;
    private OnRefreshDataListener mOnRefreshDataListener;
    private int mUsbFlag = 0;

    public VideoDataModel(){
        mMediaStorageAccessProxyModel = MediaStorageAccessProxyModel.getInstance();
        mMediaStorageAccessProxyModel.registerOnMediaScannerStateListener(mIMediaScannerStateListener);
        mMediaStorageAccessProxyModel.registerOnServiceConnectionCompletedListener(mOnServiceConnectionCompletedListener);
    }

    @Override
    public void setUsbFlag(int usbFlag) {
        this.mUsbFlag = usbFlag;
    }

    @Override
    public void onLoadVideoInfoList(final OnLoadDataListener listener,final int usbFlag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoInfo> videoInfos = mMediaStorageAccessProxyModel.queryAllVideos(usbFlag);
                if(null != listener){
                    listener.onLoadData(videoInfos);
                }
            }
        }).start();
    }

    @Override
    public void registerOnRefreshDataListener(OnRefreshDataListener listener) {
        this.mOnRefreshDataListener = listener;
    }

    @Override
    public void unregisterOnRefreshDataListener() {
        this.mOnRefreshDataListener = null;
        mMediaStorageAccessProxyModel.unregisterOnMediaScannerStateListener(mIMediaScannerStateListener);
        mMediaStorageAccessProxyModel.unregisterOnServiceConnectionCompletedListener();
    }


    private IMediaScannerStateListener.Stub mIMediaScannerStateListener = new IMediaScannerStateListener.Stub() {
        @Override
        public void onScannerStart(int usbFlag) throws RemoteException {

        }

        @Override
        public void onScanning(int usbFlag) throws RemoteException {
            refreshData();
        }

        @Override
        public void onScannerStoped(int usbFlag) throws RemoteException {

        }

        @Override
        public void onScannerDone(int usbFlag) throws RemoteException {
            refreshData();
        }

        @Override
        public void onUsbMountStateOne() throws RemoteException {

        }

        @Override
        public void onUsbMountStateUsbTwo() throws RemoteException {

        }

        @Override
        public void onUsbUnMountStateOne() throws RemoteException {

        }

        @Override
        public void onUsbUnMountStateTwo() throws RemoteException {

        }
    };

    private OnServiceConnectionCompletedListener mOnServiceConnectionCompletedListener = new OnServiceConnectionCompletedListener(){
        @Override
        public void onServiceConnectionCompleted() {
            refreshData();
        }
    };


    private void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoInfo> videoList = mMediaStorageAccessProxyModel.queryAllVideos(mUsbFlag);
                if(null != mOnRefreshDataListener){
                    mOnRefreshDataListener.onUpdateData(videoList);
                }
            }
        }).start();
    }


}
