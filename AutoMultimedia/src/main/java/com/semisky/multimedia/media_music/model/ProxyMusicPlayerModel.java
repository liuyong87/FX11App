package com.semisky.multimedia.media_music.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.semisky.multimedia.aidl.music.IProxyMusicPlayer;
import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaCtrlConst;
import com.semisky.multimedia.common.utils.Logutil;

/**
 * Created by LiuYong on 2018/8/27.
 */

public class ProxyMusicPlayerModel extends IProxyMusicPlayer.Stub {
    private static final String TAG = Logutil.makeTagLog(ProxyMusicPlayerModel.class);
    private static ProxyMusicPlayerModel _INSTANCE;
    private OnServiceConnectCompletedListener mOnServiceConnectCompletedListener;
    private IProxyMusicPlayer mProxyMusicPlayerService;
    private boolean mIsBound = false; //绑定服务标记位


    public interface OnServiceConnectCompletedListener {
        void onServiceConnectCompleted();
    }


    public void registerOnServiceConnectCompletedListener(OnServiceConnectCompletedListener l) {
        this.mOnServiceConnectCompletedListener = l;
    }

    public void unregisterOnServiceConnectCompletedListener() {
        this.mOnServiceConnectCompletedListener = null;
    }

    private ProxyMusicPlayerModel() {

    }

    public static ProxyMusicPlayerModel getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new ProxyMusicPlayerModel();
        }
        return _INSTANCE;
    }

    // 切换音源
    public void changeAudioSource(int usbFlag) {
        Intent it = new Intent();
        it.setClassName(MediaCtrlConst.SERVICE_PKG, MediaCtrlConst.SERVICE_CLZ);
        it.setAction(MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                it.putExtra(MediaCtrlConst.PARAM_CMD, MediaCtrlConst.CMD_AUDIO_SOURCE_USB1);
                break;
            case Definition.FLAG_USB2:
                it.putExtra(MediaCtrlConst.PARAM_CMD, MediaCtrlConst.CMD_AUDIO_SOURCE_USB2);
                break;
        }
        MediaApplication.getContext().startService(it);
    }

    public void startService(Context ctx) {
        if (isConnected()) {
            Logutil.i(TAG, "startService() Music Service Connected !!!");
            return;
        }
        Logutil.i(TAG, "startService() ...");
        Intent intent = new Intent();
        intent.setClassName(MediaCtrlConst.SERVICE_PKG, MediaCtrlConst.SERVICE_CLZ);
        intent.setAction(MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
        intent.putExtra(MediaCtrlConst.PARAM_CMD, MediaCtrlConst.CMD_RESUME_PLAY);
        ctx.startService(intent);
    }

    boolean ismIsBound = false;

    public void bindService(Context ctx) {
        if (isConnected()) {
            Logutil.i(TAG, "bindService() Music Service Connected !!!");
            return;
        }
        Logutil.i(TAG, "bindService() ...");
        Intent intent = new Intent();
        intent.setClassName(MediaCtrlConst.SERVICE_PKG, MediaCtrlConst.SERVICE_CLZ);
        ismIsBound = ctx.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Logutil.i("lcc", "bindService  mIsBound " + mIsBound);
        Logutil.i("lcc", "bindService  ismIsBound " + ismIsBound);
    }

    public void unbindService(Context ctx) {
        Logutil.i("lcc", "unbindService");
        if (!isConnected()) {
            Logutil.w(TAG, "unbindService() Before Unbind Service ....");
            return;
        }
        Logutil.i("lcc", "unbindService mIsBound " + mIsBound);
        Logutil.i("lcc", "unbindService ismIsBound " + ismIsBound);
        if (mIsBound) {
            Logutil.i(TAG, "unbindService() ...");
            Intent intent = new Intent();
            intent.setClassName(MediaCtrlConst.SERVICE_PKG, MediaCtrlConst.SERVICE_CLZ);
            ctx.unbindService(mConn);
            this.mProxyMusicPlayerService = null;
            mIsBound = false;
            ismIsBound = false;
        }

    }


    public void onListPlay(String url) {
        Logutil.i(TAG, "onListPlay() ..." + url);
        Intent intent = new Intent();
        intent.setAction(MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
        intent.setClassName(MediaCtrlConst.SERVICE_PKG, MediaCtrlConst.SERVICE_CLZ);
        intent.putExtra(MediaCtrlConst.PARAM_CMD, MediaCtrlConst.CMD_LIST_PLAY);
        intent.putExtra(MediaCtrlConst.PARAM_URL, url);
        MediaApplication.getContext().startService(intent);
    }


    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logutil.i(TAG, "onServiceConnected() name=" + name.getClassName());
            mProxyMusicPlayerService = IProxyMusicPlayer.Stub.asInterface(service);
            if (null != mOnServiceConnectCompletedListener) {
                mOnServiceConnectCompletedListener.onServiceConnectCompleted();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logutil.w(TAG, "onServiceDisconnected() name=" + name.getClassName());
            mProxyMusicPlayerService = null;
        }
    };


    public boolean isConnected() {
        boolean isConnected = (null != mProxyMusicPlayerService);
        Logutil.i(TAG, "isConnected() ..." + isConnected);
        return isConnected;
    }


    @Override
    public void registerCallback(IProxyProgramChangeCallback callback) {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.registerCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unregisterCallback(IProxyProgramChangeCallback callback) {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.unregisterCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getSongName() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getSongName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getArtistName() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getArtistName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAlbumName() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getAlbumName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int getTotalPorgress() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getTotalPorgress();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int getCurrentProgress() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getCurrentProgress();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public String getCurProgramPos() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getCurProgramPos();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean isFavorite() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.isFavorite();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public int getPlayMode() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.getPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (isConnected()) {
            try {
                return mProxyMusicPlayerService.isPlaying();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onSwitchNext() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchPrev() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchPrev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchFastForward() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchFastForward();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchStopFastForward() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchStopFastForward();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchFastBackward() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchFastBackward();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchStopFastBackward() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchStopFastBackward();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchPlayOrPause() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchPlayOrPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchResumePlay() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchResumePlay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchPlayMode() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwitchFavorite() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSwitchFavorite();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSeekTo(int progress) {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onSeekTo(progress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateProgressWithThreadEnabled(boolean enabled) {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onUpdateProgressWithThreadEnabled(enabled);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeleteFavorite() {
        if (isConnected()) {
            try {
                mProxyMusicPlayerService.onDeleteFavorite();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void notifyServiceRequestAudio() {
        try {
            if (isConnected()) {
                mProxyMusicPlayerService.notifyServiceRequestAudio();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCurrentLyricUrl() {
        try {
            if (isConnected()) {
                return mProxyMusicPlayerService.getCurrentLyricUrl();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
