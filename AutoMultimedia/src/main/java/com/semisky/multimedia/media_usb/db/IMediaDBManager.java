package com.semisky.multimedia.media_usb.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

public interface IMediaDBManager {
    interface OnBatchDataInsertListener {
        void onNotifyDataChanage();
    }

    /**
     * 停止批量插入媒体数据操作
     */
    void stopBatchInsert();

    /**
     * 准备批量插入媒体数据操作
     */
    void prepareBatchInsert();

    /**
     * 批量插入媒体数据操作
     *
     * @param contentValues
     */
    void insertBatchDataToDB(ContentValues contentValues, OnBatchDataInsertListener listener);

    /**
     * 批量插入最后媒体数据操作
     */
    void insertLastBatchDataToDB(OnBatchDataInsertListener listener);

    /**
     * @param contentValues
     * @return
     */
    long insertFavoriteMusic(ContentValues contentValues);

    /**
     * 获取所有音乐文件
     *
     * @param usbFlag U盘标识
     * @return 所有音乐数据游标
     */
    Cursor queryAllMusics(int usbFlag);

    /**
     * 查询所有收藏音乐文件
     *
     * @param usbFlag
     * @return
     */
    Cursor queryAllFavoriteMusics(int usbFlag);

    /**
     * 获取所有视频文件
     *
     * @param usbFlag U盘标识
     * @return 所有视频数据游标
     */
    Cursor queryAllVideos(int usbFlag);

    /**
     * 获取所有图片文件
     *
     * @param usbFlag U盘标识
     * @return 所有图片数据游标
     */
    Cursor queryAllPhotos(int usbFlag);

    /**
     * 查询音乐指定目录下的文件夹及文件
     *
     * @param dir 路径
     * @return
     */
    Cursor queryMusicDirectoryUnder(String dir);

    /**
     * 查询视频指定目录下的文件夹及文件
     *
     * @param dir 路径
     * @return
     */
    Cursor queryVideoDirectoryUnder(String dir);

    /**
     * 查询图片指定目录下的文件夹及文件
     *
     * @param dir 路径
     * @return
     */
    Cursor queryPhotoDirectoryUnder(String dir);

    /**
     * 查询指定收藏音乐信息
     *
     * @param usbFlag
     * @param url
     * @return
     */
    Cursor querySpecifyMuiscFavorite(int usbFlag, String url);


    /**
     * 查询当前音乐文件夹
     */
    Cursor queryAllMusicFolder(int usbFlag, String path);

    /**
     * 查询文件夹下的音乐文件
     *
     * @param usbFlag U盘标识
     * @param path    路径
     * @return
     */
    Cursor queryFolderUnderMusicFile(int usbFlag, String path);

    /**
     * 查询歌曲歌词
     *
     * @param usbFlag U盘标识
     * @param lrcName 歌词名字
     * @return
     */
    Cursor queryLyric(int usbFlag, String lrcName);

    /**
     * 删除所有音乐
     *
     * @param usbFlag U盘标识
     * @return
     */
    long deleteAllMusics(int usbFlag);

    /**
     * 删除所有歌词
     *
     * @param usbFlag
     * @return
     */
    long deleteAllLyrics(int usbFlag);

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
     * @param id
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


}
