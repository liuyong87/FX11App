package com.semisky.multimedia.common.utils;

import java.lang.reflect.Method;

/**
 * 获取系统属性工具类
 *
 * @author liuyong
 */
public class SystemPropertiesUtils {

    private static final String TAG = Logutil.makeTagLog(SystemPropertiesUtils.class);

    private static final boolean DEBUG = true;

    private static Class<?> mClassType = null;
    private static Method mGetMethod = null;
    private static Method mGetIntMethod = null;

    private static void init() {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties");

                mGetMethod = mClassType.getDeclaredMethod("get", String.class);
                mGetIntMethod = mClassType.getDeclaredMethod("getInt", String.class, int.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        init();

        String value = null;

        try {
            value = (String) mGetMethod.invoke(mClassType, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    // int SystemProperties.get(String key, int def){}
    public static int getInt(String key, int def) {
        init();

        int value = def;
        try {
            Integer v = (Integer) mGetIntMethod.invoke(mClassType, key, def);
            value = v.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
