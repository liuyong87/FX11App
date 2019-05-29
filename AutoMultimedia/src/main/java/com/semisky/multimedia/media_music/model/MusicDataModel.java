package com.semisky.multimedia.media_music.model;


import android.os.RemoteException;
import android.util.Log;

import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.presenter.IMusicListPresenter;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel.OnServiceConnectionCompletedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/8.
 */

public class MusicDataModel implements IMusicDataModel {
    private static final String TAG = Logutil.makeTagLog(MusicDataModel.class);
    private MediaStorageAccessProxyModel mediaStorageAccessProxyModel;
    private OnRefreshDataListener mOnRefreshDataListener;
    private int usbFlag_ = 0;

    public MusicDataModel(int usbFlag) {
        this.usbFlag_ = usbFlag;
        mediaStorageAccessProxyModel = MediaStorageAccessProxyModel.getInstance();
        mediaStorageAccessProxyModel.registerOnMediaScannerStateListener(mIMediaScannerStateListener);
        mediaStorageAccessProxyModel.registerOnServiceConnectionCompletedListener(mOnServiceConnectionCompletedListener);
    }

    @Override
    public void registerOnRefreshDataListener(OnRefreshDataListener listener) {
        this.mOnRefreshDataListener = listener;
    }

    @Override
    public void unregisterOnRefreshDataListener() {
        this.mOnRefreshDataListener = null;
        mediaStorageAccessProxyModel.unregisterOnMediaScannerStateListener(mIMediaScannerStateListener);
        mediaStorageAccessProxyModel.unregisterOnServiceConnectionCompletedListener();
    }

    @Override
    public void onLoadMusicFolder(OnLoadDataListener loadDataListener, final int usbFlag, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<FolderInfo> folderInfos = mediaStorageAccessProxyModel.queryAllMusicFolder(usbFlag,path);
            }
        }).start();
    }

    @Override
    public void onLoadMusicInfoList(final OnLoadDataListener listener, final int usbFlag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musicInfos = mediaStorageAccessProxyModel.queryAllMusics(usbFlag);

                if (null != listener) {
                    listener.onLoadData(musicInfos);
                }
            }
        }).start();
    }


    private IMediaScannerStateListener.Stub mIMediaScannerStateListener = new IMediaScannerStateListener.Stub() {
        @Override
        public void onScannerStart(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScannerStart() ...");

        }

        @Override
        public void onScanning(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScanning() ...");
            if (usbFlag == usbFlag_) {
                refreshData(usbFlag, false);
            }
        }

        @Override
        public void onScannerStoped(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScannerStoped() ...");
        }

        @Override
        public void onScannerDone(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScannerDone() ...");
            if (usbFlag == usbFlag_) {
                refreshData(usbFlag, true);
            }

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

    private OnServiceConnectionCompletedListener mOnServiceConnectionCompletedListener = new OnServiceConnectionCompletedListener() {
        @Override
        public void onServiceConnectionCompleted() {
            Logutil.i(TAG, "onServiceConnectionCompleted() ...");
            boolean isEnd = mediaStorageAccessProxyModel.isMediaScanFinished(usbFlag_);
            Log.i(TAG, "onServiceConnectionCompleted() isScanEnd " + isEnd);
            refreshData(usbFlag_, isEnd);
        }
    };

    // 刷新媒体数据
    private void refreshData(final int usbFlag, final boolean isScanning) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musicInfos = mediaStorageAccessProxyModel.queryAllMusics(usbFlag);

                if (null != mOnRefreshDataListener) {
                    mOnRefreshDataListener.onUpdateData(musicInfos, isScanning);
                }
            }
        }).start();
    }


    @Override
    public boolean isMediaScanFinished() {
        return mediaStorageAccessProxyModel.isMediaScanFinished(usbFlag_);
    }

    @Override
    public void deleteAllMusic(int usbFlag) {
        mediaStorageAccessProxyModel.deleteAllMusics(usbFlag);
    }


}
