package com.semisky.multimedia.media_usb.mediascan;

/**
 * 媒体扫描器接口
 *
 * @author liuyong
 */
public interface IMediaScanner {
    /**
     * 无效扫描状态
     */
    int MEDIA_SCAN_STATE_INVALID = -1;
    /**
     * 正在扫描状态
     */
    int MEDIA_SCAN_STATE_SCANNING = 1;
    /**
     * 扫描完成状态
     */
    int MEDIA_SCAN_STATE_DONE = 2;
    /**
     * 强制停止扫描状态
     */
    int MEDIA_SCAN_STATE_STOPED = 3;


    /**
     * 注册媒体文件扫描状态监听
     *
     * @param listener
     */
    void registerOnMediaScannerListener(OnMediaScannerListener listener);

    /**
     * 反注册媒体文件扫描状态监听
     */
    void unregisterOnMediaScannerListener();

    /**
     * USB挂载
     *
     * @param usbPath
     */
    void onUSBMounted(String usbPath);

    /**
     * USB卸载
     *
     * @param usbPath
     */
    void onUSBUnMounted(String usbPath);

    int getScannerStatus();

    /**
     * 获取首个扫描到媒体音乐URL
     *
     * @param usbFlag
     * @return
     */
    String getScanFirstMusicUrl(int usbFlag);

    /**
     * 获取首个扫描到媒体视频URL
     *
     * @param usbFlag
     * @return
     */
    String getScanFirstVideoUrl(int usbFlag);

    /**
     * 获取首个扫描到媒体图片URL
     *
     * @param usbFlag
     * @return
     */
    String getScanFirstPhotoUrl(int usbFlag);

    /**
     * 获取扫描状态
     */
    boolean isMediaScanFinished(int usbFlag);

}
