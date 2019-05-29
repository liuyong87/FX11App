package com.semisky.multimedia.aidl.usb;

import java.util.List;
import java.lang.String;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;

interface IMediaStorageServiceProxy {
	/**
	 * 注册媒体文件扫描状态监听
	 * 
	 * @param listener
	 */
	void registerOnMediaScannerStateListener(IMediaScannerStateListener listener);
	/**
	 * 反注册媒体文件扫描状态监听
	 * 
	 * @param listener
	 */
	void unregisterOnMediaScannerStateListener(IMediaScannerStateListener listener);
	/**
	 * 插入收藏音乐
	 * 
	 * @param musicInfo
	 * @return
	 */
	long insertFavoriteMusic(in MusicInfo musicInfo);
	 /**
     * 指定音乐URL是否已收藏
     *
     * @param mUsbFlag
     * @param url
     * @return
     */
     boolean isFavoriteWithSpecifyMusicUrl(int mUsbFlag, String url);
	/**
	 * 查询所有收藏音乐文件
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	List<MusicInfo> queryAllFavoriteMusics(int mUsbFlag);

	/**
	 * 获取全部音乐
	 * 
	 * @param mUsbFlag
	 */
	List<MusicInfo> queryAllMusics(int mUsbFlag);
	/**
	 * 查询所有媒体视频
	 * 
	 * @param mUsbFlag
	 *            U盘标识
	 * @return 视频集合
	 */
	List<VideoInfo> queryAllVideos(int mUsbFlag);

	/**
	 * 查询所有媒体图片
	 * 
	 * @param mUsbFlag
	 * @return 图片集合
	 */
	List<PhotoInfo> queryAllPhotos(int mUsbFlag);
	
	/**
	 * 查询指定目录信息
	 * @param curDir 当前切换目录
	 * @return 目录信息集合
	 */
	List<FolderInfo> querySpecifyDirectoryUnder(String curDir);

	/**
	 * 获取媒体音乐总数
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	int queryMusicsSize(int mUsbFlag);

	/**
	 * 获取媒体视频总数
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	int queryVideosSize(int mUsbFlag);

	/**
	 * 获取媒体图片总数
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	int queryPhotosSize(int mUsbFlag);
	/**
     * 查询歌词
     *
     * @param url
     * @return
     */
	String queryLyricUrl(String url);
	/**
	 * 删除所有音乐
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	long deleteAllMusics(int mUsbFlag);

	/**
	 * 删除所有视频
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	long deleteAllVideos(int mUsbFlag);

	/**
	 * 删除所有图片
	 * 
	 * @param mUsbFlag
	 * @return
	 */
	long deleteAllPhotos(int mUsbFlag);
	/**
	 * 删除收藏音乐
	 * 
	 * @param id 自增长ID
	 * @return
	 */
	long deleteFavoriteMusic(int id);
	/**
     * 删除指定收藏音乐
     *
     * @param mUsbFlag U盘标识
     * @param url     媒体路径
     * @return
     */
    long deleteFavoriteWithMusicUrl(int mUsbFlag, String url);
    /**
     * 删除批量收藏媒体资源
     *
     * @param list
     */
    void deleteBatchFavorite(in List<String> list);
     /**
      * 获取扫描是否完成
      *
      * @param list
      */
     boolean isMediaScanFinished(int mUsbFlag);

     /**
     * 查询所有的文件夹
     */
     List<FolderInfo> queryAllMusicFolder(int mUsbFlag,String path);

     /**
     * 查询文件下所有的音乐数据
     */
     List<MusicInfo> queryFolderUnderMusic(int mUsbFlag,String path);
      /**
      * 删除指定U盘歌词
      */
     long deleteAllLyrics(int mUsbFlag);

}
