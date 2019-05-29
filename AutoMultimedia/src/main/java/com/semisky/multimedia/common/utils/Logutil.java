package com.semisky.multimedia.common.utils;

import android.util.Log;

public class Logutil {
    private static final String PREFIX_TAG = "TomL";
    private static boolean DEBUG = true;

    public static String makeTagLog(Class clz) {
        return PREFIX_TAG + clz.getSimpleName();
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

}
