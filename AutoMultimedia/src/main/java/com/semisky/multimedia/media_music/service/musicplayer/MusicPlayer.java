package com.semisky.multimedia.media_music.service.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.Looper;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_view.ToastCustomHint;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.model.IMusicParserModel;
import com.semisky.multimedia.media_music.model.MusicAudioFocusModel;
import com.semisky.multimedia.media_music.model.MusicParserModel;
import com.semisky.multimedia.media_music.service.manger.MusicPlayManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 音乐播放器
 * Created by LiuYong on 2018/8/22.
 */

public class MusicPlayer implements IMusicPlayer<MusicInfo>,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = Logutil.makeTagLog(MusicPlayer.class);
    private static MusicPlayer _INSTANCE;
    private static final int CHANGE_PREV_PROGRAM_TIME_LIMIT_MAX_10S = 10999;

    private Handler _handler;
    private UpdateProgressRunnable mUpdateProgressRunnable;// 播放进度更新轮询线程
    private LastMediaInfoSaveRunnable mLastMediaInfoSaveRunnable;// 媒体断点信息保存轮询线程
    private FastForwardRunnable mFastForwardRunnable;// 快进线程
    private FastBackwardRunnable mFastBackwardRunnable;// 快退线程
    private ProgramNextRunnable mProgramNextRunnable;// 下个节目线程
    private ProgramPrevRunnable mProgramPrevRunnable;// 上个节目线程

    private MediaPlayer mPlayer;// 媒体播放器
    private MusicPlayList mMusicPlayList;// 媒体播放清单
    private IMusicParserModel mMusicParserModel;

    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnErrorListener mOnErrorListener;
    private OnProgressChangeListener mOnProgressChangeListener;
    private OnMediaInfoChangerListener<MusicInfo> mOnMediaInfoChangerListener;
    private OnMediaPlayStateListener mOnMediaPlayStateListener;
    private OnPlayProgramChangeListener mOnPlayProgramChangeListener;

    private boolean mIsPrepared;// 媒体是否准备完成
    private int mSeekWhenPrepared;// 媒体准备完成设置进度
    private boolean mStartWhenPrepared;// 媒体准备完成可播放标识
    private boolean mIsAutoPlay = true;
    private String mNumOfCurrentAndTotalProgram;// 当前歌曲序号/歌曲总数
    private boolean isCanPrevious = false ; // 是否可以上一曲 （只有在播放异常是，此标记位才有效）
    private boolean isHasAudioFocus = true ;//是否有音频焦点
    private boolean isAllowPlaying = true ;//是否允许播放

    private int mCurUsbSourceFlag = -1;// 当前USB源标识


    private MusicPlayer() {
        initMediaPlayer();
        initMusicPlayList();
        initHandler();
        initMusicParserModel();

    }

    public static MusicPlayer getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new MusicPlayer();
        }
        return _INSTANCE;
    }

    // register listener


    @Override
    public void setmOnSeekCompleteListener(OnSeekCompleteListener l) {
        this.mOnSeekCompleteListener = l;
    }

    @Override
    public void setOnPlayProgramChangeListener(OnPlayProgramChangeListener l) {
        this.mOnPlayProgramChangeListener = l;
    }

    @Override
    public void setOnMediaInfoChangerListener(OnMediaInfoChangerListener l) {
        this.mOnMediaInfoChangerListener = l;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    @Override
    public void setOnErrorListener(OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    @Override
    public void setOnProgressChangeListener(OnProgressChangeListener l) {
        this.mOnProgressChangeListener = l;
    }

    @Override
    public void setOnMediaPlayStateListener(OnMediaPlayStateListener l) {
        this.mOnMediaPlayStateListener = l;
    }

    // 媒体播放状态改变通知
    private void notifyMediaPlayStateChange(boolean isPlaying) {
        if (null != mOnMediaPlayStateListener) {
            mOnMediaPlayStateListener.onChangePlayState(isPlaying);
        }
    }

    // IMusicPlayer

    @Override
    public void setPlayList(List<MusicInfo> playList) {
        int size = (null != playList ? playList.size() : 0);
        boolean isEquals = (size == mMusicPlayList.getSize() ? true : false);
        if (!isEquals) {
            this.mMusicPlayList.addMusicInfos(playList);
            onRefreshCurPlayingUrlPos();
        }
    }

    @Override
    public boolean hasData() {
        return mMusicPlayList.hasData();
    }

    @Override
    public boolean isPrepared() {
        return mIsPrepared;
    }

    @Override
    public IMusicPlayer seekTo(int msec) {
        Logutil.d(TAG, "===================");
        Logutil.d(TAG, "seekTo() msec [ " + msec + " ]");
        Logutil.d(TAG, "seekTo() mIsPrepared [ " + mIsPrepared + " ]");
        Logutil.d(TAG, "seekTo() isInitMediaPlayer [ " + isInitMediaPlayer() + " ]");
        Logutil.d(TAG, "===================");

        if (isInitMediaPlayer() && mIsPrepared) {
            this.mPlayer.seekTo(msec);
        } else {
            this.mSeekWhenPrepared = msec;
        }
        return this;
    }

    @Override
    public IMusicPlayer setAutoPlay(boolean isAutoPlay) {
        this.mIsAutoPlay = isAutoPlay;
        return this;
    }

    @Override
    public IMusicPlayer setMusicPath(String url) {
        this.mSeekWhenPrepared = 0;
        mMusicPlayList.setmCurrentPlayingUrl(url);
        onRefreshCurPlayingUrlPos();
        //        mMusicPlayList.refreshCurPlayUrlPos();
        return this;
    }

    @Override
    public IMusicPlayer onPreparePlay() {
        onPreparePlay(mMusicPlayList.getmCurrentPlayingUrl());
        return this;
    }


    @Override
    public MusicInfo getCurrentID3Info() {
        if (null != mMusicPlayList) {
            return mMusicPlayList.getmCurrentID3Info();
        }
        return null;
    }

    @Override
    public int getDuration() {
        if (isInitMediaPlayer() && this.mIsPrepared) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentProgress() {
        if (isInitMediaPlayer() && this.mIsPrepared) {

            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    // MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        Logutil.i(TAG, "onPrepared() ...");
        this.isCanPrevious = false ;
        this.mIsPrepared = true;
        if (this.mSeekWhenPrepared != 0) {
            mPlayer.seekTo(mSeekWhenPrepared);
            this.mSeekWhenPrepared = 0;
        }

        mMusicPlayList.setmStopSinglePlay(false);
        Logutil.i(TAG, "================");
        Logutil.i(TAG, "onPrepared() mSeekWhenPrepared = " + mSeekWhenPrepared);
        Logutil.i(TAG, "onPrepared() mStartWhenPrepared = " + mStartWhenPrepared);
        Logutil.i(TAG, "onPrepared() mIsAutoPlay = " + mIsAutoPlay);
        Logutil.i(TAG, "onPrepared() curProgress = " + mp.getCurrentPosition());
        Logutil.i(TAG, "onPrepared() totalProgress = " + mp.getDuration());
        Logutil.i(TAG, "onPrepared() getmCurrentPlayingUrl = " + mMusicPlayList.getmCurrentPlayingUrl());

        Logutil.i(TAG, "================");

        if (null != mOnPreparedListener) {
            this.mOnPreparedListener.onPrepared(mp);
        }

        if (this.mStartWhenPrepared && this.mIsAutoPlay && isAllowPlaying && MusicAudioFocusModel.getInstance().hasAudioFocus()) {
                boolean isPlay = start();
                Logutil.i(TAG, "onPrepared() isPlay = " + isPlay);
                startUpdateProgressRunnable();// 启动媒体进度更新线程
                startLastMediaInfoSaveRunnable();
//            notifyMediaPlayStateChange(isPlay); 已在start中回调此函数
        }
        onRefreshCurPlayingUrlPos();
        filterUsbSourceFlag(mMusicPlayList.getmCurrentPlayingUrl());

    }

    // 过滤当前媒体URL所在USB源下用对应USB标识时行标记
    private void filterUsbSourceFlag(String musicUrl){
        if(null == musicUrl){
            return;
        }
        if(musicUrl.startsWith(Definition.PATH_USB1)){
            this.mCurUsbSourceFlag = Definition.FLAG_USB1;
        }else if(musicUrl.startsWith(Definition.PATH_USB2)){
            this.mCurUsbSourceFlag = Definition.FLAG_USB2;
        }
        PreferencesManager.saveLastMusicSourceUsbFlag(mCurUsbSourceFlag);
    }

    // MediaPlayer.OnCompletionListener
    @Override
    public void onCompletion(MediaPlayer mp) {
        Logutil.i(TAG, "onCompletion() ...");
        if (null != this.mOnCompletionListener) {
            this.mOnCompletionListener.onCompletion(mp);
        }
        stopUpdateProgressRunnable();
        stopLastMediaInfoSaveRunnable();
        //todo
//        if (!USBManager.getInstance().isFirstUsbMounted()) {
//            Logutil.e(TAG, "onCompletion() USB UNMOUNTED !!!");
//            return;
//        }
        if (isPrepared()) {
            next();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Logutil.i(TAG, "onSeekComplete() totalProgress=" + mp.getDuration());
        Logutil.i(TAG, "onSeekComplete() curProgress=" + mp.getCurrentPosition());
        if (null != mOnSeekCompleteListener) {
            this.mOnSeekCompleteListener.onSeekComplete(mp);
        }
    }

    // MediaPlayer.OnErrorListener
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logutil.i(TAG, "onError() ..." + mMusicPlayList.getmCurrentPlayingUrl());
        if (null != mOnErrorListener) {
            this.mOnErrorListener.onError(mp, what, extra);
        }
        mMusicPlayList.setmStopSinglePlay(true); //异常节目，标记位（单曲循环引发的问题）
        isCanPrevious = true ;
        stopUpdateProgressRunnable();
        stopLastMediaInfoSaveRunnable();
        notifyMediaPlayStateChange(false);
        //todo
//        if (!USBManager.getInstance().isFirstUsbMounted()) {
//            Logutil.e(TAG, "onError() USB UNMOUNTED !!!");
//            return false;
//        }
        onRefreshCurPlayingUrlPos();
        // 是否有下个曲目资源
        if (mMusicPlayList.hasNext(MusicPlayList.CHANGE_NEXT_WITH_PLAY_EXCEPTION)) {
            Logutil.i(TAG, "onError() NEXT ...");
            startProgramNextRunnable(ProgramNextRunnable.DELAY_TIME_5S);
        }
        // 添加异常媒体URL
        mMusicPlayList.addDamageUrlToList(mMusicPlayList.getmCurrentPlayingUrl());
        return false;
    }

    @Override
    public boolean isPlaying() {
        boolean isPlaying = false;
        if (isInitMediaPlayer()) {
            isPlaying = mPlayer.isPlaying();
        }
        Logutil.i(TAG, "=====> isPlaying() ..." + isPlaying);
        return isPlaying;
    }

    @Override
    public void prev() {
        //当播放不能播放的音频时，上一曲，下一曲需要清除 “损坏文件的标志”
        ToastCustomHint.closeDialog();
        // 上一曲
        if (canPrev()) {
            startProgramPrevRunnable(ProgramPrevRunnable.DELAY_TIME_350MS);
        } else {
            // 播放异常情况下可以上一曲
            if (isCanPrevious){
                stopProgramNextRunnable();
                startProgramPrevRunnable(ProgramPrevRunnable.DELAY_TIME_350MS);
            }else {
                //重播
                rePlay();
            }

        }
    }

    // 是否可以上一曲
    private boolean canPrev() {
        if (mIsPrepared && getCurrentProgress() < CHANGE_PREV_PROGRAM_TIME_LIMIT_MAX_10S) {
            return true;
        }
        return false;
    }

    // 重新开始播放
    private void rePlay() {
        if (mIsPrepared) {
            seekTo(0);
            //处于暂停状态没有播放，恢复播放
            if (!isPlaying()){
                boolean state = start();
//                if (state) {
//                    startUpdateProgressRunnable();
//                    notifyMediaPlayStateChange(true); 已在start中调用此函数
//                }
            }

        }
    }

    // 启动上个节目线程
    private void startProgramPrevRunnable(int delayMillis) {
        if (null == mProgramPrevRunnable) {
            this.mProgramPrevRunnable = new ProgramPrevRunnable(this);
        }
        _handler.removeCallbacks(mProgramPrevRunnable);
        _handler.postDelayed(mProgramPrevRunnable, delayMillis);
    }

    // 停止上个节目线程
    private void stopProgramPrevRunnable() {
        if (null != mProgramPrevRunnable) {
            Logutil.i(TAG, "stopProgramPrevRunnable() ...");
            _handler.removeCallbacks(mProgramPrevRunnable);
        }
    }

    // 上个节目线程
    private static class ProgramPrevRunnable implements Runnable {
        public static final int DELAY_TIME_350MS = 350;
        public static final int DELAY_TIME_5S = 5000;
        private WeakReference<MusicPlayer> mPlayerRfr;
        private boolean mIsRunning = false;

        public boolean isRunning() {
            return this.mIsRunning;
        }

        public ProgramPrevRunnable(MusicPlayer player) {
            this.mPlayerRfr = new WeakReference<MusicPlayer>(player);
        }

        @Override
        public void run() {

            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                Logutil.e(TAG, "ProgramPrevRunnable() WeakReference is Empty !!!");
                return;
            }
            this.mIsRunning = true;
            mPlayerRfr.get().programPrev();
            this.mIsRunning = false;
        }
    }

    private void programPrev() {
        if (isInitMediaPlayer() && mMusicPlayList.prepare()) {
            this.mIsAutoPlay = true;
            if (isFastBackwardRunnableRunning()) {
                stopFastBackward(false);
            }
            if (isFastForwardRunnableRunning()) {
                stopFastForwardRunnable(false);
            }
            stopUpdateProgressRunnable();
            stopLastMediaInfoSaveRunnable();
            MusicInfo prev = mMusicPlayList.prev();
            onPreparePlay(prev.getUrl());
        }
    }

    @Override
    public void stopPrev() {
        stopProgramPrevRunnable();
    }

    @Override
    public void next() {
        //当播放不能播放的音频时，上一曲，下一曲需要清除 “损坏文件的标志”
        ToastCustomHint.closeDialog();
        startProgramNextRunnable(ProgramNextRunnable.DELAY_TIME_350MS);
    }


    // 启动下个节目线程
    private void startProgramNextRunnable(int delayMillis) {
        if (null == mProgramNextRunnable) {
            this.mProgramNextRunnable = new ProgramNextRunnable(this);
        }
        _handler.removeCallbacks(mProgramNextRunnable);
        _handler.postDelayed(mProgramNextRunnable, delayMillis);
    }

    // 停止下个节目线程
    private void stopProgramNextRunnable() {
        if (null != mProgramNextRunnable) {
            Logutil.i(TAG, "stopProgramNextRunnable() ...");
            _handler.removeCallbacks(mProgramNextRunnable);
        }
    }

    // 下个节目线程
    private static class ProgramNextRunnable implements Runnable {
        public static final int DELAY_TIME_350MS = 350;
        public static final int DELAY_TIME_5S = 5000;
        private WeakReference<MusicPlayer> mPlayerRfr;
        private boolean mIsRunning = false;

        public boolean isRunning() {
            return this.mIsRunning;
        }

        public ProgramNextRunnable(MusicPlayer player) {
            this.mPlayerRfr = new WeakReference<MusicPlayer>(player);
        }

        @Override
        public void run() {

            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                Logutil.e(TAG, "ProgramNextRunnable() WeakReference is Empty !!!");
                return;
            }
            this.mIsRunning = true;
            mPlayerRfr.get().programNext();
            this.mIsRunning = false;
        }
    }

    // 切换下个节目
    private void programNext() {
        if (isInitMediaPlayer() && mMusicPlayList.prepare()) {
            this.mIsAutoPlay = true;
            stopUpdateProgressRunnable();
            stopLastMediaInfoSaveRunnable();
            MusicInfo next = mMusicPlayList.next();
            onPreparePlay(next.getUrl());
            if (isFastBackwardRunnableRunning()) {
                stopFastBackward(false);
            }
            if (isFastForwardRunnableRunning()) {
                stopFastForwardRunnable(false);
            }
        }
    }

    @Override
    public void stopNext() {
        stopProgramNextRunnable();
    }

    @Override
    public void fastBackward() {
        if (isPrepared()) {
            if (isFastBackwardRunnableRunning()) {
                Logutil.w(TAG, "------------FastBackwardRunnable Running-------------------");
                return;
            }
            if (isFastForwardRunnableRunning()) {
                Logutil.w(TAG, "------------FastForwardRunnable Running-------------------");
                return;
            }
            startFastBackwardRunnable();
        }
    }

    @Override
    public void stopFastBackward(boolean fromAudioFocusLossEvent) {
        if (isPrepared()) {
            if (isFastForwardRunnableRunning()) {
                Logutil.w(TAG, "------------FastForwardRunnable Running-------------------");
                return;
            }
            stopFastBackwardRunnable(fromAudioFocusLossEvent);
        }
    }

    @Override
    public void fastForward() {
        if (isPrepared()) {
            if (isFastBackwardRunnableRunning()) {
                Logutil.w(TAG, "------------FastBackwardRunnable Running-------------------");
                return;
            }
            if (isFastForwardRunnableRunning()) {
                Logutil.w(TAG, "------------FastForwardRunnable Running-------------------");
                return;
            }
            startFastForwardRunnable();
        }
    }

    @Override
    public void stopFastForward(boolean fromAudioFocusLossEvent) {
        if (isPrepared()) {
            if (isFastBackwardRunnableRunning()) {
                Logutil.w(TAG, "------------FastBackwardRunnable Running-------------------");
                return;
            }
            stopFastForwardRunnable(fromAudioFocusLossEvent);
        }
    }

    @Override
    public void playOrPause() {
        if (isPrepared()) {
            if (isPlaying()) {
                boolean state = pause();
                if (state) {
//                    stopUpdateProgressRunnable();
//                    stopLastMediaInfoSaveRunnable(); 已在start()中调用函数
                    MusicPlayManager.getInstance().setIsClickStopMusicPlay(true);

                }
            } else {
                boolean state = start();
//                if (state) {
//                    startUpdateProgressRunnable(); 已在start()中调用函数
//                }
            }
        }
    }

    @Override
    public void saveLastMediaInfo() {
        try {
            if (isInitMediaPlayer()) {
                int curProgress = mPlayer.getCurrentPosition();
                PreferencesManager.saveLastMusicProgress(curProgress);
            }
        } catch (Exception e) {
            Logutil.e(TAG, "===========>saveLastMediaInfo FAIL !!! ," + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Logutil.i(TAG, "stop() ...");
        stopUpdateProgressRunnable();
        stopLastMediaInfoSaveRunnable();
        mPlayer.stop();
        this.mIsPrepared = false;
    }

    // 媒体播放
    @Override
    public boolean start() {
        //在播放的时候设置当前状态到中间件
    //    SemiskyIVIManager.getInstance().setCurrentAppStatus(AutoConstants.AppType.MEDIA_MUSIC,AutoConstants.CurrentAppStatus.STATE_PLAYING);
        if (isInitMediaPlayer() && mIsPrepared) {
            if (!mPlayer.isPlaying()) {
                this.mIsAutoPlay = true;
                this.mPlayer.setVolume(1.0f, 1.0f);
                this.mPlayer.start();
                startUpdate();
                Logutil.i(TAG, "start() ...");
                return true;
            }
            return mPlayer.isPlaying();
        }
        return false;
    }

    // 媒体暂停
    @Override
    public boolean pause() {
        if (isInitMediaPlayer() && mIsPrepared) {
            if (mPlayer.isPlaying()) {
                this.mIsAutoPlay = false;
                mPlayer.pause();
                stopUpdate();
                Logutil.i(TAG, "pause() ...");
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPlayMode(int playMode) {
        mMusicPlayList.setmPlayMode(playMode);
    }

    @Override
    public String getCurrentPlayingUrl() {
        return mMusicPlayList.getmCurrentPlayingUrl();
    }

    @Override
    public String getmNumOfCurrentAndTotalProgram() {
        return (null != this.mNumOfCurrentAndTotalProgram ? mNumOfCurrentAndTotalProgram : "0/0");
    }

    @Override
    public void onUpdateProgressWithThreadEnabled(boolean enabled) {
        Logutil.i(TAG, "onUpdateProgressWithThreadEnabled() enabled=" + enabled + ",mIsPrepared=" + mIsPrepared);
        if (mIsPrepared) {
            if (enabled) {
                startUpdateProgressRunnable();
                return;
            }
            stopUpdateProgressRunnable();
        }
    }

    @Override
    public void onMuteVolumeEnable(boolean enable) {
        if (isPrepared()) {
            float volume = enable ? 0.0f : 1.0f;
            mPlayer.setVolume(volume, volume);
            Logutil.i(TAG, "onMuteVolumeEnable() volume =" + volume);
        }
    }

    @Override
    public void setFavoriteStatusWithCurrentMusic(boolean isFavorite) {
        mMusicPlayList.setFavoriteStatusWithCurrentMusic(isFavorite);
    }

    @Override
    public boolean isFavoriteWithCurrentMusic() {
        return mMusicPlayList.isFavoriteWithCurrentMusic();
    }

    // utils

    // 启动快进线程
    private void startFastForwardRunnable() {
        if (null == mFastForwardRunnable) {
            this.mFastForwardRunnable = new FastForwardRunnable(this);
        }

        if (!mFastForwardRunnable.isRunning()) {
            this.stopUpdateProgressRunnable();
            onMuteVolumeEnable(true);
            this.mFastForwardRunnable.onPrepare();
            this._handler.post(mFastForwardRunnable);
            return;
        }
        Logutil.i(TAG, "******** FastForwardRunnable Running ********");
    }

    // 停止快进线程
    private void stopFastForwardRunnable(boolean fromAudioFocusLossEvent) {
        if (null != mFastForwardRunnable && mFastForwardRunnable.isRunning()) {
            this.mFastForwardRunnable.onStop(fromAudioFocusLossEvent);
            this._handler.removeCallbacks(mFastForwardRunnable);
        }
    }

    // 是否快进线程在运行
    private boolean isFastForwardRunnableRunning() {
        return (null != mFastForwardRunnable && mFastForwardRunnable.isRunning());
    }

    // 快进线程
    private static class FastForwardRunnable implements Runnable {
        private WeakReference<MusicPlayer> mPlayerRfr;
        // 是否停止快进 true:停止 ，false:不停止
        private volatile boolean mIsStopFastForward = true;
        private volatile boolean mIsRunning = false;
        private volatile boolean mFromAudioFocsLossEvent = false;
        // 单次10s快进
        private static final int STEP_UNIT_10S = 10000;// 10s=10,000ms
        private static final int DELAY_TIME_1S = 1000;


        public FastForwardRunnable(MusicPlayer player) {
            this.mPlayerRfr = new WeakReference<MusicPlayer>(player);
        }

        public void onPrepare() {
            this.mIsStopFastForward = false;
        }

        public boolean isRunning() {
            return this.mIsRunning;
        }

        public void onStop(boolean fromAudioFocsLossEvent) {
            this.mIsRunning = false;
            this.mIsStopFastForward = true;
            this.mFromAudioFocsLossEvent = fromAudioFocsLossEvent;
            checkIsRestorePlay();
        }
        public void stopRunning(){
            this.mIsRunning = false;
            this.mIsStopFastForward = true;
        }

        @Override
        public void run() {
            this.mIsRunning = true;
            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                this.mIsRunning = false;
                Logutil.w(TAG, "FastForwardRunnable.run() mPlayerRfr == null !!!");
                return;
            }
            // 停止快进
            if (mIsStopFastForward) {
                this.mIsRunning = false;
                Logutil.w(TAG, "FastForwardRunnable.run() STOPED !!!!");
                return;
            }

            if (mPlayerRfr.get().isPrepared()) {
                int totalProgress = mPlayerRfr.get().getDuration();
                int curProgress = mPlayerRfr.get().getCurrentProgress();
                int steppingProgress = curProgress + STEP_UNIT_10S;

                // 步进进度大于总进度时：1.直接设置总进度 2.结束快进操作
                if (steppingProgress >= totalProgress) {
                    steppingProgress = totalProgress;
                    // 禁止线程运行标识
                    this.mIsStopFastForward = true;
                    mPlayerRfr.get().programNext();
                    return;

                }

                mPlayerRfr.get().seekTo(steppingProgress);
                // 1S后再次启动快进线程
                if (!mIsStopFastForward) {
                    mPlayerRfr.get()._handler.postDelayed(this, DELAY_TIME_1S);
                } else {
                    this.mIsRunning = false;// 状态标识线程停止运行
                    checkIsRestorePlay();
                    Logutil.w(TAG, "FastForwardRunnable.run() STOPED !!!!!!");
                }
            }
        }

        // 恢复播放操作
        private void checkIsRestorePlay() {
            Logutil.i(TAG, "restorePlay() mFromAudioFocsLossEvent=" + mFromAudioFocsLossEvent);
            if (mPlayerRfr.get().isPrepared()) {
                if (!mFromAudioFocsLossEvent) {
                    mPlayerRfr.get().start();
                    mPlayerRfr.get().startUpdateProgressRunnable();
                    mPlayerRfr.get().notifyMediaPlayStateChange(mPlayerRfr.get().isPlaying()); //已在start()中调用
                }
                mPlayerRfr.get().onMuteVolumeEnable(false);
                this.mFromAudioFocsLossEvent = false;
            }
        }
    }


    // 启动快退线程
    private void startFastBackwardRunnable() {
        if (null == mFastBackwardRunnable) {
            this.mFastBackwardRunnable = new FastBackwardRunnable(this);
        }
        if (!mFastBackwardRunnable.isRunning()) {
            stopUpdateProgressRunnable();
            onMuteVolumeEnable(true);
            this.mFastBackwardRunnable.onPrepare();
            this._handler.post(mFastBackwardRunnable);
            return;
        }
        Logutil.i(TAG, "******** FastBackwardRunnable Running *********");
    }

    // 停止快退线程
    private void stopFastBackwardRunnable(boolean fromAudioFocusLossEvent) {
        if (null != mFastBackwardRunnable && mFastBackwardRunnable.isRunning()) {
            this.mFastBackwardRunnable.onStop(fromAudioFocusLossEvent);
            this._handler.removeCallbacks(mFastBackwardRunnable);
        }
    }

    // 是否快退线程是运行
    private boolean isFastBackwardRunnableRunning() {
        return (null != mFastBackwardRunnable && mFastBackwardRunnable.isRunning());
    }

    // 快退线程
    private static class FastBackwardRunnable implements Runnable {
        private WeakReference<MusicPlayer> mPlayerRfr;
        // 是否停止快退 true:停止 ，false:不停止
        private volatile boolean mIsStopFastBackward = true;
        private volatile boolean mIsRunning = false;
        private volatile boolean mFromAudioFocsLossEvent = false;
        // 单次10s快退
        private static final int STEP_UNIT_10S = 10000;// 10s=10,000ms
        private static final int DELAY_TIME_1S = 1000;
        private static final int MIN_PROGRESS = 0;


        public FastBackwardRunnable(MusicPlayer player) {
            this.mPlayerRfr = new WeakReference<MusicPlayer>(player);
        }

        public void onPrepare() {
            this.mIsStopFastBackward = false;
        }

        public boolean isRunning() {
            return this.mIsRunning;
        }

        public void onStop(boolean fromAudioFocsLossEvent) {
            this.mIsRunning = false;
            this.mIsStopFastBackward = true;
            this.mFromAudioFocsLossEvent = fromAudioFocsLossEvent;
            checkIsRestorePlay();
        }
        public void stopRunning(){
            this.mIsRunning = false;
            this.mIsStopFastBackward = true;

        }

        @Override
        public void run() {
            this.mIsRunning = true;
            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                Logutil.w(TAG, "FastBackwardRunnable.run() null == mPlayerRfr !!!");
                return;
            }
            // 停止快进
            if (mIsStopFastBackward) {
                this.mIsRunning = false;
                Logutil.w(TAG, "FastBackwardRunnable.run() STOPED !!!");
                return;
            }

            if (mPlayerRfr.get().isPrepared()) {
                int curProgress = mPlayerRfr.get().getCurrentProgress();
                int stepBackProgress = curProgress - STEP_UNIT_10S;

                // 步退进度小于0时：1.直接设置0 2.结束快退操作
                if (stepBackProgress <= MIN_PROGRESS) {
                    stepBackProgress = MIN_PROGRESS;
                    // 禁止线程运行标识
                    this.mIsStopFastBackward = true;
                }

                mPlayerRfr.get().seekTo(stepBackProgress);
                // 1S后再次启动快退线程
                if (!mIsStopFastBackward) {
                    mPlayerRfr.get()._handler.postDelayed(this, DELAY_TIME_1S);
                } else {
                    this.mIsRunning = false;// 状态标识线程停止运行
                    checkIsRestorePlay();
                    Logutil.w(TAG, "FastBackwardRunnable.run() STOPED !!!!!!");
                }
            }
        }

        // 恢复播放操作
        private void checkIsRestorePlay() {
            Logutil.i(TAG, "restorePlay() mFromAudioFocsLossEvent=" + mFromAudioFocsLossEvent);
            if (mPlayerRfr.get().isPrepared()) {
                if (!mFromAudioFocsLossEvent) {
                    mPlayerRfr.get().start();
                    mPlayerRfr.get().startUpdateProgressRunnable();
                    mPlayerRfr.get().notifyMediaPlayStateChange(mPlayerRfr.get().isPlaying());// 已在start()中调用
                }
                mPlayerRfr.get()._handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPlayerRfr.get().onMuteVolumeEnable(false);
                    }
                },500);
                this.mFromAudioFocsLossEvent = false;
            }
        }
    }

    /**
     * 返回当前播放歌曲的下标
     */
    @Override
    public int getPlayIndex(){
         int position = mMusicPlayList.getRefreshCurPlayingUrlPos();
         return position >= 0 ?(position +1 ) : 0;
    }

    // 刷新当前播放资源位置
    private void onRefreshCurPlayingUrlPos() {
        int size = mMusicPlayList.getSize();
        int pos = mMusicPlayList.getRefreshCurPlayingUrlPos();
        pos = pos >= 0 ? (pos + 1) : 0;
        String curPos = (pos + "/" + size);
        this.mNumOfCurrentAndTotalProgram = curPos;
        Logutil.i(TAG, "onRefreshCurPlayingUrlPos() ..." + curPos);
        if (null != mOnPlayProgramChangeListener) {
            mOnPlayProgramChangeListener.onChangePlayProgram(curPos);
        }
    }

    // 初始化媒体播放器
    private void initMediaPlayer() {
        Logutil.i(TAG, "initMediaPlayer() ...");
        if (null != this.mPlayer) {
            this.mPlayer.reset();
            this.mPlayer.release();
            this.mPlayer = null;
        }
        if (null == this.mPlayer) {
            this.mPlayer = new MediaPlayer();
            this.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.mPlayer.setOnCompletionListener(this);
            this.mPlayer.setOnErrorListener(this);
            this.mPlayer.setOnPreparedListener(this);
            this.mPlayer.setOnSeekCompleteListener(this);
        }
    }

    private void initMusicParserModel() {
        this.mMusicParserModel = new MusicParserModel();
    }

    // 初始化Handler
    private void initHandler() {
        this._handler = new Handler(Looper.getMainLooper());
    }

    // 初始化音乐播放清单
    private void initMusicPlayList() {
        this.mMusicPlayList = new MusicPlayList();
    }

    // 是否初始化媒体播放器
    private boolean isInitMediaPlayer() {
        return (null != this.mPlayer);
    }

    // 是否初始化音乐播放列表
    private boolean isInitMusicPlayList() {
        return (null != this.mMusicPlayList);
    }


    // 媒体准备播放
    private void onPreparePlay(String url) {
        Logutil.i(TAG, "onPreparePlay() ..." + url);
        if (!isInitMediaPlayer()) {
            Logutil.e(TAG, "onPreparePlay() MediaPlayer NO INIT !!!");
            return;
        }

        if (null == url || !new File(url).exists()) {
            Logutil.e(TAG, "onPreparePlay() URL NO EXISTS !!!\n" + url);
            return;
        }

        stopUpdateProgressRunnable();// 停止播放进度更新线程
        try {
            this.mStartWhenPrepared = true;
            this.mIsPrepared = false;
            this.mPlayer.reset();
            this.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.mPlayer.setDataSource(url);
            this.mPlayer.prepare();
        } catch (Exception e) {
            Logutil.e(TAG, "onPreparePlay() FAIL !!!,url=" + url);
            onError(mPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_IO);
            e.printStackTrace();
        }

        // 解析媒体信息
        parserMediaInfo(url);
    }


    // 通知播放进度改变
    private void notifyChangerProgress() {
        if (null != mOnProgressChangeListener) {
            mOnProgressChangeListener.onChangeProgress(this.getCurrentProgress());
        }
    }

    // 启动更新播放进度线程
    private void startUpdateProgressRunnable() {
        if (null == mUpdateProgressRunnable) {
            this.mUpdateProgressRunnable = new UpdateProgressRunnable(this);
        }
        boolean isRunning = isUpdateProgressRunnableRunning();
        Logutil.i(TAG, "startUpdateProgressRunnable() ....." + isRunning);
        if (!mUpdateProgressRunnable.isRunning()) {
            mUpdateProgressRunnable.prepare();
            _handler.post(mUpdateProgressRunnable);
        }
    }

    // 停止更新播放进度线程
    private void stopUpdateProgressRunnable() {
        if (null != mUpdateProgressRunnable && mUpdateProgressRunnable.isRunning()) {
            mUpdateProgressRunnable.stop();
            _handler.removeCallbacks(mUpdateProgressRunnable);
        }
    }

    // 是否更新进度线程在运行
    private boolean isUpdateProgressRunnableRunning() {
        return (null != mUpdateProgressRunnable && mUpdateProgressRunnable.isRunning());
    }

    // 更新播放进度线程
    private static class UpdateProgressRunnable implements Runnable {
        WeakReference<MusicPlayer> mRfr;
        private volatile boolean mIsStop = false;
        private volatile boolean mIsRunning = false;

        public UpdateProgressRunnable(MusicPlayer mRfr) {
            this.mRfr = new WeakReference<MusicPlayer>(mRfr);
        }

        public void prepare() {
            mIsStop = false;
        }

        public void stop() {
            mIsStop = true;
            mIsRunning = false;
        }

        public boolean isRunning() {
            return this.mIsRunning;
        }

        @Override
        public void run() {
            this.mIsRunning = true;
            if (null == mRfr || null == mRfr.get()) {
                Logutil.w(TAG, "UpdateProgressRunnable stop run !!!");
                this.mIsRunning = false;
                return;
            }

            if (!mIsStop) {
                mRfr.get()._handler.postDelayed(this, 500);
                mRfr.get().notifyChangerProgress();
            } else {
                Logutil.w(TAG, "UpdateProgressRunnable stop run !!!*!!!");
                this.mIsRunning = false;
            }
        }
    }

    // 启动保存断点媒体信息线程
    private void startLastMediaInfoSaveRunnable() {
        Logutil.i(TAG, "startLastMediaInfoSaveRunnable() ...");
        if (null == mLastMediaInfoSaveRunnable) {
            this.mLastMediaInfoSaveRunnable = new LastMediaInfoSaveRunnable(this);
        }
        if (null != mLastMediaInfoSaveRunnable) {
            _handler.removeCallbacks(mLastMediaInfoSaveRunnable);
            mLastMediaInfoSaveRunnable.prepare();
            _handler.post(mLastMediaInfoSaveRunnable);
        }
    }

    // 停止保存断点媒体信息线程
    private void stopLastMediaInfoSaveRunnable() {
        Logutil.i(TAG, "stopLastMediaInfoSaveRunnable() ...");
        if (null != mLastMediaInfoSaveRunnable) {
            mLastMediaInfoSaveRunnable.stop();
            _handler.removeCallbacks(mLastMediaInfoSaveRunnable);
        }
    }

    // 保存断点媒体信息线程
    private static class LastMediaInfoSaveRunnable implements Runnable {
        WeakReference<MusicPlayer> mRfr;
        private boolean mIsStop = false;

        public LastMediaInfoSaveRunnable(MusicPlayer mRfr) {
            this.mRfr = new WeakReference<MusicPlayer>(mRfr);
        }

        public void prepare() {
            mIsStop = false;
        }

        public void stop() {
            mIsStop = true;
        }

        @Override
        public void run() {
            if (null == mRfr || null == mRfr.get()) {
                Logutil.w(TAG, "LastMediaInfoSaveRunnable stop run !!!");
                return;
            }

            if (!mIsStop) {
                mRfr.get()._handler.postDelayed(this, 1000);
                mRfr.get().saveLastMediaInfo();
            }
        }
    }

    // 解析媒体信息
    private void parserMediaInfo(String url) {
        mMusicParserModel.parserMusicInfo(url, new IMusicParserModel.OnMediaParserListener<MusicInfo>() {
            @Override
            public void onMediaInfo(MusicInfo info) {
                if (null != mOnMediaInfoChangerListener) {
                    mOnMediaInfoChangerListener.onChangeMediaInfo(info);
                    mMusicPlayList.setmCurrentID3Info(info);// 设置当前媒体ID3信息
                }
            }
        });
    }

    @Override
    public void removePlayList() {
        Logutil.i(TAG, "removePlayList() ...");
        mMusicPlayList.onDestory();
    }

    @Override
    public void onRelease() {
        Logutil.i(TAG, "removePlayList() ...");
        this.mIsPrepared = false;
    }

    @Override
    public String playIndexMusic(int index) {
        return mMusicPlayList.getIndexPaht(index);
    }

    @Override
    public void clean() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        _INSTANCE =null;
    }

    @Override
    public void setAudioFocusState(boolean audioFocusState) {
        isHasAudioFocus = audioFocusState ;
    }

    @Override
    public void setIsAllowPlaying(boolean isAllowPlaying) {
        this.isAllowPlaying = isAllowPlaying;
    }

    @Override
    public void stopFastRunnable() {
        if (null != mFastBackwardRunnable && mFastBackwardRunnable.isRunning()) {
            this.mFastBackwardRunnable.stopRunning();
            this._handler.removeCallbacks(mFastBackwardRunnable);
        }
        if (null != mFastForwardRunnable && mFastForwardRunnable.isRunning()) {
            this.mFastForwardRunnable.stopRunning();
            this._handler.removeCallbacks(mFastForwardRunnable);
        }
        onMuteVolumeEnable(false);// 蓝牙电话，倒车应用 打断快进时不需要禁止声音
    }


    /**
     * 暂停播放处理，停止更新
     */

    private void stopUpdate(){
        notifyMediaPlayStateChange(false);// 通知播放状态改变
        stopUpdateProgressRunnable();//停止更新进度
        stopLastMediaInfoSaveRunnable();//停止保存播放进度点

    }
    /**
     * 播放中，启动更新
     */
    private void startUpdate(){
        notifyMediaPlayStateChange(true);//通知播放状态改变
        MusicPlayManager.getInstance().setIsClickStopMusicPlay(false); //恢复播放，手动暂停即为失效状态
        startUpdateProgressRunnable();
        startLastMediaInfoSaveRunnable();
    }


    @Override
    public int getCurUsbSourceFlag() {
        return this.mCurUsbSourceFlag;
    }
}
