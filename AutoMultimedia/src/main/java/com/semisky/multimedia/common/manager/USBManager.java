package com.semisky.multimedia.common.manager;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;

import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/27.
 */

public class USBManager {
    private static final String TAG = Logutil.makeTagLog(USBManager.class);
    private List<OnUSBStateChangeListener> mUSBObserverList;
    private static USBManager _INSTANCE;
    private static final int DEV_USB_ID = 0x08;// USB设备ID

    public static final int STATE_USB_CHECKING = 10;// U盘准备挂载状态
    public static final int STATE_USB_MOUNTED = 11;// U盘挂载
    public static final int STATE_USB_UNMOUNTED = 12;// U盘卸载
    public static final int STATE_USB_REMOVED = 13;// U盘移除
    public static final int STATE_USB_DEVICE_ATTACHED = 21;// 设备挂载
    public static final int STATE_USB_DEVICE_DETACHED = 22;// 设备卸载


    private static boolean isMountUsbOne = false;
    private static boolean isMountUsbTwo = false;
    private static String firstMountUsb = null;
    private Handler mForegroundHandler = new Handler(Looper.getMainLooper());


    private USBManager() {
        mUSBObserverList = new ArrayList<OnUSBStateChangeListener>();
        mUSBObserverList.clear();
    }

    public static USBManager getInstance() {
        if (null == _INSTANCE) {
            synchronized (USBManager.class) {
                if (null == _INSTANCE) {
                    _INSTANCE = new USBManager();
                }
            }

        }
        return _INSTANCE;
    }

    /**
     * USB状态监听接口
     */
    public interface OnUSBStateChangeListener {
        void onChangeState(String usbPath, int stateCode);
    }

    public void registerOnUSBStateChangeListener(OnUSBStateChangeListener l) {
        if (null != l && !mUSBObserverList.contains(l)) {
            mUSBObserverList.add(l);
        }
    }

    public void unregisterOnUSBStateChangeListener(OnUSBStateChangeListener l) {
        if (null != l && mUSBObserverList.contains(l)) {
            mUSBObserverList.remove(l);
        }
    }

    public synchronized void notifyChangeUSBState(final String usbPath, final int stateCode) {
        mForegroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != mUSBObserverList && mUSBObserverList.size() > 0) {
                    for (int i = 0; i < mUSBObserverList.size(); i++) {
                        mUSBObserverList.get(i).onChangeState(usbPath, stateCode);
                    }
                }
            }
        });
    }


    /**
     * 判断是否为USB设备
     *
     * @param intent
     * @return
     */
    public boolean isUSB(Intent intent) {
        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device.getInterfaceCount() == 0) {
            return false;
        }
        int devId = device.getInterface(0).getInterfaceClass();
        Logutil.i(TAG, "devId=" + devId);
        if (DEV_USB_ID == devId) {
            return true;
        }
        return false;
    }


    public void setIsMountUsb(boolean isMount, String usbFlag) {
        if (usbFlag.equals(Definition.PATH_USB1)) {
            isMountUsbOne = isMount;
            if (firstMountUsb == null && isMount) {
                firstMountUsb = Definition.PATH_USB1;
                MediaApplication.setCurrentUSB(Definition.FLAG_USB1);
            }
        } else if (usbFlag.equals(Definition.PATH_USB2)) {
            isMountUsbTwo = isMount;
            if (firstMountUsb == null && isMount) {
                firstMountUsb = Definition.PATH_USB2;
                MediaApplication.setCurrentUSB(Definition.FLAG_USB2);
            }
        }
        setFirstMountUsbState(isMount, usbFlag);
    }

    /**
     * u盘拔出时切换usb插入顺序
     *
     * @param isMount
     * @param usbFlag
     */
    private void setFirstMountUsbState(boolean isMount, String usbFlag) {
        if (!isMount && (firstMountUsb != null)) {
            if (usbFlag.equals(Definition.PATH_USB1) && firstMountUsb.equals(usbFlag)) {
                if (getUsbTwoMount()) {
                    firstMountUsb = Definition.PATH_USB2;
                    MediaApplication.setCurrentUSB(Definition.FLAG_USB2);
                }
            } else if (usbFlag.equals(Definition.PATH_USB2) && firstMountUsb.equals(usbFlag)) {
                if (getUsbOneMount()) {
                    firstMountUsb = Definition.PATH_USB1;
                    MediaApplication.setCurrentUSB(Definition.FLAG_USB1);
                }
            }
        }
    }

    public boolean getUsbOneMount() {
        return isMountUsbOne;
    }

    public boolean getUsbTwoMount() {
        return isMountUsbTwo;
    }

    public String getFirstMountUsb() {
        Logutil.i(TAG, "getFirstMountUsb = " + firstMountUsb);
        return firstMountUsb;
    }

    public void clearFirstMountUsbState() {
        firstMountUsb = null;
    }

    public int getUsbFlag() {
        if (firstMountUsb == null) {
            return Definition.FLAG_USB1;
        }
        if (firstMountUsb.equals(Definition.PATH_USB1)) {
            return Definition.FLAG_USB1;
        }
        if (firstMountUsb.equals(Definition.PATH_USB2)) {
            return Definition.FLAG_USB2;
        }
        return Definition.FLAG_USB1;

    }

    public int getUsbFlag(String usbPath) {
        if (usbPath.equals(Definition.PATH_USB1)) {
            return Definition.FLAG_USB1;
        } else if (usbPath.equals(Definition.PATH_USB2)) {
            return Definition.FLAG_USB2;
        }
        return -1;
    }


}
