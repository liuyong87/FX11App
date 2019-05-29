package com.semisky.multimedia.common.manager;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.semisky.autoservice.aidl.IBackModeChanged;
import com.semisky.autoservice.aidl.IBtCallStatusChangeListener;
import com.semisky.autoservice.aidl.IKeyListener;
import com.semisky.autoservice.manager.AudioManager;
import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.autoservice.manager.AutoManager;
import com.semisky.autoservice.manager.ICMManager;
import com.semisky.autoservice.manager.KeyManager;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.interfaces.IBackModeChange;
import com.semisky.multimedia.common.interfaces.IBtCallStatus;
import com.semisky.multimedia.common.interfaces.OnCloseDialogListener;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.systemui.aidl.BarControlManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 中间件管理
 * Created by LiuYong on 2018/9/12.
 */

public class SemiskyIVIManager {
    private static final String TAG = Logutil.makeTagLog(SemiskyIVIManager.class);

    private static SemiskyIVIManager _INSTANCE;

    private List<IBackModeChange> iBackModeChangeList = new ArrayList<IBackModeChange>();
    private List<IBtCallStatus> iBtCallStatuses = new ArrayList<IBtCallStatus>();


    private SemiskyIVIManager() {
    }

    public static SemiskyIVIManager getInstance() {
        if (null == _INSTANCE) {
            synchronized (SemiskyIVIManager.class) {
                if (null == _INSTANCE) {
                    _INSTANCE = new SemiskyIVIManager();
                }
            }
        }
        return _INSTANCE;
    }

    /**
     * 是否蓝牙已连接
     *
     * @return
     */
    public boolean isBtConnected() {
        int btStatus = AutoManager.getInstance().getBtConnectionState();
        if (AutoConstants.BtConnectionState.STATE_CONNECTED_A2DP == btStatus ||
                AutoConstants.BtConnectionState.STATE_CONNECTED_A2DP_AND_HFP == btStatus) {
            return true;

        }
        return false;
    }


    /**
     * 显示SystemUI底部状态栏
     */
    public void showBottomBar() {
        try {
            Log.i(TAG, "showBottomBar() ...");
            BarControlManager.getInstance(MediaApplication.getContext()).showBottomBar();
        } catch (Exception e) {
            Log.e(TAG, "showBottomBar() FAIL !!!");
            e.printStackTrace();
        }
    }

    /**
     * 隐藏SystemUI底部状态栏
     */
    public void dismissBottomBar() {
        try {
            Log.i(TAG, "dismissBottomBar() ...");
            BarControlManager.getInstance(MediaApplication.getContext()).dismissBottomBar();
        } catch (Exception e) {
            Log.e(TAG, "dismissBottomBar() FAIL !!!");
            e.printStackTrace();
        }
    }

    /**
     * 绑定底部导航服务
     */
    public void bindBottombarService() {

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BarControlManager.getInstance(MediaApplication.getContext()).bindService();
                    Log.i(TAG, "bindBottombarService() ...");
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "bindBottombarService() FAIL !!!");
            e.printStackTrace();
        }
    }

    /**
     * 打开多媒体音频通道
     */
    public void openAndroidStreamVolume() {
        if (getCurrentAudioType() != AudioManager.STREAM_ANDROID) {
            Logutil.i(TAG, "openStreamVolume(STREAM_ANDROID)....");
            AudioManager.getInstance().openStreamVolume(AudioManager.STREAM_ANDROID);
        }
    }

    /**
     * 打开多媒体视频音频通道
     */
    public void openAndroidStreamVolumeByVideo() {
        if (getCurrentAudioType() != AudioManager.STREAM_ANDROID_VIDEO) {
            Logutil.i(TAG, "openStreamVolume(STREAM_ANDROID_VIDEO)....");
            AudioManager.getInstance().openStreamVolume(AudioManager.STREAM_ANDROID_VIDEO);
        }
    }

    /**
     * 获取当前音源
     *
     * @return
     */
    public int getCurrentAudioType() {
        int currentAudioType = com.semisky.autoservice.manager.AudioManager.getInstance().getCurrentAudioType();
        Logutil.i(TAG, "getCurrentAudioType() currentAudioType=" + currentAudioType);
        return currentAudioType;
    }

    /**
     * 仪表交互：设置当前歌曲URL
     */
    public void setMusicImageUrl(String url) {
        ICMManager.getInstance().setMusicImageUrl(url);
        try {
            Log.i(TAG, "setMusicImageUrl() ..." + url);
            ICMManager.getInstance().setMusicImageUrl(url);
        } catch (Exception e) {
            Log.e(TAG, "setMusicImageUrl() FAIL !!!");
            e.printStackTrace();
        }
    }

    /**
     * 仪表交互：设置当前歌曲歌手
     */
    public void setCurrentSingerName(String artistName) {
        try {
            Log.i(TAG, "setCurrentSingerName() ..." + artistName);
            ICMManager.getInstance().setCurrentSingerName(artistName);
        } catch (Exception e) {
            Log.e(TAG, "setCurrentSingerName() FAIL !!!");
            e.printStackTrace();
        }
    }

    /**
     * 仪表交互：设置当前节目名字
     */
    public void setCurrentSourceName(String programName) {
        try {
            Log.i(TAG, "setCurrentSourceName() ..." + programName);
            ICMManager.getInstance().setCurrentSourceName(programName);
        } catch (Exception e) {
            Log.e(TAG, "setCurrentSourceName() FAIL !!!");
            e.printStackTrace();
        }
    }

    /**
     * 仪表交互：设置当前多媒体播放状态到仪表
     *
     * @param playStatus
     */
    public void setCurrentPlayStatus(boolean playStatus) {
        Log.i(TAG, "setCurrentPlayStatus() ..." + playStatus);
        try {
            ICMManager.getInstance().setCurrentPlayStatus(playStatus);
        } catch (Exception e) {
            Log.e(TAG, "setCurrentPlayStatus() FAIL !!!!");
            e.printStackTrace();
        }
    }

    /**
     * 设置APP状态到中间件
     *
     * @param className
     * @param title
     * @param status
     */
    public void setAppStatus(String className, String title, int status) {
        Logutil.i(TAG, "setAppStatus() className=" + className);
        Logutil.i(TAG, "setAppStatus() title=" + title);
        Logutil.i(TAG, "setAppStatus() status=" + status);
        try {
            AutoManager.getInstance().setAppStatus(className, title, status);
        } catch (Exception e) {
            Logutil.e(TAG, "setAppStatus() fail!!!,error info-:" + e.getMessage());
        }
    }

    /**
     * 是否导航在前台
     *
     * @return
     */
    public boolean isNaviAtForeground() {
        boolean isNaviAtForeground = AutoManager.getInstance().getNaviStatus();
        Logutil.i(TAG, "isNaviAtForeground=" + isNaviAtForeground);
        return isNaviAtForeground;
    }

    /**
     * 复位禁止多媒体播放接口
     */
    public void resetStopMediaPlay() {
        Logutil.i(TAG, "resetStopMediaPlay() ...");
        AutoManager.getInstance().setStopMediaPlay(false);
    }

    /**
     * 是否禁止媒体播放操作
     *
     * @return
     */
    public boolean isStopMediaPlay() {
        boolean isStopMediaPlay = AutoManager.getInstance().ShouldStopMediaPlay();
        Logutil.i(TAG, "isStopMediaPlay=" + isStopMediaPlay);
        return isStopMediaPlay;
    }

    /**
     * 是否有高优先级应用在运行
     *
     * @return
     */
    public boolean isHighPriorityAppRunning() {
        boolean b = AutoManager.getInstance().isHighPriorityAppRunning();
        if (b) {
            InterruptEventManager.getInstance().setHighPriorityAppRunning(b);
        }
        return b;
    }

    /**
     * 显示title
     */
    public void setTitleName(String className, String title) {
        setAppStatus(className, title, AutoConstants.AppStatus.RUN_FOREGROUND);
    }

    /**
     * 关闭屏保
     */
    public void closeScreenSave() {
        if (isScreenSavedOpened()) {
            Intent intent = new Intent();
            intent.setAction("com.semisky.action.CLOCK_SCREEN_SERVICE");
            intent.putExtra("CMD", 1);//关闭屏保
            MediaApplication.getContext().startService(intent);
        }
    }

    /**
     * 是否屏保打开
     *
     * @return
     */
    public boolean isScreenSavedOpened() {
        return AutoManager.getInstance().isScreenSavedOpened();
    }

    /**
     * 抑制多媒体启动监听
     */
    OnCloseDialogListener onCloseDialogListener;

    public void registerCloseDialogListener(OnCloseDialogListener onCloseDialogListener) {
        this.onCloseDialogListener = onCloseDialogListener;
    }

    public void unRegisterCloseDialogListener() {
        onCloseDialogListener = null;
    }

    /**
     * 注册倒车状态监听
     */
    IBackModeChanged.Stub iBackModeChanged = new IBackModeChanged.Stub() {
        @Override
        public void onBackModeChange(boolean b) throws RemoteException {
            notifyBackModeChanged(b);
            if (b) {
                if (onCloseDialogListener != null) {
                    onCloseDialogListener.closeDialog();
                }
                InterruptEventManager.getInstance().setInterruptEventFromParking(b);
            }
        }
    };

    public void registerBackModeListener() {
        AutoManager.getInstance().registerBackModeListener(iBackModeChanged);
    }

    public void registerBackModeChanged(IBackModeChange iBackModeChange) {
        if (iBackModeChangeList != null && iBackModeChange != null && !iBackModeChangeList.contains(iBackModeChange)) {
            iBackModeChangeList.add(iBackModeChange);
        }
    }

    public void unRegisterBackModeChanged(IBackModeChange iBackModeChange) {
        if (iBackModeChangeList != null && iBackModeChange != null && iBackModeChangeList.contains(iBackModeChange)) {
            iBackModeChangeList.remove(iBackModeChange);
        }
    }

    private void notifyBackModeChanged(boolean b) {
        if (iBackModeChangeList == null || iBackModeChangeList.size() <= 0) {
            return;
        }
        for (int i = 0; i < iBackModeChangeList.size(); i++) {
            iBackModeChangeList.get(i).backModeChanged(b);
        }
    }

    public void unRegisterBackModeListener() {
        InterruptEventManager.getInstance().setInterruptEventFromParking(false);
        AutoManager.getInstance().unregisterBackModeListener(iBackModeChanged);
    }

    public void registerBtStatusChanger(IBtCallStatus iBtCallStatus) {
        if (iBtCallStatuses != null && !iBtCallStatuses.contains(iBtCallStatus)) {
            iBtCallStatuses.add(iBtCallStatus);
        }
    }

    public void unRegisterBtStatusChange(IBtCallStatus iBtCallStatus) {
        if (iBtCallStatuses != null && iBtCallStatuses.contains(iBtCallStatus)) {
            iBtCallStatuses.remove(iBtCallStatus);
        }
    }

    private void notifyBtStatusChange(int status) {
        if (iBtCallStatuses != null && iBtCallStatuses.size() > 0) {
            for (int i = 0; i < iBtCallStatuses.size(); i++) {
                iBtCallStatuses.get(i).btStateChange(status);
            }
        }
    }

    /**
     * 蓝牙通话状态监听
     */
    IBtCallStatusChangeListener btCallStatusChangeListener = new IBtCallStatusChangeListener.Stub() {
        @Override
        public void onBtCallStatusChanged(int i) throws RemoteException {
            notifyBtStatusChange(i);
            if (i != 7) { //蓝牙状态。activity 启动时就一直为蓝牙电话状态
                if (onCloseDialogListener != null) {
                    onCloseDialogListener.closeDialog();
                }
                InterruptEventManager.getInstance().setInterruptEventFromBTCall(true);
            }
        }
    };

    public void registerBtCallStatusChangeListener() {
        AutoManager.getInstance().registerBtCallStatusChangeListener(btCallStatusChangeListener);
    }

    public void unRegisterBtCallStatusChangeListener() {
        InterruptEventManager.getInstance().setInterruptEventFromBTCall(false);
        AutoManager.getInstance().unregisterBtCallStatusChangeListener(btCallStatusChangeListener);
    }

    /**
     * 监听mode事件
     */
    private IKeyListener.Stub mKeyListener = new IKeyListener.Stub() {

        @Override
        public void onKey(int keyCode, int action) throws RemoteException {
            Logutil.i(TAG, "keycode : " + keyCode + " action : " + action);
            switch (keyCode) {
                case KeyManager.KEYCODE_MODE:
                    Logutil.e(TAG, "keyCode mode " + KeyManager.KEYCODE_MODE);
                    if (onCloseDialogListener != null) {
                        onCloseDialogListener.closeDialog();
                    }
                    InterruptEventManager.getInstance().setInterruptEventFromMode(true);
                    break;
                case KeyManager.KEYCODE_HOME:
                    Logutil.e(TAG, "keyCode home " + KeyManager.KEYCODE_HOME);
                    if (onCloseDialogListener != null) {
                        onCloseDialogListener.closeDialog();
                    }
                    InterruptEventManager.getInstance().setInterruptEventFromHome(true);
                    break;
                case KeyManager.KEYCODE_AM_FM:
                    Logutil.e(TAG, "keyCode fm " + KeyManager.KEYCODE_AM_FM);
                    if (onCloseDialogListener != null) {
                        onCloseDialogListener.closeDialog();
                    }
                    InterruptEventManager.getInstance().setInterruptEventFromAudio(true);
                    break;
                case KeyManager.KEYCODE_NAVI:
                    Logutil.e(TAG, "keyCode navi " + KeyManager.KEYCODE_NAVI);
                    if (onCloseDialogListener != null) {
                        onCloseDialogListener.closeDialog();
                    }
                    InterruptEventManager.getInstance().setInterruptEventFromNavi(true);
                    break;

            }
        }
    };

    public void registerKeyListener() {
        KeyManager.getInstance().setOnKeyListener(mKeyListener);
    }

    public void unRegisterKeyListener() {
        if (iBtCallStatuses != null && iBtCallStatuses.size() > 0) {
            iBtCallStatuses.clear();
        }
        if (iBackModeChangeList != null && iBackModeChangeList.size() > 0) {
            iBackModeChangeList.clear();
        }
        InterruptEventManager.getInstance().setInterruptEventFromMode(false);
        InterruptEventManager.getInstance().setInterruptEventFromNavi(false);
        InterruptEventManager.getInstance().setInterruptEventFromAudio(false);
        InterruptEventManager.getInstance().setInterruptEventFromHome(false);
        InterruptEventManager.getInstance().setHighPriorityAppRunning(false);
        InterruptEventManager.getInstance().setmFromLauncherStart(false);
        KeyManager.getInstance().unregisterOnKeyListener(mKeyListener);
    }

    /**
     * 设置多媒体允许启动状态
     */
    public void setAllowMusicPlay() {
        AutoManager.getInstance().setStopMediaPlay(false);
    }

    public boolean getAllowMusicPlay() {
        Logutil.i("lcc", "SemiskyIVIManager Allow Music Play :" + AutoManager.getInstance().ShouldStopMediaPlay());
        return AutoManager.getInstance().ShouldStopMediaPlay();
    }


    /**
     * 获取手刹状态
     */
    public boolean getHandBrakeState() {
        boolean b = AutoManager.getInstance().isHandBrakeMode();
        return b;
    }

    /**
     * 设置状态到中间件（关于上下一曲带来的问题）
     */
    public void setCurrentAppStatus(int appType, int status) {
        Logutil.i(TAG, "appType: " + appType + " status: " + status);
        AutoManager.getInstance().setCurrentAppStatus(appType, status);

    }

}
