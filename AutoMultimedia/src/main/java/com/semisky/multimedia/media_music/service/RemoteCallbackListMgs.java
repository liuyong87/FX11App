package com.semisky.multimedia.media_music.service;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;

/**
 * Created by LiuYong on 2018/8/27.
 */

public class RemoteCallbackListMgs implements MediaRemoteCallbackList.OnCallbackDiedListener {
    private MediaRemoteCallbackList mCallback = new MediaRemoteCallbackList();

    public void RemoteCallbackListMgs() {
        mCallback.registerListener(this);
    }

    public void registerCallback(IProxyProgramChangeCallback callback) {
        mCallback.register(callback);
    }

    public void unregisterCallback(IProxyProgramChangeCallback callback) {
        mCallback.unregister(callback);
    }

    @Override
    public void onCallbackDied(IProxyProgramChangeCallback callback) {
        mCallback.unregister(callback);
    }


    /**
     * 通知音乐名变化
     *
     * @param songName 歌名
     */
    public void notifyChangeSongName(String songName) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeSongName(songName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知歌手名变化
     *
     * @param artistName 歌手名
     */
    public void notifyChangeArtistName(String artistName) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeArtistName(artistName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知专辑名变化
     *
     * @param albumName 专辑名
     */
    public void notifyChangeAlbumName(String albumName) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeAlbumName(albumName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知改变总进度
     *
     * @param progress 进度
     */
    public void notifyChangeTotalProgress(int progress) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeTotalProgress(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知当前播放进度改变
     *
     * @param progress 进度
     */
    public void notifyChangeCurrentProgress(int progress) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeCurrentProgress(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知播放曲目位置改变
     *
     * @param pos 曲目位置
     */
    public void notifyChangeCurProgramPos(String pos) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeCurProgramPos(pos);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知播放模式改变
     *
     * @param playmode 播放模式
     */
    public void notifyChangePlayMode(int playmode) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangePlayMode(playmode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知播放状态改变
     *
     * @param playStatus 播放状态
     */
    public void notifyChangePlayStatus(boolean playStatus) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangePlayStatus(playStatus);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知收藏改变
     */
    public void notifyChangeFavorite(boolean isFavorite) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeFavorite(isFavorite);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知播放错误
     *
     * @param what 媒体播放错误标识
     */
    public void notifyPlayError(int what) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onPlayError(what);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知播放曲目资源路径改变
     *
     * @param url 媒体播放资源路径
     */
    public void notifyChangeUrl(String url) {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onChangeUrl(url);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }

    /**
     * 通知媒体资源准备完成
     */
    public void notifyMediaPrepareCompleted() {
        if (null != mCallback && mCallback.getRegisteredCallbackCount() > 0) {
            synchronized (mCallback) {
                final int N = mCallback.getRegisteredCallbackCount();
                mCallback.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mCallback.getBroadcastItem(i).onMediaPrepareCompleted();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallback.finishBroadcast();
            }
        }
    }


}
