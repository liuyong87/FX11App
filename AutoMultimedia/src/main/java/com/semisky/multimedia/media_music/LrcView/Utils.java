package com.semisky.multimedia.media_music.LrcView;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by chenhongrui on 2019/4/9
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
public class Utils {

    /**
     * 根据文件名获取assets目录下的文件内容
     */
    public static String getLrcText(AssetManager assetManager, String fileName) {
        String lrcText = null;
        try {
            InputStream is = assetManager.open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcText = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcText;
    }

    static List<LrcEntity> parseLrc(String lrcText) {
        if (TextUtils.isEmpty(lrcText)) {
            return null;
        }

        List<LrcEntity> entryList = new ArrayList<>();
        String[] array = lrcText.split("\\n");
        for (String line : array) {
            List<LrcEntity> list = parseLine(line);
            if (list != null && !list.isEmpty()) {
                entryList.addAll(list);
            }
        }

        Collections.sort(entryList);
        return entryList;
    }

    private static List<LrcEntity> parseLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }

        line = line.trim();
        Matcher lineMatcher = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d{2,3}])+)([\\s\\S]*)").matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }

        String times = lineMatcher.group(1);
        String text = lineMatcher.group(3);
        List<LrcEntity> entryList = new ArrayList<>();

        Matcher timeMatcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d){2,3}]").matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + (mil >= 100L ? mil : mil * 10);
            entryList.add(new LrcEntity(time, text));
        }
        return entryList;
    }

    public static float dp2px(Float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static String formatTime(long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return mm + ":" + ss;
    }

    public static float getFloat(float value, float minValue, float maxValue) {
        return Math.min(maxValue, Math.max(minValue, value));
    }
}
