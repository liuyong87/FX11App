package com.semisky.multimedia.media_usb.mediascan;

import android.content.ContentValues;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.db.IMediaDBManager;
import com.semisky.multimedia.media_usb.db.MediaDBManager;

import java.io.File;


/**
 * 媒体扫描器
 *
 * @author liuyong
 */
public final class MediaScanner implements IMediaScanner {
    private static final String TAG = Logutil.makeTagLog(MediaScanner.class);
    private static MediaScanner _instance;

    private String mScanFirstMusicUrlOfUsb1 = null;
    private String mScanFirstMusicUrlOfUsb2 = null;
    private String mScanFirstVideoUrlOfUsb1 = null;
    private String mScanFirstVideoUrlOfUsb2 = null;
    private String mScanFirstPhotoUrlOfUsb1 = null;
    private String mScanFirstPhotoUrlOfUsb2 = null;

    private Handler mBackgroundHandler = null;
    private MediaScannerThread mMediaScannerThread = null;
    private EngineScannerRunnable mEngineScannerRunnableByUsb1;
    private EngineScannerRunnable mEngineScannerRunnableByUsb2;
    private OnMediaScannerListener mOnMediaScannerListener;

    private boolean usb1ScanIsEnd = false;
    private boolean usb2ScanIsEnd = false;

    private int mUSBScannerStatus = MEDIA_SCAN_STATE_INVALID;// 媒体扫描器状态

    private MediaScanner() {
        initMediaScannerThread();// 初始化后台扫描线程
    }

    public static MediaScanner getInstance() {
        if (null == _instance) {
            _instance = new MediaScanner();
        }
        return _instance;
    }

    @Override
    public void registerOnMediaScannerListener(OnMediaScannerListener listener) {
        this.mOnMediaScannerListener = listener;
    }

    @Override
    public void unregisterOnMediaScannerListener() {
        mOnMediaScannerListener = null;
    }

    // 媒体扫描开始通知
    private void notifyScannerStart(int usbFlag) {
        if (null != this.mOnMediaScannerListener) {
            this.mOnMediaScannerListener.onScannerStart(usbFlag);
        }
    }

    // 媒体文件正在扫描通知
    private void notifyScanning(int usbFlag) {
        if (null != this.mOnMediaScannerListener) {
            this.mOnMediaScannerListener.onScanning(usbFlag);
        }
    }

    // 媒体文件扫描停止通知
    private void notifyScannerStoped(int usbFlag) {
        if (null != this.mOnMediaScannerListener) {
            this.mOnMediaScannerListener.onScannerStop(usbFlag);
        }
    }

    // 媒体文件扫描完成通知
    private void notifyScannerDone(int usbFlag) {
        if (null != this.mOnMediaScannerListener) {
            this.mOnMediaScannerListener.onScannerDone(usbFlag);
        }
    }

    // U盘路径转换U盘标识
    private int conversionUsbPathToUsbFlag(String usbPath) {
        if (usbPath.endsWith(Definition.PATH_USB1)) {
            return Definition.FLAG_USB1;
        } else if (usbPath.endsWith(Definition.PATH_USB2)) {
            return Definition.FLAG_USB2;
        }
        return -1;
    }

    @Override
    public void onUSBMounted(String usbPath) {
        Logutil.d(TAG, "onUSBMounted() ...");
        removeScanFirstMediaUrl(usbPath);
        startEngineScannerRunnable(usbPath);
    }

    @Override
    public void onUSBUnMounted(String usbPath) {
        Logutil.d(TAG, "onUSBUnMounted() ...");
        removeScanFirstMediaUrl(usbPath);
        stopEngineScannerRunnable(usbPath);
    }

    @Override
    public int getScannerStatus() {
        return this.mUSBScannerStatus;
    }

    @Override
    public String getScanFirstMusicUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                return this.mScanFirstMusicUrlOfUsb1;
            case Definition.FLAG_USB2:
                return this.mScanFirstMusicUrlOfUsb2;
        }
        return null;
    }

    @Override
    public String getScanFirstVideoUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                return this.mScanFirstVideoUrlOfUsb1;
            case Definition.FLAG_USB2:
                return this.mScanFirstVideoUrlOfUsb2;
        }
        return null;
    }

    @Override
    public String getScanFirstPhotoUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                return this.mScanFirstPhotoUrlOfUsb1;
            case Definition.FLAG_USB2:
                return this.mScanFirstPhotoUrlOfUsb2;
        }
        return null;
    }

    @Override
    public boolean isMediaScanFinished(int usbFlag) {
        if (usbFlag == Definition.FLAG_USB1) {
            if (null != mEngineScannerRunnableByUsb1) {
                return (mEngineScannerRunnableByUsb1.getMediaScanState() == MEDIA_SCAN_STATE_DONE);
            }
        } else if (usbFlag == Definition.FLAG_USB2) {
            if (null != mEngineScannerRunnableByUsb2) {
                return (mEngineScannerRunnableByUsb2.getMediaScanState() == MEDIA_SCAN_STATE_DONE);
            }
        }
        return false;
    }

    /**
     * 启动媒体文件扫描线程
     *
     * @param usbPath
     */
    private void startEngineScannerRunnable(String usbPath) {
        if (null == usbPath) {
            return;
        }
        Logutil.d(TAG, "startEngineScannerRunnable() usbPath=" + usbPath);
        switch (conversionUsbPathToUsbFlag(usbPath)) {
            case Definition.FLAG_USB1:
                usb1ScanIsEnd = false;
                if (null == mEngineScannerRunnableByUsb1) {
                    this.mEngineScannerRunnableByUsb1 = new EngineScannerRunnable(this);
                }
                if (this.mEngineScannerRunnableByUsb1.isRunning()) {
                    this.mEngineScannerRunnableByUsb1.stop();
                    this.mEngineScannerRunnableByUsb1 = new EngineScannerRunnable(this);
                }
                this.mEngineScannerRunnableByUsb1.prepare();
                this.mEngineScannerRunnableByUsb1.setScanPath(usbPath);
                this.mBackgroundHandler.post(mEngineScannerRunnableByUsb1);
                break;
            case Definition.FLAG_USB2:
                usb2ScanIsEnd = false;
                if (null == mEngineScannerRunnableByUsb2) {
                    this.mEngineScannerRunnableByUsb2 = new EngineScannerRunnable(this);
                } else if (this.mEngineScannerRunnableByUsb2.isRunning()) {
                    this.mEngineScannerRunnableByUsb2.stop();
                    this.mEngineScannerRunnableByUsb2 = new EngineScannerRunnable(this);
                }
                this.mEngineScannerRunnableByUsb2.prepare();
                this.mEngineScannerRunnableByUsb2.setScanPath(usbPath);
                this.mBackgroundHandler.post(mEngineScannerRunnableByUsb2);
                break;
        }
    }

    /**
     * 停止媒体文件扫描线程
     *
     * @param usbPath
     */
    private void stopEngineScannerRunnable(String usbPath) {
        Logutil.d(TAG, "stopEngineScannerRunnable() usbPath=" + usbPath);
        switch (conversionUsbPathToUsbFlag(usbPath)) {
            case Definition.FLAG_USB1:
                if (null != mEngineScannerRunnableByUsb1) {
                    mEngineScannerRunnableByUsb1.stop();
                    this.mBackgroundHandler.removeCallbacks(mEngineScannerRunnableByUsb1);
                }
            case Definition.FLAG_USB2:
                if (null != mEngineScannerRunnableByUsb2) {
                    mEngineScannerRunnableByUsb2.stop();
                    this.mBackgroundHandler.removeCallbacks(mEngineScannerRunnableByUsb2);
                }
                break;
        }
    }

    // 多媒体扫描线程
    private static final class EngineScannerRunnable implements Runnable {

        private IMediaDBManager mMediaDBManagerRfr;// 媒体数据库管理类
        private IMediaParser mMediaParserRfr;// 媒体解析类
        private MediaScanner mMediaScannerRfr;// 媒体扫描类
        private FuncFileTransverseScan mFuncFileRecursion;// 递归功能类
        private FuncFileTransverseScan.OnFileRecursionListener mOnFileRecursionListener;// 递归扫描媒体状态监听接口
        protected IMediaDBManager.OnBatchDataInsertListener mOnBatchDataInsertListener;// 媒体数据批量插入状态监听接口
        private String mPath;
        private int mUsbFlag = -1;
        private int mMediaScanState = MEDIA_SCAN_STATE_INVALID;// 媒体扫描状态

        EngineScannerRunnable(MediaScanner engineScanner) {
            this.mMediaDBManagerRfr = MediaDBManager.getMediaDBManager();
            this.mMediaParserRfr = MediaParser.getInstance();
            this.mMediaScannerRfr = engineScanner;
            this.mFuncFileRecursion = new FuncFileTransverseScan();
        }

        public EngineScannerRunnable prepare() {
            if (null != mFuncFileRecursion) {
                mFuncFileRecursion.prepare();
            }
            return this;
        }

        public EngineScannerRunnable setScanPath(String path) {
            this.mPath = path;
            if (null != this.mMediaScannerRfr) {
                this.mUsbFlag = this.mMediaScannerRfr.conversionUsbPathToUsbFlag(this.mPath);
            }
            return this;
        }

        public boolean isRunning() {
            if (null != mFuncFileRecursion) {
                return mFuncFileRecursion.isRunning(mUsbFlag);
            }
            return false;
        }

        public void stop() {
            if (null != mFuncFileRecursion) {
                mFuncFileRecursion.stop();
            }
        }

        public int getMediaScanState() {
            return this.mMediaScanState;
        }

        @Override
        public void run() {
            Logutil.i(TAG, "EngineScannerRunnable() Running !!!");
            if (null == mMediaScannerRfr) {
                Logutil.e(TAG, "mEngineScannerRfr==NULL");
                return;
            }

            // 批量插入媒体数据监听接口实现
            mOnBatchDataInsertListener = new IMediaDBManager.OnBatchDataInsertListener() {
                @Override
                public void onNotifyDataChanage() {
                    mMediaScannerRfr.notifyScanning(mUsbFlag);// 媒体文件正在扫描通知
                }
            };

            // 递归扫描媒体状态监听接口实现
            mOnFileRecursionListener = new FuncFileTransverseScan.OnFileRecursionListener() {
                @Override
                public void onScanFileStart() {
                    Logutil.i(TAG, "onScanFileStart() ...");
                    mMediaScannerRfr.mUSBScannerStatus = MEDIA_SCAN_STATE_SCANNING;
                    mMediaDBManagerRfr.prepareBatchInsert();// 准备批量插入数据操作
                    mMediaScannerRfr.notifyScannerStart(mUsbFlag);// 媒体扫描开始通知
                    insertLastMediaInfoToDB(mPath);// 断点记忆媒体信息优先插入数据库
                }

                @Override
                public void onScanResult(ConstantsMediaSuffix.MediaSuffixType suffixType, String uri) {
                    mMediaScannerRfr.mUSBScannerStatus = MEDIA_SCAN_STATE_SCANNING;
                    batchInsertDataToDB(suffixType, uri);
                    recordScanFirstMediaUrl(suffixType, uri);
                }

                @Override
                public void onScanFileDone() {
                    Logutil.i(TAG, "onScanFileDone() ...");
                    mMediaScannerRfr.mUSBScannerStatus = MEDIA_SCAN_STATE_DONE;
                    // 批量插入最后媒体数据
                    mMediaDBManagerRfr.insertLastBatchDataToDB(mOnBatchDataInsertListener);
                    mMediaScannerRfr.notifyScannerDone(mUsbFlag);// 媒体文件扫描完成通知
                    Logutil.i(TAG, "onScanFileDone() ..." + " mUsbFlag: " + mUsbFlag);
                    if (mUsbFlag == Definition.FLAG_USB1) {
                        mMediaScannerRfr.usb1ScanIsEnd = true;
                    } else if (mUsbFlag == Definition.FLAG_USB2) {
                        mMediaScannerRfr.usb2ScanIsEnd = true;
                    }
                }

                @Override
                public void onScanFileStopping() {
                    Logutil.i(TAG, "onScanFileStopping() ...");
                    mMediaScannerRfr.mUSBScannerStatus = MEDIA_SCAN_STATE_STOPED;
                    mMediaDBManagerRfr.stopBatchInsert();// 停止批量插入数据操作
                    mMediaScannerRfr.notifyScannerStoped(mUsbFlag);// 媒体文件扫描停止通知
                }

                @Override
                public void onScanFileStoped() {
                    Logutil.i(TAG, "onScanFileStoped() ...");
                    mMediaScannerRfr.mUSBScannerStatus = MEDIA_SCAN_STATE_STOPED;
                    mMediaDBManagerRfr.stopBatchInsert();// 停止批量插入数据操作
                    mMediaScannerRfr.notifyScannerStoped(mUsbFlag);// 媒体文件扫描停止通知
                }

            };
            mFuncFileRecursion.scanFileOfUsb(mOnFileRecursionListener, mPath);
        }

        // 记录首次扫描到的媒体URL
        private void recordScanFirstMediaUrl(ConstantsMediaSuffix.MediaSuffixType type, String url) {

            switch (type) {
                case SUFFIX_TYPE_AUDIO:
                    if (Definition.FLAG_USB1 == mUsbFlag) {
                        if (null == mMediaScannerRfr.mScanFirstMusicUrlOfUsb1) {
                            mMediaScannerRfr.mScanFirstMusicUrlOfUsb1 = url;
                            PreferencesManager.saveScanFirstMusicUrl(mUsbFlag, url);
                        }
                    } else if (Definition.FLAG_USB2 == mUsbFlag) {
                        if (null == mMediaScannerRfr.mScanFirstMusicUrlOfUsb2) {
                            mMediaScannerRfr.mScanFirstMusicUrlOfUsb2 = url;
                            PreferencesManager.saveScanFirstMusicUrl(mUsbFlag, url);
                        }
                    }
                    break;
                case SUFFIX_TYPE_VIDEO:
                    if (Definition.FLAG_USB1 == mUsbFlag) {
                        if (null == mMediaScannerRfr.mScanFirstVideoUrlOfUsb1) {
                            mMediaScannerRfr.mScanFirstVideoUrlOfUsb1 = url;
                            PreferencesManager.saveScanFirstVideoUrl(mUsbFlag, url);
                        }
                    } else if (Definition.FLAG_USB2 == mUsbFlag) {
                        if (null == mMediaScannerRfr.mScanFirstVideoUrlOfUsb2) {
                            mMediaScannerRfr.mScanFirstVideoUrlOfUsb2 = url;
                            PreferencesManager.saveScanFirstVideoUrl(mUsbFlag, url);
                        }
                    }
                    break;
                case SUFFIX_TYPE_PHOTO:
                    if (Definition.FLAG_USB1 == mUsbFlag) {
                        if (null == mMediaScannerRfr.mScanFirstPhotoUrlOfUsb1) {
                            mMediaScannerRfr.mScanFirstPhotoUrlOfUsb1 = url;
                            PreferencesManager.saveScanFirstPhotoUrl(mUsbFlag, url);
                        }
                    } else if (Definition.FLAG_USB2 == mUsbFlag) {
                        if (null == mMediaScannerRfr.mScanFirstPhotoUrlOfUsb2) {
                            mMediaScannerRfr.mScanFirstPhotoUrlOfUsb2 = url;
                            PreferencesManager.saveScanFirstPhotoUrl(mUsbFlag, url);
                        }
                    }
                    break;
            }
        }

        // 插入断点记忆媒体信息到数据库
        private void insertLastMediaInfoToDB(String curUsbPath) {
            Logutil.i(TAG, "insertLastMediaInfoToDB() ...");
            String lastMusicUrl = PreferencesManager.getLastMusicUrl(AppUtil.conversionUrlToUsbFlag(curUsbPath));
            String lastVideoUrl = PreferencesManager.getLastVideoUrl(AppUtil.conversionUrlToUsbFlag(curUsbPath));
            String lastPhotoUrl = PreferencesManager.getLastPhotoUrl(AppUtil.conversionUrlToUsbFlag(curUsbPath));

            if (canInsertWithLastUrl(curUsbPath, lastMusicUrl)) {
                Logutil.i(TAG, "==================MUSIC URL INSERT=========================");
                batchInsertDataToDB(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_AUDIO, lastMusicUrl);
            }
            if (canInsertWithLastUrl(curUsbPath, lastVideoUrl)) {
                if (AppUtil.isIgonreScanFileSuffix(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_VIDEO, lastVideoUrl)) {
                    PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_MUSIC);
                } else {
                    Logutil.i(TAG, "==================VIDEO URL INSERT=========================");
                    batchInsertDataToDB(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_VIDEO, lastVideoUrl);
                }

            }
            if (canInsertWithLastUrl(curUsbPath, lastPhotoUrl)) {
                Logutil.i(TAG, "==================PHOTO URL INSERT=========================");
                batchInsertDataToDB(ConstantsMediaSuffix.MediaSuffixType.SUFFIX_TYPE_PHOTO, lastPhotoUrl);
            }
        }

        // 断点记资源是否可以插入到数据库
        private boolean canInsertWithLastUrl(String curUsbPath, String url) {
            boolean isInsert = false;
            boolean isMatchUsbPath = false;
            boolean isExistsLastUrl = (null != url && new File(url).exists());

            if (isExistsLastUrl) {
                isMatchUsbPath = url.startsWith(curUsbPath);
                isInsert = isMatchUsbPath ? true : false;
            }
            Logutil.i(TAG, "================");
            Logutil.i(TAG, "canInsertWithLastUrl() curUsbPath=" + curUsbPath);
            Logutil.i(TAG, "canInsertWithLastUrl() url=" + url);
            Logutil.i(TAG, "canInsertWithLastUrl() isMatchUsbPath=" + isMatchUsbPath);
            Logutil.i(TAG, "canInsertWithLastUrl() isExistsLastUrl=" + isExistsLastUrl);
            Logutil.i(TAG, "canInsertWithLastUrl() isInsert=" + isInsert);
            Logutil.i(TAG, "================");
            return isInsert;
        }

        // 批量插入数据
        private void batchInsertDataToDB(ConstantsMediaSuffix.MediaSuffixType suffixType, String uri) {
            ContentValues values = null;
            switch (suffixType) {
                case SUFFIX_TYPE_AUDIO:
                    values = mMediaParserRfr.getContentValuesByMusic(mUsbFlag, uri);
                    break;
                case SUFFIX_TYPE_VIDEO:
                    values = mMediaParserRfr.getContentValuesByVideo(mUsbFlag, uri);
                    break;
                case SUFFIX_TYPE_PHOTO:
                    values = mMediaParserRfr.getContentValuesByPhoto(mUsbFlag, uri);
                    break;
                case SUFFIX_TYPE_LRC:
                    values = mMediaParserRfr.getContentValuesByLyric(mUsbFlag, uri);
                    break;
            }
            if (null != values) {
                // 批量插入媒体数据
                mMediaDBManagerRfr.insertBatchDataToDB(values, mOnBatchDataInsertListener);
            }
        }

    }// end >>>> EngineScannerRunnable inner class

    // 初始化后线程
    private void initMediaScannerThread() {
        Logutil.i(TAG, "===============initMediaScannerThread()===================");
        if (null == mMediaScannerThread) {
            mMediaScannerThread = new MediaScannerThread("MediaScannerThread");
            mMediaScannerThread.start();
        }
        if (null == mBackgroundHandler) {
            mBackgroundHandler = new Handler(mMediaScannerThread.getLooper(), mMediaScannerThread);
        }
    }

    // 后台队列线程
    private class MediaScannerThread extends HandlerThread implements Handler.Callback {
        MediaScannerThread(String name) {
            super(name);
        }

        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    }

    // 移除掃描到首個媒體資源
    private void removeScanFirstMediaUrl(String usbPath) {
        Logutil.i(TAG, "removeScanFirstMediaUrl() ..." + usbPath);
        PreferencesManager.saveScanFirstMusicUrl(AppUtil.conversionUsbPathToUsbFlag(usbPath), null);
        PreferencesManager.saveScanFirstVideoUrl(AppUtil.conversionUsbPathToUsbFlag(usbPath), null);
        PreferencesManager.saveScanFirstPhotoUrl(AppUtil.conversionUsbPathToUsbFlag(usbPath), null);
        if (AppUtil.conversionUsbPathToUsbFlag(usbPath) == Definition.FLAG_USB1) {
            mScanFirstMusicUrlOfUsb1 = null;
            mScanFirstVideoUrlOfUsb1 = null;
            mScanFirstPhotoUrlOfUsb1 = null;
        } else if (AppUtil.conversionUsbPathToUsbFlag(usbPath) == Definition.FLAG_USB2) {
            mScanFirstMusicUrlOfUsb2 = null;
            mScanFirstVideoUrlOfUsb2 = null;
            mScanFirstPhotoUrlOfUsb2 = null;
        }
    }


}
