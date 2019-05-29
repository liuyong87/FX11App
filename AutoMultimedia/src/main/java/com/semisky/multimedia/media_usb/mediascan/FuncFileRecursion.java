package com.semisky.multimedia.media_usb.mediascan;



import com.semisky.multimedia.common.utils.Logutil;

import java.io.File;

/**
 * 文件件递归操作
 * Created by liuyong on 18-4-24.
 */

public class FuncFileRecursion {
    private static final String TAG = FuncFileRecursion.class.getSimpleName();
    private OnFileRecursionListener mListener;
    private volatile boolean isStopScan = false;// 是否停止扫描
    private volatile boolean isRunning = false;// 是否运行

    public interface OnFileRecursionListener {
        void onScanFileStart();

        void onScanResult(ConstantsMediaSuffix.MediaSuffixType suffixType, String uri);

        void onScanFileDone();

        void onScanFileStopping();

        void onScanFileStoped();
    }

    private void notifyScanFileStart() {
        if (null != mListener) {
            mListener.onScanFileStart();
        }
    }

    private void notifyScanResult(ConstantsMediaSuffix.MediaSuffixType suffixType, String uri) {
        if (null != mListener) {
            mListener.onScanResult(suffixType,uri);
        }
    }

    private void notifyScanFileDone() {
        if (null != mListener) {
            mListener.onScanFileDone();
        }
    }

    private void notifyScanFileStopping(){
        if (null != mListener) {
            mListener.onScanFileStopping();
        }
    }

    private void notifyScanFileStoped(){
        if (null != mListener) {
            mListener.onScanFileStoped();
        }
    }

    public FuncFileRecursion prepare(){
        this.isStopScan = false;
        Logutil.i(TAG,"prepare() ...");
        return this;
    }

    public FuncFileRecursion stop(){
        this.isStopScan = true;
        return this;
    }

    public boolean isRunning(){
        return this.isRunning;
    }

    public void scanFileOfUsb(OnFileRecursionListener listener, String targetFile) {
        this.mListener = listener;
        notifyScanFileStart();// 通知扫描媒体文件开始
        scanFile(new File(targetFile));// 执行递归扫描函数
        isRunning = false;
        Logutil.i(TAG,"scanFileOfUsb() isStopScan="+isStopScan+",isRunning="+isRunning);
        if(isStopScan){
            notifyScanFileStoped();
        }else {
            notifyScanFileDone();// 通知扫描媒体文件完成
        }
    }

    // 递归扫描函数
    private void scanFile(File targetFile) {
        isRunning = true;
        if(isStopScan){//如果usb unmount 时，立即停止程序
            notifyScanFileStopping();
            return;
        }

        if (targetFile == null || !targetFile.exists()) {
            notifyScanFileStopping();
            return;
        }

        // 如果是文件夹
        if (targetFile.isDirectory()) {
            File[] files = targetFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if(isStopScan){//如果usb unmount 时，立即停止程序
                        notifyScanFileStopping();
                        return;
                    }
                    scanFile(file);// 递归扫描
                }
            }
        }

        // 如果是文件
        else {
            String fileUri = targetFile.getAbsolutePath();// 文件路径

            if (check(fileUri, ConstantsMediaSuffix.SUFFIX_ARRAY_PHOTO)) {// 如果是图片文件
                notifyScanResult(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_PHOTO,fileUri);
                return;
            }

            if (check(fileUri, ConstantsMediaSuffix.SUFFIX_ARRAY_AUDIO)) {// 如果是音频文件
                notifyScanResult(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_AUDIO,fileUri);
                return;
            }

            if (check(fileUri, ConstantsMediaSuffix.SUFFIX_ARRAY_VIDEO)) {// 如果是视频文件
                notifyScanResult(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_VIDEO,fileUri);
                return;
            }
        }
    }

    /**
     * 通过文件名判断是什么类型的文件.
     */
    boolean check(final String name, final String[] extensions) {
        for (String end : extensions) {
            // name永远不会为null,无需异常处理
            if (name.toLowerCase().endsWith(end)) {
                return true;
            }
        }
        return false;
    }

}
