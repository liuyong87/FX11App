package com.semisky.multimedia.common.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;


/**
 * 偏好管理
 *
 * @author liuyong
 */
public class PreferencesManager {
    private static final String TAG = Logutil.makeTagLog(PreferencesManager.class);
    private static final String SHARE_NAME = "MultimediaPreferences";

    private static SharedPreferences getSP() {
        if (null != MediaApplication.getContext()) {
            return MediaApplication.getContext().getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        }
        return null;
    }

    // COMMON---------------------------------------------------------------------------------START
    // 保存首个挂载U盘标识
    public static void saveFirstMountedUsbFlag(int usbFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("firstMountedUsbFlag", usbFlag).commit();
        }
    }

    // 获取首个挂载U盘标识
    public static int getFirstMountedUsbFlag() {
        if (null != getSP()) {
            return getSP().getInt("firstMountedUsbFlag", -1);
        }
        return -1;
    }


    // 保存应用界面标识
    public static void saveLastAppFlag(int appFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("appFlag", appFlag).commit();
        }
    }

    // 获取应用界面标识
    public static int getLastAppFlag() {
        if (null != getSP()) {
            return getSP().getInt("appFlag", -1);
        }
        return -1;
    }

    // MUSIC----------------------------------------------------------------------------------START
    // 保存断点音乐媒体源U盘标识
    public static void saveLastMusicSourceUsbFlag(int usbFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("LastMusicSourceUsbFlag", usbFlag).commit();
        }
    }

    // 获取断点音乐媒体源U盘标识
    public static int getLastMusicSourceUsbFlag() {
        if (null != getSP()) {
            return getSP().getInt("LastMusicSourceUsbFlag", Definition.FLAG_USB1);
        }
        return Definition.FLAG_USB1;
    }

    // 保存音乐URL
    public static void saveLastMusicUrl(int usbFlag, String musicUri) {
        if (null != getSP()) {
            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    getSP().edit().putString("musicUrlByUsb1", musicUri).commit();
                    break;
                case Definition.FLAG_USB2:
                    getSP().edit().putString("musicUrlByUsb2", musicUri).commit();
                    break;
            }

        }
    }

    // 获取音乐URL
    public static String getLastMusicUrl(int usbFlag) {
        String musicUri = "";
        if (null != getSP()) {
            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    musicUri = getSP().getString("musicUrlByUsb1", "");
                    break;
                case Definition.FLAG_USB2:
                    musicUri = getSP().getString("musicUrlByUsb2", "");
                    break;
            }
        }
        return musicUri;
    }

    // 保存音乐播放进度
    public static void saveLastMusicProgress(int progress) {
        if (null != getSP()) {
            getSP().edit().putInt("musicProgress", progress).commit();
        }
    }

    // 获取音乐播放进度
    public static int getLastMusicProgress() {
        if (null != getSP()) {
            return getSP().getInt("musicProgress", 0);
        }
        return 0;
    }

    // 保存音乐播放模式
    public static void saveLastPlayMode(int playMode) {
        if (null != getSP()) {
            getSP().edit().putInt("playMode", playMode).commit();
        }
    }

    // 获取音乐播放模式
    public static int getLastPlayMode() {
        if (null != getSP()) {
            return getSP().getInt("playMode", PlayMode.LOOP);
        }
        return PlayMode.LOOP;
    }

    // 保存扫描到的首个音乐URL
    public static void saveScanFirstMusicUrl(int usbFlag, String url) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstMusicUrlOfUsb1", url).commit();
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstMusicUrlOfUsb2", url).commit();
                }
                break;
        }
    }

    // 获取扫描到的首个音乐URL
    public static String getScanFirstMusicUrl(int usbFlag) {

        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    return getSP().getString("scanFirstMusicUrlOfUsb1", "");
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    return getSP().getString("scanFirstMusicUrlOfUsb2", "");
                }
                break;
        }
        return "";
    }

    // VIDEO----------------------------------------------------------------------------------START
    // 保存断点视频媒体源U盘标识
    public static void saveLastVideoSourceUsbFlag(int usbFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("LastVideoSourceUsbFlag", usbFlag).commit();
        }
    }

    // 获取断点视频媒体源U盘标识
    public static int getLastVideoSourceUsbFlag() {
        if (null != getSP()) {
            return getSP().getInt("LastVideoSourceUsbFlag", Definition.FLAG_USB1);
        }
        return Definition.FLAG_USB1;
    }

    // 保存视频URL
    public static void saveLastVideoUrl(int usbFlag, String url) {
        if (null != getSP()) {
            Logutil.i(TAG, "saveLastVideoUrl() usbFlag :" + usbFlag + ",url :" + url);
            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    getSP().edit().putString("videoUrlByUsb1", url).commit();
                    break;
                case Definition.FLAG_USB2:
                    getSP().edit().putString("videoUrlByUsb2", url).commit();
                    break;
            }

        }
    }

    // 获取视频URL
    public static String getLastVideoUrl(int usbFlag) {
        String videoUri = "";
        if (null != getSP()) {
            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    videoUri = getSP().getString("videoUrlByUsb1", "");
                    break;
                case Definition.FLAG_USB2:
                    videoUri = getSP().getString("videoUrlByUsb2", "");
                    break;
            }
        }
        return videoUri;
    }

    // 保存视频播放进度
    public static void saveLastVideoProgress(int progress) {
        if (null != getSP()) {
            getSP().edit().putInt("videoProgress", progress).commit();
        }
    }

    // 获取视频播放进度
    public static int getLastVideoProgress() {
        if (null != getSP()) {
            return getSP().getInt("videoProgress", 0);
        }
        return 0;
    }

    // 保存扫描到的首个视频URL
    public static void saveScanFirstVideoUrl(int usbFlag, String url) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstVideoUrlOfUsb1", url).commit();
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstVideoUrlOfUsb2", url).commit();
                }
                break;
        }
    }

    // 保存扫描到的首个视频URL
    public static String getScanFirstVideoUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    return getSP().getString("scanFirstVideoUrlOfUsb1", "");
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    return getSP().getString("scanFirstVideoUrlOfUsb2", "");
                }
                break;
        }
        return "";
    }

    // PHOTO----------------------------------------------------------------------------------START

    // 保存断点图片媒体源U盘标识
    public static void saveLastPhotoSourceUsbFlag(int usbFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("LastPhotoSourceUsbFlag", usbFlag).commit();
        }
    }

    // 获取断点图片媒体源U盘标识
    public static int getLastPhotoSourceUsbFlag() {
        if (null != getSP()) {
            return getSP().getInt("LastPhotoSourceUsbFlag", Definition.FLAG_USB1);
        }
        return Definition.FLAG_USB1;
    }

    // 保存图片URL
    public static void saveLastPhotoUrl(int usbFlag, String url) {
        if (null != getSP()) {
            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    getSP().edit().putString("photoUrlByUsb1", url).commit();
                    break;
                case Definition.FLAG_USB2:
                    getSP().edit().putString("photoUrlByUsb2", url).commit();
                    break;
            }

        }
    }

    public static String getLastPhotoUrl(int usbFlag) {
        String photoUri = "";
        if (null != getSP()) {
            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    photoUri = getSP().getString("photoUrlByUsb1", "");
                    break;
                case Definition.FLAG_USB2:
                    photoUri = getSP().getString("photoUrlByUsb2", "");
                    break;
            }
        }
        return photoUri;
    }

    // 保存扫描到的首个图片URL
    public static void saveScanFirstPhotoUrl(int usbFlag, String url) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstPhotoUrlOfUsb1", url).commit();
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstPhotoUrlOfUsb2", url).commit();
                }
                break;
        }
    }

    // 保存扫描到的首个图片URL
    public static String getScanFirstPhotoUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    return getSP().getString("scanFirstPhotoUrlOfUsb1", null);
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    return getSP().getString("scanFirstPhotoUrlOfUsb2", null);
                }
                break;
        }
        return null;
    }

}
