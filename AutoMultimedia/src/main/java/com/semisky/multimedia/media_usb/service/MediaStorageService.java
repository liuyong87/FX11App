package com.semisky.multimedia.media_usb.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.AppFlag;
import com.semisky.multimedia.common.constants.Definition.MediaStorageConst;
import com.semisky.multimedia.common.interfaces.OnCloseDialogListener;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.mediascan.IMediaScanner;
import com.semisky.multimedia.media_usb.mediascan.MediaScanner;
import com.semisky.multimedia.media_usb.mediascan.OnMediaScannerListener;
import com.semisky.multimedia.media_usb.model.AppStartPolicyModel;
import com.semisky.multimedia.media_usb.model.IAppStartPolicyModel;
import com.semisky.multimedia.media_usb.model.IMediaStorageDataModel;
import com.semisky.multimedia.media_usb.model.MediaStorageDataModel;
import com.semisky.multimedia.media_usb.view.MediaScanDialog;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * 本地媒体存储服务
 *
 * @author liuyong
 */
public class MediaStorageService extends Service implements IMediaStorageService {
    private static final String TAG = Logutil.makeTagLog(MediaStorageService.class);
    private LocalRemoteCallbackList mLocalRemoteCallbackList = new LocalRemoteCallbackList();
    private IMediaScanner mMediaScanner;
    private IMediaStorageDataModel mMediaStorageDataModel;
    private IAppStartPolicyModel mAppStartPolicyModel;
    //    private boolean isScannerComplete=false;
    private Handler _handler = new MediaScanHandler(this);

    public class LocalRemoteCallbackList extends RemoteCallbackList<IMediaScannerStateListener> {

        @Override
        public void onCallbackDied(IMediaScannerStateListener callback) {
            super.onCallbackDied(callback);
            if (null != mLocalRemoteCallbackList) {
                mLocalRemoteCallbackList.unregister(callback);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logutil.d(TAG, "onCreate() ...");
        init();
        MediaStorageServiceProxy.getInstance().onAttached(this);
    }

    private void init() {
        mMediaScanner = MediaScanner.getInstance();
        mMediaScanner.registerOnMediaScannerListener(mOnMediaScannerListener);
        mMediaStorageDataModel = new MediaStorageDataModel();
        mAppStartPolicyModel = AppStartPolicyModel.getInstance();
        mAppStartPolicyModel.setOnAutoStartAppStateListener(mOnAutoStartAppStateListener);

    }

    private boolean isbindMediaStorageDataModel() {
        return (null != mMediaStorageDataModel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logutil.d(TAG, "onStartCommand() ...");
        if (null == intent) {
            Logutil.w(TAG, "onStartCommand() ...intent is Empty !!!");
            return START_NOT_STICKY;
        }
        handlerIntent(intent);
        return START_STICKY;
    }

    // 处理意图
    private void handlerIntent(Intent intent) {
        String action = intent.getAction();
        Logutil.i(TAG, "handlerIntent() action=" + action);
        int cmd = -1000;
        String usbPath = "";

        if (MediaStorageConst.ACTION_OPS_CONTROL.equals(action)) {
            cmd = intent.getIntExtra(MediaStorageConst.PARAM_CMD, -1000);// 获取控件命令
            usbPath = intent.getStringExtra(MediaStorageConst.PARAM_USB_PATH);
            Logutil.i(TAG, "handlerIntent() cmd=" + cmd + ",usbPath=" + usbPath);
            switch (cmd) {
                case MediaStorageConst.CMD_USB_MOUNTED:
                    handlerMountedUsb(usbPath);
                    break;
                case MediaStorageConst.CMD_USB_UNMOUNTED:
                    deleteAllMedia(usbPath);
                    handlerUnmountedUsb(usbPath); //卸载事件
                    break;
                case MediaStorageConst.CMD_DEL_DB_DATA:// 删除数据库媒体数据
                    deleteAllMedia(usbPath);
                    break;
            }
        } else if (Definition.MediaStorageConst.ACTION_DEBUG.equals(action)) {
            cmd = intent.getIntExtra(MediaStorageConst.PARAM_CMD, -1000);
            printLogger(cmd);
        }
    }


    private void deleteAllMedia(String usbPath) {
        int usbFlag = AppUtil.conversionUsbPathToUsbFlag(usbPath);
        Logutil.i(TAG, "deleteAllMedia() ..." + usbFlag);
        if (usbFlag != -1) {
            mMediaStorageDataModel.deleteAllMusics(usbFlag);
            mMediaStorageDataModel.deleteAllVideos(usbFlag);
            mMediaStorageDataModel.deleteAllPhotos(usbFlag);
            mMediaStorageDataModel.deleteAllLyrics(usbFlag);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Logutil.d(TAG, "onBind() ...");
        return MediaStorageServiceProxy.getInstance();
    }

    @Override
    public void printLogger(int cmd) {
        Logutil.d(TAG, "printLogger() CMD=" + cmd);
        switch (cmd) {
            case Definition.MediaStorageConst.CMD_PRINT_LOG:
                printCallBackInfo();
                break;
            default:
                break;
        }
    }

    private OnMediaScannerListener mOnMediaScannerListener = new OnMediaScannerListener() {

        @Override
        public void onScannerStart(int usbFlag) {
            onNotifyMediaScannerStart(usbFlag);
        }

        @Override
        public void onScanning(int usbFlag) {
            onNotifyMediaScanning(usbFlag);
        }

        @Override
        public void onScannerStop(int usbFlag) {
            onNotifyMediaScannerStoped(usbFlag);
        }

        @Override
        public void onScannerDone(int usbFlag) {
            onNotifyMediaScannerDone(usbFlag);
        }

    };

    private IAppStartPolicyModel.OnAutoStartAppStateListener mOnAutoStartAppStateListener = new IAppStartPolicyModel.OnAutoStartAppStateListener() {
        @Override
        public void onJumpTo(int appFlag, boolean isForeground) {
            Logutil.i(TAG, "onJumpTo() appFlag=" + appFlag + " , isForeground=" + isForeground);
            if (appFlag >= AppFlag.TYPE_MUSIC && appFlag <= AppFlag.TYPE_LIST) {
                if (isForeground) {
                    AppUtil.enterPlayerView(appFlag);
                    _handler.sendEmptyMessage(MediaScanHandler.MSG_MEDIA_SACAN_DIALOG_DISMISS);
                } else if (appFlag == AppFlag.TYPE_MUSIC && !isForeground) {
                    AppUtil.resumeBackgroundMusicPlay(MediaStorageService.this);
                    _handler.sendEmptyMessage(MediaScanHandler.MSG_MEDIA_SACAN_DIALOG_DISMISS);
                }
            }
        }
    };

    @Override
    public void registerOnMediaScannerStateListener(IMediaScannerStateListener listener) {
        Logutil.d(TAG, "registerOnMediaScannerStateListener() ...");
        if (null != mLocalRemoteCallbackList) {
            synchronized (mLocalRemoteCallbackList) {
                mLocalRemoteCallbackList.register(listener);
//                printCallBackInfo();
            }
        }
    }

    @Override
    public void unregisterOnMediaScannerStateListener(IMediaScannerStateListener listener) {
        Logutil.d(TAG, "unregisterOnMediaScannerStateListener() ...");
        if (null != mLocalRemoteCallbackList) {
            synchronized (mLocalRemoteCallbackList) {
                mLocalRemoteCallbackList.unregister(listener);
//                printCallBackInfo();
            }
        }
    }

    // 打印监听回调接口对象信息
    private synchronized void printCallBackInfo() {
        int callbackSize = 0;
        try {
            synchronized (mLocalRemoteCallbackList) {
                callbackSize = (null != mLocalRemoteCallbackList ? mLocalRemoteCallbackList.getRegisteredCallbackCount() : 0);
                Logutil.d(TAG, "printLogger() callbackSize=" + callbackSize);
                if (null != mLocalRemoteCallbackList) {
                    mLocalRemoteCallbackList.beginBroadcast();
                    for (int j = 0; j < mLocalRemoteCallbackList.getRegisteredCallbackCount(); j++) {
                        IMediaScannerStateListener listener = mLocalRemoteCallbackList.getBroadcastItem(j);
                        Logutil.d(TAG, "printLogger() listenerObj=" + listener);
                    }
                    mLocalRemoteCallbackList.finishBroadcast();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onNotifyMediaScannerStart(int usbFlag) {
        Logutil.i(TAG, "onNotifyMediaScannerStart() usbFlag=" + usbFlag);
        if (null != mLocalRemoteCallbackList && mLocalRemoteCallbackList.getRegisteredCallbackCount() > 0) {
            synchronized (mLocalRemoteCallbackList) {
                final int N = mLocalRemoteCallbackList.getRegisteredCallbackCount();
                mLocalRemoteCallbackList.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        mLocalRemoteCallbackList.getBroadcastItem(i).onScannerStart(usbFlag);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mLocalRemoteCallbackList.finishBroadcast();
        }
    }

    private void onNotifyMediaScanning(int usbFlag) {
        Logutil.i(TAG, "onNotifyMediaScanning() usbFlag=" + usbFlag);
        if (null != mLocalRemoteCallbackList && mLocalRemoteCallbackList.getRegisteredCallbackCount() > 0) {
            synchronized (mLocalRemoteCallbackList) {
                mLocalRemoteCallbackList.beginBroadcast();
                final int N = mLocalRemoteCallbackList.getRegisteredCallbackCount();
                for (int i = 0; i < N; i++) {
                    try {
                        mLocalRemoteCallbackList.getBroadcastItem(i).onScanning(usbFlag);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mLocalRemoteCallbackList.finishBroadcast();
        }
    }

    private void onNotifyMediaScannerStoped(int usbFlag) {
        Logutil.i(TAG, "onNotifyMediaScannerStoped() usbFlag=" + usbFlag);
        if (null != mLocalRemoteCallbackList && mLocalRemoteCallbackList.getRegisteredCallbackCount() > 0) {
            synchronized (mLocalRemoteCallbackList) {
                mLocalRemoteCallbackList.beginBroadcast();
                final int N = mLocalRemoteCallbackList.getRegisteredCallbackCount();
                for (int i = 0; i < N; i++) {
                    try {
                        mLocalRemoteCallbackList.getBroadcastItem(i).onScannerStoped(usbFlag);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mLocalRemoteCallbackList.finishBroadcast();
        }
    }

    private void onNotifyMediaScannerDone(int usbFlag) {
        Logutil.i(TAG, "onNotifyMediaScannerDone() usbFlag=" + usbFlag);
        if (null != mLocalRemoteCallbackList && mLocalRemoteCallbackList.getRegisteredCallbackCount() > 0) {
            synchronized (mLocalRemoteCallbackList) {
                mLocalRemoteCallbackList.beginBroadcast();
                final int N = mLocalRemoteCallbackList.getRegisteredCallbackCount();
                for (int i = 0; i < N; i++) {
                    try {
                        mLocalRemoteCallbackList.getBroadcastItem(i).onScannerDone(usbFlag);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mLocalRemoteCallbackList.finishBroadcast();
        }
    }


    @Override
    public long insertFavoriteMusic(MusicInfo musicInfo) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.insertFavoriteMusic(musicInfo);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MusicInfo> queryAllMusics(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryAllMusics(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MusicInfo> queryAllFavoriteMusics(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryAllFavoriteMusics(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isFavoriteWithSpecifyMusicUrl(int usbFlag, String url) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.isFavoriteWithSpecifyMusicUrl(usbFlag, url);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VideoInfo> queryAllVideos(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryAllVideos(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PhotoInfo> queryAllPhotos(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryAllPhotos(usbFlag);
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FolderInfo> querySpecifyDirectoryUnder(String curDir) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.querySpecifyDirectoryUnder(curDir);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public int queryMusicsSize(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryMusicsSize(usbFlag);
        }
        return 0;
    }

    @Override
    public int queryVideosSize(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryVideosSize(usbFlag);
        }
        return 0;
    }

    @Override
    public int queryPhotosSize(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryPhotosSize(usbFlag);
        }
        return 0;
    }

    @Override
    public String queryLyricUrl(String url) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.queryLyricUrl(url);
        }
        return null;
    }

    @Override
    public long deleteAllMusics(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.deleteAllMusics(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteAllVideos(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.deleteAllVideos(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteAllPhotos(int usbFlag) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.deleteAllPhotos(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteFavoriteMusic(int id) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.deleteFavoriteMusic(id);
        }
        return 0;
    }

    @Override
    public long deleteFavoriteWithMusicUrl(int usbFlag, String url) {
        if (isbindMediaStorageDataModel()) {
            return mMediaStorageDataModel.deleteFavoriteWithMusicUrl(usbFlag, url);
        }
        return 0;
    }

    @Override
    public void deleteBatchFavorite(List<String> list) {
        if (isbindMediaStorageDataModel()) {
            mMediaStorageDataModel.deleteBatchFavorite(list);
        }
    }

    @Override
    public String getScanFirstMusicUrl(int usbFlag) {
        return mMediaScanner.getScanFirstMusicUrl(usbFlag);
    }

    @Override
    public String getScanFirstVideoUrl(int usbFlag) {
        return mMediaScanner.getScanFirstVideoUrl(usbFlag);
    }

    @Override
    public String getScanFirstPhotoUrl(int usbFlag) {
        return mMediaScanner.getScanFirstPhotoUrl(usbFlag);
    }

    @Override
    public boolean isMediaScanFinished(int usbFlag) {
        return mMediaScanner.isMediaScanFinished(usbFlag);
    }

    @Override
    public List<FolderInfo> queryAllMusicFolder(int usbFlag, String path) {
        return mMediaStorageDataModel.queryAllMusicFolder(usbFlag, path);
    }

    @Override
    public List<MusicInfo> queryFolderUnderMusic(int usbFlag, String path) {
        return mMediaStorageDataModel.queryFolderUnderMusic(usbFlag, path);
    }

    @Override
    public long deleteAllLyrics(int usbFlag) {
        return mMediaStorageDataModel.deleteAllLyrics(usbFlag);
    }

    private MediaScanDialog mMediaScanDialog;

//    // 显示U盘未挂载信息弹窗
//    private void alertUSBUnmounted() {
//        Logutil.i(TAG, "showMediaScanDialog() ...");
//        if (null == mMediaScanDialog) {
//            mMediaScanDialog = new MediaScanDialog(this);
//        }
//        if (null != mMediaScanDialog && !mMediaScanDialog.isShowing()) {
//            mMediaScanDialog.show();
//        }
//        mMediaScanDialog.alertUSBUnmounted();
//    }
//
//    // 显示U盘加载失败信息弹窗
//    private void alertUSBUFailure(int delay) {
//        Logutil.i(TAG, "showMediaScanDialog() ...");
//        if (null == mMediaScanDialog) {
//            mMediaScanDialog = new MediaScanDialog(this);
//        }
//        if (null != mMediaScanDialog && !mMediaScanDialog.isShowing()) {
//            mMediaScanDialog.show();
//        }
//        mMediaScanDialog.alertUSBFailure();
//        _handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mMediaScanDialog.cancel();
//            }
//        }, delay);
//    }
//
//    // 显示媒体加载弹窗
//    private void alertUSBLoadding() {
//        Logutil.i(TAG, "showMediaScanDialog() ...");
//        if (null == mMediaScanDialog) {
//            mMediaScanDialog = new MediaScanDialog(this);
//        }
//        if (null != mMediaScanDialog && !mMediaScanDialog.isShowing()) {
//            mMediaScanDialog.show();
//        }
//        ToastCustom.closeDialog();
//        mMediaScanDialog.alertUSBLoadding();
//        /**
//         * 因极点操作，插入U盘瞬间点击launcher可能会进入到列表状态，挂载后，会appExit，又杀掉activity,扫描
//         * 过程中再次启动多媒体（bug8573）
//         *   设置一个状态，才允许启动 .
//         *
//         */
//        _handler.postDelayed(startAPPRunnableFlag, 500);
//
//
//    }
//
//    Runnable startAPPRunnableFlag = new Runnable() {
//        @Override
//        public void run() {
//
//            InterruptEventManager.getInstance().setmFromLauncherStart(true);
//        }
//    };
//
//    // 关闭媒体加载弹窗
//    private void closeMediaScanDialog() {
//        Logutil.i(TAG, "closeMediaScanDialog() ...");
//        if (null != mMediaScanDialog && mMediaScanDialog.isShowing()) {
//            mMediaScanDialog.dismiss();
//        }
//    }

    private static class MediaScanHandler extends Handler {
        private static final int MSG_MEDIA_SACAN_DIALOG_DISMISS = 1;
        private static final int MSG_MEDIA_SCAN_STATUS_LOADDING = 2;
        private static final int MSG_MEDIA_SCAN_STATUS_USB_UNMOUNTED = 3;
        private static final int MSG_MEDIA_SCAN_STATUS_USB_FAILURE = 4;

        WeakReference<MediaStorageService> mServiceRfr;

        MediaScanHandler(MediaStorageService service) {
            mServiceRfr = new WeakReference<MediaStorageService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null == mServiceRfr || null == mServiceRfr.get()) {
                return;
            }
        }
    }

    // utils

    // 处理U盘挂载
    protected void handlerMountedUsb(String usbPath) {

        registerAutoServiceListener();//关于中间件的所有打断监听事件
        deleteAllMedia(usbPath);// 清除媒体数据库数据
        mAppStartPolicyModel.onRelease();
        mMediaScanner.onUSBMounted(usbPath);// 扫描媒体文件
    }

    // 处理U盘卸载
    protected void handlerUnmountedUsb(String usbPath) {
//        _handler.removeCallbacks(startAPPRunnableFlag);//这个状态是在插入U盘瞬间狂点击launcher,导致bug，避免在刚显示就拔出U盘，标记位不为初始值
        unRegisterAutoServiceListener();//注销关于中间件所有的打断事件
        mMediaScanner.onUSBUnMounted(usbPath);// 停止扫描媒体文件
        mAppStartPolicyModel.onRelease();
        // TODO: 2019/4/28

    }

    OnCloseDialogListener closeDialogListener = new OnCloseDialogListener() {
        @Override
        public void closeDialog() {
            _handler.sendEmptyMessage(MediaScanHandler.MSG_MEDIA_SACAN_DIALOG_DISMISS);
        }
    };

    /**
     * 判断多媒体界面是否在栈顶
     */
    private void getActivityIsTop() {
//        if (MediaApplication.mActivitys != null && MediaApplication.mActivitys.size() > 0) {
//            ActivityManager manager = (ActivityManager) getSystemService(MediaApplication.getContext().ACTIVITY_SERVICE);
//            String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
//            for (Activity activity : MediaApplication.mActivitys) {
//                if (activity.getClass().getName().equals(name)) {
//                    MediaApplication.setIsStartMainActivity(true);
//                    return;
//                }
//            }
//        }

    }

    private void registerAutoServiceListener() {
        SemiskyIVIManager.getInstance().registerCloseDialogListener(closeDialogListener);
        SemiskyIVIManager.getInstance().registerKeyListener(); //从中间件监听mode事件
        SemiskyIVIManager.getInstance().registerBackModeListener();//挂载时监听倒车状态
        SemiskyIVIManager.getInstance().registerBtCallStatusChangeListener();//挂载时监听蓝牙电话状态
    }

    private void unRegisterAutoServiceListener() {
        SemiskyIVIManager.getInstance().unRegisterCloseDialogListener();//关闭dialog 监听注销
        SemiskyIVIManager.getInstance().unRegisterKeyListener();
        SemiskyIVIManager.getInstance().unRegisterBackModeListener();//卸载时注销监听倒车状态
        SemiskyIVIManager.getInstance().unRegisterBtCallStatusChangeListener();//卸载时注销监听蓝牙电话状态
    }
}
