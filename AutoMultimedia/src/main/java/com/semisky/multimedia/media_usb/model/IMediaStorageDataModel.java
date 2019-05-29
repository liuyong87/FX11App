package com.semisky.multimedia.media_usb.model;


import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.video.VideoInfo;

import java.util.List;

public interface IMediaStorageDataModel {
    /**
     * 插入收藏音乐
     *
     * @param musicInfo
     * @return
     */
    long insertFavoriteMusic(MusicInfo musicInfo);

    /**
     * 查询所有收藏音乐文件
     *
     * @param usbFlag
     * @return
     */
    List<MusicInfo> queryAllFavoriteMusics(int usbFlag);

    /**
     * 查询所有媒体音乐
     *
     * @param usbFlag U盘标识
     * @return 音乐集合
     */
    List<MusicInfo> queryAllMusics(int usbFlag);

    /**
     * 查询所有的音乐文件夹
     */
    List<FolderInfo> queryAllMusicFolder(int usbFlag, String path);

    /**
     * 查询音乐文件夹下的所有音乐文件
     */
    List<MusicInfo> queryFolderUnderMusic(int usbFlag, String path);

    /**
     * 查询所有媒体视频
     *
     * @param usbFlag U盘标识
     * @return 视频集合
     */
    List<VideoInfo> queryAllVideos(int usbFlag);

    /**
     * 查询所有媒体图片
     *
     * @param usbFlag
     * @return 图片集合
     */
    List<PhotoInfo> queryAllPhotos(int usbFlag);

    /**
     * 查询指定目录下媒体信息集合
     *
     * @param dir
     * @return
     */
    List<FolderInfo> querySpecifyDirectoryUnder(String dir);

    /**
     * 获取媒体音乐总数
     *
     * @param usbFlag
     * @return
     */
    int queryMusicsSize(int usbFlag);

    /**
     * 获取媒体视频总数
     *
     * @param usbFlag
     * @return
     */
    int queryVideosSize(int usbFlag);

    /**
     * 获取媒体图片总数
     *
     * @param usbFlag
     * @return
     */
    int queryPhotosSize(int usbFlag);

    /**
     * 获取歌词
     * @param url 音乐URL
     * @return
     */
    String queryLyricUrl(String url);

    /**
     * 指定音乐URL是否已收藏
     *
     * @param usbFlag
     * @param url
     * @return
     */
    boolean isFavoriteWithSpecifyMusicUrl(int usbFlag, String url);

    /**
     * 删除所有音乐
     *
     * @param usbFlag
     * @return
     */
    long deleteAllMusics(int usbFlag);

    /**
     * 删除所有视频
     *
     * @param usbFlag
     * @return
     */
    long deleteAllVideos(int usbFlag);

    /**
     * 删除所有图片
     *
     * @param usbFlag
     * @return
     */
    long deleteAllPhotos(int usbFlag);

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
     * @param usbFlag U盘标识
     * @param url     媒体路径
     * @return
     */
    long deleteFavoriteWithMusicUrl(int usbFlag, String url);

    /**
     * 删除批量收藏媒体资源
     *
     * @param list
     */
    void deleteBatchFavorite(List<String> list);

    /**
     * 删除指定U盘所有歌词
     * @param usbFlag
     * @return
     */
    long deleteAllLyrics(int usbFlag);


}
