package com.semisky.multimedia.media_usb.service;

import android.os.RemoteException;


import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.aidl.usb.IMediaStorageServiceProxy;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.Collections;
import java.util.List;


public class MediaStorageServiceProxy extends IMediaStorageServiceProxy.Stub {
    private static final String TAG = Logutil.makeTagLog(MediaStorageServiceProxy.class);
    private static MediaStorageServiceProxy _instance;// 媒体存储管理代理服务接口(供客户端调用)
    private IMediaStorageService mLocalService;// 媒体存储管理服务接口


    private MediaStorageServiceProxy() {
        Logutil.d(TAG, "MediaStorageServiceProxy() INIT ...");

    }

    public static MediaStorageServiceProxy getInstance() {
        if (null == _instance) {
            _instance = new MediaStorageServiceProxy();
        }
        return _instance;
    }

    public void onAttached(IMediaStorageService service) {
        this.mLocalService = service;
    }

    private boolean isBindStorageService() {
        boolean hasAttachService = (null != this.mLocalService);
        Logutil.d(TAG, "isBindStorageService() ..." + hasAttachService);
        return hasAttachService;
    }

    @Override
    public void registerOnMediaScannerStateListener(IMediaScannerStateListener listener) throws RemoteException {
        if (isBindStorageService()) {
            this.mLocalService.registerOnMediaScannerStateListener(listener);
        }
    }

    @Override
    public void unregisterOnMediaScannerStateListener(IMediaScannerStateListener listener) throws RemoteException {
        if (isBindStorageService()) {
            this.mLocalService.unregisterOnMediaScannerStateListener(listener);
        }
    }

    @Override
    public long insertFavoriteMusic(MusicInfo musicInfo) throws RemoteException {
        if (isBindStorageService()) {
            return mLocalService.insertFavoriteMusic(musicInfo);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MusicInfo> queryAllFavoriteMusics(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryAllFavoriteMusics(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isFavoriteWithSpecifyMusicUrl(int usbFlag, String url) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.isFavoriteWithSpecifyMusicUrl(usbFlag, url);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MusicInfo> queryAllMusics(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryAllMusics(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VideoInfo> queryAllVideos(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryAllVideos(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PhotoInfo> queryAllPhotos(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryAllPhotos(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FolderInfo> querySpecifyDirectoryUnder(String curDir) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.querySpecifyDirectoryUnder(curDir);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public int queryMusicsSize(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryMusicsSize(usbFlag);
        }
        return 0;
    }

    @Override
    public int queryVideosSize(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryVideosSize(usbFlag);
        }
        return 0;
    }

    @Override
    public int queryPhotosSize(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryPhotosSize(usbFlag);
        }
        return 0;
    }

    @Override
    public String queryLyricUrl(String url) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.queryLyricUrl(url);
        }
        return null;
    }

    @Override
    public long deleteAllMusics(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.deleteAllMusics(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteAllVideos(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.deleteAllVideos(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteAllPhotos(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.deleteAllPhotos(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteFavoriteMusic(int id) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.deleteFavoriteMusic(id);
        }
        return 0;
    }

    @Override
    public long deleteFavoriteWithMusicUrl(int usbFlag, String url) throws RemoteException {
        if (isBindStorageService()) {
            return this.mLocalService.deleteFavoriteWithMusicUrl(usbFlag, url);
        }
        return 0;
    }

    @Override
    public void deleteBatchFavorite(List<String> list) throws RemoteException {
        if (isBindStorageService()) {
            mLocalService.deleteBatchFavorite(list);
        }
    }

    @Override
    public boolean isMediaScanFinished(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return mLocalService.isMediaScanFinished(usbFlag);
        }
        return false;
    }

    @Override
    public List<FolderInfo> queryAllMusicFolder(int usbFlag, String path) throws RemoteException {
        if (isBindStorageService()) {
            return mLocalService.queryAllMusicFolder(usbFlag, path);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MusicInfo> queryFolderUnderMusic(int usbFlag, String path) throws RemoteException {
        if (isBindStorageService()) {
            return mLocalService.queryFolderUnderMusic(usbFlag, path);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public long deleteAllLyrics(int usbFlag) throws RemoteException {
        if (isBindStorageService()) {
            return mLocalService.deleteAllLyrics(usbFlag);
        }
        return 0;
    }
}
