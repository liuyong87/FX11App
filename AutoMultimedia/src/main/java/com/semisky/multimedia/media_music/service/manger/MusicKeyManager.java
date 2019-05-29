package com.semisky.multimedia.media_music.service.manger;

import android.os.RemoteException;
import android.util.Log;

import com.semisky.autoservice.aidl.IKeyListener;
import com.semisky.autoservice.manager.KeyManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.service.ILocalMusicService;

/**
 * Created by LiuYong on 2018/9/6.
 */

public class MusicKeyManager {
    private static final String TAG = Logutil.makeTagLog(MusicKeyManager.class);
    private static MusicKeyManager _INSTANCE;
    private ILocalMusicService mLocalMusicService;

    private boolean mIsStopKeyEvent = true;

    // 是否绑定音乐服务接口
    private boolean isBindService() {
        return (null != this.mLocalMusicService);
    }

    /**
     * 绑定音乐服务接口
     *
     * @param service
     */
    public void onAttatch(ILocalMusicService service) {
        Logutil.i(TAG, "onAttatch() ...");
        this.mLocalMusicService = service;
        KeyManager.getInstance().setOnKeyListener(mKeyListener);
    }

    /**
     * 解绑音乐服务接口
     */
    public void onDetach() {
        Logutil.i(TAG, "onDetach() ...");
        this.mLocalMusicService = null;
        KeyManager.getInstance().unregisterOnKeyListener(mKeyListener);
    }

    /**
     * 设置禁止按键事件监听状态
     *
     * @param enable
     */
    public void setStopKeyEventEnable(boolean enable) {
        this.mIsStopKeyEvent = enable;
    }

    /**
     * 是否禁止按键事件监听
     *
     * @return
     */
    public boolean isStopKeyEvent() {
        return this.mIsStopKeyEvent;
    }

    private MusicKeyManager() {
    }

    public static MusicKeyManager getInstance() {
        if (null == _INSTANCE) {
            synchronized (MusicKeyManager.class) {
                if (null == _INSTANCE) {
                    _INSTANCE = new MusicKeyManager();
                }
            }
        }
        return _INSTANCE;
    }


    private IKeyListener.Stub mKeyListener = new IKeyListener.Stub() {
        @Override
        public void onKey(int keyCode, int action) throws RemoteException {
            if (mIsStopKeyEvent) {
                Logutil.w(TAG, "MusicPlayer STOP KEY EVENT !!!");
                return;
            }
            Logutil.i(TAG, "==========IKeyListener START=========");
            Logutil.w(TAG, "onKey() action =" + action + " , keyCode=" + keyCode);
            Logutil.i(TAG, "==========IKeyListener END=========");


            // 单击事件
            switch (action) {
                case KeyManager.ACTION_PRESS:// 单击事件
                    if (KeyManager.KEYCODE_CHANNEL_UP == keyCode) {// 单击下一曲
                        Logutil.i(TAG, "SINGLE CLICK NEXT ...");
                        if (isBindService()) {
                            mLocalMusicService.onSwitchNext();
                        }
                    } else if (KeyManager.KEYCODE_CHANNEL_DOWN == keyCode) {// 单击上一曲
                        Logutil.i(TAG, "SINGLE CLICK PREV ...");
                        if (isBindService()) {
                            mLocalMusicService.onSwitchPrev();
                        }
                    }
                    // 暂停/播放事件
                    else if(KeyManager.KEYCODE_PLAY == keyCode){
                        Log.i(TAG,"KEYCODE_PLAY ...");
                        mLocalMusicService.start();
                    }else if(KeyManager.KEYCODE_PAUSE == keyCode){
                        Log.i(TAG,"KEYCODE_PAUSE ...");
                        mLocalMusicService.pause();
                    }
                    break;
                case KeyManager.ACTION_LONG_PRESS:// 长按事件按下
                    if (KeyManager.KEYCODE_CHANNEL_UP == keyCode) {// 启动快进
                        Logutil.i(TAG, "START FAST FORWARD ...");
                        if (isBindService()) {
                            mLocalMusicService.onSwitchFastForward();
                        }
                    } else if (KeyManager.KEYCODE_CHANNEL_DOWN == keyCode) {// 启动快退
                        Logutil.i(TAG, "START FAST BACKWARD ...");
                        if (isBindService()) {
                            mLocalMusicService.onSwitchFastBackward();
                        }
                    }else if (KeyManager.KEYCODE_PWR == keyCode){
                        Logutil.i(TAG, "stop FAST BACKWARD  or FastForward...");
                        if (isBindService()) {
                            mLocalMusicService.stopFastBackOrFastF(false);
                        }
                    }
                    break;
                case KeyManager.ACTION_RELEASE:// 长按事件释放
                    if (KeyManager.KEYCODE_CHANNEL_UP == keyCode) {// 结束快进
                        Logutil.i(TAG, "END FAST FORWARD ...");
                        mLocalMusicService.onSwitchStopFastForward();
                    } else if (KeyManager.KEYCODE_CHANNEL_DOWN == keyCode) {// 结束快退
                        Logutil.i(TAG, "END FAST BACKWARD ...");
                        mLocalMusicService.onSwitchStopFastBackward();
                    }
                    break;
                default:
                    Logutil.w(TAG, "other key event !!!");

                    break;
            }
        }
    };
}
