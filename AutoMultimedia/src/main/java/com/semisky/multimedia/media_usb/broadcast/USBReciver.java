package com.semisky.multimedia.media_usb.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;

import java.io.File;

/**
 * Created by Anter on 2018/8/4.
 */

public class USBReciver extends BroadcastReceiver {
    private static final String TAG = Logutil.makeTagLog(USBReciver.class);


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = getAction(intent);
        String usbPath = getPath(intent);

        Logutil.i(TAG, "===========USB STATE INFO START======================");
        Logutil.i(TAG, "onReceive() action = " + action);
        Logutil.i(TAG, "onReceive() usbPath = " + usbPath);
        Logutil.i(TAG, "===========USB STATE INFO END  ======================");

        if (Definition.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            usbDetached(intent);
        } else if (Definition.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            handlerUSBDeviceAttached(intent, usbPath);
        }
        if (!isUSBPath(usbPath)) {
            Log.i("lcc", "usbPath = " + usbPath);
            Log.i("lcc", "usbPathX = " + Definition.PATH_USB1);

            return;
        }
        //保存第一次U盘挂载上来的路径
        if (Intent.ACTION_MEDIA_CHECKING.equals(action)) {// 插入外部存储装置事件
            // 清除数据库数据
            handlerUSBChecking(usbPath);
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {// U盘挂载
            // 启动U盘扫描服务
            handlerUSBMounted(usbPath);
        } else if (Intent.ACTION_MEDIA_REMOVED.equals(action)) {// U盘卸载
            handlerUSBURemoved(intent);

        }
    }

    /**
     * 设备挂载
     *
     * @param intent
     * @param usbPath
     */
    private void handlerUSBDeviceAttached(Intent intent, String usbPath) {
        boolean isUsb = USBManager.getInstance().isUSB(intent);
        Logutil.i(TAG, "handlerUSBDeviceDetached() ..." + isUsb + ",usbPath=" + usbPath);
        if (isUsb) {
            SemiskyIVIManager.getInstance().closeScreenSave();//如果屏保时打开状态，关闭屏保
            USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_DEVICE_ATTACHED);
        }
    }


    /**
     * U盘挂载处理
     *
     * @param usbPath
     */
    private void handlerUSBMounted(String usbPath) {
        Logutil.i(TAG, "handlerUSBMouted() ..." + usbPath);
        // 设置首个挂载U盘标识
        USBManager.getInstance().setIsMountUsb(true, usbPath);
        USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_MOUNTED);
        MediaStorageAccessProxyModel.getInstance().onStartScanPath(usbPath);

    }

    /**
     * U盘卸载处理
     *
     * @param intent
     */
    private void handlerUSBURemoved(Intent intent) {
        Log.i(TAG, "handlerUSBURemoved ");
        String usbPath = getPath(intent);
        Logutil.i(TAG, "usbPath :" + usbPath);
        MediaStorageAccessProxyModel.getInstance().onStopScanPath(usbPath);
        USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_REMOVED);
        USBManager.getInstance().setIsMountUsb(false, usbPath);

    }

    private void usbDetached(Intent intent) {
        if (USBManager.getInstance().isUSB(intent)) {
            Log.i("lcc","detached");
            USBManager.getInstance().notifyChangeUSBState("usb", USBManager.STATE_USB_DEVICE_DETACHED);
        }
    }


    /**
     * U盘挂载前的检查处理
     *
     * @param usbPath
     */
    private void handlerUSBChecking(String usbPath) {
        USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_CHECKING);
        MediaStorageAccessProxyModel.getInstance().onDeleteAllMedia(usbPath);
    }

    /**
     * 获取U盘路径
     *
     * @param intent
     * @return
     */
    private String getPath(Intent intent) {
        if (null != intent) {
            Uri dataUri = intent.getData();
            if (null != dataUri) {
                return dataUri.getPath();
            }
        }
        return "";
    }

    /**
     * 获取广播意图
     *
     * @param intent
     * @return
     */
    private String getAction(Intent intent) {
        if (null != intent) {
            return intent.getAction();
        }
        return "";
    }

    private boolean isUSBPath(String usbPath) {
        boolean isUSBpath = false;
        if (Definition.PATH_USB1.equals(usbPath)) {
            isUSBpath = true;
        } else if (Definition.PATH_USB2.equals(usbPath)) {
            isUSBpath = true;
        }
        Logutil.i(TAG, "isUSBpath=" + isUSBpath);
        return isUSBpath;
    }
}
