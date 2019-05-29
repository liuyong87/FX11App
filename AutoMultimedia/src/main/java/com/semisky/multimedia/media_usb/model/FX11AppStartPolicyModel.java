package com.semisky.multimedia.media_usb.model;

import android.content.Intent;
import android.util.Log;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.USBCheckUtil;

public class FX11AppStartPolicyModel extends AbstractAppStartPolicy {
    private static final String TAG = Logutil.makeTagLog(FX11AppStartPolicyModel.class);
    private static FX11AppStartPolicyModel _INSTANCE;
    private MediaStorageAccessProxyModel mDataModel;

    // Constructor
    private FX11AppStartPolicyModel() {
        mDataModel = MediaStorageAccessProxyModel.getInstance();
    }

    public static FX11AppStartPolicyModel getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new FX11AppStartPolicyModel();
        }
        return _INSTANCE;
    }


    @Override
    public void handlerAppStartEvent(Intent intent) {
        handlerStartMediaCheck(intent);
    }

    private void handlerStartMediaCheck(Intent intent) {
        // 前后台标识（只有Music支持前后播放）
        int startMode = intent.getIntExtra(AutoConstants.START_MODE_KEY, AutoConstants.AppStatus.RUN_FOREGROUND);
        // 是否为前台标识
        boolean isForeground = startMode == AutoConstants.AppStatus.RUN_FOREGROUND;
        // 多媒体源标识（FX11：目前是USB1、USB2、BT Music）
        int mediaSource = intent.getIntExtra(AutoConstants.MEDIA_START_SOURCE_KEY, AutoConstants.MediaSource.USB1);
        // 多媒体应用标识（FX11目前多媒体应用包括：本地音乐、本地视频、本地图片、BT Music）
        int mediaAppFlag = intent.getIntExtra(AutoConstants.MEDIA_START_TYPE_KEY, AutoConstants.MediaType.MUSIC);

        Log.i(TAG, "================");
        Log.i(TAG, "handlerStartMediaCheck startMode :" + startMode);
        Log.i(TAG, "handlerStartMediaCheck isForeground :" + isForeground);
        Log.i(TAG, "handlerStartMediaCheck mediaSource :" + mediaSource);
        Log.i(TAG, "handlerStartMediaCheck mediaAppFlag :" + mediaAppFlag);
        Log.i(TAG, "================");
        switch (mediaAppFlag) {
            case AutoConstants.MediaType.MUSIC:
                Log.i(TAG, "MediaType.MUSIC ...");
                boolean hasMusicData = hasMusicData(mediaSource);
                Log.i(TAG, "MediaType.MUSIC hasMusicData :" + hasMusicData);
                // 1.检查当前传递过来的指定USB标识,媒体数据库是否有音乐数据
                if (hasMusicData) {

                    PreferencesManager.saveLastMusicSourceUsbFlag(mediaSource);
                    AppUtil.jumpToLocalMusic(mediaSource, isForeground);
                }
                // 2.检查其它USB源是否有数据
                else {
                    // 获取其它USB源
                    int newMediaSource = mediaSource == Definition.FLAG_USB1 ? Definition.FLAG_USB2 : Definition.FLAG_USB1;
                    boolean hasOtherUsbSourceData = hasMusicData(newMediaSource);

                    Log.i(TAG, "MediaType.MUSIC newMediaSource :" + newMediaSource);
                    Log.i(TAG, "MediaType.MUSIC hasOtherUsbSourceData :" + hasOtherUsbSourceData);
                    // 新USB源是否有数据
                    if (hasOtherUsbSourceData) {
                        PreferencesManager.saveLastMusicSourceUsbFlag(newMediaSource);
                        AppUtil.jumpToLocalMusic(newMediaSource, isForeground);
                    } else {
                        // 以上条件都不成立，默认进入音乐音乐USB1列表
                        PreferencesManager.saveLastMusicSourceUsbFlag(Definition.FLAG_USB1);
                        AppUtil.jumpToLocalMusicList(Definition.FLAG_USB1);
                    }

                }
                break;
            case AutoConstants.MediaType.VIDEO:
                Log.i(TAG, "MediaType.VIDEO ...");
                boolean hasVideoData = hasVideoData(mediaSource);
                Log.i(TAG, "MediaType.VIDEO hasVideoData :" + hasVideoData);
                // 1.检查当前传递过来的指定USB标识,媒体数据库是否有视频数据
                if (hasVideoData) {
                    PreferencesManager.saveLastVideoSourceUsbFlag(mediaSource);
                    AppUtil.jumpToLocalVideo(mediaSource);
                }
                // 2.检查其它USB标识,媒体数据库是否有视频数据
                else {
                    // 获取其它USB源
                    int newMediaSource = mediaSource == Definition.FLAG_USB1 ? Definition.FLAG_USB2 : Definition.FLAG_USB1;
                    boolean hasOtherUsbSourceData = hasVideoData(newMediaSource);
                    Log.i(TAG, "MediaType.VIDEO newMediaSource :" + newMediaSource);
                    Log.i(TAG, "MediaType.VIDEO hasOtherUsbSourceData :" + hasOtherUsbSourceData);
                    if (hasOtherUsbSourceData) {
                        PreferencesManager.saveLastVideoSourceUsbFlag(newMediaSource);
                        AppUtil.jumpToLocalVideo(newMediaSource);
                    } else {
                        PreferencesManager.saveLastVideoSourceUsbFlag(Definition.FLAG_USB1);
                        AppUtil.jumpToLocalVideoList(Definition.FLAG_USB1);
                    }
                }
                break;
            case AutoConstants.MediaType.PICTURE:
                Log.i(TAG, "MediaType.PICTURE ...");
                boolean hasPhotoData = hasPhotoData(mediaSource);
                Log.i(TAG, "MediaType.PICTURE hasPhotoData :" + hasPhotoData);
                // 1.检查当前传递过来的指定USB标识,媒体数据库是否有图片数据
                if (hasPhotoData) {
                    PreferencesManager.saveLastPhotoSourceUsbFlag(mediaSource);
                    AppUtil.jumpToLocalPhoto(mediaSource);
                }
                // 2.检查其它USB标识,媒体数据库是否有图片数据
                else {
                    int newMediaSource = mediaSource == AutoConstants.MediaSource.USB1 ? Definition.FLAG_USB2 : Definition.FLAG_USB1;
                    boolean hasOtherUsbSourceMediaData = hasPhotoData(newMediaSource);
                    Log.i(TAG, "MediaType.PICTURE newMediaSource :" + newMediaSource);
                    Log.i(TAG, "MediaType.PICTURE hasOtherUsbSourceMediaData :" + hasOtherUsbSourceMediaData);
                    if (hasOtherUsbSourceMediaData) {
                        PreferencesManager.saveLastPhotoSourceUsbFlag(newMediaSource);
                        AppUtil.jumpToLocalPhoto(newMediaSource);
                    } else {
                        PreferencesManager.saveLastPhotoSourceUsbFlag(Definition.FLAG_USB1);
                        AppUtil.jumpToLocalPhotoList(Definition.FLAG_USB1);
                    }
                }
                break;
        }
    }


    // 检查当前USB源媒体音乐数据
    boolean hasMusicData(int usbFlag) {

        boolean hasData = false;
        if (Definition.FLAG_USB1 == usbFlag) {
            boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
            int listSize1 = mDataModel.queryMusicsSize(Definition.FLAG_USB1);
            if (isMountedUsb1 && listSize1 > 0) {
                hasData = true;
            }
        } else if (Definition.FLAG_USB2 == usbFlag) {
            boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
            int listSize2 = mDataModel.queryMusicsSize(Definition.FLAG_USB2);
            if (isMountedUsb2 && listSize2 > 0) {
                hasData = true;
            }
        }
        return hasData;
    }

    // 检查当前USB源媒体视频数据
    boolean hasVideoData(int usbFlag) {

        boolean hasData = false;
        if (Definition.FLAG_USB1 == usbFlag) {
            boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
            int listSize1 = mDataModel.queryVideosSize(Definition.FLAG_USB1);
            if (isMountedUsb1 && listSize1 > 0) {
                hasData = true;
            }
        } else if (Definition.FLAG_USB2 == usbFlag) {
            boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
            int listSize2 = mDataModel.queryVideosSize(Definition.FLAG_USB2);
            if (isMountedUsb2 && listSize2 > 0) {
                hasData = true;
            }
        }
        return hasData;
    }

    // 检查当前USB源媒体图片数据
    boolean hasPhotoData(int usbFlag) {

        boolean hasData = false;
        if (Definition.FLAG_USB1 == usbFlag) {
            boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
            int listSize1 = mDataModel.queryPhotosSize(Definition.FLAG_USB1);
            if (isMountedUsb1 && listSize1 > 0) {
                hasData = true;
            }
        } else if (Definition.FLAG_USB2 == usbFlag) {
            boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
            int listSize2 = mDataModel.queryPhotosSize(Definition.FLAG_USB2);
            if (isMountedUsb2 && listSize2 > 0) {
                hasData = true;
            }
        }
        return hasData;
    }


}
