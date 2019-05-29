package com.semisky.multimedia.aidl.usb;

interface IMediaScannerStateListener {
	/**
	 * 媒体文件扫描开始
	 * 
	 * @param mUsbFlag
	 */
	void onScannerStart(int mUsbFlag);

	/**
	 * 媒体文件正常扫描
	 * 
	 * @param mUsbFlag
	 */
	void onScanning(int mUsbFlag);

	/**
	 * 媒体文件扫描停止
	 * 
	 * @param mUsbFlag
	 */
	void onScannerStoped(int mUsbFlag);

	/**
	 * 媒体文件扫描完成
	 * 
	 * @param mUsbFlag
	 */
	void onScannerDone(int mUsbFlag);
	/**
	*
	* usb挂载状态
	*/
	void onUsbMountStateOne();
	void onUsbMountStateUsbTwo();
	/**
    *
    * usb取消挂载状态
    */
    void onUsbUnMountStateOne();
    void onUsbUnMountStateTwo();

}
