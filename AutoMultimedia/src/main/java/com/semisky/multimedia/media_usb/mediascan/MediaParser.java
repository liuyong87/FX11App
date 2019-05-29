package com.semisky.multimedia.media_usb.mediascan;

import android.content.ContentValues;
import android.media.MediaMetadataRetriever;


import com.semisky.multimedia.common.utils.EncodingUtil;
import com.semisky.multimedia.common.utils.FileUtil;
import com.semisky.multimedia.common.utils.HanziToPinyinUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.db.DBConfiguration;

import java.io.File;
/**
 * 媒体解析实现类
 *
 * @author liuyong
 */
public final class MediaParser implements IMediaParser {
    private static final String TAG = Logutil.makeTagLog(MediaParser.class);
    private static MediaParser _instance;

    private MediaParser() {

    }

    public static MediaParser getInstance() {
        if (null == _instance) {
            _instance = new MediaParser();
        }
        return _instance;
    }


    @Override
    public ContentValues getContentValuesByMusic(int usbFlag, String filePath) {
        ContentValues contentValues = null;
        MediaMetadataRetriever mmr = null;

        String musicUrl = "Unknown";
        String musicFolderUrl = "Unknown";
        String musicTitle = "Unknown";
        String musicTitlePinYing = "Unknown";
        String musicArtist = "Unknown";
        String musicArtistPinYing = "";
        String musicAlbum = "Unknown";
        String musicAlbumPinYing = "Unknown";
        int musicDuration = 0;

        // 实例化媒体解析类
        mmr = new MediaMetadataRetriever();

        try {
            if (new File(filePath).exists()) {
                mmr.setDataSource(filePath);
            } else {
                Logutil.e(TAG, "parserMusic() URL NO EXISTS !!! " + filePath);
                safeCloseMediaMetadataRetriever(mmr);
                return null;
            }
        } catch (Exception e) {
            Logutil.e(TAG, "MediaScanner:setDataSource() error !!! url: " + filePath);
            safeCloseMediaMetadataRetriever(mmr);
            return null;
        }

        // duration
        String sDuration = "0";

        try {
            sDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            musicDuration = Integer.valueOf((sDuration != null ? sDuration : "0"));// 时长有可能是乱码，防止类型转换异常
        } catch (Exception e) {
            Logutil.e(TAG, "Get Total time fail , url: " + filePath);
            musicDuration = 0;
        }

        // url
        musicUrl = filePath;
        // folder url
        musicFolderUrl = musicUrl.substring(0, musicUrl.lastIndexOf(File.separator));

        // title
        // 获得带后缀的名字
        musicTitle = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        // title pinying
        // 获得带后缀名字拼音
        try {
            musicTitlePinYing = HanziToPinyinUtil.getIntance().getSortKey(musicTitle,
                    HanziToPinyinUtil.FullNameStyle.CHINESE);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        // 获得歌手名字
        try {
            musicArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (null != musicArtist) {
                EncodingUtil encodingUtil = new EncodingUtil();
                musicArtist = encodingUtil.getEncodeString(musicArtist, null);
            }
        } catch (Exception e1) {
            Logutil.e(TAG, "Get artist fail !!! , url=" + filePath);
        }
        if (musicArtist == null || musicArtist != null && musicArtist.length() <= 0 || "".equals(musicArtist)) {
            musicArtist = "Unknown";
        }

        // 获取歌手拼音
        try {
            musicArtistPinYing = HanziToPinyinUtil.getIntance().getSortKey(musicArtist,
                    HanziToPinyinUtil.FullNameStyle.CHINESE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获得Album
        try {
            musicAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            // add 2018-4-20
            if (null != musicAlbum) {
                EncodingUtil encodingUtil = new EncodingUtil();
                musicAlbum = encodingUtil.getEncodeString(musicAlbum, null);
            }
        } catch (Exception e1) {
            musicAlbum = null;
        }
        if (musicAlbum == null || musicAlbum != null && musicAlbum.length() <= 0) {
            musicAlbum = "Unknown";
        }
        // 获取歌手拼音
        try {
            musicAlbumPinYing = HanziToPinyinUtil.getIntance().getSortKey(musicAlbum,
                    HanziToPinyinUtil.FullNameStyle.CHINESE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        safeCloseMediaMetadataRetriever(mmr);
        musicFolderUrl = filePath.substring(0, filePath.lastIndexOf(File.separator));// 文件所属文件夹
        contentValues = new ContentValues();
        contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_MUSIC);
        contentValues.put(DBConfiguration.MusicConfiguration.USB_FLAG, usbFlag);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_URL, musicUrl);
        contentValues.put(DBConfiguration.MusicConfiguration.FILE_TYPE, DBConfiguration.FLAG_MUSIC);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL, musicFolderUrl);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE, musicTitle);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING, musicTitlePinYing);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ARTIST, musicArtist);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ARTIST_PINYING, musicArtistPinYing);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ALBUM, musicAlbum);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ALBUM_PINYIN, musicAlbumPinYing);
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_DURATION, musicDuration);
        return contentValues;
    }

    private void safeCloseMediaMetadataRetriever(MediaMetadataRetriever mmr) {
        if (null != mmr) {
            try {
                mmr.release();
                mmr = null;
            } catch (Exception e) {
                Logutil.w(TAG, "----------------------safeCloseMediaMetadataRetriever()");
            }
        }
    }

    // 分离出字符串最后斜杠后的字符串
    private String splitLastSeparatorString(String str) {
        String newStr = str.substring(str.lastIndexOf(File.separator) + 1);
        return newStr;
    }

    @Override
    public ContentValues getContentValuesByVideo(int usbFlag, String filePath) {
        MediaMetadataRetriever mmr = null;

        if (filePath == null) {
            return null;
        }
        // 实例化媒体解析类
        mmr = new MediaMetadataRetriever();

        try {
            if (new File(filePath).exists()) {
                mmr.setDataSource(filePath);
            } else {
                Logutil.e(TAG, "parserMusic() URL NO EXISTS !!! " + filePath);
                safeCloseMediaMetadataRetriever(mmr);
                return null;
            }
        } catch (Exception e) {
            Logutil.e(TAG, "MediaScanner:setDataSource() error !!! url: " + filePath);
            safeCloseMediaMetadataRetriever(mmr);
            return null;
        }

        // duration
        String sDuration = "0";
        int videoDuration = 0;

        try {
            sDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            videoDuration = Integer.valueOf((sDuration != null ? sDuration : "0"));// 时长有可能是乱码，防止类型转换异常
        } catch (Exception e) {
            Logutil.e(TAG, "Get Total time fail , url: " + filePath);
            videoDuration = 0;
        }


        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);// 带后缀文件名
        String fileNamePinYin = HanziToPinyinUtil.getIntance().getSortKey(fileName,
                HanziToPinyinUtil.FullNameStyle.CHINESE).toLowerCase();// 文件名拼音
        String videoFolderUrl = filePath.substring(0, filePath.lastIndexOf(File.separator));// 文件所属文件夹

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_VIDEO);
        contentValues.put(DBConfiguration.VideoConfiguration.FILE_TYPE, DBConfiguration.FLAG_VIDEO);
        contentValues.put(DBConfiguration.VideoConfiguration.USB_FLAG, usbFlag);
        contentValues.put(DBConfiguration.VideoConfiguration.FILE_URL, filePath);
        contentValues.put(DBConfiguration.VideoConfiguration.FILE_NAME, fileName);
        contentValues.put(DBConfiguration.VideoConfiguration.FILE_NAME_PINYIN, fileNamePinYin);
        contentValues.put(DBConfiguration.VideoConfiguration.FILE_FOLDER_URL, videoFolderUrl);
        contentValues.put(DBConfiguration.VideoConfiguration.FILE_DURATION, videoDuration);

        return contentValues;
    }

    @Override
    public ContentValues getContentValuesByPhoto(int usbFlag, String filePath) {

        if (filePath == null) {
            return null;
        }
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);// 带后缀文件名
        String fileNamePinYin = HanziToPinyinUtil.getIntance().getSortKey(fileName,
                HanziToPinyinUtil.FullNameStyle.CHINESE).toLowerCase();// 文件名拼音
        String pictureFolderUrl = filePath.substring(0, filePath.lastIndexOf(File.separator));// 文件所属文件夹

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_PHOTO);
        contentValues.put(DBConfiguration.PhotoConfiguration.FILE_TYPE, DBConfiguration.FLAG_PHOTO);
        contentValues.put(DBConfiguration.PhotoConfiguration.USB_FLAG, usbFlag);
        contentValues.put(DBConfiguration.PhotoConfiguration.FILE_URL, filePath);
        contentValues.put(DBConfiguration.PhotoConfiguration.FILE_NAME, fileName);
        contentValues.put(DBConfiguration.PhotoConfiguration.FILE_NAME_PINYIN, fileNamePinYin);
        contentValues.put(DBConfiguration.PhotoConfiguration.FILE_FORDER_URL, pictureFolderUrl);
        return contentValues;
    }

    @Override
    public ContentValues getContentValuesByLyric(int usbFlag, String filePath) {
        if(filePath == null){
            return null;
        }

        String lrcName = FileUtil.getFileNameFromUrl(filePath);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfiguration.LyricConfiguration.FILE_TYPE,DBConfiguration.FLAG_LRC);
        contentValues.put(DBConfiguration.LyricConfiguration.USB_FLAG,usbFlag);
        contentValues.put(DBConfiguration.LyricConfiguration.LRC_URL,filePath);
        contentValues.put(DBConfiguration.LyricConfiguration.LRC_NAME,lrcName);
        return contentValues;
    }
}
