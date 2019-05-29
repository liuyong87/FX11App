package com.semisky.multimedia.media_usb.model;

import android.util.Log;

import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.AppFlag;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.manager.InterruptEventManager;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.USBCheckUtil;
import com.semisky.multimedia.media_music.view.MusicPlayerActivity;
import com.semisky.multimedia.media_photo.manager.PhotoManager;
import com.semisky.multimedia.media_photo.view.PhotoPlayerActivity;
import com.semisky.multimedia.media_video.manager.VideoStateManager;
import com.semisky.multimedia.media_video.view.VideoPlayerActivity;

import java.io.File;

/**
 * 媒体应用启动策略模型
 * Created by LiuYong on 2018/8/8.
 */

public class AppStartPolicyModel implements IAppStartPolicyModel {
    private static final String TAG = Logutil.makeTagLog(AppStartPolicyModel.class);
    private static AppStartPolicyModel _INSTANCE;
    private IMediaStorageDataModel mMediaStorageDataModel;
    private OnAutoStartAppStateListener mOnAutoStartAppStateListener;

    private boolean mIsStartFirstApp = false;// 是否启动了首个多媒体应用界面（music、video、photo）

    private AppStartPolicyModel() {
        mMediaStorageDataModel = new MediaStorageDataModel();
    }

    public static AppStartPolicyModel getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new AppStartPolicyModel();
        }
        return _INSTANCE;
    }

    @Override
    public void setOnAutoStartAppStateListener(OnAutoStartAppStateListener l) {
        this.mOnAutoStartAppStateListener = l;
    }

    @Override
    public void unRegisterOnAutoStartAppStateListener() {
        this.mOnAutoStartAppStateListener = null;
    }

    @Override
    public void onUserStartApp() {

        if (!isMountedUsb()) {
            Log.i(TAG, "onUserSkipApp() USB_UNMOUNTED !!!");
            return;
        }


    }

    /**
     * 自动跳转条件：
     * 1.）无高优先级，方可跳转
     * 跳转断点记忆播放界面
     */
    @Override
    public void onAutoStartApp(boolean fromScanDoneEvent) {
        // Level 1

        boolean isHightProrityAppRunning = SemiskyIVIManager.getInstance().isHighPriorityAppRunning();
        boolean isStopMediaPlay = SemiskyIVIManager.getInstance().isStopMediaPlay();
        boolean hasInterruptEvent = InterruptEventManager.getInstance().hasInterruptEvent();
        boolean isNaviAtForeground = SemiskyIVIManager.getInstance().isNaviAtForeground();
        boolean hasTopMusic = AppActivityManager.getInstance().isTopActivity(MusicPlayerActivity.class.getName());
        boolean hasTopVideo = AppActivityManager.getInstance().isTopActivity(VideoPlayerActivity.class.getName());
        boolean hasTopPhoto = AppActivityManager.getInstance().isTopActivity(PhotoPlayerActivity.class.getName());

        Logutil.i(TAG, "=======Level 1 Start=========");
        Logutil.i(TAG, "onAutoStartApp() isHightProrityAppRunning=" + isHightProrityAppRunning);
        Logutil.i(TAG, "onAutoStartApp() isStopMediaPlay=" + isStopMediaPlay);
        Logutil.i(TAG, "onAutoStartApp() hasInterruptEvent=" + hasInterruptEvent);
        Logutil.i(TAG, "onAutoStartApp() mIsStartFirstApp=" + mIsStartFirstApp);
        Logutil.i(TAG, "onAutoStartApp() isNaviAtForeground=" + isNaviAtForeground);
        Logutil.i(TAG, "onAutoStartApp() hasTopMusic=" + hasTopMusic);
        Logutil.i(TAG, "onAutoStartApp() hasTopVideo=" + hasTopVideo);
        Logutil.i(TAG, "onAutoStartApp() hasTopPhoto=" + hasTopPhoto);
        Logutil.i(TAG, "================");
        if (isStopMediaPlay){
            Logutil.i(TAG,"stop startAPP");
            return;
        }
        if (hasTopMusic || hasTopVideo || hasTopPhoto){
            mIsStartFirstApp = true;
            return;
        }
        if (isHightProrityAppRunning
                || hasInterruptEvent
                || mIsStartFirstApp) {
            Logutil.w(TAG, "onAutoStartApp() STOP JUMP TO MULTIMEDIA APP !!!");
            return;
        }
        startApp(fromScanDoneEvent,isNaviAtForeground);

    }

    @Override
    public void onRelease() {
        Logutil.i(TAG, "onRelease() ...");
        this.mIsStartFirstApp = false;
        VideoStateManager.getInstance().clean();
        PhotoManager.getPhotoManager().cleanState();
    }

    //---------------------- utils------------------------------------------

    /**
     * 多媒体自启动状态改变通知
     *
     * @param appFlag
     * @param isForeground
     */
    private void notifyAutoStartAppStateChange(int appFlag, boolean isForeground) {
        if (null != mOnAutoStartAppStateListener) {
            this.mOnAutoStartAppStateListener.onJumpTo(appFlag, isForeground);
        }
    }

    // 应用标识是否有效相对于媒体数据
    private boolean isAvalidAppFlagRelativeToMediaData(int appFlag) {
        int size = -1;
        switch (appFlag) {
            case AppFlag.TYPE_MUSIC:
                size = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB1);
                break;
            case AppFlag.TYPE_VIDEO:
                size = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB1);
                break;
            case AppFlag.TYPE_PHOTO:
                size = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB1);
                break;
            default:
                Logutil.w(TAG, "INVALID APP FLAG !!!");
                break;
        }
        boolean isValidAppFlag = (size > 0) ? true : false;
        return isValidAppFlag;
    }

    // 应用标识是否有效相对断点记忆
    private boolean isAvalidAppFlagRelativeToBrakPointMemory(int appFlag) {
        String lastUrl = null;
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                lastUrl = PreferencesManager.getLastMusicUrl(PreferencesManager.getLastMusicSourceUsbFlag());
                break;
            case Definition.AppFlag.TYPE_VIDEO:
                lastUrl = PreferencesManager.getLastVideoUrl(PreferencesManager.getLastVideoSourceUsbFlag());
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                lastUrl = PreferencesManager.getLastPhotoUrl(PreferencesManager.getLastPhotoSourceUsbFlag());
                break;
            default:
                Logutil.w(TAG, "INVALID APP FLAG !!!");
                break;
        }
        boolean isvalidLastUrl = (null != lastUrl && new File(lastUrl).exists());
        if (Definition.DEBUG_ENG) {
            Logutil.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Logutil.i(TAG, "appFlag=" + appFlag);
            Logutil.i(TAG, "lastUrl=" + lastUrl);
            Logutil.i(TAG, "isvalidLastUrl=" + isvalidLastUrl);
            Logutil.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        return isvalidLastUrl;
    }

    // 获取断点应用标识
    private int getLastAppFlag() {
        int lastAppFlag = PreferencesManager.getLastAppFlag();
        if (Definition.DEBUG_ENG) {
            Logutil.i(TAG, "getAppFlag() ..." + lastAppFlag);
        }
        return lastAppFlag;
    }

    // U盘是否挂载
    private boolean isMountedUsb() {
        boolean isMountedUsb = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
        if (Definition.DEBUG_ENG) {
            Logutil.i(TAG, "isMountedUsb() ..." + isMountedUsb);
        }
        return isMountedUsb;
    }
    //启动app
    private void startApp(boolean fromScanDoneEvent,boolean isNaviAtForeground){
        int appFlag = getLastAppFlag();

        Logutil.i(TAG, "=======Level 2 Start=========");
        Logutil.i(TAG, "onAutoStartApp() appFlag=" + appFlag);
        Logutil.i(TAG, "=============================");

        if (AppFlag.TYPE_PHOTO == appFlag || AppFlag.TYPE_INVALID == appFlag) {
            appFlag = AppFlag.TYPE_MUSIC;
        }

        boolean isAvalidAppFlagRelativeToBrakPointMemory = isAvalidAppFlagRelativeToBrakPointMemory(appFlag);


        if (!isAvalidAppFlagRelativeToBrakPointMemory) {
            appFlag = AppFlag.TYPE_MUSIC;
            isAvalidAppFlagRelativeToBrakPointMemory = isAvalidAppFlagRelativeToBrakPointMemory(appFlag);
        }

        Logutil.i(TAG, "=======Level 3 Start=========");
        Logutil.i(TAG, "onAutoStartApp() isAvalidAppFlagRelativeToBrakPointMemory=" + isAvalidAppFlagRelativeToBrakPointMemory);
        Logutil.i(TAG, "onAutoStartApp() appFlag=" + appFlag);
        Logutil.i(TAG, "=============================");

        // 播放记忆媒体
        if (isAvalidAppFlagRelativeToBrakPointMemory) {
            if (isNaviAtForeground) {
                if (AppFlag.TYPE_MUSIC == appFlag) {
                    // 后台播放断点记忆音乐
                    mIsStartFirstApp = true;
                    notifyAutoStartAppStateChange(appFlag,false);
                }
                return;
            } else {
                // 跳转对应应用标识界面媒体播放
                mIsStartFirstApp = true;
                notifyAutoStartAppStateChange(appFlag,true);
            }
            return;
        }

        boolean isAvalidAppFlagRelativeToMusicData = isAvalidAppFlagRelativeToMediaData(AppFlag.TYPE_MUSIC);
        String scanFirstMusicUrl = PreferencesManager.getScanFirstMusicUrl(Definition.FLAG_USB1);
        boolean isExistsByScanFirstMusicUrl = (null != scanFirstMusicUrl && new File(scanFirstMusicUrl).exists() ? true : false);

        Logutil.i(TAG, "=======Level 4 Start=========");
        Logutil.i(TAG, "onAutoStartApp() isAvalidAppFlagRelativeToMusicData=" + isAvalidAppFlagRelativeToMusicData);
        Logutil.i(TAG, "onAutoStartApp() scanFirstMusicUrl=" + scanFirstMusicUrl);
        Logutil.i(TAG, "onAutoStartApp() isExistsByScanFirstMusicUrl=" + isExistsByScanFirstMusicUrl);
        Logutil.i(TAG, "=============================");

        // 播放默认的媒体资源（BM2718项目默认播放媒体音乐资源）
        if (isAvalidAppFlagRelativeToMusicData) {
            if (isExistsByScanFirstMusicUrl) {
                if (isNaviAtForeground) {
                    // 后台启动音乐播放操作
                    mIsStartFirstApp = true;
                    notifyAutoStartAppStateChange(AppFlag.TYPE_MUSIC,false);
                } else {
                    AppUtil.enterPlayerView(AppFlag.TYPE_MUSIC);
                    mIsStartFirstApp = true;
                    notifyAutoStartAppStateChange(AppFlag.TYPE_MUSIC,true);
                }
            }
            return;
        }

        Logutil.i(TAG, "=======Level 5 Start=========");
        Logutil.i(TAG, "onAutoStartApp() fromScanDoneEvent=" + fromScanDoneEvent);
        Logutil.i(TAG, "=============================");
        if (fromScanDoneEvent) {
            mIsStartFirstApp = true;
            notifyAutoStartAppStateChange(AppFlag.TYPE_LIST,true);
        }

    }
}
