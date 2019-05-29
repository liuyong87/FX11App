package com.semisky.multimedia.media_usb.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.constants.Definition.MediaFileType;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.ComparatorMusicInfo;
import com.semisky.multimedia.common.utils.ComparatorVideoInfo;
import com.semisky.multimedia.common.utils.FileUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.db.DBConfiguration;
import com.semisky.multimedia.media_usb.db.IMediaDBManager;
import com.semisky.multimedia.media_usb.db.MediaDBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MediaStorageDataModel implements IMediaStorageDataModel {
    private static final String TAG = Logutil.makeTagLog(MediaStorageDataModel.class);
    private IMediaDBManager mMediaDBManager;

    public MediaStorageDataModel() {
        mMediaDBManager = MediaDBManager.getMediaDBManager();
    }

    private boolean isBindMediaDBManager() {
        return (null != mMediaDBManager);
    }

    @Override
    public long insertFavoriteMusic(MusicInfo musicInfo) {
        long insertRow = -1;
        if (isBindMediaDBManager() && null != musicInfo) {
            ContentValues values = convertMusicInfoToContentValues(musicInfo);
            if (null != values) {
                insertRow = mMediaDBManager.insertFavoriteMusic(values);
            }
        }
        Logutil.i(TAG, "insertFavoriteMusic() ..." + insertRow);
        return insertRow;
    }

    private ContentValues convertMusicInfoToContentValues(MusicInfo musicInfo) {
        ContentValues values = new ContentValues();
        values.put(DBConfiguration.MusicConfiguration.USB_FLAG, musicInfo.getUsbFlag());// 2.U盘标识
        values.put(DBConfiguration.MusicConfiguration.FILE_TYPE, musicInfo.getFileType());// 3.文件类型
        values.put(DBConfiguration.MusicConfiguration.MUSIC_URL, musicInfo.getUrl());// 4.文件路径
        values.put(DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL, musicInfo.getFileFolder());// 5.文件所属文件夹
        values.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE, musicInfo.getTitle());// 6.歌名
        values.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING, musicInfo.getTitlePinYing());// 7.歌名拼音
        values.put(DBConfiguration.MusicConfiguration.MUSIC_ARTIST, musicInfo.getArtist());// 8.歌手名
        values.put(DBConfiguration.MusicConfiguration.MUSIC_ARTIST_PINYING, musicInfo.getArtistPinYing());// 9.歌手名拼音
        values.put(DBConfiguration.MusicConfiguration.MUSIC_ALBUM, musicInfo.getAlbum());// 10.专辑名
        values.put(DBConfiguration.MusicConfiguration.MUSIC_ALBUM_PINYIN, musicInfo.getAlbumPinYing());// 11.专辑名拼音
        values.put(DBConfiguration.MusicConfiguration.MUSIC_DURATION, musicInfo.getDuration());// 12.总时长
        values.put(DBConfiguration.MusicConfiguration.MUSIC_FAVORITE, musicInfo.getFavorite());// 13.收藏
        return values;
    }

    @Override
    public List<MusicInfo> queryAllFavoriteMusics(int usbFlag) {
        if (isBindMediaDBManager()) {
            Cursor crs = mMediaDBManager.queryAllFavoriteMusics(usbFlag);
            Logutil.i(TAG, "queryAllFavoriteMusics() ..." + (crs != null ? crs.getCount() : 0));
            if (null != crs && crs.getColumnCount() > 0) {
                List<MusicInfo> mList = new ArrayList<MusicInfo>();
                try {
                    while (crs.moveToNext()) {

                        MusicInfo musicInfo = getMusicInfoFromCursor(crs);
                        if (null != musicInfo) {
                            mList.add(musicInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    crs.close();
                }
                Logutil.i(TAG, "queryAllFavoriteMusics() SIZE[" + (mList.size()) + "]");
                return mList;
            } else if (crs != null) {
                if (!crs.isClosed()) {
                    crs.close();
                }

            }
        }
        return null;
    }

    @Override
    public boolean isFavoriteWithSpecifyMusicUrl(int usbFlag, String url) {

        if (isBindMediaDBManager()) {
            Cursor crs = mMediaDBManager.querySpecifyMuiscFavorite(usbFlag, url);
            Logutil.i(TAG, "isFavoriteWithSpecifyMusicUrl() ..." + (crs != null ? crs.getCount() : 0));
            if (null != url && null != crs && crs.getCount() > 0) {
                if (!crs.isClosed()) {
                    crs.close();
                }
                return true;
            }
            if (!crs.isClosed()) {
                crs.close();
            }
        }


        return false;
    }

    @Override
    public List<MusicInfo> queryAllMusics(int usbFlag) {
        Cursor crs = mMediaDBManager.queryAllMusics(usbFlag);
        Logutil.i(TAG, "queryAllMusics() ..." + (crs != null ? crs.getCount() : 0));

        if (null != crs && crs.getColumnCount() > 0) {
            List<MusicInfo> mList = new ArrayList<MusicInfo>();
            try {
                while (crs.moveToNext()) {

                    MusicInfo musicInfo = getMusicInfoFromCursor(crs);
                    if (null != musicInfo) {

                        mList.add(musicInfo);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                crs.close();
            }

            Collections.sort(mList, new ComparatorMusicInfo());
            Logutil.i(TAG, "queryAllMusics() SIZE[" + (mList.size()) + "]");
            return mList;
        }// end-> if(null != cursor && cursor.getColumnCount() > 0)
        return Collections.EMPTY_LIST;
    }

    // 通过指定游标获取信息封装到音乐实体类中
    private MusicInfo getMusicInfoFromCursor(Cursor crs) {
        MusicInfo musicInfo = new MusicInfo();
        int _id_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration._ID);
        int usbFlag_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.USB_FLAG);
        int fileType_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.FILE_TYPE);
        int musicUrl_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_URL);
        int folderUrl_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL);
        int title_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_TITLE);
        int titlePinYin_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING);
        int artist_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_ARTIST);
        int artistPinYin_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_ARTIST_PINYING);
        int album_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_ALBUM);
        int albumPinYin_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_ALBUM_PINYIN);
        int duration_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_DURATION);
        int favorite_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_FAVORITE);

        musicInfo.set_id(crs.getInt(_id_columnIndex));// 1.自增长id
        musicInfo.setUsbFlag(crs.getInt(usbFlag_columnIndex));// 2.U盘标识
        musicInfo.setFileType(crs.getInt(fileType_columnIndex));// 3.文件类型
        musicInfo.setUrl(crs.getString(musicUrl_columnIndex));// 4.文件路径
        musicInfo.setFileFolder(crs.getString(folderUrl_columnIndex));// 5.文件所属文件夹
        musicInfo.setTitle(crs.getString(title_columnIndex));// 6.歌名
        musicInfo.setTitlePinYing(crs.getString(titlePinYin_columnIndex));// 7.歌名拼音
        musicInfo.setArtist(crs.getString(artist_columnIndex));// 8.歌手名
        musicInfo.setArtistPinYing(crs.getString(artistPinYin_columnIndex));// 9.歌手名拼音
        musicInfo.setAlbum(crs.getString(album_columnIndex));// 10.专辑名
        musicInfo.setAlbumPinYing(crs.getString(albumPinYin_columnIndex));// 11.专辑名拼音
        musicInfo.setDuration(crs.getInt(duration_columnIndex));// 12.总时长
        musicInfo.setFavorite(crs.getInt(favorite_columnIndex));// 13.收藏
        return musicInfo;
    }

    @Override
    public List<FolderInfo> queryAllMusicFolder(int usbFlag, String path) {
        if (isBindMediaDBManager()) {
            Cursor cursor = mMediaDBManager.queryAllMusicFolder(usbFlag, path);
            if (null != cursor) {
                if (cursor.getCount() > 0) {
                    // 待定
                }
            }
        }
        return null;
    }

    @Override
    public List<MusicInfo> queryFolderUnderMusic(int usbFlag, String path) {
        if (isBindMediaDBManager()) {
            Cursor cursor = mMediaDBManager.queryFolderUnderMusicFile(usbFlag, path);
            if (null != cursor) {
                if (cursor.getCount() > 0) {
                    List<MusicInfo> list = new ArrayList<MusicInfo>();
                    while (cursor.moveToNext()) {
                        MusicInfo musicInfo = getMusicInfoFromCursor(cursor);
                        list.add(musicInfo);
                    }
                    return list;
                }
                cursor.close();
            }

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<VideoInfo> queryAllVideos(int usbFlag) {
        Cursor crs = mMediaDBManager.queryAllVideos(usbFlag);
        Logutil.i(TAG, "queryAllVideos() ..." + (crs != null ? crs.getCount() : 0));
        if (null != crs && crs.getCount() > 0) {
            List<VideoInfo> list = new ArrayList<VideoInfo>();
            try {
                while (crs.moveToNext()) {
                    VideoInfo videoInfo = getVideoInfoFromCursor(crs);
                    if (null != videoInfo) {
                        list.add(videoInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                crs.close();
            }
            Collections.sort(list, new ComparatorVideoInfo());
            Logutil.i(TAG, "queryAllVideos() SIZE[" + list.size() + "]");
            return list;
        } else if (crs != null) {
            if (!crs.isClosed()) {
                crs.close();
            }
        }
        return null;
    }

    // 通过指定游标获取信息封装到视频实体类中
    private VideoInfo getVideoInfoFromCursor(Cursor crs) {

        int _id_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration._ID);
        int usbFlag_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.USB_FLAG);
        int fileType_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_TYPE);
        int fileUrl_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_URL);
        int fileFolderUrl_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_FOLDER_URL);
        int fileName_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_NAME);
        int fileNamePinYin_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_NAME_PINYIN);
        int duration_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_DURATION);

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.set_id(crs.getInt(_id_columnIndex));// 1.自增长ID
        videoInfo.setUsbFlag(crs.getInt(usbFlag_columnIndex));// 2.U盘标识
        videoInfo.setFileType(crs.getInt(fileType_columnIndex));// 3.文件类型
        videoInfo.setFileUrl(crs.getString(fileUrl_columnIndex));// 4.文件路径
        videoInfo.setFileFolder(crs.getString(fileFolderUrl_columnIndex));// 5.文件所属文件夹
        videoInfo.setFileName(crs.getString(fileName_columnIndex));// 6.文件名
        videoInfo.setFileNamePinYin(crs.getString(fileNamePinYin_columnIndex));// 7.文件名拼音
        videoInfo.setDuration(crs.getInt(duration_columnIndex));// 8.文件总时长
        return videoInfo;
    }

    @Override
    public List<PhotoInfo> queryAllPhotos(int usbFlag) {
        Cursor crs = mMediaDBManager.queryAllPhotos(usbFlag);
        Logutil.i(TAG, "queryAllPhotos() ..." + (crs != null ? crs.getCount() : 0));

        if (null != crs && crs.getCount() > 0) {
            List<PhotoInfo> list = new ArrayList<PhotoInfo>();
            try {
                while (crs.moveToNext()) {
                    PhotoInfo photoInfo = getPhotoInfoFromCursor(crs);
                    if (null != photoInfo) {
                        list.add(photoInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                crs.close();
            }
            Logutil.i(TAG, "queryAllPhotos() SIZE[" + list.size() + "]");
            return list;
        } else if (crs != null) {
            if (!crs.isClosed()) {
                crs.close();
            }
        }
        return null;
    }

    // 通过指定游标获取信息封装到图片实体类中
    private PhotoInfo getPhotoInfoFromCursor(Cursor crs) {
        int _id_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration._ID);
        int usbFlag_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.USB_FLAG);
        int fileType_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_TYPE);
        int fileUrl_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_URL);
        int fileFolderUrl_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_FORDER_URL);
        int fileName_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_NAME);
        int fileNamePinYinIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_NAME_PINYIN);

        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.set_id(crs.getInt(_id_columnIndex));// 1.自增长ID
        photoInfo.setUsbFlag(crs.getInt(usbFlag_columnIndex));// 2.U盘标识
        photoInfo.setFileUrl(crs.getString(fileUrl_columnIndex));// 3.文件路径
        photoInfo.setFileType(crs.getInt(fileType_columnIndex));// 4.文件类型
        photoInfo.setFileFolder(crs.getString(fileFolderUrl_columnIndex));// 5.文件所属文件夹
        photoInfo.setFileName(crs.getString(fileName_columnIndex));// 6.文件名
        photoInfo.setFileNamePinYin(crs.getString(fileNamePinYinIndex));// 7.文件名拼音
        return photoInfo;
    }

    List<FolderInfo> mTempDirList = new ArrayList<FolderInfo>();// 暂存文件夹
    List<FolderInfo> mTempFileList = new ArrayList<FolderInfo>();// 暂存文件
    List<FolderInfo> mMainDirList = new ArrayList<FolderInfo>();// 暂存合并后文件与文件夹集合
    private Object syncLock = new Object();

    @SuppressWarnings("unchecked")
    @Override
    public List<FolderInfo> querySpecifyDirectoryUnder(String curDir) {
        Logutil.i(TAG, "querySpecifyDirectoryUnder() curDir=" + curDir);
        synchronized (syncLock) {
            if (isBindMediaDBManager()) {
                Cursor musicCrs = mMediaDBManager.queryMusicDirectoryUnder(curDir);
                Cursor videoCrs = mMediaDBManager.queryVideoDirectoryUnder(curDir);
                Cursor photoCrs = mMediaDBManager.queryPhotoDirectoryUnder(curDir);
                List<FolderInfo> list = new ArrayList<FolderInfo>();
                //corsor没有关闭异常
                try {
                    handlerMusicDirInfo(curDir, musicCrs);
                    handlerVideoDirInfo(curDir, videoCrs);
                    handlerPhotoDirInfo(curDir, photoCrs);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    musicCrs.close();
                    videoCrs.close();
                    photoCrs.close();
                }

                // 整合集合数据
                mMainDirList.addAll(mTempDirList);
                mMainDirList.addAll(mTempDirList.size(), mTempFileList);
                list.addAll(mMainDirList);
                // 清除集合暂存数据
                mMainDirList.clear();
                mTempDirList.clear();
                mTempFileList.clear();
                Logutil.i(TAG, "querySpecifyDirectoryUnder() ..." + list.size());
                return list;
            }
            Logutil.i(TAG, "querySpecifyDirectoryUnder() ... 00");
            return Collections.EMPTY_LIST;
        }
    }

    // 处理音乐目录信息
    private void handlerMusicDirInfo(String curDir, Cursor crs) {
        Logutil.i(TAG, "handlerMusicDirInfo() ..." + (crs != null ? crs.getCount() : 0));
        if (null != crs && crs.getCount() > 0) {
            while (crs.moveToNext()) {

                int url_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_URL);
                int usbFlag_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.USB_FLAG);
                String url = crs.getString(url_columnIndex);// 获取音乐URL
                int usbFlag = crs.getInt(usbFlag_columnIndex);// U盘标识

                // 判定当前URL是否为目录
                boolean isDir = FileUtil.isDirByRelativeCurrentDir(curDir, url);
                // 是目录
                if (isDir) {
                    String dirName = FileUtil.getDirNameByRelativeCurrentDir(curDir, url);// 目录名字
                    String folderUrl = (curDir + "/" + dirName);

                    FolderInfo folderInfo = new FolderInfo();
                    folderInfo.setUsbFlag(usbFlag);
                    folderInfo.setType(MediaFileType.TYPE_FOLDER);
                    folderInfo.setUrl(folderUrl);
                    folderInfo.setName(dirName);
                    folderInfo.setNamePinYin("NA");

                    if (!mTempDirList.contains(folderInfo)) {
                        mTempDirList.add(folderInfo);
                    }
                }
                // 是文件
                else {
                    int fileType_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.FILE_TYPE);
                    int title_columnIndex = crs.getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_TITLE);
                    int titlePinYin_columnIndex = crs
                            .getColumnIndex(DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING);

                    FolderInfo folderInfo = new FolderInfo();
                    folderInfo.setUsbFlag(usbFlag);
                    folderInfo.setType(crs.getInt(fileType_columnIndex));
                    folderInfo.setUrl(url);
                    folderInfo.setName(crs.getString(title_columnIndex));
                    folderInfo.setNamePinYin(crs.getString(titlePinYin_columnIndex));
                    mTempFileList.add(folderInfo);
                }
            }
            Logutil.i(TAG, "handlerMusicDirInfo() mTempDirList.Size()=" + mTempDirList.size());
            Logutil.i(TAG, "handlerMusicDirInfo() mTempFileList.Size()=" + mTempFileList.size());
        }
    }

    // 处理视频目录信息
    private void handlerVideoDirInfo(String curDir, Cursor crs) {
        Logutil.i(TAG, "handlerVideoDirInfo() ..." + (crs != null ? crs.getCount() : 0));
        if (null != crs && crs.getCount() > 0) {
            while (crs.moveToNext()) {
                int fileUrl_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_URL);
                int usbFlag_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.USB_FLAG);
                String fileUrl = crs.getString(fileUrl_columnIndex);// 文件URL
                int usbFlag = crs.getInt(usbFlag_columnIndex);

                boolean isDir = FileUtil.isDirByRelativeCurrentDir(curDir, fileUrl);
                // 是目录
                if (isDir) {
                    String dirName = FileUtil.getDirNameByRelativeCurrentDir(curDir, fileUrl);
                    String folderUrl = (curDir + "/" + dirName);

                    FolderInfo folderInfo = new FolderInfo();
                    folderInfo.setUsbFlag(usbFlag);
                    folderInfo.setType(MediaFileType.TYPE_FOLDER);
                    folderInfo.setUrl(folderUrl);
                    folderInfo.setName(dirName);
                    folderInfo.setNamePinYin("NA");

                    if (!mTempDirList.contains(folderInfo)) {
                        mTempDirList.add(folderInfo);
                    }
                }
                // 是文件
                else {
                    int fileType_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_TYPE);
                    int fileName_columnIndex = crs.getColumnIndex(DBConfiguration.VideoConfiguration.FILE_NAME);
                    int filePinYin_columnIndex = crs
                            .getColumnIndex(DBConfiguration.VideoConfiguration.FILE_NAME_PINYIN);

                    FolderInfo folderInfo = new FolderInfo();
                    folderInfo.setUsbFlag(usbFlag);
                    folderInfo.setType(crs.getInt(fileType_columnIndex));
                    folderInfo.setUrl(fileUrl);
                    folderInfo.setName(crs.getString(fileName_columnIndex));
                    folderInfo.setNamePinYin(crs.getString(filePinYin_columnIndex));
                    mTempFileList.add(folderInfo);
                }
            }
            Logutil.i(TAG, "handlerVideoDirInfo() mTempDirList.Size()=" + mTempDirList.size());
            Logutil.i(TAG, "handlerVideoDirInfo() mTempFileList.Size()=" + mTempFileList.size());
        }
    }

    // 处理图片目录信息
    private void handlerPhotoDirInfo(String curDir, Cursor crs) {
        Logutil.i(TAG, "handlerPhotoDirInfo() ..." + (crs != null ? crs.getCount() : 0));
        if (null != crs && crs.getCount() > 0) {
            while (crs.moveToNext()) {
                int fileUrl_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_URL);
                int usbFlag_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.USB_FLAG);
                String fileUrl = crs.getString(fileUrl_columnIndex);// 文件URL
                int usbFlag = crs.getInt(usbFlag_columnIndex);

                boolean isDir = FileUtil.isDirByRelativeCurrentDir(curDir, fileUrl);

                // 是目录
                if (isDir) {
                    String dirName = FileUtil.getDirNameByRelativeCurrentDir(curDir, fileUrl);
                    String folderUrl = (curDir + "/" + dirName);

                    FolderInfo folderInfo = new FolderInfo();
                    folderInfo.setUsbFlag(usbFlag);
                    folderInfo.setType(MediaFileType.TYPE_FOLDER);
                    folderInfo.setUrl(folderUrl);
                    folderInfo.setName(dirName);
                    folderInfo.setNamePinYin("NA");

                    if (!mTempDirList.contains(folderInfo)) {
                        mTempDirList.add(folderInfo);
                    }
                }
                // 是文件
                else {
                    int fileType_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_TYPE);
                    int fileName_columnIndex = crs.getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_NAME);
                    int filePinYin_columnIndex = crs
                            .getColumnIndex(DBConfiguration.PhotoConfiguration.FILE_NAME_PINYIN);

                    FolderInfo folderInfo = new FolderInfo();
                    folderInfo.setUsbFlag(usbFlag);
                    folderInfo.setType(crs.getInt(fileType_columnIndex));
                    folderInfo.setUrl(fileUrl);
                    folderInfo.setName(crs.getString(fileName_columnIndex));
                    folderInfo.setNamePinYin(crs.getString(filePinYin_columnIndex));
                    mTempFileList.add(folderInfo);
                }
            }
            Logutil.i(TAG, "handlerPhotoDirInfo() mTempDirList.Size()=" + mTempDirList.size());
            Logutil.i(TAG, "handlerPhotoDirInfo() mTempFileList.Size()=" + mTempFileList.size());
        }
    }

    @Override
    public int queryMusicsSize(int usbFlag) {
        int size = 0;
        if (isBindMediaDBManager()) {
            Cursor crs = mMediaDBManager.queryAllMusics(usbFlag);
            size = (crs != null ? crs.getCount() : 0);
            if (!crs.isClosed()) {
                crs.close();
            }
        }
        Logutil.i(TAG, "queryMusicsSize() SIZE[" + size + "]");
        return size;
    }

    @Override
    public int queryVideosSize(int usbFlag) {
        int size = 0;
        if (isBindMediaDBManager()) {
            Cursor crs = mMediaDBManager.queryAllVideos(usbFlag);
            size = (crs != null ? crs.getCount() : 0);
            if (!crs.isClosed()) {
                crs.close();
            }
        }
        Logutil.i(TAG, "queryVideosSize() SIZE[" + size + "]");
        return size;

    }

    @Override
    public int queryPhotosSize(int usbFlag) {
        int size = 0;
        if (isBindMediaDBManager()) {
            Cursor crs = mMediaDBManager.queryAllPhotos(usbFlag);
            size = (crs != null ? crs.getCount() : 0);
            if (!crs.isClosed()) {
                crs.close();
            }
        }
        Logutil.i(TAG, "queryPhotosSize() SIZE[" + size + "]");
        return size;
    }

    @Override
    public String queryLyricUrl(String url) {
        if (null == url) {
            return null;
        }
        String lrcUrl = null;
        String lrcName = FileUtil.getFileNameFromUrl(url);
        int usbFlag = AppUtil.conversionUrlToUsbFlag(url);

        Log.i(TAG,"queryLyricUrl() lrcName : "+lrcName);
        Log.i(TAG,"queryLyricUrl() usbFlag : "+usbFlag);

        Cursor cr = mMediaDBManager.queryLyric(usbFlag, lrcName);
        if (null == cr) {
            return null;
        }
        int count = cr.getCount();
        Log.i(TAG, "queryLyricUrl() ..." + cr.getCount());
        try {
            if (count > 0) {
                while (cr.moveToNext()) {
                    String lrc = cr.getString(cr.getColumnIndex(DBConfiguration.LyricConfiguration.LRC_NAME));
                    lrcUrl = cr.getString(cr.getColumnIndex(DBConfiguration.LyricConfiguration.LRC_URL));
                    Log.i(TAG, "queryLyricUrl() lrcUrl : " + lrcUrl+",lrc : "+lrc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cr.close();
        return lrcUrl;
    }

    @Override
    public long deleteAllMusics(int usbFlag) {
        Logutil.i(TAG, "deleteAllMusics() ..." + usbFlag);
        if (isBindMediaDBManager()) {
            return mMediaDBManager.deleteAllMusics(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteAllVideos(int usbFlag) {
        Logutil.i(TAG, "deleteAllVideos() ..." + usbFlag);
        if (isBindMediaDBManager()) {
            return mMediaDBManager.deleteAllVideos(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteAllPhotos(int usbFlag) {
        Logutil.i(TAG, "deleteAllPhotos() ..." + usbFlag);
        if (isBindMediaDBManager()) {
            return mMediaDBManager.deleteAllPhotos(usbFlag);
        }
        return 0;
    }

    @Override
    public long deleteFavoriteMusic(int id) {
        if (isBindMediaDBManager()) {
            return mMediaDBManager.deleteFavoriteMusic(id);
        }
        return 0;
    }

    @Override
    public long deleteFavoriteWithMusicUrl(int usbFlag, String url) {
        if (isBindMediaDBManager()) {
            return mMediaDBManager.deleteFavoriteWithMusicUrl(usbFlag, url);
        }
        return 0;
    }

    @Override
    public void deleteBatchFavorite(List<String> list) {
        if (isBindMediaDBManager()) {
            mMediaDBManager.deleteBatchFavorite(list);
        }
    }

    @Override
    public long deleteAllLyrics(int usbFlag) {
        if (isBindMediaDBManager()) {
            return mMediaDBManager.deleteAllLyrics(usbFlag);
        }
        return 0;
    }
}
