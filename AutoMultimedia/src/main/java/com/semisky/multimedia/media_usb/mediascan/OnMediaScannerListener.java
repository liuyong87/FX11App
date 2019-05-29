package com.semisky.multimedia.media_usb.mediascan;
/**
 * 媒体扫描变化监听接口
 * @author liuyong
 *
 */
public interface OnMediaScannerListener {
	/**
	 * 开始扫描媒体
	 * @param usbFlag
	 */
	void onScannerStart(int usbFlag);
	/**
	 * 正在扫描媒体
	 * @param usbFlag
	 */
	void onScanning(int usbFlag);
	/**
	 * 停止扫描媒体
	 * @param usbFlag
	 */
	void onScannerStop(int usbFlag);
	/**
	 * 媒体扫描完成
	 * @param usbFlag
	 */
	void onScannerDone(int usbFlag);
}
