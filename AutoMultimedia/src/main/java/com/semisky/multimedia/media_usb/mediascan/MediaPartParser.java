package com.semisky.multimedia.media_usb.mediascan;

import android.content.ContentValues;


import com.semisky.multimedia.common.utils.FileUtil;
import com.semisky.multimedia.common.utils.HanziToPinyinUtil;
import com.semisky.multimedia.media_music.model.IMusicParserModel;
import com.semisky.multimedia.media_music.model.MusicParserModel;
import com.semisky.multimedia.media_usb.db.DBConfiguration;

import java.io.File;

/**
 * 解析部分媒体工具类
 *
 * @author liuyong
 */
public class MediaPartParser implements IMediaParser {
     IMusicParserModel musicParserModel =new MusicParserModel();

    @Override
    public ContentValues getContentValuesByMusic(int usbFlag, String filePath) {
        if (null == filePath) {
            return null;
        }
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);// 带后缀文件名
        String musicFileNamePinYin = HanziToPinyinUtil.getIntance().getSortKey(fileName,
                HanziToPinyinUtil.FullNameStyle.CHINESE).toLowerCase();// 文件名拼音
        String musicFolderUrl = filePath.substring(0, filePath.lastIndexOf(File.separator));// 文件所属文件夹

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_MUSIC);// 批量插入时公共标识文件类型
        contentValues.put(DBConfiguration.MusicConfiguration.USB_FLAG, usbFlag);// U盘标识
        contentValues.put(DBConfiguration.MusicConfiguration.FILE_TYPE, DBConfiguration.FLAG_MUSIC);// 文件类型
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_URL, filePath);// 文件路径
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL, musicFolderUrl);// 文件所属文件夹
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE, fileName);// 文件名
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING, musicFileNamePinYin);// 文件名拼音
        contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_FAVORITE, 0);// 0:未收藏,1:表示收藏
        return contentValues;
    }

    @Override
    public ContentValues getContentValuesByVideo(int usbFlag, String filePath) {
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
        return contentValues;
    }

    @Override
    public ContentValues getContentValuesByPhoto(int usbFlag, String filePath) {
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
