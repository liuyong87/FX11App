package com.semisky.multimedia.common.utils;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.video.VideoInfo;

import java.util.Comparator;

/**
 * Created by LiuYong on 2018/9/21.
 */

public class ComparatorVideoInfo implements Comparator<VideoInfo> {
    @Override
    public int compare(VideoInfo obj0, VideoInfo obj1) {
        String left = obj0.getFileNamePinYin().substring(0, 1);
        String right = obj1.getFileNamePinYin().substring(0, 1);

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
