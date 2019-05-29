package com.semisky.multimedia.media_usb.mediascan;

import android.content.ContentValues;

/**
 * 媒体解析接口
 * @author liuyong
 *
 */
public interface IMediaParser {
	/**
	 * 获取媒体音乐信息
	 * 
	 * @param filePath
	 * @return
	 */
	ContentValues getContentValuesByMusic(int usbFlag, String filePath);

	/**
	 * 获取媒体视频信息
	 *
	 * @param filePath
	 * @return
	 */
	ContentValues getContentValuesByVideo(int usbFlag, String filePath);

	/**
	 * 获取媒体图片信息
	 *
	 * @param filePath
	 * @return
	 */
	ContentValues getContentValuesByPhoto(int usbFlag, String filePath);
	/**
	 * 获取媒体歌词信息
	 *
	 * @param filePath
	 * @return
	 */
	ContentValues getContentValuesByLyric(int usbFlag, String filePath);

}
