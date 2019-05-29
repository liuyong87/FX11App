package com.semisky.multimedia.media_music.LrcView;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcParseUtil {

    public static List<LrcEntity> parseLrc(File lrcFile) {
        if (lrcFile == null || !lrcFile.exists()) {
            return null;
        }

        List<LrcEntity> entryList = new ArrayList<LrcEntity>();
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(lrcFile);
            bis = new BufferedInputStream(fis);
            reader = new BufferedReader(new InputStreamReader(bis,
                    CharsetTranscoder.getFileIncode(lrcFile)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                List<LrcEntity> list = parseLine(line);
                if (list != null && !list.isEmpty()) {
                    entryList.addAll(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(entryList);
        return entryList;
    }

    static List<LrcEntity> parseLrc(String lrcText) {
        if (TextUtils.isEmpty(lrcText)) {
            return null;
        }

        List<LrcEntity> entryList = new ArrayList<LrcEntity>();
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
        Matcher lineMatcher = Pattern.compile(
                "((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)").matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }

        String times = lineMatcher.group(1);
        String text = lineMatcher.group(3);
        List<LrcEntity> entryList = new ArrayList<LrcEntity>();

        Matcher timeMatcher = Pattern.compile(
                "\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\]").matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec
                    * DateUtils.SECOND_IN_MILLIS + mil * 10;
            entryList.add(new LrcEntity(time, text));
        }
        return entryList;
    }
}
