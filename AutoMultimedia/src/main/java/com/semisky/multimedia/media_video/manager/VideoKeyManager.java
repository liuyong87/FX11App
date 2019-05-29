package com.semisky.multimedia.media_video.manager;

import android.os.RemoteException;

import com.semisky.autoservice.aidl.IKeyListener;
import com.semisky.autoservice.manager.KeyManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.service.ILocalMusicService;
import com.semisky.multimedia.media_video.presenter.IVideoPlayerPresenter;

/**
 * Created by LiuYong on 2018/9/6.
 */

public class VideoKeyManager {
    private static final String TAG = Logutil.makeTagLog(VideoKeyManager.class);
    private static VideoKeyManager _INSTANCE;
    private IVideoPlayerPresenter mVideoPlayerPresenter;

    private boolean mIsStopKeyEvent = true;

    // 是否绑定音乐服务接口
    private boolean isBindService() {
        return (null != this.mVideoPlayerPresenter);
    }

    /**
     * 绑定视频表示层接口
     *
     * @param presenter
     */
    public void onAttach(IVideoPlayerPresenter presenter) {
        Logutil.i(TAG, "onAttatch() ...");
        this.mVideoPlayerPresenter = presenter;
        KeyManager.getInstance().setOnKeyListener(mKeyListener);
    }

    /**
     * 解绑绑视频表示层接口
     */
    public void onDetach() {
        Logutil.i(TAG, "onDetach() ...");
        this.mVideoPlayerPresenter = null;
        KeyManager.getInstance().unregisterOnKeyListener(mKeyListener);
    }

    /**
     * 设置禁止按键事件监听状态
     *
     * @param enable
     */
    public void setStopKeyEvent(boolean enable) {
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

    private VideoKeyManager() {
    }

    public static VideoKeyManager getInstance() {
        if (null == _INSTANCE) {
            synchronized (VideoKeyManager.class) {
                if (null == _INSTANCE) {
                    _INSTANCE = new VideoKeyManager();
                }
            }
        }
        return _INSTANCE;
    }


    private IKeyListener.Stub mKeyListener = new IKeyListener.Stub() {
        @Override
        public void onKey(int keyCode, int action) throws RemoteException {
            if (mIsStopKeyEvent) {
                Logutil.w(TAG, "VideoPlayer STOP KEY EVENT !!!");
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
                            mVideoPlayerPresenter.onNextProgram();
                        }
                    } else if (KeyManager.KEYCODE_CHANNEL_DOWN == keyCode) {// 单击上一曲
                        Logutil.i(TAG, "SINGLE CLICK PREV ...");
                        if (isBindService()) {
                            mVideoPlayerPresenter.onPrevProgram();
                        }
                    }else if (KeyManager.KEYCODE_MODE == keyCode){
                        if (isBindService()){
                            mVideoPlayerPresenter.stopFastOrBackTask(true);
                        }
                    }else if (KeyManager.KEYCODE_PWR == keyCode){

                    }
                    break;
                case KeyManager.ACTION_LONG_PRESS:// 长按事件按下
                    if (KeyManager.KEYCODE_CHANNEL_UP == keyCode) {// 启动快进
                        Logutil.i(TAG, "START FAST FORWARD ...");
                        if (isBindService()) {
                            mVideoPlayerPresenter.stopPlayVideo();
                            mVideoPlayerPresenter.onFastForward();
                        }
                    } else if (KeyManager.KEYCODE_CHANNEL_DOWN == keyCode) {// 启动快退
                        Logutil.i(TAG, "START FAST BACKWARD ...");
                        if (isBindService()) {
                            mVideoPlayerPresenter.stopPlayVideo();
                            mVideoPlayerPresenter.onFastBackward();
                        }
                    }else if (KeyManager.KEYCODE_PWR == keyCode || KeyManager.KEYCODE_MODE == keyCode){
                        Logutil.i("lcc","start long power");
                        if (isBindService()){
                            mVideoPlayerPresenter.stopFastOrBackTask(false);
                        }
                    }
                    break;
                case KeyManager.ACTION_RELEASE:// 长按事件释放
                    if (KeyManager.KEYCODE_CHANNEL_UP == keyCode) {// 结束快进
                        Logutil.i(TAG, "END FAST FORWARD ...");
                        if(isBindService()){
                            mVideoPlayerPresenter.onStopFastForward();
                        }
                    } else if (KeyManager.KEYCODE_CHANNEL_DOWN == keyCode) {// 结束快退
                        Logutil.i(TAG, "END FAST BACKWARD ...");
                        if(isBindService()){
                            mVideoPlayerPresenter.onStopFastBackward();
                        }
                    }
                    break;
                default:
                    Logutil.w(TAG, "other key event !!!");
                    break;
            }
        }
    };
}
