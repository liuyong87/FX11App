package com.semisky.multimedia.media_bt_music.presenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.semisky.autoservice.aidl.IBtConnectStatusChangeListener;
import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.autoservice.manager.AutoManager;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_bt_music.view.IBTMusicView;

public class BTMusicPresenter<V extends IBTMusicView> extends BasePresenter<V> implements IBTMusicPresenter {
    private static final String TAG = Logutil.makeTagLog(BTMusicPresenter.class);
    private boolean mIsStopBtStatusListener = false;

    public BTMusicPresenter() {
        AutoManager.getInstance().registerBtConnectStatusChangeListener(mIBtConnectStatusChangeListener);
    }

    private IBtConnectStatusChangeListener.Stub mIBtConnectStatusChangeListener = new IBtConnectStatusChangeListener.Stub() {
        @Override
        public void onBtConnectStatusChanged(int btStatus) throws RemoteException {
            Log.i(TAG, "onBtConnectStatusChanged() ..." + btStatus);
            boolean state = (AutoConstants.BtConnectionState.STATE_CONNECTED_A2DP == btStatus ||
                    AutoConstants.BtConnectionState.STATE_CONNECTED_A2DP_AND_HFP == btStatus);
            Log.i(TAG, "onBtConnectStatusChanged() state : " + state+",mIsStopBtStatusListener :"+mIsStopBtStatusListener);
            if(mIsStopBtStatusListener){
                return;
            }
            updateBtConnectStateToView(state);
           if(state){
               AppUtil.jumpToBTMusic();
           }
        }
    };


    private void updateBtConnectStateToView(final boolean isConnected){
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if(isBindView()){
                    mViewRef.get().onBTConnectionStatusChanged(isConnected);
                }
            }
        });
    }

    @Override
    public void checkBtMusicConnect() {
        if (!isBindView()) {
            return;
        }
        mViewRef.get().onBTConnectionStatusChanged(SemiskyIVIManager.getInstance().isBtConnected());


    }

    @Override
    public void reqBtMusicConnect() {
        if (!SemiskyIVIManager.getInstance().isBtConnected()) {
            jumpToBTSettings();
        }
    }

    // 跳转蓝牙设置界面
    private void jumpToBTSettings() {
        ComponentName componentName = new ComponentName("com.semisky.autosetting", "com.semisky.autosetting.SettingActivity");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("BT", "bt");
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(componentName);

        try {
            if (null != intent.resolveActivity(MediaApplication.getContext().getPackageManager())) {
                MediaApplication.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            Logutil.w(TAG, "Jump to bluetooth settings fail !!!");
            e.printStackTrace();
        }

    }

    @Override
    public void onViewResume() {
        this.mIsStopBtStatusListener = false;
    }

    @Override
    public void onViewStop() {
        this.mIsStopBtStatusListener = true;
    }

    @Override
    public void destory() {
        Log.i(TAG, "destory() ...");
    }



}
