package com.semisky.multimedia.common.utils;

import java.util.Locale;

/**
 * 时间格式工具类
 * Created by Anter on 2018/8/3.
 */

public class FormatTimeUtil {
    /**
     * 格式化成播放器显示的时间格式
     *
     * @param time
     * @return
     */
    public static String makeFormatTime(int time) {
        time /= 1000;
        long min = time / 60 % 60;
        long hour = time / 60 / 60;
        long second = time % 60;
        if (time < 3600) {
            return String.format(Locale.getDefault(), "%02d:%02d", min, second).toString();
        }
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, second).toString();
    }
}
