package com.semisky.multimedia.media_music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.autoservice.manager.AutoTestManager;
import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaCtrlConst;
import com.semisky.multimedia.common.interfaces.IBackModeChange;
import com.semisky.multimedia.common.interfaces.IBtCallStatus;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.FormatTimeUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;
import com.semisky.multimedia.media_list.MultimediaListManger;
import com.semisky.multimedia.media_music.model.IMusicDataModel;
import com.semisky.multimedia.media_music.model.IMusicParserModel;
import com.semisky.multimedia.media_music.model.MusicAudioFocusModel;
import com.semisky.multimedia.media_music.model.MusicDataModel;
import com.semisky.multimedia.media_music.model.MusicParserModel;
import com.semisky.multimedia.media_music.service.manger.MusicKeyManager;
import com.semisky.multimedia.media_music.service.manger.MusicPlayManager;
import com.semisky.multimedia.media_music.service.musicplayer.IMusicPlayer;
import com.semisky.multimedia.media_music.service.musicplayer.MusicPlayer;
import com.semisky.multimedia.media_music.view.MusicPlayerActivity;
import com.semisky.multimedia.media_photo.view.PhotoPlayerActivity;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;

import java.io.File;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/22.
 */

public class LocalMusicService extends Service implements ILocalMusicService {
    private static final String TAG = Logutil.makeTagLog(LocalMusicService.class);
    private IMusicDataModel mMusicDataModel;
    private IMusicParserModel mMusicParserModel;
    private IMusicPlayer<MusicInfo> mMusicPlayer;
    private RemoteCallbackListMgs mRemoteCallbackListMgs;
    private int mCurrentUsbSource = Definition.FLAG_USB_INVALID;

    @Override
    public void onCreate() {
        super.onCreate();
        ProxyMusicPlayerImpl.getInstance().onAttach(this);
        init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logutil.i(TAG, "onBind() ...");
        return ProxyMusicPlayerImpl.getInstance();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logutil.i(TAG, "onStartCommand() ...");
        handlerIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logutil.w(TAG, "onLowMemory() ...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logutil.i(TAG, "onDestroy() ...");
        ProxyMusicPlayerImpl.getInstance().onDetach();

        MusicKeyManager.getInstance().onDetach();
    }

    // Utils


    // 处理意图
    private void handlerIntent(Intent intent) {
        if (null == intent) {
            Logutil.i(TAG, "onStartCommand() ...intent==null");
            return;
        }

        String action = intent.getAction();
        Logutil.i(TAG, "handlerIntent() ...action=" + action);

        if (MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL.equals(action)) {
            int cmd = intent.getIntExtra(MediaCtrlConst.PARAM_CMD, MediaCtrlConst.CMD_INVALID);
            Logutil.i(TAG, "handlerIntent() ...cmd=" + cmd);
            checkData();
            switch (cmd) {
                case MediaCtrlConst.CMD_RESUME_PLAY:// 恢复媒体资源播放
                    if (canRestorePlay()) {
                        onRestorePlay();
                    }
                    break;
                case MediaCtrlConst.CMD_NEXT:
                    mMusicPlayer.next();
                    break;
                case MediaCtrlConst.CMD_PREV:
                    mMusicPlayer.prev();
                    break;
                case MediaCtrlConst.CMD_PAUSE:
                    mMusicPlayer.playOrPause();
                    break;
                case MediaCtrlConst.CMD_START:// 节目播放
                    mMusicPlayer.playOrPause();
                    break;
                case MediaCtrlConst.CMD_LIST_PLAY:// 列表媒体播放
                    String url = intent.getStringExtra(MediaCtrlConst.PARAM_URL);
                    Logutil.i(TAG, "handlerIntent() url=" + url);
                    if (canListPlay(url)) {
                        onListPlay(url);
                    }
                    break;
                case MediaCtrlConst.CMD_PLAY_TOGGLE:// 媒体播放切换
                    Logutil.i("test", "factory test sing play: ");
                    this.mMusicPlayer.setPlayMode(PlayMode.SINGLE);// 设置单曲循环播放模式
                    break;
                case MediaCtrlConst.CMD_PLAY_PLAY_LOOP: //列表循环
                    Logutil.i("test", "factory test playLoop: ");
                    this.mMusicPlayer.setPlayMode(PlayMode.LOOP);
                    break;
                case MediaCtrlConst.CMD_PLAY_PLAY_SHUFFLE:
                    Logutil.i("test", "factory test playShuffle: ");
                    this.mMusicPlayer.setPlayMode(PlayMode.SHUFFLE);
                    break;
                case MediaCtrlConst.CMD_PLAY_INDEX:
                    int position = this.mMusicPlayer.getPlayIndex();
                    Logutil.i("test", "factory test playIndex: " + position);
                    AutoTestManager.getInstance().setCurrenIndex(position);
                    break;
                case MediaCtrlConst.CMD_AUDIO_SOURCE_USB1:
                    Logutil.i("test", "CMD_AUDIO_SOURCE_USB1");
                    changeUsbAudioSource(Definition.FLAG_USB1);
                    break;
                case MediaCtrlConst.CMD_AUDIO_SOURCE_USB2:
                    Logutil.i("test", "CMD_AUDIO_SOURCE_USB2");
                    changeUsbAudioSource(Definition.FLAG_USB2);
                    break;
                default:
                    if (cmd >= 1) {// 大于 = 1 表示播放指定下标曲目 (工厂测试使用)
                        if (mMusicPlayer.playIndexMusic(cmd) != null) {
                            Logutil.i("test", "play indexUrl " + cmd);
                            mMusicPlayer.setMusicPath(mMusicPlayer.playIndexMusic(cmd - 1)).setAutoPlay(true).onPreparePlay();
                        }
                    }
                    break;
            }
        } else if (MediaCtrlConst.ACTION_SERVICE_MUSIC_DEBUG_LOG.equals(action)) {
            printDebugLogger();
        }
    }

    // 打开调试日志
    private void printDebugLogger() {
        Logutil.d(TAG, "================DEBUG LOG START=====================");
        if (null == mMusicPlayer) {
            return;
        }
        Logutil.d(TAG, "PlayMode : " + (getPlayMode()));
        Logutil.d(TAG, "isPrepared : " + (mMusicPlayer.isPrepared()));
        Logutil.d(TAG, "isPlaying : " + mMusicPlayer.isPlaying());
        Logutil.d(TAG, "totalTimeProgress : " + FormatTimeUtil.makeFormatTime(mMusicPlayer.getDuration()));
        Logutil.d(TAG, "curTimeProgress : " + FormatTimeUtil.makeFormatTime(mMusicPlayer.getCurrentProgress()));
        Logutil.d(TAG, "================DEBUG LOG   END=====================");
    }


    /**
     * 切换USB音源
     * @param audioSource
     */
    private void changeUsbAudioSource(int audioSource) {

        Log.i(TAG,"");
        if(mCurrentUsbSource == audioSource){
            return;
        }
        switch (audioSource) {
            case Definition.FLAG_USB1:
                break;
            case Definition.FLAG_USB2:
                break;
        }
    }



    private IMusicPlayer.OnProgressChangeListener mOnProgressChangeListener = new IMusicPlayer.OnProgressChangeListener() {
        @Override
        public void onChangeProgress(int progress) {
            mRemoteCallbackListMgs.notifyChangeCurrentProgress(progress);

        }
    };

    private IMusicPlayer.OnMediaInfoChangerListener<MusicInfo> mOnMediaInfoChangerListener = new IMusicPlayer.OnMediaInfoChangerListener<MusicInfo>() {
        @Override
        public void onChangeMediaInfo(MusicInfo info) {
            if (null == info) {
                Logutil.w(TAG, "onChangeMediaInfo() info == NULL !!!");
                info = new MusicInfo();
            }
            mRemoteCallbackListMgs.notifyChangeSongName(info.getTitle());
            mRemoteCallbackListMgs.notifyChangeArtistName(info.getArtist());
            mRemoteCallbackListMgs.notifyChangeAlbumName(info.getAlbum());

            SemiskyIVIManager.getInstance().setMusicImageUrl(info.getUrl());// 歌曲URL设置到中间件
            SemiskyIVIManager.getInstance().setCurrentSingerName(null != info.getArtist() ? info.getArtist() : "");// 歌手名设置到中间件
            SemiskyIVIManager.getInstance().setCurrentSourceName(null != info.getTitle() ? info.getTitle() : "");// 歌曲名设置到中间件
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mRemoteCallbackListMgs.notifyChangeTotalProgress(0);
            mRemoteCallbackListMgs.notifyChangeCurrentProgress(0);
            refreshFavoriteState(mMusicPlayer.getCurrentPlayingUrl());
            mRemoteCallbackListMgs.notifyPlayError(extra);
            mRemoteCallbackListMgs.notifyChangeUrl(mMusicPlayer.getCurrentPlayingUrl());
            MultimediaListManger.getInstance().setmPlayingUrlWithMusic(mMusicPlayer.getCurrentPlayingUrl());//异常节目时也需要在列表界面显示高亮条目 bug8825
            return false;
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Logutil.i("lcc", "onPrepared isHighPriorityAppRunning: " + SemiskyIVIManager.getInstance().isHighPriorityAppRunning() + "");
            Logutil.i("lcc", "onPrepared:  hasAudioFocus" + MusicAudioFocusModel.getInstance().hasAudioFocus() + "");
            Logutil.i("lcc", "onPrepared:  hasNaviAtForeground" + SemiskyIVIManager.getInstance().isNaviAtForeground() + "");
            if (!MusicAudioFocusModel.getInstance().hasAudioFocus() && !SemiskyIVIManager.getInstance().isHighPriorityAppRunning()
                    || SemiskyIVIManager.getInstance().isNaviAtForeground()) {
                Logutil.i("lcc", "onPrepared:  requestFocus");
                MusicAudioFocusModel.getInstance().init(getApplication());
                MusicAudioFocusModel.getInstance().registerAudioFocus(mOnAudioFocusChangeListener);
                MusicKeyManager.getInstance().setStopKeyEventEnable(false);
                MusicAudioFocusModel.getInstance().onRequestAudioFocus();
            }
            mRemoteCallbackListMgs.notifyChangeTotalProgress(mp.getDuration());
            mRemoteCallbackListMgs.notifyChangeCurrentProgress(mp.getCurrentPosition());
            mRemoteCallbackListMgs.notifyMediaPrepareCompleted();
            mRemoteCallbackListMgs.notifyChangeUrl(mMusicPlayer.getCurrentPlayingUrl());

            // 设置列表当前播放歌曲URL
            MultimediaListManger.getInstance().setmPlayingUrlWithMusic(mMusicPlayer.getCurrentPlayingUrl());
            MultimediaListManger.getInstance().notifyItemHighLightChange();

            refreshFavoriteState(mMusicPlayer.getCurrentPlayingUrl());
//            PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_MUSIC);// 保存断点记忆应用标识
            PreferencesManager.saveLastMusicUrl(mMusicPlayer.getCurUsbSourceFlag(), mMusicPlayer.getCurrentPlayingUrl());// 保存断点记忆资源路径
            checkActivityIsAtForeground();
            checkAppFlag();

        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            mRemoteCallbackListMgs.notifyChangeCurrentProgress(mp.getCurrentPosition());
        }
    };

    private IMusicPlayer.OnMediaPlayStateListener mOnMediaPlayStateListener = new IMusicPlayer.OnMediaPlayStateListener() {
        @Override
        public void onChangePlayState(boolean isPlay) {
            Logutil.i(TAG, "onChangePlayState() ..." + isPlay);
            mRemoteCallbackListMgs.notifyChangePlayStatus(isPlay);
            SemiskyIVIManager.getInstance().setCurrentPlayStatus(isPlay);
        }
    };

    private IMusicPlayer.OnPlayProgramChangeListener mOnPlayProgramChangeListener = new IMusicPlayer.OnPlayProgramChangeListener() {
        @Override
        public void onChangePlayProgram(String pos) {
            mRemoteCallbackListMgs.notifyChangeCurProgramPos(pos);
        }
    };

    private void init() {
        Logutil.i(TAG, "init() ...");
        initHandlerThread();
        // 初始化数据模型
        this.mMusicDataModel = new MusicDataModel(MediaApplication.getCurrentUSB());
        // 初始化媒体解析模型
        this.mMusicParserModel = new MusicParserModel();
        this.mMusicPlayer = MusicPlayer.getInstance();
        this.mMusicDataModel.registerOnRefreshDataListener(this.OnRefreshDataListener);
        // 初始化媒体播放器

        this.mMusicPlayer.setOnProgressChangeListener(mOnProgressChangeListener);
        this.mMusicPlayer.setOnMediaInfoChangerListener(mOnMediaInfoChangerListener);
        this.mMusicPlayer.setOnPreparedListener(mOnPreparedListener);
        this.mMusicPlayer.setOnErrorListener(mOnErrorListener);
        this.mMusicPlayer.setmOnSeekCompleteListener(mOnSeekCompleteListener);
        this.mMusicPlayer.setOnMediaPlayStateListener(mOnMediaPlayStateListener);
        this.mMusicPlayer.setOnPlayProgramChangeListener(mOnPlayProgramChangeListener);
        this.mMusicPlayer.setPlayMode(PreferencesManager.getLastPlayMode());// 设置断点播放模式
        this.mRemoteCallbackListMgs = new RemoteCallbackListMgs();
        USBManager.getInstance().registerOnUSBStateChangeListener(mOnUSBStateChangeListener);
        MusicAudioFocusModel.getInstance().init(getApplication());
        MusicAudioFocusModel.getInstance().registerAudioFocus(mOnAudioFocusChangeListener);
        // 方控按键事件监听
        MusicKeyManager.getInstance().onAttatch(this);

        PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_MUSIC);// 保存断点记忆应用标识
        SemiskyIVIManager.getInstance().registerBtStatusChanger(iBtCallStatusListener);
        SemiskyIVIManager.getInstance().registerBackModeChanged(iBackModeChange);
    }

    // 检查媒体数据
    private void checkData() {
        if (!mMusicPlayer.hasData()) {
            onLoadData();
        }
    }

    // 加载数据
    private void onLoadData() {
        mMusicDataModel.onLoadMusicInfoList(new IMusicDataModel.OnLoadDataListener<MusicInfo>() {
            @Override
            public void onLoadData(List<MusicInfo> dataList) {
                Logutil.i(TAG, "onLoadData() ..." + (dataList != null ? dataList.size() : 0));
                mMusicPlayer.setPlayList(dataList);
            }
        }, PreferencesManager.getLastMusicSourceUsbFlag());
    }

    // 刷新媒体数据
    private IMusicDataModel.OnRefreshDataListener OnRefreshDataListener = new IMusicDataModel.OnRefreshDataListener() {
        @Override
        public void onUpdateData(List<MusicInfo> dataList, boolean isScanEnd) {
            Logutil.i(TAG, "onUpdateData() ..." + (dataList != null ? dataList.size() : 0));
            mMusicPlayer.setPlayList(dataList);


        }
    };

    private USBManager.OnUSBStateChangeListener mOnUSBStateChangeListener = new USBManager.OnUSBStateChangeListener() {

        @Override
        public void onChangeState(String usbPath, int stateCode) {

            Logutil.i(TAG, "onChangeState() usbPath=" + usbPath + ",stateCode=" + stateCode);
            switch (stateCode) {
                case USBManager.STATE_USB_MOUNTED:
                    Logutil.i(TAG, "STATE_USB_MOUNTED() ...");
                    break;
                case USBManager.STATE_USB_DEVICE_DETACHED:
                    Logutil.i(TAG, "STATE_USB_DEVICE_DETACHED() ...");
                    USBManager.getInstance().unregisterOnUSBStateChangeListener(mOnUSBStateChangeListener);
                    MusicPlayManager.getInstance().setIsClickStopMusicPlay(false); //手动暂停即为失效状态
                    mMusicPlayer.pause();
                    mMusicPlayer.stop();
                    mMusicPlayer.onRelease();
                    mMusicPlayer.clean();
                    stopSelf();
                    MusicAudioFocusModel.getInstance().onAbandonAudioFocus();
                    setAppState(AutoConstants.AppStatus.DESTROY);
                    break;
                case USBManager.STATE_USB_REMOVED:
                    Logutil.i(TAG, "STATE_USB_REMOVED() ...");
                    // mMusicPlayer.stop();
                    break;
            }
        }
    };

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Logutil.i(TAG, "onAudioFocusChange() AUDIOFOCUS_GAIN ...");
                    mMusicPlayer.setAudioFocusState(true);
                    if (!MusicPlayManager.getInstance().getIsClickStopMusicPlay() && !mMusicPlayer.isPlaying()) {
                        //非手动暂停即可恢复播放
                        mMusicPlayer.start();
                    }
                    MusicKeyManager.getInstance().setStopKeyEventEnable(false);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Logutil.i(TAG, "onAudioFocusChange() AUDIOFOCUS_LOSS_TRANSIENT ...");
                    mMusicPlayer.setAudioFocusState(false);
                    mMusicPlayer.stopFastBackward(true);
                    mMusicPlayer.stopFastForward(true);
                    MusicKeyManager.getInstance().setStopKeyEventEnable(true);
                    mMusicPlayer.stopNext();
                    mMusicPlayer.stopPrev();
                    mMusicPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Logutil.i(TAG, "onAudioFocusChange() AUDIOFOCUS_LOSS ...");
                    mMusicPlayer.stopFastBackward(true);
                    mMusicPlayer.stopFastForward(true);
                    mMusicPlayer.stopNext();
                    mMusicPlayer.stopPrev();
                    mMusicPlayer.pause();
                    mMusicPlayer.onRelease();
                    MusicPlayManager.getInstance().setIsClickStopMusicPlay(false); //手动暂停即为失效状态
//                    MediaApplication.finishActivity(MusicPlayerActivity.class);
                    MusicAudioFocusModel.getInstance().onAbandonAudioFocus();
                    MusicKeyManager.getInstance().setStopKeyEventEnable(true);
                    setAppState(AutoConstants.AppStatus.DESTROY);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Logutil.i(TAG, "onAudioFocusChange() AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ...");
                    break;
            }
        }
    };


    /**
     * 列表媒体播放
     *
     * @param url
     */
    private void onListPlay(String url) {
        Logutil.i(TAG, "onListPlay() url=" + url);
        if (null != url) {
            mMusicPlayer.stopNext();//停止下一曲。损坏资源时，会延迟三秒播放，不会播放用户选择的音乐。
            mMusicPlayer.setMusicPath(url).setAutoPlay(true).onPreparePlay();
        }
    }


    @Override
    public void registerCallback(IProxyProgramChangeCallback callback) {
        mRemoteCallbackListMgs.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(IProxyProgramChangeCallback callback) {
        mRemoteCallbackListMgs.unregisterCallback(callback);
    }

    @Override
    public String getSongName() {
        if (null != mMusicPlayer.getCurrentID3Info()) {
            return mMusicPlayer.getCurrentID3Info().getTitle();
        }
        return null;
    }

    @Override
    public String getArtistName() {
        if (null != mMusicPlayer.getCurrentID3Info()) {
            return mMusicPlayer.getCurrentID3Info().getArtist();
        }
        return null;
    }

    @Override
    public String getAlbumName() {
        if (null != mMusicPlayer.getCurrentID3Info()) {
            return mMusicPlayer.getCurrentID3Info().getAlbum();
        }
        return null;
    }

    @Override
    public int getTotalPorgress() {
        return mMusicPlayer.getDuration();
    }

    @Override
    public int getCurrentProgress() {
        return mMusicPlayer.getCurrentProgress();
    }

    @Override
    public String getCurProgramPos() {
        return mMusicPlayer.getmNumOfCurrentAndTotalProgram();
    }

    @Override
    public boolean isFavorite() {
        return mMusicPlayer.isFavoriteWithCurrentMusic();
    }

    @Override
    public int getPlayMode() {
        return PreferencesManager.getLastPlayMode();
    }

    @Override
    public boolean isPlaying() {
        return mMusicPlayer.isPlaying();
    }

    @Override
    public void onSwitchNext() {
        Logutil.i(TAG, "onSwitchNext() ...");
        mMusicPlayer.next();
    }

    @Override
    public void onSwitchPrev() {
        Logutil.i(TAG, "onSwitchPrev() ...");
        mMusicPlayer.prev();
    }

    @Override
    public void onSwitchFastForward() {
        Logutil.i(TAG, "onSwitchFastForward() ...");
        mMusicPlayer.fastForward();
    }

    @Override
    public void onSwitchStopFastForward() {
        Logutil.i(TAG, "onSwitchStopFastForward() ...");
        mMusicPlayer.stopFastForward(false);
    }

    @Override
    public void onSwitchFastBackward() {
        Logutil.i(TAG, "onSwitchFastBackward() ...");
        mMusicPlayer.fastBackward();
    }

    @Override
    public void onSwitchStopFastBackward() {
        Logutil.i(TAG, "onSwitchStopFastBackward() ...");
        mMusicPlayer.stopFastBackward(false);
    }

    @Override
    public void onSwitchPlayOrPause() {
        Logutil.i(TAG, "onSwitchPlayOrPause() ...");
        mMusicPlayer.playOrPause();
    }

    @Override
    public void start() {
        mMusicPlayer.start();
    }

    @Override
    public void pause() {
        mMusicPlayer.pause();
    }

    @Override
    public void onSwitchResumePlay() {
        boolean isPlay = mMusicPlayer.start();
        Logutil.i(TAG, "onSwitchResumePlay() ..." + isPlay);
//        mRemoteCallbackListMgs.notifyChangePlayStatus(isPlay);   已在start中通知播放状态
    }

    @Override
    public void onSwitchPlayMode() {
        Logutil.i(TAG, "onSwitchPlayMode() ...");
        int lastPlayMode = PreferencesManager.getLastPlayMode();// 获取断点记忆播放模式
        int nextPlayMode = PlayMode.switchNextMode(lastPlayMode);// 获取下一个播放模式
        mMusicPlayer.setPlayMode(nextPlayMode);// 设置音乐播放器播放模式
        PreferencesManager.saveLastPlayMode(nextPlayMode);// 保存播放模式偏好
        mRemoteCallbackListMgs.notifyChangePlayMode(nextPlayMode);// 播放改变通知
    }

    @Override
    public void onSwitchFavorite() {

    }

    @Override
    public void onSeekTo(int progress) {
        mMusicPlayer.seekTo(progress);
    }

    @Override
    public void onUpdateProgressWithThreadEnabled(boolean enabled) {
        mMusicPlayer.onUpdateProgressWithThreadEnabled(enabled);
    }

    @Override
    public void onDeleteFromFavorite() {

    }

    @Override
    public void RequestAudio() {
        Logutil.i("lcc", "Service RequestAudio hasAudioFocus!!! " + MusicAudioFocusModel.getInstance().hasAudioFocus());
        Logutil.i("lcc", "Service RequestAudio !!! hasHighAppRunning" + SemiskyIVIManager.getInstance().isHighPriorityAppRunning());
        if (!MusicAudioFocusModel.getInstance().hasAudioFocus() && !SemiskyIVIManager.getInstance().isHighPriorityAppRunning()) {
            MusicAudioFocusModel.getInstance().init(getApplication());
            MusicAudioFocusModel.getInstance().registerAudioFocus(mOnAudioFocusChangeListener);
            MusicKeyManager.getInstance().setStopKeyEventEnable(false);
            MusicAudioFocusModel.getInstance().onRequestAudioFocus();
            Logutil.i("lcc", "Service already RequestAudio !!!" + MusicAudioFocusModel.getInstance().hasAudioFocus() + "");
        }

    }

    @Override
    public void stopFastBackOrFastF(boolean isH) {
        Logutil.i(TAG, "stopFastBackOrFastF() ...");
        if (isH) {
            mMusicPlayer.stopFastRunnable();
        } else {
            onSwitchStopFastBackward();
            onSwitchStopFastForward();
        }
    }

    @Override
    public String getCurrentLyricUrl() {
        String musicUrl = mMusicPlayer.getCurrentPlayingUrl();
        String lrcUrl = MediaStorageAccessProxyModel.getInstance().queryLyricUrl(musicUrl);
        Log.i(TAG, "getCurrentLyricUrl() musicUrl : " + musicUrl);
        Log.i(TAG, "getCurrentLyricUrl() lrcUrl : " + lrcUrl);
        return lrcUrl;
    }

    // utils

    // 检查当前多媒体界面是否在前台
    private void checkActivityIsAtForeground() {
        Logutil.i(TAG, "checkActivityIsAtForeground() ...");
        mBackgroundHandler.removeCallbacks(mCheckActivityTaskRunnable);
        mBackgroundHandler.post(mCheckActivityTaskRunnable);
    }

    private Runnable mCheckActivityTaskRunnable = new Runnable() {
        @Override
        public void run() {
            // 当前界面是否在前台
            String clzName = MusicPlayerActivity.class.getName();
            boolean isTopActivity = AppActivityManager.getInstance().isTopActivity(clzName);
            Logutil.i(TAG, "CheckActivityTaskRunnable.run() isTopActivity=" + isTopActivity);
            if (!isTopActivity) {
                setAppState(AutoConstants.AppStatus.RUN_BACKGROUND);
            }
        }
    };

    private void setAppState(int status) {
        String clzName = MusicPlayerActivity.class.getName();
        SemiskyIVIManager.getInstance().setAppStatus(clzName, "", status);
    }

    // 恢复媒体播放
    private void onRestorePlay() {
        String lastUrl = PreferencesManager.getLastMusicUrl(PreferencesManager.getLastMusicSourceUsbFlag());

        int lastProgress = PreferencesManager.getLastMusicProgress();
        boolean isExistsWithLastUrl = (null != lastUrl && new File(lastUrl).exists());
        String scanFirstMusicUrl = PreferencesManager.getScanFirstMusicUrl(PreferencesManager.getLastMusicSourceUsbFlag());
        boolean isExistsByScanFirstMusicUrl = (null != lastUrl && new File(scanFirstMusicUrl).exists());

        Logutil.i(TAG, "===============");
        Logutil.i(TAG, "onRestorePlay() lastUrl=" + lastUrl);
        Logutil.i(TAG, "onRestorePlay() lastProgress=" + lastProgress);
        Logutil.i(TAG, "onRestorePlay() isExistsWithLastUrl=" + isExistsWithLastUrl);
        Logutil.i(TAG, "onRestorePlay() scanFirstMusicUrl=" + scanFirstMusicUrl);
        Logutil.i(TAG, "onRestorePlay() isExistsByScanFirstMusicUrl=" + isExistsByScanFirstMusicUrl);
        Logutil.i(TAG, "===============");

        if (isExistsWithLastUrl) {
            Logutil.i(TAG, "onRestorePlay() Suc !!!!");
            mMusicPlayer.setMusicPath(lastUrl)
                    .setAutoPlay(true)
                    .seekTo(lastProgress)
                    .onPreparePlay();
        } else if (isExistsByScanFirstMusicUrl) {
            Logutil.i(TAG, "onRestorePlay() First Music !!!!");
            mMusicPlayer.setMusicPath(scanFirstMusicUrl)
                    .setAutoPlay(true)
                    .seekTo(0)
                    .onPreparePlay();
        }
    }

    // 是否可以恢复播放
    private boolean canRestorePlay() {
        boolean isPrepared = mMusicPlayer.isPrepared();
        boolean hasAudioFocus = MusicAudioFocusModel.getInstance().hasAudioFocus();
        Logutil.i(TAG, "canRestorePlay() isPrepared=" + isPrepared);
        Logutil.i(TAG, "canRestorePlay() hasAudioFocus=" + hasAudioFocus);
        return (!isPrepared || !hasAudioFocus);
    }

    // 是不否可以列表播放操作
    private boolean canListPlay(String url) {
        boolean isPrepared = mMusicPlayer.isPrepared();
        boolean hasAudioFocus = MusicAudioFocusModel.getInstance().hasAudioFocus();
        String curPlayingUrl = mMusicPlayer.getCurrentPlayingUrl();
        boolean isSameWithUrl = (null != url && null != curPlayingUrl && url.equals(curPlayingUrl) ? true : false);
        if (!isPrepared || !hasAudioFocus || !isSameWithUrl) {
            return true;
        }
        Logutil.i(TAG, "canListPlay() isPrepared=" + isPrepared);
        Logutil.i(TAG, "canListPlay() hasAudioFocus=" + hasAudioFocus);
        Logutil.i(TAG, "canListPlay() isSameWithUrl=" + isSameWithUrl);
        return false;
    }

    /**
     * 刷新收藏状态
     */
    private void refreshFavoriteState(String url) {

    }

    /**
     * 初始化后台线程
     */
    private HandlerThread mMusicServiceHandlerThread;
    private Handler mBackgroundHandler;

    private void initHandlerThread() {
        Logutil.i(TAG, "initHandlerThread() ...");
        if (null == mMusicServiceHandlerThread) {
            mMusicServiceHandlerThread = new HandlerThread("");
            mMusicServiceHandlerThread.start();
        }
        if (null == mBackgroundHandler) {
            mBackgroundHandler = new Handler(mMusicServiceHandlerThread.getLooper());
        }
    }

    /**
     * 通话状态
     * 极限测试带来的问题
     */
    IBtCallStatus iBtCallStatusListener = new IBtCallStatus() {
        @Override
        public void btStateChange(int state) {
            //电话挂断
            if (state == 7) {
                // 当前界面是否在前台
                String clzName = MusicPlayerActivity.class.getName();
                boolean isTopActivity = AppActivityManager.getInstance().isTopActivity(clzName);
                Logutil.i("lcc", "btStateChange isTopActivity==" + isTopActivity);
                Logutil.i("lcc", "btStateChange hasAudioFocus== " + MusicAudioFocusModel.getInstance().hasAudioFocus() + "");
                mMusicPlayer.setIsAllowPlaying(true);
                if (!MusicAudioFocusModel.getInstance().hasAudioFocus() && isTopActivity) {
                    MusicAudioFocusModel.getInstance().init(getApplication());
                    MusicAudioFocusModel.getInstance().registerAudioFocus(mOnAudioFocusChangeListener);
                    MusicKeyManager.getInstance().setStopKeyEventEnable(false);
                    MusicAudioFocusModel.getInstance().onRequestAudioFocus();
                    Logutil.i("lcc", "btStateChange already hasAudioFocus !!!" + MusicAudioFocusModel.getInstance().hasAudioFocus() + "");
                    if (!MusicPlayManager.getInstance().getIsClickStopMusicPlay() && !mMusicPlayer.isPlaying()) {
                        //非手动暂停即可恢复播放
                        mMusicPlayer.start();
                    }
                }
            } else {
                //电话拉起状态。禁止播放。
                mMusicPlayer.setIsAllowPlaying(false);
                stopFastBackOrFastF(true);
            }
        }
    };
    /**
     * 针对U盘插入跳转瞬间
     */
    private IBackModeChange iBackModeChange = new IBackModeChange() {
        @Override
        public void backModeChanged(final boolean state) {
            if (!state) {
                // 当前界面是否在前台
                String clzName = MusicPlayerActivity.class.getName();
                boolean isTopActivity = AppActivityManager.getInstance().isTopActivity(clzName);
                Logutil.i("lcc", "iBackModeChange isTopActivity==" + isTopActivity);
                Logutil.i("lcc", "iBackModeChange hasAudioFocus== " + MusicAudioFocusModel.getInstance().hasAudioFocus() + "");
                mMusicPlayer.setIsAllowPlaying(true);
                if (!MusicAudioFocusModel.getInstance().hasAudioFocus() && isTopActivity) {
                    MusicAudioFocusModel.getInstance().init(getApplication());
                    MusicAudioFocusModel.getInstance().registerAudioFocus(mOnAudioFocusChangeListener);
                    MusicKeyManager.getInstance().setStopKeyEventEnable(false);
                    MusicAudioFocusModel.getInstance().onRequestAudioFocus();
                    Logutil.i("lcc", "iBackModeChange already hasAudioFocus !!!" + MusicAudioFocusModel.getInstance().hasAudioFocus() + "");
                    if (!MusicPlayManager.getInstance().getIsClickStopMusicPlay() && !mMusicPlayer.isPlaying()) {
                        //非手动暂停即可恢复播放
                        mMusicPlayer.start();
                    }
                }

            } else {
                stopFastBackOrFastF(true);
            }
        }


    };

    /**
     * bug 8772 在图片界面界面 mode切换到音乐，点击home 再点击多媒体，会进入音乐 （应该进入图片）
     * 因可能是service 被系统杀死，导致重置了标记位
     */
    private void checkAppFlag() {
        String clzName = PhotoPlayerActivity.class.getName();
        boolean isTopActivity = AppActivityManager.getInstance().isTopActivity(clzName);
        Logutil.i(TAG, "checkAppFlag PhotoPlayerActivityIsTopActivity=" + isTopActivity);
        if (isTopActivity) {
            //如果播放中过程是 图片在前台，标记位为图片
            PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_PHOTO);
        } else {
            PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_MUSIC);
        }
    }

}
