package com.semisky.multimedia.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.semisky.autoservice.manager.AutoManager;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaListConst;
import com.semisky.multimedia.media_list.MultimediaListActivity;
import com.semisky.multimedia.media_music.service.LocalMusicService;
import com.semisky.multimedia.media_music.view.MusicListActivity;
import com.semisky.multimedia.media_music.view.MusicPlayerActivity;
import com.semisky.multimedia.media_photo.view.PhotoPlayerActivity;
import com.semisky.multimedia.media_usb.broadcast.USBReciver;
import com.semisky.multimedia.media_usb.mediascan.ConstantsMediaSuffix;
import com.semisky.multimedia.media_usb.mediascan.ConstantsMediaSuffix.MediaSuffixType;
import com.semisky.multimedia.media_video.view.VideoListActivity;
import com.semisky.multimedia.media_video.view.VideoPlayerActivity;

import java.util.logging.Logger;

/**
 * Created by Anter on 2018/8/6.
 */

public class AppUtil {
    private static final String TAG = Logutil.makeTagLog(AppUtil.class);


    public static void jumpToLocalMusic(int usbFlag, boolean isForeground) {
        if (isForeground) {// 前台进入音乐播放界面
            Log.i(TAG, "Foreground play music !!!");
            Intent it = new Intent();
            it.setClass(MediaApplication.getContext(), MusicPlayerActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra(Definition.KEY_USB_FLAG, usbFlag);
            MediaApplication.getContext().startActivity(it);
        } else {// 后台播放多媒体音乐
            Log.i(TAG, "Background play music !!!");
            Intent it = new Intent();
            it.setClass(MediaApplication.getContext(), LocalMusicService.class);
            it.setAction(Definition.MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
            it.putExtra(Definition.MediaCtrlConst.PARAM_CMD, Definition.MediaCtrlConst.CMD_RESUME_PLAY);
            MediaApplication.getContext().startService(it);
        }
    }

    public static void jumpToLocalMusicList(int usbFlag) {
        Log.i(TAG,"jumpToLocalMusicList() usbFlag: "+usbFlag);
        Intent it = new Intent();
        it.setClass(MediaApplication.getContext(), MusicListActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int fragmenFlag = usbFlag == Definition.FLAG_USB1 ?
                MediaListConst.FRAGMENT_LIST_USB1_MUSIC : MediaListConst.FRAGMENT_LIST_USB2_MUSIC;
        it.putExtra(MediaListConst.FRAGMENT_FLAG, fragmenFlag);
        Log.i(TAG,"jumpToLocalMusicList() fragmenFlag: "+fragmenFlag);
        MediaApplication.getContext().startActivity(it);
    }

    public static void jumpToLocalVideo(int usbFlag) {
        Intent it = new Intent();
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        it.setClass(MediaApplication.getContext(), VideoPlayerActivity.class);
        MediaApplication.getContext().startActivity(it);
    }

    public static void jumpToLocalVideoList(int usbFlag) {
        Intent it = new Intent();
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        it.setClass(MediaApplication.getContext(), VideoListActivity.class);
        int fragmenFlag = usbFlag == Definition.FLAG_USB1 ?
                MediaListConst.FRAGMENT_LIST_USB1_BY_VIDEO : MediaListConst.FRAGMENT_LIST_USB2_BY_VIDEO;
        it.putExtra(MediaListConst.FRAGMENT_FLAG, fragmenFlag);
        MediaApplication.getContext().startActivity(it);
    }

    public static void jumpToLocalPhoto(int usbFlag) {
        Intent it = new Intent();
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        it.setClass(MediaApplication.getContext(), PhotoPlayerActivity.class);
        MediaApplication.getContext().startActivity(it);
    }

    public static void jumpToLocalPhotoList(int usbFlag) {
        Intent it = new Intent();
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        it.setClass(MediaApplication.getContext(), PhotoPlayerActivity.class);
        int fragmenFlag = usbFlag == Definition.FLAG_USB1 ?
                MediaListConst.FRAGMENT_LIST_USB1_BY_PHOTO : MediaListConst.FRAGMENT_LIST_USB2_BY_PHOTO;
        it.putExtra(MediaListConst.FRAGMENT_FLAG, fragmenFlag);
        MediaApplication.getContext().startActivity(it);
    }


    // 跳转蓝牙音乐播放界面
    public static void jumpToBTMusic() {
        Intent it = new Intent();
        it.setClassName("com.semisky.bluetooth",
                "com.semisky.bluetooth.BtMusicActivity");
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            if (null != it.resolveActivity(MediaApplication.getContext().getPackageManager())) {
                MediaApplication.getContext().startActivity(it);
            }
        } catch (Exception e) {
            Logutil.e(TAG, "Jump to bluetooth music fail !!!");
            e.printStackTrace();
        }
    }

    /**
     * 是否为忽略的文件后缀
     *
     * @param suffixType
     * @param url
     * @return
     */
    public static boolean isIgonreScanFileSuffix(MediaSuffixType suffixType, String url) {
        switch (suffixType) {
            case SUFFIX_TYPE_AUDIO:
                return false;
            case SUFFIX_TYPE_VIDEO:
                if (null == url) {
                    return false;
                }
                String suffixStr = url.substring(url.lastIndexOf(".")).toLowerCase();
                for (String suffix : ConstantsMediaSuffix.IGNORE_SUFFIX_ARRAY_VIDEO) {
                    if (suffix.equals(suffixStr)) {
                        return true;
                    }
                }
                return false;
            case SUFFIX_TYPE_PHOTO:
                return false;
        }
        return false;
    }

    /**
     * 恢复后台音乐播放
     *
     * @param ctx
     */
    public static void resumeBackgroundMusicPlay(Context ctx) {
        Logutil.i(TAG, "resumeBackgroundMusicPlay() ...");
        Intent intent = new Intent();
        intent.setClassName(Definition.MediaCtrlConst.SERVICE_PKG, Definition.MediaCtrlConst.SERVICE_CLZ);
        intent.setAction(Definition.MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
        intent.putExtra(Definition.MediaCtrlConst.PARAM_CMD, Definition.MediaCtrlConst.CMD_RESUME_PLAY);
        ctx.startService(intent);
    }

    /**
     * 从资源路径获取U盘标识
     *
     * @param url
     * @return
     */
    public static int getUsbFlagFrom(String url) {
        int usbFlag = -1;
        if (null != url) {
            if (url.startsWith(Definition.PATH_USB1)) {
                usbFlag = Definition.FLAG_USB1;
            } else if (url.startsWith(Definition.PATH_USB2)) {
                usbFlag = Definition.FLAG_USB2;
            }
        }
        Logutil.i(TAG, "getUsbFlagFrom() ..." + usbFlag);
        return usbFlag;
    }

    /**
     * 获取当前平台USB路径
     *
     * @param usbFlag usb 标识
     * @return
     */
    public static String getUsbPathWithCurrentPlatform(int usbFlag) {
        String usbPath = null;
        // 当前平台USB路径
        if (isCurrentPlatform()) {

            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    usbPath = Definition.PATH_USB1_BM2718_PLATFORM;
                    break;
                case Definition.FLAG_USB2:
                    usbPath = Definition.PATH_USB2_BM2718_PLATFORM;
                    break;
            }
            Logutil.i(TAG, "getUsbPathWithCurrentPlatform() PATH_USB_BM2718_PLATFORM =" + usbPath);
            return usbPath;
        }
        // 其它平台USB路径
        else {

            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    usbPath = Definition.PATH_USB1_OTHER_PLATFORM;
                    break;
                case Definition.FLAG_USB2:
                    usbPath = Definition.PATH_USB2_OTHER_PLATFORM;
                    break;
            }
            Logutil.i(TAG, "getUsbPathWithCurrentPlatform() PATH_USB_OTHER_PLATFORM =" + usbPath);
            return usbPath;
        }
    }

    /**
     * 是否为当前平台
     *
     * @return
     */
    public static boolean isCurrentPlatform() {
        String platform = SystemPropertiesUtils.get("ro.build.description");
        Logutil.d(TAG, "isCurrentPlatform()->platform: " + platform);
        if (null != platform && platform.contains(Definition.CURRENT_PLATFORM_KEYWORDS)) {
            return true;
        }
        return false;
    }

    /**
     * 进入指定播放器视图界面
     *
     * @param appFlag
     */
    public static void enterPlayerView(int appFlag, String url) {
        Intent intent = new Intent();
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                intent.setClass(MediaApplication.getContext(), MusicPlayerActivity.class);

                break;
            case Definition.AppFlag.TYPE_VIDEO:
                intent.setClass(MediaApplication.getContext(), VideoPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                intent.setClass(MediaApplication.getContext(), PhotoPlayerActivity.class);
                break;
        }
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MediaApplication.getContext().startActivity(intent);
    }

    /**
     * 进入指定播放器视图界面
     *
     * @param appFlag
     */
    public static void enterPlayerView(int appFlag) {
        Intent intent = new Intent();
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                intent.setClass(MediaApplication.getContext(), MusicPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_VIDEO:
                intent.setClass(MediaApplication.getContext(), VideoPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                intent.setClass(MediaApplication.getContext(), PhotoPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_LIST:
                intent.setClass(MediaApplication.getContext(), MultimediaListActivity.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MediaApplication.getContext().startActivity(intent);
        ToastCustom.closeDialog();//进入Activity后，立即关闭显示的toast 提示信息
    }

    /**
     * U盘路径转换U盘标识
     */
    public static int conversionUsbPathToUsbFlag(String usbPath) {
        if(usbPath == null){
            return -1;
        }
        if (usbPath.endsWith(Definition.PATH_USB1)) {
            return Definition.FLAG_USB1;
        } else if (usbPath.endsWith(Definition.PATH_USB2)) {
            return Definition.FLAG_USB2;
        }
        return -1;
    }

    public static int conversionUrlToUsbFlag(String url) {
        if (url.startsWith(Definition.PATH_USB1)) {
            return Definition.FLAG_USB1;
        } else if (url.startsWith(Definition.PATH_USB2)) {
            return Definition.FLAG_USB2;
        }
        return -1;
    }

    public static void startSoundSetting() {
        Intent intent = new Intent();
        intent.setClassName("com.semisky.autosetting", "com.semisky.autosetting.SettingActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", 4);
        MediaApplication.getContext().startActivity(intent);

    }

    /**
     * 判断多媒体界面是否在栈顶
     */
    public static boolean getActivityIsTop(Context context, String activityName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(MediaApplication.getContext().ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        if (name.equals(activityName)) {
            return true;
        }
        return false;
    }


}
