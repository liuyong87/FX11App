package com.semisky.multimedia.common.utils;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.Comparator;

/**
 * Created by LiuYong on 2018/9/21.
 */

public class ComparatorMusicInfo implements Comparator<MusicInfo> {
    @Override
    public int compare(MusicInfo obj0, MusicInfo obj1) {
        String left = obj0.getTitlePinYing().substring(0, 1);
        String right = obj1.getTitlePinYing().substring(0, 1);

        if (left.equals(right)) {
            return 0;
        } else if ("@".equals(left)) {
            return 1;
        } else if ("@".equals(right)) {
            return -1;
        } else if ("#".equals(left)) {
            return 1;
        } else if ("#".equals(right)) {
            return -1;
        } else {
            return left.compareTo(right);
        }
    }
}
