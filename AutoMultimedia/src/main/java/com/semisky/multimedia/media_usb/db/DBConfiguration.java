package com.semisky.multimedia.media_usb.db;

import android.provider.BaseColumns;

/**
 * Created by liuyong on 2018/06/19
 */
public class DBConfiguration {
    public static final String DATABASE_NAME = "AutoMultimedia.db";// 数据库名
    public static final int DATABASE_VERSION = 2;// 数据库版本
    public static final String USB_FLAG = "mediaFlag";// U盘标识

    // 媒体文件类型
    public static final String FILE_TYPE = "fileType";
    public static final int FLAG_MUSIC = 0;
    public static final int FLAG_VIDEO = 1;
    public static final int FLAG_PHOTO = 2;
    public static final int FLAG_LRC = 3;

    /**
     * 图片数据库配置
     */
    public static class PhotoConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "photos";// 表名
        public static final String USB_FLAG = "usbFlag";// U盘标识
        public static final String FILE_TYPE = "fileType";// 文件类型
        public static final String FILE_URL = "fileUrl";// 图片路径
        public static final String FILE_FORDER_URL = "fileForderUrl";// 所属文件夹路径
        public static final String FILE_NAME = "fileName";// 图片名字
        public static final String FILE_NAME_PINYIN = "fileNamePinYin";// 图片名字全拼
        public static final String DEFAULT_SORT_ORDER = FILE_NAME_PINYIN + " COLLATE LOCALIZED ASC";// 排序方式
    }

    /**
     * 音乐文件夹数据库配置
     */
    public static class MusicFolderConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "musicsFolder";// 表名
        public static final String USB_FLAG = "usbFlag";// U盘标识
        public static final String FOLDER_URL = "folderUrl";// 文件夹路径
        public static final String FOLDER_NAME = "folderName";// 文件夹名字
    }

    /**
     * 音乐数据库配置
     */
    public static class MusicConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "musics";// 表名
        public static final String USB_FLAG = "usbFlag";// U盘标识
        public static final String FILE_TYPE = "fileType";// 文件类型
        public static final String MUSIC_URL = "fileUrl";// 音乐路径
        public static final String MUSIC_TITLE = "musicTitle";// 音乐名字
        public static final String MUSIC_TITLE_PINYING = "musicTitlePinYing";// 音乐名字全拼
        public static final String MUSIC_ARTIST = "musicArtist";// 歌手
        public static final String MUSIC_ARTIST_PINYING = "musicArtistPinYing";// 歌手名拼音
        public static final String MUSIC_ALBUM = "musicAlbum";// 专辑
        public static final String MUSIC_ALBUM_PINYIN = "albumPinYin";// 专辑名拼音
        public static final String MUSIC_DURATION = "musicDuration";// 总时长
        public static final String MUSIC_FOLDER_URL = "fileFolderUrl";// 所属文件夹路径
        public static final String MUSIC_FAVORITE = "muiscFavorite";// 收藏标识
        public static final String DEFAULT_SORT_ORDER = MUSIC_TITLE_PINYING + " COLLATE LOCALIZED ASC";// 排序方式
        public static final String ARTIST_DEFAULT_SORT_ORDER = MUSIC_ARTIST_PINYING + " COLLATE LOCALIZED ASC";// 歌手默认排序方式
    }

    // 歌曲收藏配置
    public static class MusicFavoriteConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "favorite_musics";// 表名
        public static final String USB_FLAG = "usbFlag";// U盘标识
        public static final String FILE_TYPE = "fileType";// 文件类型
        public static final String MUSIC_URL = "fileUrl";// 音乐路径
        public static final String MUSIC_TITLE = "musicTitle";// 音乐名字
        public static final String MUSIC_TITLE_PINYING = "musicTitlePinYing";// 音乐名字全拼
        public static final String MUSIC_ARTIST = "musicArtist";// 歌手
        public static final String MUSIC_ARTIST_PINYING = "musicArtistPinYing";// 歌手名拼音
        public static final String MUSIC_ALBUM = "musicAlbum";// 专辑
        public static final String MUSIC_ALBUM_PINYIN = "albumPinYin";// 专辑名拼音
        public static final String MUSIC_DURATION = "musicDuration";// 总时长
        public static final String MUSIC_FOLDER_URL = "fileFolderUrl";// 所属文件夹路径
        public static final String MUSIC_FAVORITE = "muiscFavorite";// 收藏标识
        public static final String DEFAULT_SORT_ORDER = MUSIC_TITLE_PINYING + " COLLATE LOCALIZED ASC";// 排序方式
        public static final String ARTIST_DEFAULT_SORT_ORDER = MUSIC_ARTIST_PINYING + " COLLATE LOCALIZED ASC";// 歌手默认排序方式
    }

    /**
     * 歌词表字段
     */
    public static class LyricConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "lyrics";// 表名
        public static final String USB_FLAG = "usbFlag";// U盘标识
        public static final String FILE_TYPE = "fileType";// 文件类型
        public static final String LRC_URL = "lrcUrl";// 歌词路径
        public static final String LRC_NAME = "lrcName";// 歌词名字(不带后缀)
    }

    /**
     * 视频数据库配置
     */
    public static class VideoConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "videos";// 表名
        public static final String USB_FLAG = "usbFlag";// U盘标识
        public static final String FILE_TYPE = "fileType";// 文件类型
        public static final String FILE_URL = "fileUrl";// 音乐路径
        public static final String FILE_NAME = "fileName";// 视频名字
        public static final String FILE_NAME_PINYIN = "fileNamePinYin";// 视频名字全拼
        public static final String FILE_FOLDER_URL = "fileFolderUrl";// 所属文件夹路径
        public static final String FILE_DURATION = "duration";// 总时长
        public static final String DEFAULT_SORT_ORDER = FILE_NAME_PINYIN + " COLLATE LOCALIZED ASC";// 排序方式
    }

}
