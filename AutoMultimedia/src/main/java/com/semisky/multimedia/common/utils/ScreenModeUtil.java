package com.semisky.multimedia.common.utils;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by Anter on 2018/8/4.
 */

public class ScreenModeUtil {

    /**
     * 非全屏显示
     *
     * @param ctx
     */
    public static void changeNormalScreenMode(Activity ctx) {
        ctx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 全屏显示
     *
     * @param ctx
     */
    public static void chanageFullScreenMode(Activity ctx) {
        ctx.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
