package com.semisky.multimedia.media_usb.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;


import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.aidl.usb.IMediaStorageServiceProxy;
import com.semisky.multimedia.aidl.usb.IMediaStorageServiceProxy.Stub;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaStorageConst;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.Collections;
import java.util.List;

public class MediaStorageAccessProxyModel extends IMediaStorageServiceProxy.Stub {
    private static final String TAG = Logutil.makeTagLog(MediaStorageAccessProxyModel.class);
    private static final String COMPONENT_PKG = "com.semisky.multimedia";
    private static final String COMPONENT_CLZ = "com.semisky.multimedia.media_usb.service.MediaStorageService";
    private Context mContext;
    private static MediaStorageAccessProxyModel _instance;
    private IMediaStorageServiceProxy mMediaStorageServiceProxy;// 代理服务对象
    private OnServiceConnectionCompletedListener mOnServiceConnectionCompletedListener;

    private MediaStorageAccessProxyModel() {
    }

    public static MediaStorageAccessProxyModel getInstance() {
        if (null == _instance) {
            _instance = new MediaStorageAccessProxyModel();
        }
        return _instance;
    }

    /**
     * 注入上下文
     *
     * @param ctx
     */
    public void onAttachContext(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * 服务连接状态监听接口
     *
     * @author liuyong
     */
    public interface OnServiceConnectionCompletedListener {
        void onServiceConnectionCompleted();
    }

    /**
     * 注册服务连接状态监听
     *
     * @param listener
     */
    public void registerOnServiceConnectionCompletedListener(OnServiceConnectionCompletedListener listener) {
        this.mOnServiceConnectionCompletedListener = listener;
    }

    /**
     * 反注册服务连接状态监听
     */
    public void unregisterOnServiceConnectionCompletedListener() {
        this.mOnServiceConnectionCompletedListener = null;
    }


    /**
     * 服务连接成功通知
     */
    private void notifyServiceConnectionCompleted() {
        if (null != mOnServiceConnectionCompletedListener) {
            this.mOnServiceConnectionCompletedListener.onServiceConnectionCompleted();
        }
    }

    /**
     * 实现服务连接接口监听
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logutil.d(TAG, "onServiceConnected() ..." + (name != null ? name.getClassName() : "NULL"));
            mMediaStorageServiceProxy = IMediaStorageServiceProxy.Stub.asInterface(service);
            notifyServiceConnectionCompleted();// 服务连接成功通知
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logutil.d(TAG, "onServiceDisconnected() ..." + (name != null ? name.getClassName() : "NULL"));
            mMediaStorageServiceProxy = null;
        }

    };

    /**
     * 代理服务是否连接
     *
     * @return 代理服务连接状态
     */
    public boolean isConnection() {
        boolean isConnection = (null != mMediaStorageServiceProxy);
        Logutil.d(TAG, "isConnection() ..." + isConnection);
        return isConnection;
    }

    /**
     * 绑定服务
     */
    public void bindService() {
        if (!isConnection()) {
            Intent bindIntent = new Intent();
            bindIntent.setClassName(COMPONENT_PKG, COMPONENT_CLZ);
            mContext.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            Logutil.d(TAG, "bindService() ...");
        }
    }

    /**
     * 解绑服务
     */
    public void unbindService() {
        if (isConnection()) {
            Intent unbindIntent = new Intent();
            unbindIntent.setClassName(COMPONENT_PKG, COMPONENT_CLZ);
            mContext.unbindService(mServiceConnection);
            _instance = null;
            mContext = null;
            Logutil.d(TAG, "unbindService() ...");
        }
    }

    /**
     * 启动服务
     */
    public void startService() {
        Logutil.d(TAG, "startService() ...");
        Intent startIntent = new Intent();
        startIntent.setClassName(COMPONENT_PKG, COMPONENT_CLZ);
        mContext.startService(startIntent);
    }

    /********************************** Database Access ********************************************/
    /**
     * 开始扫描U盘
     *
     * @param usbPath
     */
    public void onStartScanPath(String usbPath) {
        Logutil.i(TAG, "onStartScanPath() ..." + usbPath);
        Intent startIntent = new Intent();
        startIntent.setClassName(COMPONENT_PKG, COMPONENT_CLZ);
        startIntent.setAction(Definition.MediaStorageConst.ACTION_OPS_CONTROL);
        startIntent.putExtra(MediaStorageConst.PARAM_CMD, MediaStorageConst.CMD_USB_MOUNTED);
        startIntent.putExtra(MediaStorageConst.PARAM_USB_PATH, usbPath);
        mContext.startService(startIntent);
    }

    /**
     * 停止扫描U盘
     *
     * @param usbPath
     */
    public void onStopScanPath(String usbPath) {
        Intent startIntent = new Intent();
        startIntent.setClassName(COMPONENT_PKG, COMPONENT_CLZ);
        startIntent.setAction(MediaStorageConst.ACTION_OPS_CONTROL);
        startIntent.putExtra(MediaStorageConst.PARAM_CMD, MediaStorageConst.CMD_USB_UNMOUNTED);
        startIntent.putExtra(MediaStorageConst.PARAM_USB_PATH, usbPath);
        mContext.startService(startIntent);
    }

    public void onDeleteAllMedia(String usbPath) {
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(MediaStorageConst.ACTION_OPS_CONTROL);
        deleteIntent.putExtra(MediaStorageConst.PARAM_CMD, MediaStorageConst.CMD_DEL_DB_DATA);
        deleteIntent.putExtra(MediaStorageConst.PARAM_USB_PATH, usbPath);
        mContext.startService(deleteIntent);
    }

    /**
     * 注册媒体文件扫描状态监听
     *
     * @param listener
     */
    @Override
    public void registerOnMediaScannerStateListener(IMediaScannerStateListener listener) {
        if (isConnection()) {
            try {
                mMediaStorageServiceProxy.registerOnMediaScannerStateListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 反注册媒体文件扫描状态监听
     *
     * @param listener
     */
    @Override
    public void unregisterOnMediaScannerStateListener(IMediaScannerStateListener listener) {
        if (isConnection()) {
            try {
                mMediaStorageServiceProxy.unregisterOnMediaScannerStateListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long insertFavoriteMusic(MusicInfo musicInfo) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.insertFavoriteMusic(musicInfo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MusicInfo> queryAllFavoriteMusics(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryAllFavoriteMusics(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isFavoriteWithSpecifyMusicUrl(int usbFlag, String url) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.isFavoriteWithSpecifyMusicUrl(usbFlag, url);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MusicInfo> queryAllMusics(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryAllMusics(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VideoInfo> queryAllVideos(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryAllVideos(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PhotoInfo> queryAllPhotos(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryAllPhotos(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FolderInfo> querySpecifyDirectoryUnder(String curDir) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.querySpecifyDirectoryUnder(curDir);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public int queryMusicsSize(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryMusicsSize(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int queryVideosSize(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryVideosSize(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int queryPhotosSize(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryPhotosSize(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public String queryLyricUrl(String url) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryLyricUrl(url);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public long deleteAllMusics(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.deleteAllMusics(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long deleteAllVideos(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.deleteAllVideos(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long deleteAllPhotos(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.deleteAllPhotos(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long deleteFavoriteMusic(int id) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.deleteFavoriteMusic(id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long deleteFavoriteWithMusicUrl(int usbFlag, String url) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.deleteFavoriteWithMusicUrl(usbFlag, url);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public void deleteBatchFavorite(List<String> list) {
        if (isConnection()) {
            try {
                mMediaStorageServiceProxy.deleteBatchFavorite(list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isMediaScanFinished(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.isMediaScanFinished(usbFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<FolderInfo> queryAllMusicFolder(int usbFlag, String path) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryAllMusicFolder(usbFlag, path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MusicInfo> queryFolderUnderMusic(int usbFlag, String path) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.queryFolderUnderMusic(usbFlag, path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public long deleteAllLyrics(int usbFlag) {
        if (isConnection()) {
            try {
                return mMediaStorageServiceProxy.deleteAllLyrics(usbFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
