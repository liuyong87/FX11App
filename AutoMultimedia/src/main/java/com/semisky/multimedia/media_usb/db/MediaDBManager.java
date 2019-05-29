package com.semisky.multimedia.media_usb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaDBManager implements IMediaDBManager {
    private static final String TAG = Logutil.makeTagLog(MediaDBManager.class);
    private static MediaDBManager _instance;
    private Context mContext;

    private static MediaDBHelper mDBHelper;
    private static SQLiteDatabase mWritableDatabase;

    private volatile boolean mIsStopBatchInsert = false;// 是否停止批处理操作
    private List<ContentValues> mTempContentValuesList = new ArrayList<ContentValues>();// 暂存未达到批量存储的数据
    private static final int LIMIT_CAPACITY = 50;// 每次限定插入数据对象数量
    private static MediaDBManager mediaDBManager;

    public static MediaDBManager getMediaDBManager() {
        if (mediaDBManager == null) {
            mediaDBManager = new MediaDBManager();
        }
        return mediaDBManager;
    }

    public MediaDBManager() {
        this.mContext = MediaApplication.getContext();
        mDBHelper = new MediaDBHelper(mContext, DBConfiguration.DATABASE_NAME, null, DBConfiguration.DATABASE_VERSION);
        this.mWritableDatabase = mDBHelper.getWritableDatabase();
        this.mWritableDatabase.setLocale(Locale.CHINESE);
    }

    // 获取DB
    private synchronized SQLiteDatabase getDB() {
        return this.mWritableDatabase;
    }


    @Override
    public void stopBatchInsert() {
        this.mIsStopBatchInsert = true;
        clearTempContentValuesList();
        Logutil.i(TAG, "stopBatchInsert()...");
    }

    @Override
    public void prepareBatchInsert() {
        this.mIsStopBatchInsert = false;
        clearTempContentValuesList();
        Logutil.i(TAG, "prepareBatchInsert()...");
    }

    private void clearTempContentValuesList() {
        if (null != mTempContentValuesList && mTempContentValuesList.size() > 0) {
            Logutil.i(TAG, "clearTempContentValuesList() ..." + mTempContentValuesList.size());
            mTempContentValuesList.clear();
        }
    }

    @Override
    public synchronized void insertBatchDataToDB(ContentValues contentValues, OnBatchDataInsertListener listener) {
        // TODO Auto-generated method stub
        if (null != contentValues && null != mTempContentValuesList) {

            mTempContentValuesList.add(contentValues);

            // 达到批量插入数量，开始批量插入数据库
            if (mTempContentValuesList.size() >= LIMIT_CAPACITY) {
                Logutil.i(TAG,
                        "insertBatchDataToDB()...."
                                + (null != mTempContentValuesList ? mTempContentValuesList.size() : 0));
                // 数据库开启事务
                getDB().beginTransaction();

                try {
                    for (ContentValues values : mTempContentValuesList) {
                        if (mIsStopBatchInsert) {
                            mTempContentValuesList.clear();
                            Logutil.i(TAG, "insertBatchDataToDB() FORCE STOP !!!");
                            return;
                        }// end->(mIsStopBatchInsert)

                        switch (values.getAsInteger(DBConfiguration.FILE_TYPE)) {
                            case DBConfiguration.FLAG_MUSIC:
                                getDB().insert(DBConfiguration.MusicConfiguration.TABLE_NAME, null, values);
                                break;
                            case DBConfiguration.FLAG_VIDEO:
                                getDB().insert(DBConfiguration.VideoConfiguration.TABLE_NAME, null, values);
                                break;
                            case DBConfiguration.FLAG_PHOTO:
                                getDB().insert(DBConfiguration.PhotoConfiguration.TABLE_NAME, null, values);
                                break;
                            case DBConfiguration.FLAG_LRC:
                                getDB().insert(DBConfiguration.LyricConfiguration.TABLE_NAME, null, values);
                                break;
                        }

                    }
                    getDB().setTransactionSuccessful();// 设置事务成功，否则事务回滚
                    mTempContentValuesList.clear();// 清空集合
                    // 批量插入数据处理通知
                    if (null != listener) {
                        listener.onNotifyDataChanage();
                    }
                } catch (Exception e) {
                    Logutil.e(TAG, "insertBatchDataToDB() Fail !!!,ERROR:" + e.getMessage());
                } finally {
                    getDB().endTransaction();
                }
            }
        }// (null != values)
    }

    @Override
    public synchronized void insertLastBatchDataToDB(OnBatchDataInsertListener listener) {
        Logutil.i(TAG, "insertLastBatchDataToDB()..."
                + (null != mTempContentValuesList ? mTempContentValuesList.size() : 0));
        if (null != mTempContentValuesList && mTempContentValuesList.size() > 0) {
            getDB().beginTransaction();
            try {
                for (ContentValues values : mTempContentValuesList) {
                    if (mIsStopBatchInsert) {
                        mTempContentValuesList.clear();
                        Logutil.i(TAG, "insertLastBatchDataToDB() FORCE STOP !!!");
                        return;
                    }// end->(mIsStopBatchInsert)
                    switch (values.getAsInteger(DBConfiguration.FILE_TYPE)) {
                        case DBConfiguration.FLAG_MUSIC:
                            getDB().insert(DBConfiguration.MusicConfiguration.TABLE_NAME, null, values);
                            break;
                        case DBConfiguration.FLAG_VIDEO:
                            getDB().insert(DBConfiguration.VideoConfiguration.TABLE_NAME, null, values);
                            break;
                        case DBConfiguration.FLAG_PHOTO:
                            getDB().insert(DBConfiguration.PhotoConfiguration.TABLE_NAME, null, values);
                            break;
                        case DBConfiguration.FLAG_LRC:
                            getDB().insert(DBConfiguration.LyricConfiguration.TABLE_NAME, null, values);
                            break;
                    }
                }// end -> for(ContentValues values : mTempContentValuesList)

                getDB().setTransactionSuccessful();
                mTempContentValuesList.clear();
                // 批量插入数据处理通知
                if (null != listener) {
                    listener.onNotifyDataChanage();
                }
            } catch (Exception e) {
                Logutil.e(TAG, "insertLastBatchDataToDB() Fail !!!,ERROR:" + e.getMessage());
            } finally {
                getDB().endTransaction();
            }
        }
    }

    @Override
    public long insertFavoriteMusic(ContentValues contentValues) {
        return getDB().insert(DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME, null, contentValues);
    }

    @Override
    public Cursor queryAllMusics(int usbFlag) {
        Cursor cursor = getDB().query(true, DBConfiguration.MusicConfiguration.TABLE_NAME, null,
                DBConfiguration.MusicConfiguration.USB_FLAG + "=?", new String[]{usbFlag + ""},
                DBConfiguration.MusicConfiguration.MUSIC_URL, null,
                DBConfiguration.MusicConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor queryAllFavoriteMusics(int usbFlag) {
        Cursor cursor = getDB().query(DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME, null,
                DBConfiguration.MusicFavoriteConfiguration.USB_FLAG + "=?", new String[]{usbFlag + ""}, null, null,
                DBConfiguration.MusicFavoriteConfiguration.DEFAULT_SORT_ORDER);
        return cursor;
    }

    @Override
    public Cursor queryAllVideos(int usbFlag) {
        Cursor cursor = getDB().query(true,
                DBConfiguration.VideoConfiguration.TABLE_NAME,
                null,
                DBConfiguration.VideoConfiguration.USB_FLAG + " =? ", new String[]{usbFlag + ""},
                DBConfiguration.VideoConfiguration.FILE_URL, null,
                DBConfiguration.VideoConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor queryAllPhotos(int usbFlag) {
        Cursor cursor = getDB().query(true, DBConfiguration.PhotoConfiguration.TABLE_NAME, null,
                DBConfiguration.PhotoConfiguration.USB_FLAG + "=?", new String[]{usbFlag + ""},
                DBConfiguration.PhotoConfiguration.FILE_URL, null,
                DBConfiguration.PhotoConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor queryMusicDirectoryUnder(String dir) {
        Cursor cursor = getDB().query(true, DBConfiguration.MusicConfiguration.TABLE_NAME, null,
                DBConfiguration.MusicConfiguration.MUSIC_URL + " like ?", new String[]{dir + "%"},
                DBConfiguration.MusicConfiguration.MUSIC_URL,
                null, DBConfiguration.MusicConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor queryVideoDirectoryUnder(String dir) {
        Cursor cursor = getDB().query(true, DBConfiguration.VideoConfiguration.TABLE_NAME, null,
                DBConfiguration.VideoConfiguration.FILE_URL + " like ?", new String[]{dir + "%"},
                DBConfiguration.VideoConfiguration.FILE_URL, null,
                DBConfiguration.VideoConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor queryPhotoDirectoryUnder(String dir) {
        Cursor cursor = getDB().query(true, DBConfiguration.PhotoConfiguration.TABLE_NAME, null,
                DBConfiguration.PhotoConfiguration.FILE_URL + " like ?", new String[]{dir + "%"},
                DBConfiguration.PhotoConfiguration.FILE_URL, null,
                DBConfiguration.PhotoConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor querySpecifyMuiscFavorite(int usbFlag, String url) {
        Cursor cursor = getDB().query(DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME,
                null,
                DBConfiguration.MusicFavoriteConfiguration.USB_FLAG + " = ?" + " and " +
                        DBConfiguration.MusicFavoriteConfiguration.MUSIC_URL + "= ?",
                new String[]{"" + usbFlag, url},
                null,
                null,
                DBConfiguration.MusicFavoriteConfiguration.DEFAULT_SORT_ORDER);
        return cursor;
    }


    @Override
    public Cursor queryAllMusicFolder(int usbFlag, String path) {
        Cursor cursor = getDB().query(true, DBConfiguration.MusicConfiguration.TABLE_NAME, null,
                DBConfiguration.MusicConfiguration.USB_FLAG + " =? and "
                        + DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL + " =? ", new String[]{usbFlag + "", path},
                DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL, null, null, null);
        return cursor;
    }

    @Override
    public Cursor queryFolderUnderMusicFile(int usbFlag, String path) {
        Cursor cursor = getDB().query(true, DBConfiguration.MusicConfiguration.TABLE_NAME, null,
                DBConfiguration.MusicConfiguration.USB_FLAG + " =? and "
                        + DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL + " =? ", new String[]{usbFlag + "", path},
                null, null, DBConfiguration.MusicConfiguration.DEFAULT_SORT_ORDER, null);
        return cursor;
    }

    @Override
    public Cursor queryLyric(int usbFlag, String lrcName) {
        Cursor cursor = getDB().query(DBConfiguration.LyricConfiguration.TABLE_NAME, null,
                DBConfiguration.LyricConfiguration.USB_FLAG + " =? and " +
                        DBConfiguration.LyricConfiguration.LRC_NAME + " =? ",
                new String[]{usbFlag + "", lrcName},
                DBConfiguration.LyricConfiguration.LRC_URL,
                null,
                null,
                null);
        return cursor;
    }

    @Override
    public long deleteAllMusics(int usbFlag) {
        try {
            return getDB().delete(DBConfiguration.MusicConfiguration.TABLE_NAME,
                    DBConfiguration.MusicConfiguration.USB_FLAG + " =?", new String[]{usbFlag + ""});
        } catch (Exception e) {
            Log.e(TAG,"deleteAllMusics() FAIL !!!");
            e.printStackTrace();
        } finally {
            reqResetSqliteSequence(getDB(), DBConfiguration.MusicConfiguration.TABLE_NAME);
        }
        return -1;
    }

    @Override
    public long deleteAllLyrics(int usbFlag) {
        try {
            return getDB().delete(DBConfiguration.LyricConfiguration.TABLE_NAME,
                    DBConfiguration.LyricConfiguration.USB_FLAG + " =? ", new String[]{usbFlag + ""});
        } catch (Exception e) {
            Log.e(TAG,"deleteAllLyrics() FAIL !!!");
            e.printStackTrace();
        } finally {
            reqResetSqliteSequence(getDB(), DBConfiguration.LyricConfiguration.TABLE_NAME);
        }
        return -1;
    }

    @Override
    public long deleteAllVideos(int usbFlag) {
        try {
            return getDB().delete(DBConfiguration.VideoConfiguration.TABLE_NAME,
                    DBConfiguration.VideoConfiguration.USB_FLAG + " =?", new String[]{usbFlag + ""});
        } catch (Exception e) {
            Log.e(TAG,"deleteAllVideos() FAIL !!!");
            e.printStackTrace();
        } finally {
            reqResetSqliteSequence(getDB(), DBConfiguration.VideoConfiguration.TABLE_NAME);
        }
        return -1;
    }

    @Override
    public long deleteAllPhotos(int usbFlag) {
        try {
            return getDB().delete(DBConfiguration.PhotoConfiguration.TABLE_NAME,
                    DBConfiguration.PhotoConfiguration.USB_FLAG + " =?", new String[]{usbFlag + ""});
        } catch (Exception e) {
            Log.e(TAG,"deleteAllPhotos() FAIL !!!");
            e.printStackTrace();
        } finally {
            reqResetSqliteSequence(getDB(), DBConfiguration.PhotoConfiguration.TABLE_NAME);
        }
        return -1;
    }

    @Override
    public long deleteFavoriteMusic(int id) {
        try {
            return getDB().delete(DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME,
                    DBConfiguration.MusicFavoriteConfiguration._ID + " =?", new String[]{id + ""});
        } catch (Exception e) {
            Log.e(TAG, "deleteFavoriteMusic() FAIL !!!");
            e.printStackTrace();
        } finally {
            reqResetSqliteSequence(getDB(), DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME);
        }
        return -1;
    }

    @Override
    public long deleteFavoriteWithMusicUrl(int usbFlag, String url) {
        try {
            return getDB().delete(DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME,
                    DBConfiguration.MusicFavoriteConfiguration.USB_FLAG + "=?" + " and " +
                            DBConfiguration.MusicFavoriteConfiguration.MUSIC_URL + " =?", new String[]{"" + usbFlag, url});
        } catch (Exception e) {
            Log.e(TAG,"deleteFavoriteWithMusicUrl() FAIL !!!");
            e.printStackTrace();
        } finally {
            reqResetSqliteSequence(getDB(), DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME);
        }
        return -1;
    }

    @Override
    public void deleteBatchFavorite(List<String> list) {
        long delFavoriteRow = -1;
        if (null == list) {
            return;
        }
        getDB().beginTransaction();
        try {
            for (String url : list) {
                delFavoriteRow = deleteFavoriteWithMusicUrl(AppUtil.getUsbFlagFrom(url), url);
                Logutil.i(TAG, "deleteBatchFavorite() ..." + delFavoriteRow);
            }
            getDB().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getDB().endTransaction();
        }
    }

    void reqResetSqliteSequence(SQLiteDatabase db, String tableName) {
        db.execSQL("update sqlite_sequence set seq = 0 where name = '" + tableName + "'");
    }


}
