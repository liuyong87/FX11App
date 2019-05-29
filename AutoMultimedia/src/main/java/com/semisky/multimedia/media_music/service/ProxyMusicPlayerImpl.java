package com.semisky.multimedia.media_music.service;

import android.os.RemoteException;

import com.semisky.multimedia.aidl.music.IProxyMusicPlayer;
import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;

/**
 * Created by LiuYong on 2018/8/27.
 */

public class ProxyMusicPlayerImpl extends IProxyMusicPlayer.Stub {
    private static final String TAG = Logutil.makeTagLog(ProxyMusicPlayerImpl.class);
    private static ProxyMusicPlayerImpl _INSTANCE;
    private ILocalMusicService mLocalMusicService;

    private ProxyMusicPlayerImpl() {

    }

    public static ProxyMusicPlayerImpl getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new ProxyMusicPlayerImpl();
        }
        return _INSTANCE;
    }

    public void onAttach(ILocalMusicService service) {
        Logutil.i(TAG, "onAttach() ...");
        this.mLocalMusicService = service;
    }

    public void onDetach() {
        Logutil.i(TAG, "onDetach() ...");
        this.mLocalMusicService = null;
    }

    private boolean isBindLocalService() {
        return (null != this.mLocalMusicService);
    }

    @Override
    public void registerCallback(IProxyProgramChangeCallback callback) throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.registerCallback(callback);
        }
    }

    @Override
    public void unregisterCallback(IProxyProgramChangeCallback callback) throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.unregisterCallback(callback);
        }
    }

    @Override
    public String getSongName() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getSongName();
        }
        return null;
    }

    @Override
    public String getArtistName() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getArtistName();
        }
        return null;
    }

    @Override
    public String getAlbumName() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getAlbumName();
        }
        return null;
    }

    @Override
    public int getTotalPorgress() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getTotalPorgress();
        }
        return 0;
    }

    @Override
    public int getCurrentProgress() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getCurrentProgress();
        }
        return 0;
    }

    @Override
    public String getCurProgramPos() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getCurProgramPos();
        }
        return null;
    }

    @Override
    public boolean isFavorite() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.isFavorite();
        }
        return false;
    }

    @Override
    public int getPlayMode() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getPlayMode();
        }
        return PlayMode.LOOP;
    }

    @Override
    public boolean isPlaying() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.isPlaying();
        }
        return false;
    }

    @Override
    public void onSwitchNext() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchNext();
        }
    }

    @Override
    public void onSwitchPrev() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchPrev();
        }
    }

    @Override
    public void onSwitchFastForward() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchFastForward();
        }
    }

    @Override
    public void onSwitchStopFastForward() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchStopFastForward();
        }
    }

    @Override
    public void onSwitchFastBackward() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchFastBackward();
        }
    }

    @Override
    public void onSwitchStopFastBackward() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchStopFastBackward();
        }
    }

    @Override
    public void onSwitchPlayOrPause() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchPlayOrPause();
        }
    }

    @Override
    public void onSwitchPlayMode() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchPlayMode();
        }
    }

    @Override
    public void onSwitchFavorite() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchFavorite();
        }
    }

    @Override
    public void onSeekTo(int progress) throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSeekTo(progress);
        }
    }

    @Override
    public void onUpdateProgressWithThreadEnabled(boolean enabled) throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onUpdateProgressWithThreadEnabled(enabled);
        }
    }

    @Override
    public void onDeleteFavorite() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onDeleteFromFavorite();
        }
    }

    @Override
    public void notifyServiceRequestAudio() throws RemoteException {
        if(isBindLocalService()){
            mLocalMusicService.RequestAudio();
        }

    }

    @Override
    public void onSwitchResumePlay() throws RemoteException {
        if (isBindLocalService()) {
            mLocalMusicService.onSwitchResumePlay();
        }
    }

    @Override
    public String getCurrentLyricUrl() throws RemoteException {
        if (isBindLocalService()) {
            return mLocalMusicService.getCurrentLyricUrl();
        }
        return null;
    }
}
