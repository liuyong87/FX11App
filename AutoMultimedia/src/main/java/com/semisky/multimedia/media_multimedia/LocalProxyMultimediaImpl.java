package com.semisky.multimedia.media_multimedia;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.IProxyMultimedia;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.InterruptEventManager;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.USBCheckUtil;
import com.semisky.multimedia.media_usb.model.IMediaStorageDataModel;
import com.semisky.multimedia.media_usb.model.MediaStorageDataModel;
import com.semisky.multimedia.media_usb.view.MediaScanDialog;

import java.io.File;

/**
 * Created by LiuYong on 2018/9/3.
 */

public class LocalProxyMultimediaImpl extends IProxyMultimedia.Stub {
    private static final String TAG = Logutil.makeTagLog(LocalProxyMultimediaImpl.class);
    private static LocalProxyMultimediaImpl _INSTANCE;
    private Context mContext;
    private IMediaStorageDataModel mMediaStorageDataModel;
    private Handler _handler = new Handler(Looper.getMainLooper());

    private LocalProxyMultimediaImpl() {
        mMediaStorageDataModel = new MediaStorageDataModel();

    }

    public static LocalProxyMultimediaImpl getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new LocalProxyMultimediaImpl();
        }
        return _INSTANCE;
    }

    public void init(Context ctx) {
        this.mContext = ctx;
    }

    @Override
    public int getMultimediaAppFlag() throws RemoteException {
        int validAppFlag = -1;
        boolean isValidAppFlag = hasValidAppFlagWith(validAppFlag);
        validAppFlag = isValidAppFlag ? validAppFlag : -1;
        Logutil.i(TAG, "getMultimediaAppFlag() ,isValidAppFlag=" + isValidAppFlag);

        return validAppFlag;
    }

    @Override
    public boolean hasValidAppFlagWith(int appFlag) throws RemoteException {
        int size = -1;
        int sizeU2 = -1;
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                size = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB1);
                sizeU2 = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB2);
                break;
            case Definition.AppFlag.TYPE_VIDEO:
                size = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB1);
                sizeU2 = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB2);
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                size = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB1);
                sizeU2 = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB2);
                break;
            default:
                Logutil.w(TAG, "INVALID APP FLAG !!!");
                break;
        }
        boolean isValidAppFlag = (size > 0) ? true : false;
        boolean isValidAppFlagU2 = sizeU2 > 0 ? true : false;
        Logutil.i(TAG, "size: " + size + " sizeU2 " + sizeU2);
        boolean usb1MountState = USBManager.getInstance().getUsbOneMount();
        boolean usb2MountState = USBManager.getInstance().getUsbTwoMount();
        Logutil.i(TAG, "usb1MountState: " + usb1MountState + " usb2MountState " + usb2MountState);
        return (isValidAppFlag || isValidAppFlagU2) && (usb1MountState || usb2MountState);
    }


    @Override
    public void onLaunchMultimedia() throws RemoteException {


    }

    @Override
    public void onLaunchMusicPlayService() throws RemoteException {
        Logutil.i(TAG, "onLaunchMusicPlayService() ...");

        Logutil.i(TAG, "back play music");
        AppUtil.resumeBackgroundMusicPlay(mContext); //启动音乐服务，后台播放
        SemiskyIVIManager.getInstance().setAllowMusicPlay();//允许多媒体播放，如果是收音机在前台，断电后，插入U盘不会跳转到多媒体。


    }


    // 2018/10/9 查询是否有音乐文件
    public boolean hasMusicFile() {
        int size = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB1);
        return size > 0 ? true : false;
    }

    @Override
    public boolean canEnterMusic(int usbFlag) throws RemoteException {
        boolean canEnter = false;

        switch (usbFlag) {
            case Definition.FLAG_USB1:
                boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
                int mediaDataUsb1 = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB1);
                canEnter = isMountedUsb1 && mediaDataUsb1 > 0;
                break;
            case Definition.FLAG_USB2:
                boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
                int mediaDataUsb2 = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB2);
                canEnter = isMountedUsb2 && mediaDataUsb2 > 0;
                break;
        }
        Log.i(TAG, "canEnterMusic() ..." + canEnter);
        return canEnter;
    }

    @Override
    public boolean canEnterVideo(int usbFlag) throws RemoteException {
        boolean canEnter = false;

        switch (usbFlag) {
            case Definition.FLAG_USB1:
                boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
                int mediaDataUsb1 = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB1);
                canEnter = isMountedUsb1 && mediaDataUsb1 > 0;
                break;
            case Definition.FLAG_USB2:
                boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
                int mediaDataUsb2 = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB2);
                canEnter = isMountedUsb2 && mediaDataUsb2 > 0;
                break;
        }
        Log.i(TAG, "canEnterVideo() ..." + canEnter);
        return canEnter;
    }

    @Override
    public boolean canEnterPhoto(int usbFlag) throws RemoteException {
        boolean canEnter = false;

        switch (usbFlag) {
            case Definition.FLAG_USB1:
                boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
                int mediaDataUsb1 = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB1);
                canEnter = isMountedUsb1 && mediaDataUsb1 > 0;
                break;
            case Definition.FLAG_USB2:
                boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
                int mediaDataUsb2 = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB2);
                canEnter = isMountedUsb2 && mediaDataUsb2 > 0;
                break;
        }
        Log.i(TAG, "canEnterPhoto() ..." + canEnter);
        return canEnter;
    }
}
