package com.semisky.multimedia.media_video.manager;

import android.os.RemoteException;

import com.semisky.autoservice.aidl.IHandBrakeChanged;
import com.semisky.autoservice.manager.AutoManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_video.presenter.IVideoPlayerPresenter;

/**
 * 手刹状态监听管理
 * Created by LiuYong on 2018/9/20.
 */

public class HandBrakeManager {
    private static final String TAG = Logutil.makeTagLog(HandBrakeManager.class);
    private static HandBrakeManager _INSTANCE;
    private boolean mIsStopHandBrakeEvevent = false;// 是否停止处理手刹状态
    private IVideoPlayerPresenter mPresenter;

    private HandBrakeManager() {
        Logutil.i(TAG, "init ...");
        AutoManager.getInstance().registerHandBrakeListener(mHandBrakeChanged);
    }

    public static HandBrakeManager getInstance() {
        if (null == _INSTANCE) {
            synchronized (HandBrakeManager.class) {
                if (null == _INSTANCE) {
                    _INSTANCE = new HandBrakeManager();
                }
            }
        }
        return _INSTANCE;
    }

    /**
     * 设置是否禁止手刹事件监听处理操作
     *
     * @param enable
     */
    public void setStopHandBrakeEveventEnable(boolean enable) {
        this.mIsStopHandBrakeEvevent = enable;
    }

    private IHandBrakeChanged.Stub mHandBrakeChanged = new IHandBrakeChanged.Stub() {
        @Override
        public void onHandBrakeChange(boolean isDriving) throws RemoteException {
            Logutil.i(TAG, "onHandBrakeChange() isDriving=" + isDriving);
            Logutil.i(TAG, "onHandBrakeChange() mIsStopHandBrakeEvevent=" + mIsStopHandBrakeEvevent);
            Logutil.i(TAG, "onHandBrakeChange() isBindPresenter=" + isBindPresenter());

            if (!mIsStopHandBrakeEvevent && isBindPresenter()) {
                mPresenter.onHandBrakeChange(isDriving);
            }
        }
    };

    public void onAttach(IVideoPlayerPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void onDetach() {
        this.mPresenter = null;
    }

    private boolean isBindPresenter() {
        return (null != mPresenter);
    }

    public void onRelease() {
        Logutil.i(TAG, "onRelease() ...");
        AutoManager.getInstance().unregisterHandBrakeListener(mHandBrakeChanged);
        this.mHandBrakeChanged = null;
        this._INSTANCE = null;
    }


}
