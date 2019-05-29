package com.semisky.multimedia.media_music.presenter;

import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.FormatTimeUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.MultimediaListManger;
import com.semisky.multimedia.media_music.LrcView.LrcEntity;
import com.semisky.multimedia.media_music.model.IMusicDataModel;
import com.semisky.multimedia.media_music.model.LrcParserModel;
import com.semisky.multimedia.media_music.model.MusicDataModel;
import com.semisky.multimedia.media_music.model.ProxyMusicPlayerModel;
import com.semisky.multimedia.media_music.view.IMusicPlayerView;

import java.util.List;

/**
 * 媒体播放器表示层
 * Created by Anter on 2018/7/30.
 */

public class MusicPlayerPresenter<V extends IMusicPlayerView> extends BasePresenter<V> implements IMusicPlayerPresenter {
    private static final String TAG = Logutil.makeTagLog(MusicPlayerPresenter.class);
    private ProxyMusicPlayerModel mProxyMusicPlayerModel;
    private int mUserDragSeekBarProgress = 0;// 用户拖动进度条的进度记录
    private boolean mStopTouchEventWhenNextProgram = true;// 当下一个节目时，禁止下一个按键控件的触摸事件
    private boolean mStopTouchEventWhenPrevProgram = true;// 当上一个节目时，禁止上一个按键控件的触摸事件
    private IMusicDataModel musicDataModel;
    private List<MusicInfo> musicInfoList;

    public MusicPlayerPresenter() {
        this.mProxyMusicPlayerModel = ProxyMusicPlayerModel.getInstance();
        this.mProxyMusicPlayerModel.registerOnServiceConnectCompletedListener(mConnectedServiceListener);
        this.musicDataModel = new MusicDataModel(MediaApplication.getCurrentUSB());
        USBManager.getInstance().registerOnUSBStateChangeListener(onUSBStateChangeListener);
    }

    @Override
    public SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return this.mOnSeekBarChangeListener;
    }

    @Override
    public void bindService() {
        if (isBindView()) {
            Logutil.i(TAG, "bindService() ...");
            mProxyMusicPlayerModel.bindService(mViewRef.get().getContext());
        }
    }

    @Override
    public void unbindService() {
        if (isBindView()) {
            Logutil.i(TAG, "unbindService() ...");
            mProxyMusicPlayerModel.unbindService(mViewRef.get().getContext());
        }
    }

    @Override
    public void onHandlerIntent(Intent intent) {
        if (null == intent) {
            return;
        }

        String url = intent.getStringExtra("url");
        if (null != url) {
            mProxyMusicPlayerModel.onListPlay(url);
            bindService();
        } else {
            mProxyMusicPlayerModel.startService(mViewRef.get().getContext());
            bindService();
        }
        Logutil.i(TAG, "onHandlerIntent() ..." + url);
    }

    @Override
    public void onFastForwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchFastForward();
        }
    }

    @Override
    public void onStopFastForwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchStopFastForward();
        }
    }

    @Override
    public void onFastBackwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchFastBackward();
        }
    }

    @Override
    public void onStopFastBackwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchStopFastBackward();
        }
    }

    @Override
    public void onSwitchPlayMode() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchPlayMode();
        }
    }

    @Override
    public void onSwitchPlayOrPause() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchPlayOrPause();
        }
    }

    @Override
    public void onPrevProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchPrev();
        }
    }

    @Override
    public void onNextProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchNext();
        }
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isBindView()) {
                mUserDragSeekBarProgress = progress;
                if (fromUser) {
                    mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress));
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mProxyMusicPlayerModel.onUpdateProgressWithThreadEnabled(false);// 暂停更新进度线程
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (isBindView()) {
                // 用户抬起手停止拖动进度条时，将当前拖动的进度设置到播放器
                mProxyMusicPlayerModel.onSeekTo(mUserDragSeekBarProgress);// 设置播放进度到播放器
                mProxyMusicPlayerModel.onUpdateProgressWithThreadEnabled(true);// 启动更新进度线程
                mProxyMusicPlayerModel.onSwitchResumePlay();// 恢复播放操作
                mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(mUserDragSeekBarProgress));
                mUserDragSeekBarProgress = 0;
            }
        }
    };

    @Override
    public void onEnterList() {
        if (isBindView()) {
            AppUtil.jumpToLocalMusicList(PreferencesManager.getLastMusicSourceUsbFlag());
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        mProxyMusicPlayerModel.unregisterCallback(mProxyProgramChangeCallback);
        USBManager.getInstance().unregisterOnUSBStateChangeListener(onUSBStateChangeListener);
    }

    private ProxyMusicPlayerModel.OnServiceConnectCompletedListener mConnectedServiceListener = new ProxyMusicPlayerModel.OnServiceConnectCompletedListener() {
        @Override
        public void onServiceConnectCompleted() {
            mProxyMusicPlayerModel.registerCallback(mProxyProgramChangeCallback);
            updateMediaInfo();
        }
    };
    private USBManager.OnUSBStateChangeListener onUSBStateChangeListener = new USBManager.OnUSBStateChangeListener() {
        @Override
        public void onChangeState(String usbPath, int stateCode) {
            switch (stateCode) {
                case USBManager.STATE_USB_DEVICE_DETACHED:
                    // TODO: 2019/4/29   添加跳转到其它界面逻辑
                    if (isBindView()) {
                        mViewRef.get().onFinish();
                    }

                    break;
            }
        }
    };

    private void updateMediaInfo() {
        Logutil.i(TAG, "updateMediaInfo() ...");
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    String songName = mProxyMusicPlayerModel.getSongName();// 歌曲名
                    String artistName = mProxyMusicPlayerModel.getArtistName();// 歌手名
                    String albumName = mProxyMusicPlayerModel.getAlbumName();// 专辑名
                    int totalProgress = mProxyMusicPlayerModel.getTotalPorgress();// 总进度
                    int curProgress = mProxyMusicPlayerModel.getCurrentProgress();// 当前进度
                    int playMode = mProxyMusicPlayerModel.getPlayMode();// 播放模式
                    boolean isPlaying = mProxyMusicPlayerModel.isPlaying();
                    boolean isFavorite = mProxyMusicPlayerModel.isFavorite();// 收藏状态

                    String sTotalTime = FormatTimeUtil.makeFormatTime(totalProgress);// 总进度格式化指定格式时间
                    String sCurTime = FormatTimeUtil.makeFormatTime(curProgress);// 当前进度格式化指定格式时间

                    Logutil.i(TAG, "================");
                    Logutil.i(TAG, "updateMediaInfo() songName=" + songName);
                    Logutil.i(TAG, "updateMediaInfo() artistName=" + artistName);
                    Logutil.i(TAG, "updateMediaInfo() albumName=" + albumName);
                    Logutil.i(TAG, "updateMediaInfo() playMode=" + playMode);
                    Logutil.i(TAG, "updateMediaInfo() isFavorite=" + isFavorite);
                    Logutil.i(TAG, "updateMediaInfo() totalProgress=" + sTotalTime);
                    Logutil.i(TAG, "updateMediaInfo() curProgress=" + sCurTime);
                    Logutil.i(TAG, "updateMediaInfo() isPlaying=" + isPlaying);
                    Logutil.i(TAG, "================");

                    mViewRef.get().onShowProgramName(songName);
                    mViewRef.get().onShowProgramArtistName(artistName);
                    mViewRef.get().onShowProgramAlbumName(albumName);
                    mViewRef.get().onChangePlayMode(playMode);

                    mViewRef.get().onUpdateDuration(totalProgress);
                    mViewRef.get().onShowProgramProgress(curProgress);
                    mViewRef.get().onShowProgramCurrentTime(sCurTime);
                    mViewRef.get().onShowProgramTotalTime(sTotalTime);

                    mViewRef.get().onChangePlayState(isPlaying);
                    mViewRef.get().onSwitchFavoriteView(isFavorite);

                }
            }
        });
    }

    private IProxyProgramChangeCallback.Stub mProxyProgramChangeCallback = new IProxyProgramChangeCallback.Stub() {
        @Override
        public void onChangeSongName(final String songName) throws RemoteException {
            Logutil.i(TAG, "onChangeSongName() ..." + (null != songName ? songName : "unkown"));

            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onShowProgramName(songName);
                    }
                }
            });

        }

        @Override
        public void onChangeArtistName(final String artistName) throws RemoteException {
            Logutil.i(TAG, "onChangeArtistName() ..." + (null != artistName ? artistName : "unkown"));
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        String newArtistName = null;
                        if (null != artistName) {
                            newArtistName = artistName.length() == 0 ? null : artistName;
                        }
                        mViewRef.get().onShowProgramArtistName(newArtistName);
                    }
                }
            });
        }

        @Override
        public void onChangeAlbumName(final String albumName) throws RemoteException {
            Logutil.i(TAG, "onChangeAlbumName() ..." + (null != albumName ? albumName : "unkown"));
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        String newAlbumName = null;

                        if (null != albumName) {
                            newAlbumName = albumName.length() == 0 ? null : albumName;
                        }
                        mViewRef.get().onShowProgramAlbumName(newAlbumName);
                    }
                }
            });
        }

        @Override
        public void onChangeTotalProgress(final int progress) throws RemoteException {
            Logutil.i(TAG, "onChangeTotalProgress() ..." + progress);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onUpdateDuration(progress);
                        mViewRef.get().onShowProgramTotalTime(FormatTimeUtil.makeFormatTime(progress));
                    }
                }
            });
        }

        int progress_ = -1;// progress 播放进度
        int lastProgress = -1;// 播放进度的前一次进度

        @Override
        public void onChangeCurrentProgress(int progress) throws RemoteException {
            //            Logutil.i(TAG, "onChangeCurrentProgress() ..." + progress);
            progress_ = progress;
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        //bug 快进完成的时候显示当前时间总会回退一秒再播放下一曲
                        //根据进度判断是否需要更新进度条，具体原因未能找到
                        //后者条件：当重新播放歌曲时，progress可能不为0,小于500毫秒的情况视为从头开始播放
                        if (progress_ > lastProgress || progress_ < 500) {
                            lastProgress = progress_;
                            mViewRef.get().onShowProgramProgress(progress_);
                            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress_));
                        } else if (lastProgress > progress_ + 500 || progress_ < 500) {
                            //快退
                            lastProgress = progress_;
                            mViewRef.get().onShowProgramProgress(progress_);
                            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress_));
                        }
                    }
                }
            });
        }

        @Override
        public void onChangeCurProgramPos(final String programPos) throws RemoteException {

        }

        @Override
        public void onChangePlayMode(final int playMode) throws RemoteException {
            Logutil.i(TAG, "onChangePlayMode() ..." + playMode);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onChangePlayMode(playMode);
                    }
                }
            });
        }

        @Override
        public void onChangePlayStatus(final boolean playStatus) throws RemoteException {
            Logutil.i(TAG, "onChangePlayStatus() ..." + playStatus);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onChangePlayState(playStatus);
                    }
                }
            });
        }

        @Override
        public void onChangeFavorite(final boolean isFavorite) throws RemoteException {
            Logutil.i(TAG, "onChangeFavorite() ..." + isFavorite);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onSwitchFavoriteView(isFavorite);
                    }
                }
            });
        }

        @Override
        public void onMediaPrepareCompleted() throws RemoteException {
            if (isBindView()) {
                mViewRef.get().onSwitchPlayProgramExceptionWarningView(false);
            }
        }

        @Override
        public void onPlayError(int what) throws RemoteException {
            if (isBindView()) {
                mViewRef.get().onSwitchPlayProgramExceptionWarningView(true);
            }
        }

        @Override
        public void onChangeUrl(String url) throws RemoteException {
            if (isBindView()) {
                mViewRef.get().refreshPlayingPosition(url);
            }
        }


    };

    @Override
    public void onSwitchFavorite() {
        if (isBindView()) {
            Logutil.i(TAG, "onSwitchFavorite() ...");
            mProxyMusicPlayerModel.onSwitchFavorite();
        }
    }

    @Override
    public void setTitleToStatusBar(String clz, String title, int state) {
        SemiskyIVIManager.getInstance().setAppStatus(clz, title, state);
    }

    @Override
    public void setStopTouchEventEnableWhenNextProgram(boolean enable) {
        if (isBindView()) {
            this.mStopTouchEventWhenNextProgram = enable;
        }
    }

    @Override
    public boolean getStopTouchEventEnableWhenNextProgram() {
        if (isBindView()) {
            return this.mStopTouchEventWhenNextProgram;
        }
        return false;
    }

    @Override
    public void setStopTouchEventEnableWhenPrevProgram(boolean enable) {
        if (isBindView()) {
            this.mStopTouchEventWhenPrevProgram = enable;
        }
    }

    @Override
    public boolean getStopTouchEventEnableWhenPrevProgram() {
        if (isBindView()) {
            return this.mStopTouchEventWhenPrevProgram;
        }
        return false;
    }

    @Override
    public void onResumeRequestAudio() {
        if (isBindView()) {
            mProxyMusicPlayerModel.notifyServiceRequestAudio();
        }
    }

    @Override
    public void onChangeSoundSetting() {

    }

    @Override
    public void loadData() {
        musicDataModel.onLoadMusicInfoList(new IMusicDataModel.OnLoadDataListener<MusicInfo>() {
            @Override
            public void onLoadData(List<MusicInfo> dataList) {
                musicInfoList = dataList;
                refreshData();
            }
        }, MediaApplication.getCurrentUSB());
    }

    @Override
    public void refreshData() {
        if (isBindView()) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    mViewRef.get().refreshMusicData(musicInfoList);
                }
            });

        }

    }

    @Override
    public List<MusicInfo> getMusicData() {
        if (isBindView()) {
            if (musicInfoList != null) {
                return musicInfoList;
            }
        }
        return null;
    }

    @Override
    public void onPlayingListUrl(String url) {
        Logutil.i(TAG, "onPlayingListUrl: " + url);
        mProxyMusicPlayerModel.onListPlay(url);
        bindService();
    }

    @Override
    public void onPlayPosition(int position) {
        if (position >= 0 && position <= musicInfoList.size()) {
            String currentUrl = MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
            String playUrl = musicInfoList.get(position).getUrl();
            if (!playUrl.equals(currentUrl)) {
                mProxyMusicPlayerModel.onListPlay(playUrl);
            }
        }
    }

    @Override
    public void setSound() {
        AppUtil.startSoundSetting();
    }

    @Override
    public void playButtonGoneAndVisible(int state) {
        if (isBindView()) {
            _handler.removeCallbacks(mDelayChangePlayStateRunnable);
            mViewRef.get().onPlayShowState(View.VISIBLE);
            _handler.postDelayed(mDelayChangePlayStateRunnable, 3000);
        }

    }

    private Runnable mDelayChangePlayStateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBindView()) {
                mViewRef.get().onPlayShowState(View.GONE);
            }
        }
    };

    @Override
    public void reqShowLrc() {
        Log.i(TAG, "reqShowLrc() ..." + mViewRef.get().isAlbumViewVisible());
        if (mViewRef.get().isAlbumViewVisible()) {// 显示歌词UI
            mViewRef.get().onAlbumViewVisible(false);
            mViewRef.get().onLrcViewVisible(true);

            if (true/*!mViewRef.get().hasLyric()*/) {
                String lrcrl = mProxyMusicPlayerModel.getCurrentLyricUrl();
                Log.i(TAG, "reqShowLrc() lrc : " + lrcrl);
            LrcParserModel.getInstance().setOnLyricParseListener(mOnLyricParseListener);
            LrcParserModel.getInstance().parseLrc(lrcrl);
            }
        } else {// 显示专辑UI
            mViewRef.get().onAlbumViewVisible(true);
            mViewRef.get().onLrcViewVisible(false);
        }


    }

    private LrcParserModel.OnLyricParseListener mOnLyricParseListener = new LrcParserModel.OnLyricParseListener(){
        @Override
        public void onLyricNotExists() {
            updateLrc(null);
        }

        @Override
        public void onLyricParseFail() {
            updateLrc(null);
        }

        @Override
        public void onLyricParseSuccess(final List<LrcEntity> lrcList) {
            updateLrc(lrcList);
        }
    };

    private void updateLrc(final List<LrcEntity> lrcList){
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if(null != lrcList){
                    mViewRef.get().onChangedLyric(lrcList);
                }
            }
        });
    }
}
