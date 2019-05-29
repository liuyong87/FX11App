package com.semisky.multimedia.common.utils;

import java.io.File;

public class FileUtil {

    /**
     * 从URL获取文件名（不包括后缀）
     * @param url
     * @return
     */
    public static String getFileNameFromUrl(String url) {
        if (null != url) {
            return url.substring(url.lastIndexOf(File.separator) + 1, url.lastIndexOf("."));
        }
        return "";
    }

    /**
     * 相对"当前目录"此URL是"文件"还是"目录"
     *
     * @param curDir
     * @param url
     * @return
     */
    public static boolean isDirByRelativeCurrentDir(String curDir, String url) {
        final int backslashtLength = 1;// 反斜杠长度
        // 从“指定位置”起始点开始索引“指定字符”第一个出现这个“指定字符”的下标位置，下标不为-1表示为目录URL
        int backslashtIndex = url.indexOf("/", curDir.length() + backslashtLength);
        // 是文件夹
        if (backslashtIndex != -1) {
            return true;
        }
        return false;
    }

    /**
     * 获取文件名字(相对"当前目录")
     *
     * @param curDir
     * @param url
     * @return
     */
    public static String getFileNameByRelativeCurrentDir(String curDir, String url) {
        final int backslashtLength = 1;// 反斜杠长度
        String fileName = url.substring(curDir.length() + backslashtLength);// 获取文件名字
        return fileName;
    }

    /**
     * 获取文件夹名字(相对"当前目录")
     *
     * @param curDir
     * @param url
     * @return
     */
    public static String getDirNameByRelativeCurrentDir(String curDir, String url) {
        final int backslashtLength = 1;// 反斜杠长度
        // 从“指定位置”起始点开始索引“指定字符”第一个出现这个“指定字符”的下标位置，下标不为-1表示为目录URL
        int backslashtIndex = url.indexOf("/", curDir.length() + backslashtLength);
        // 获取文件夹名字
        String dirName = url.substring(curDir.length() + backslashtLength, backslashtIndex);
        return dirName;
    }

}
