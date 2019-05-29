package com.semisky.multimedia.common.utils;

import com.semisky.multimedia.common.constants.Definition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Anter on 2018/8/6.
 */

public class USBCheckUtil {
    private static final String TAG = Logutil.makeTagLog(USBCheckUtil.class);

    private static String PATH_A = "udisk0";
    private static String PATH_B = "udisk1";
    /**
     * 检查USB是否挂载
     * @return
     */
    public static boolean isUdiskExist(String usbPath) {
        Logutil.d(TAG, "isUdiskExist()...");
        if(null == usbPath){
            return false;
        }

        if(usbPath.endsWith(PATH_A)){
            usbPath = PATH_A;
        }else if(usbPath.endsWith(PATH_B)){
            usbPath = PATH_B;
        }else {
            return false;
        }

        String path = "/proc/mounts";

        boolean ret = false;
        try {
            String encoding = "GBK";
            File file = new File(path);
            if ((file.isFile()) && (file.exists())) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while (((lineTxt = bufferedReader.readLine()) != null) && (!ret)) {
                    String[] a = lineTxt.split(" ");//将读出来的一行字符串用 空格 来分割成字符串数组并存储进数组a里面
                    String str = a[0];//取出位置0处的字符串

                    if ((str.contains("/dev/block/vold")) &&
                            (a[1].contains(usbPath))) {
                        ret = true;
                    }
                }

                read.close();
            } else {
                Logutil.d(TAG, "can't find file: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logutil.d(TAG, "isUdiskExist()="+ret);
        return ret;
    }
}
