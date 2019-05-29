package com.semisky.multimedia.media_video.presenter;


import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.interfaces.IBackModeChange;
import com.semisky.multimedia.common.interfaces.IBtCallStatus;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.FormatTimeUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.ScreenModeUtil;
import com.semisky.multimedia.media_list.MultimediaListManger;
import com.semisky.multimedia.media_video.bean.VideoPlayList;
import com.semisky.multimedia.media_video.manager.HandBrakeManager;
import com.semisky.multimedia.media_video.manager.VideoKeyManager;
import com.semisky.multimedia.media_video.manager.VideoStateManager;
import com.semisky.multimedia.media_video.model.IVideoDataModel;
import com.semisky.multimedia.media_video.model.VideoAudioFocusModel;
import com.semisky.multimedia.media_video.model.VideoDataModel;
import com.semisky.multimedia.media_video.view.IVideoPlayerView;
import com.semisky.multimedia.media_video.view.VideoListActivity;
import com.semisky.multimedia.media_video.view.VideoPlayerActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Anter on 2018/8/7.
 */

public class VideoPlayerPresenter<V extends IVideoPlayerView> extends BasePresenter<V> implements IVideoPlayerPresenter {
    private static final String TAG = Logutil.makeTagLog(VideoPlayerPresenter.class);
    private static final int CHANGE_PREV_PROGRAM_TIME_LIMIT_MAX_10S = 10999;

    private IVideoDataModel mVideoDataModel;// 视频媒体数据模型
    private VideoPlayList mVideoPlayList;// 视频播放列表

    private UpateProgressRunnable mUpateProgressRunnable;
    private PollingSaveInfoRunnable mPollingSaveInfoRunnable;
    private FastBackwardRunnable mFastBackwardRunnable;
    private FastForwardRunnable mFastForwardRunnable;
    private ProgramNextRunnable mProgramNextRunnable;
    private ProgramPrevRunnable mProgramPrevRunnable;

    private int mProgressWithDragging = 0;// 手动拖动进度
    private boolean mIsFullScreen = false;// 默认非全屏

    private boolean mStopTouchEventWhenNextProgram = true;// 当下一个节目时，禁止下一个按键控件的触摸事件
    private boolean mStopTouchEventWhenPrevProgram = true;// 当上一个节目时，禁止上一个按键控件的触摸事件
    private boolean mIsTrackingSeekBar = false;

    private int mUsbFlag = Definition.FLAG_USB1;


    public VideoPlayerPresenter() {
        this.mVideoDataModel = new VideoDataModel();
        this.mVideoDataModel.registerOnRefreshDataListener(mOnRefreshDataListener);
        this.mVideoPlayList = new VideoPlayList();
        this.mUpateProgressRunnable = new UpateProgressRunnable(this);
        this.mPollingSaveInfoRunnable = new PollingSaveInfoRunnable(this);
        VideoKeyManager.getInstance().onAttach(this);
        HandBrakeManager.getInstance().onAttach(this);
        SemiskyIVIManager.getInstance().registerBtStatusChanger(iBtCallStatus);
        SemiskyIVIManager.getInstance().registerBackModeChanged(iBackModeChange);
    }

    // MediaPlayer Listener
    @Override
    public OnPreparedListener getOnPreparedListener() {
        return this.mOnPreparedListener;
    }

    @Override
    public OnInfoListener getOnInfoListener() {
        return this.mOnInfoListener;
    }

    @Override
    public OnCompletionListener getOnCompletionListener() {
        return this.mOnCompletionListener;
    }

    @Override
    public OnErrorListener getOnErrorListener() {
        return this.mOnErrorListener;
    }

    @Override
    public OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return this.mOnSeekBarChangeListener;
    }

    @Override
    public OnSeekCompleteListener getOnSeekCompleteListener() {
        return this.mOnSeekCompleteListener;
    }

    private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (isBindView()) {
                int curProgress = mp.getCurrentPosition();
                mViewRef.get().onShowProgramProgress(curProgress);
                mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(curProgress));
            }
        }
    };

    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mProgressWithDragging = progress;
            if (fromUser) {
                mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (isPrepared()) {
                stopUpdateProgressRunnable();
                mIsTrackingSeekBar = true ;
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (isPrepared()) {
                Logutil.i(TAG, "onStopTrackingTouch() ..." + mProgressWithDragging);
                seekTo(mProgressWithDragging);
                startUpdateProgressRunnable();
                startVideo();
                mIsTrackingSeekBar = false ;
                startFullScreenModeTimeoutRunnable();
            }
        }
    };


    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Logutil.i(TAG, "onPrepared() ...");
            if (isBindView()) {
                int duration = mViewRef.get().getDuration();
                int curProgress = mViewRef.get().getCurrentPosition();
                mViewRef.get().onChangeSeekbarMaxProgress(duration);
                mViewRef.get().onShowProgramTotalTime(FormatTimeUtil.makeFormatTime(duration));
                mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(curProgress));
                PreferencesManager.saveLastVideoUrl(AppUtil.conversionUrlToUsbFlag(mVideoPlayList.getmCurrentPlayingUrl()),mVideoPlayList.getmCurrentPlayingUrl());
                startUpdateProgressRunnable();// 启动更新播放进度线程
                startPollingSaveInfoRunnable();// 启动播放媒体信息保存线程
                PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_VIDEO);
                MultimediaListManger.getInstance().setmPlayingUrlWithVideo(mVideoPlayList.getmCurrentPlayingUrl());
                MultimediaListManger.getInstance().notifyItemHighLightChange();
            }
        }
    };

    private OnInfoListener mOnInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Logutil.i(TAG, "onInfo() what=" + what + " , extra=" + extra);
            Logutil.i(TAG, "onInfo() curUrl=" + mVideoPlayList.getmCurrentPlayingUrl());
            switch (what) {
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    mViewRef.get().onChangePlaySwitchStateView(mViewRef.get().isPlaying());
                    mViewRef.get().onSwitchTransitionBlackView(false);
                    startFullScreenModeTimeoutRunnable();
                    break;
            }
            return false;
        }
    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Logutil.i(TAG, "onCompletion() ...");
            stopUpdateProgressRunnable();// 停止更新播放进度线程
            stopPollingSaveInfoRunnable();// 停止播放媒体信息保存线程
            //todo
//            if (!USBManager.getInstance().isFirstUsbMounted()) {
//                Logutil.e(TAG, "onCompletion() USB UNMOUNTED !!!");
//                return;
//            }
            onNextProgram();
            mViewRef.get().onShowProgramProgress(0);
            mViewRef.get().onSwitchTransitionBlackView(true);
            mViewRef.get().onChangePlaySwitchStateView(false);
            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(0));

        }
    };

    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logutil.i(TAG, "onError() what=" + what + " , extra=" + extra);
            // TODO: 2019/4/28  
//            if (!USBManager.getInstance().isFirstUsbMounted()) {
//                Logutil.e(TAG, "onCompletion() USB UNMOUNTED !!!");
//                return false;
//            }
            mViewRef.get().onShowProgramProgress(0);
            mViewRef.get().onSwitchTransitionBlackView(true);// 显示黑色画布
            mViewRef.get().onChangePlaySwitchStateView(false);// 显示播放按键（播放器是暂停的）
            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(0));
            mViewRef.get().onSwitchPlayVideoExceptionWarningView(true);

            PreferencesManager.saveLastVideoUrl(AppUtil.conversionUrlToUsbFlag(mVideoPlayList.getmCurrentPlayingUrl()),mVideoPlayList.getmCurrentPlayingUrl());
//            PreferencesManager.saveLastVideoProgress(0);
            stopUpdateProgressRunnable();// 停止更新播放进度线程
            stopPollingSaveInfoRunnable();// 停止播放媒体信息保存线程
            removeFullScreenModeTimeoutRunnable();// 清除超时全屏线程
            onChangeNoramlScreenMode();// 播放异常切换到非全屏
            if (mVideoPlayList.hasNext(VideoPlayList.CHANGE_NEXT_WITH_PLAY_EXCEPTION)) {
                startProgramNextRunnable(ProgramNextRunnable.DELAY_TIME_5S);
            }
            mVideoPlayList.addDamageUrlToList(mVideoPlayList.getmCurrentPlayingUrl());
            return false;
        }
    };

    private OnAudioFocusChangeListener mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logutil.i(TAG, "onAudioFocusChange() ..." + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Logutil.i(TAG, "Video onAudioFocusChange() AUDIOFOCUS_GAIN...");
                    if (mViewRef ==null || mViewRef.get() == null){
                        return;
                    }
                    //获得焦点
                    mViewRef.get().setIsHasAudioFocus(true);
                    //如果不在前台，禁止恢复播放
                    boolean isTop = AppUtil.getActivityIsTop(MediaApplication.getContext(),VideoPlayerActivity.class.getName());
                    if (!isTop){
                        Logutil.i(TAG,"stop play video");
                        return;
                    }
                    //获得焦点,并且不是手动暂停，才能恢复播放
                    if (!mViewRef.get().isPlaying() && (!VideoStateManager.getInstance().getVideoIsClickStop() || VideoStateManager.getInstance().getVideoIsLossFocus())) {
                        restorePlay();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Logutil.i(TAG, "Video onAudioFocusChange() AUDIOFOCUS_LOSS...");
                    //永久丢失焦点
                    VideoStateManager.getInstance().setAudioFocus_loss(true);
//                    if (mViewRef.get().isPlaying()){
//                        notifyMediaPlayStateChange(false);
//                        onPauseUpdateProgress();
//                        pauseVideo();
//                    }
                    onStopKeyEventEnable(true);
                    onStopHandBrakeEventEnable(true);
                    unRegisterAudioFocusChange();
                    VideoStateManager.getInstance().setVideoIsClickStop(false);
                    onAbandonAudioFocus();
                    // TODO: 2019/4/28
//                    MediaApplication.finishActivity(VideoPlayerActivity.class);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Logutil.i(TAG, "Video onAudioFocusChange() AUDIOFOCUS_LOSS_TRANSIENT...");
                    //短暂丢失焦点，和长焦点丢失同样处理，本质上都为丢失焦点，一个需要释放，一个不需要释放
                    if (mViewRef == null || mViewRef.get() == null){
                        //音频焦点未释放，mViewRef 可能为空
                        return;
                    }

                    mViewRef.get().setIsHasAudioFocus(false);
                    if (mViewRef.get().isPlaying()){
                        notifyMediaPlayStateChange(false);
                        pauseVideo();
                        onPauseUpdateProgress();
                    }

                    onStopKeyEventEnable(true);
                    onStopHandBrakeEventEnable(true);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Logutil.i(TAG, "Video onAudioFocusChange() AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK...");
                    //todo
                    break;
            }
        }
    };

    @Override
    public void onHandlerIntent(Intent intent) {
        Logutil.i(TAG, "onHandlerIntent() ...");
        //获取车机手刹状态
        if (!SemiskyIVIManager.getInstance().getHandBrakeState()){
            //非手刹状态，显示弹窗警告
            mViewRef.get().onSwitchWatchVideoWarningView(true);
        }
        if (null != intent) {
            String url = intent.getStringExtra("url");
            Logutil.i(TAG, "onHandlerIntent() ..." + url);

            // 检查是否有媒体数据
            if (!mVideoPlayList.hasData()) {
                onLoadData();
            }

            // 列表播放,当前url和最后一次保存的url不一样时，才允许 progress=0 的状态 播放
            if (null != url && !url.equals(PreferencesManager.getLastVideoUrl(PreferencesManager.getLastVideoSourceUsbFlag()))) {
                if (canListPlay(url)) {
                    VideoStateManager.getInstance().setVideoIsClickStop(false);
//                    mViewRef.get().setIsNeedPlay(false);
                    stopProgramNextRunnable(); //移除下一个节目事件。异常视频会延迟播放，点击列表播放，可能造成不能播放选择的视频
                    playVideo(url, 0, true);
                }
            } else {
                // 断点记忆播放
                onRestorePlayVideo();
            }
        }// end >> (null != intent)

    }

    // 是否可以列表播放
    private boolean canListPlay(String url) {
        String curPlayingUrl = mVideoPlayList.getmCurrentPlayingUrl();
        boolean isSameWithUrl = (null != url && null != curPlayingUrl && url.equals(curPlayingUrl));
        boolean hasAudioFocus = VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).hasAudioFocus();
        boolean isPrepared = mViewRef.get().isPrepared();
        Logutil.i(TAG, "canListPlay() curPlayingUrl=" + (null != curPlayingUrl ? curPlayingUrl : "null"));
        Logutil.i(TAG, "canListPlay() preparePlayUrl=" + (null != url ? url : "null"));
        Logutil.i(TAG, "canListPlay() hasAudioFocus=" + hasAudioFocus);
        Logutil.i(TAG, "canListPlay() isPrepared=" + isPrepared);
        if (!hasAudioFocus || !isPrepared || !isSameWithUrl) {
            return true;
        }
        return false;
    }

    @Override
    public void onLoadData() {
        if (isBindView()) {
            mVideoDataModel.onLoadVideoInfoList(new IVideoDataModel.OnLoadDataListener<VideoInfo>() {
                @Override
                public void onLoadData(List<VideoInfo> dataList) {
                    if(!isBindView()){
                        Logutil.i(TAG,"isBindView false");
                        return;
                    }
                    mVideoPlayList.addVideoList(dataList);
                    mVideoPlayList.refreshCurPlayUrlPos();

                }
            },mUsbFlag);
        }
    }

    private IVideoDataModel.OnRefreshDataListener mOnRefreshDataListener = new IVideoDataModel.OnRefreshDataListener() {
        @Override
        public void onUpdateData(List<VideoInfo> dataList) {
            mVideoPlayList.addVideoList(dataList);
            mVideoPlayList.refreshCurPlayUrlPos();
        }
    };

    @Override
    public void onRestorePlayVideo() {
        Logutil.i(TAG, "onRestorePlayVideo() ...");
        String lastUrl = PreferencesManager.getLastVideoUrl(PreferencesManager.getLastVideoSourceUsbFlag());
        int lastProgress = PreferencesManager.getLastVideoProgress();
        boolean isExsistLastUrl = false;

        if (null != lastUrl) {
            isExsistLastUrl = new File(lastUrl).exists();
        }

        Logutil.i(TAG, "#######");
        Logutil.i(TAG, "onRestorePlayVideo() lastUrl=" + lastUrl);
        Logutil.i(TAG, "onRestorePlayVideo() lastProgress=" + lastProgress);
        Logutil.i(TAG, "onRestorePlayVideo() isExsistLastUrl=" + isExsistLastUrl);
        Logutil.i(TAG, "#######");

        if (isExsistLastUrl) {
            playVideo(lastUrl, lastProgress, true);//
        }

    }

    @Override
    public void onPrevProgram() {
        Logutil.i(TAG, "onPrevProgram() ...");
        VideoStateManager.getInstance().setVideoIsClickStop(false);
        if (canPrev()) {
            mViewRef.get().closeDialog();
            stopFastForwardRunnable(false);
            stopFastBackwardRunnable(false);
            stopProgramNextRunnable();
            startProgramPrevRunnable();
        } else {
            rePlay();
        }
    }

    private void programPrev() {
        if (isBindView() && mVideoPlayList.hasData()) {
            startFullScreenModeTimeoutRunnable();
            VideoInfo prev = mVideoPlayList.prev();
            playVideo(prev.getFileUrl(), 0, true);
        }
    }

    // 是否可以上一曲
    private boolean canPrev() {
        //bug 视频播放中，不管大于十秒还是小于十秒，短按上一曲都应该响应。（测试部用例需求）
        return true;
//        if (mViewRef.get().isPrepared() && getCurrentProgress() < CHANGE_PREV_PROGRAM_TIME_LIMIT_MAX_10S) {
//            return true;
//        }
//        return false;
    }

    // 重新开始播放
    private void rePlay() {
        if (mViewRef.get().isPrepared()) {
            seekTo(0);
        }
    }

    @Override
    public void onNextProgram() {
        Logutil.i(TAG, "onNextProgram() ...");
        VideoStateManager.getInstance().setVideoIsClickStop(false);
//        mViewRef.get().setIsNeedPlay(false);//点击下一曲时，手动暂停状态需失效（）
        mViewRef.get().closeDialog();
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopFastForwardRunnable(false);
                stopFastBackwardRunnable(false);
            }
        },400);
        stopProgramPrevRunnable();
        startProgramNextRunnable(ProgramNextRunnable.DELAY_TIME_350MS);
    }

    private void programNext() {
        if (isBindView() && mVideoPlayList.hasData()) {
            startFullScreenModeTimeoutRunnable();
            VideoInfo next = mVideoPlayList.next();
            playVideo(next.getFileUrl(), 0, true);
        }
    }


    @Override
    public void onFastBackward() {
        if (isBindView()) {
            if (isFastBackwardRunnableRunning()) {
                Logutil.w(TAG, "onFastBackward() FastBackwardRunnable Running !!!");
                return;
            } else if (isFastForwardRunnableRunning()) {
                Logutil.w(TAG, "onFastForward() FastForwardRunnable Running !!!");
                return;
            }
            startFastBackwardRunnable();
        }
    }

    @Override
    public void onStopFastBackward() {
        if (isBindView()) {
            if (isFastForwardRunnableRunning()) {
                Logutil.w(TAG, "onFastForward() FastForwardRunnable Running !!!");
                return;
            }
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    stopFastBackwardRunnable(false);
                }
            });
            startFullScreenModeTimeoutRunnable();
        }
    }

    @Override
    public void onFastForward() {
        if (isBindView()) {
            if (isFastBackwardRunnableRunning()) {
                Logutil.w(TAG, "onFastBackward() FastBackwardRunnable Running !!!");
                return;
            } else if (isFastForwardRunnableRunning()) {
                Logutil.w(TAG, "onFastForward() FastForwardRunnable Running !!!");
                return;
            }
            startFastForwardRunnable();
        }
    }

    @Override
    public void onStopFastForward() {
        if (isBindView()) {
            if (isFastBackwardRunnableRunning()) {
                Logutil.w(TAG, "onStopFastForward() FastBackwardRunnable Running !!!");
                return;
            }
            lastPosition = 0;
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    stopFastForwardRunnable(false);
                }
            });
            startFullScreenModeTimeoutRunnable();
        }
    }

    @Override
    public void onSwitchPlayOrPause() {
        if (isBindView()) {
            if (mViewRef.get().isPrepared()) {
                startFullScreenModeTimeoutRunnable();
                if (mViewRef.get().isPlaying()) {
                    VideoStateManager.getInstance().setVideoIsClickStop(true);
                    pauseVideo();
                } else {
                    startVideo();
                }
            }
        }
    }

    @Override
    public void onEnterList() {
        if (isBindView()) {
            Log.i(TAG,"onEnterList() ...");
            removeFullScreenModeTimeoutRunnable();
            Intent it = new Intent();
            it.setClass(MediaApplication.getContext(),VideoListActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MediaApplication.getContext().startActivity(it);
        }
    }

    @Override
    public void onSaveLastMediaInfos() {
        Logutil.i(TAG, "onSaveLastMediaInfos() ...");
        if (isBindView() && isPrepared()) {
            saveLastMediaInfo();
        }
    }

    @Override
    public void onPauseUpdateProgress() {
        Logutil.i(TAG, "onPauseUpdateProgress() ...");
        stopUpdateProgressRunnable();
        stopPollingSaveInfoRunnable();
    }

    @Override
    public void registerAudioFocusChange() {
        if (isBindView()) {
            Logutil.i(TAG, "registerAudioFocusChange() ...");
            VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).registerAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    @Override
    public void unRegisterAudioFocusChange() {
        if (isBindView()) {
            Logutil.i(TAG, "unRegisterAudioFocusChange() ...");
            VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).unregisterAudioFocus();
        }
    }

    @Override
    public void onRequestAudioFocus() {
        if (isBindView()) {
            if (SemiskyIVIManager.getInstance().isHighPriorityAppRunning()){
                return;
            }
            VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).onRequestAudioFocus();
        }
    }

    @Override
    public void onAbandonAudioFocus() {
//        if (isBindView()) {
            VideoAudioFocusModel.getInstance(MediaApplication.getContext()).onAbandonAudioFocus();
//        }
    }


    @Override
    public void onSwitchScreentMode() {
        Log.i(TAG,"onSwitchScreentMode() ..."+mIsFullScreen);
        if (isBindView() /*&& mVideoPlayList.hasData()*/) {
            if (mIsFullScreen) {// 切换到非全屏
                onChangeNoramlScreenMode();
                startFullScreenModeTimeoutRunnable();// 启动超时全屏线程
            } else {// 切换到全屏
                onChangeFullScreenMode();
            }
        }
    }

    @Override
    public void onStopKeyEventEnable(boolean enable) {
        VideoKeyManager.getInstance().setStopKeyEvent(enable);
    }

    @Override
    public void onHandBrakeChange(final boolean isDriving) {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    mViewRef.get().onSwitchWatchVideoWarningView(isDriving);
                }
            }
        });

    }

    @Override
    public void onStopHandBrakeEventEnable(boolean enable) {
        HandBrakeManager.getInstance().setStopHandBrakeEveventEnable(enable);
    }

    // 切换到非全屏显示模式
    private void onChangeNoramlScreenMode() {
        if (isBindView()) {
            mIsFullScreen = false;
            mViewRef.get().onChangeScreenMode(mIsFullScreen);
            ScreenModeUtil.changeNormalScreenMode(mViewRef.get().getContext());
            SemiskyIVIManager.getInstance().showBottomBar();
        }
    }

    // 切换到全屏显示模式
    private void onChangeFullScreenMode() {
        if (isBindView()) {
            if ((mFastBackwardRunnable != null && mFastBackwardRunnable.isRunning()) ||
                    (mFastForwardRunnable !=null && mFastForwardRunnable .isRunning()) || mIsTrackingSeekBar){
                Log.w(TAG,"onChangeFullScreenMode() STOP ...");
                return;
            }
            mIsFullScreen = true;
            mViewRef.get().onChangeScreenMode(mIsFullScreen);
            ScreenModeUtil.chanageFullScreenMode(mViewRef.get().getContext());
            SemiskyIVIManager.getInstance().dismissBottomBar();
        }
    }

    // 清除延时全屏线程
    @Override
    public void removeFullScreenModeTimeoutRunnable() {
        _handler.removeCallbacks(mDelayChangeFullScreenModeRunnable);
    }

    // 延时全屏模式显示（延时5s）
    private void startFullScreenModeTimeoutRunnable() {
        _handler.removeCallbacks(mDelayChangeFullScreenModeRunnable);
        _handler.postDelayed(mDelayChangeFullScreenModeRunnable, 5000);
    }

    // 延时全屏
    private Runnable mDelayChangeFullScreenModeRunnable = new Runnable() {
        @Override
        public void run() {
            onChangeFullScreenMode();
        }
    };

    @Override
    public void setTitleToStatusBar(String clz, String title) {
        SemiskyIVIManager.getInstance().setAppStatus(clz, title, AutoConstants.AppStatus.RUN_FOREGROUND);
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
    public void btnBack() {
      //  AppUtil.enterList(Definition.MediaListConst.FRAGMENT_LIST_VIDEO);
        // TODO: 2019/4/28  
//        _handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MediaApplication.finishActivity(VideoPlayerActivity.class);
//                mViewRef.get().onSwitchWatchVideoWarningView(false);
//            }
//        },300);

    }

    @Override
    public void stopPlayVideo() {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                pauseVideo(true);
            }
        });

    }

    @Override
    public void finishDelayed() {
        SemiskyIVIManager.getInstance().unRegisterBtStatusChange(iBtCallStatus);
        SemiskyIVIManager.getInstance().unRegisterBackModeChanged(iBackModeChange);
    }

    @Override
    public void stopFastOrBackTask(boolean isH) {
        if (isH){
             onMuteVolumeEnable(false);//快进时会静音 蓝牙电话，倒车，取消掉静音
            if (null != mFastBackwardRunnable && mFastBackwardRunnable.isRunning()) {
                this.mFastBackwardRunnable.stopRunning();
                this._handler.removeCallbacks(mFastBackwardRunnable);
            }
            if (null != mFastForwardRunnable && mFastForwardRunnable.isRunning()) {
                Logutil.i(TAG, "stopFastForwardRunnable() ...");
                this.mFastForwardRunnable.stopRunning();
                this._handler.removeCallbacks(mFastForwardRunnable);
            }
        }else {
            onStopFastBackward();
            onStopFastForward();
        }

    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        Logutil.i(TAG, "onDetachView() ...");
        VideoKeyManager.getInstance().onDetach();
        HandBrakeManager.getInstance().onDetach();
        _handler.removeCallbacksAndMessages(null);
        mVideoPlayList.onDestory();
        mVideoDataModel.unregisterOnRefreshDataListener();
    }


    // utils

    // 显示观看视频警示视图


    // 设置媒体静音状态
    private void onMuteVolumeEnable(boolean isMute) {
        Logutil.i(TAG, "onMuteVolumeEnable() isMute=" + isMute);
        if (isBindView()) {
            mViewRef.get().onMuteVolumeEnable(isMute);
        }
    }

    // 通知媒体播放器播放状态改变
    private void notifyMediaPlayStateChange(boolean isPlaying) {
        if (isBindView()) {
            mViewRef.get().onChangePlaySwitchStateView(isPlaying);
        }
    }

    // 当前播放进度
    private int getCurrentProgress() {
        if (isBindView()) {
            return mViewRef.get().getCurrentPosition();
        }
        return 0;
    }

    // 媒体总进度
    private int getDuration() {
        if (isBindView()) {
            return mViewRef.get().getDuration();
        }
        return 0;
    }

    // 准备播放媒体资源
    private void playVideo(String url, int progress, boolean isAutoPlay) {
        MultimediaListManger.getInstance().setmPlayingUrlWithVideo(url); //点击异常视频时不会高亮显示
        mVideoPlayList.setmCurrentPlayingUrl(url);
        mVideoPlayList.refreshCurPlayUrlPos();
        mViewRef.get().onPlayVideo(url, progress, isAutoPlay);
    }

    // 开始播放视频
    private void startVideo() {
        if (isBindView() && isPrepared()) {
            if (!mViewRef.get().isPlaying()) {
                notifyMediaPlayStateChange(true);
                mViewRef.get().onStartVideo();
                Logutil.i(TAG, "startVideo()..............");
            }
        }
    }

    // 暂停播放视频
    public void pauseVideo() {
        if (isBindView() && isPrepared()) {
            mViewRef.get().onPauseVideo();
            mViewRef.get().onChangePlaySwitchStateView(false);
        }
    }
    //暂停播放视频。bug,上一曲快进时，到最后的位置会重复播放下第一次进来的界面
    public void pauseVideo(boolean NoChangeStatus){
        //如果是播放状态快进，只是程序内部暂停，这个时候不需要改变显示暂停的状态
        if (isBindView() && isPrepared()) {
            mViewRef.get().onPauseVideo();
        }
    }

    // 设置指定播放位置
    private void seekTo(int progress) {
        if (isBindView()) {
            if (isPrepared()) {
                mViewRef.get().onSeekTo(progress);
            }
        }
    }

    // 视频资源是否准备完成
    private boolean isPrepared() {
        boolean isPrepared = false;
        if (isBindView()) {
            isPrepared = mViewRef.get().isPrepared();
        }
        Logutil.i(TAG, "isPrepared() ..." + isPrepared);
        return isPrepared;
    }

    // 更新播放进度
    private void updateProgress() {
        if (isBindView()) {
            int curProgress = mViewRef.get().getCurrentPosition();
            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(curProgress));
            mViewRef.get().onShowProgramProgress(curProgress);
        }
    }
    //----------------------------------------------------------------------------------------------

    private void startProgramPrevRunnable() {
        if (null == mProgramPrevRunnable) {
            mProgramPrevRunnable = new ProgramPrevRunnable(this);
        }

        _handler.removeCallbacks(mProgramPrevRunnable);
        _handler.postDelayed(mProgramPrevRunnable, ProgramNextRunnable.DELAY_TIME_500MS);
    }

    private void stopProgramPrevRunnable() {
        if (null != mProgramPrevRunnable) {
            _handler.removeCallbacks(mProgramPrevRunnable);
        }
    }

    private static class ProgramPrevRunnable implements Runnable {
        protected static final int DELAY_TIME_500MS = 500;
        WeakReference<VideoPlayerPresenter> mPlayerRfr;

        public ProgramPrevRunnable(VideoPlayerPresenter player) {
            mPlayerRfr = new WeakReference<VideoPlayerPresenter>(player);
        }

        private boolean mIsRunning = false;

        public boolean isRunning() {
            return this.mIsRunning;
        }

        @Override
        public void run() {
            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                this.mIsRunning = false;
                return;
            }
            this.mIsRunning = true;
            mPlayerRfr.get().programPrev();
            this.mIsRunning = false;
        }
    }

    //----------------------------------------------------------------------------------------------
    private void startProgramNextRunnable(int delayMillis) {
        if (null == mProgramNextRunnable) {
            mProgramNextRunnable = new ProgramNextRunnable(this);
        }

        _handler.removeCallbacks(mProgramNextRunnable);
        _handler.postDelayed(mProgramNextRunnable, delayMillis);
    }

    private void stopProgramNextRunnable() {
        if (null != mProgramNextRunnable) {
            _handler.removeCallbacks(mProgramNextRunnable);
        }
    }

    private static class ProgramNextRunnable implements Runnable {
        protected static final int DELAY_TIME_350MS = 350;
        protected static final int DELAY_TIME_500MS = 500;
        protected static final int DELAY_TIME_5S = 5000;
        WeakReference<VideoPlayerPresenter> mPlayerRfr;

        public ProgramNextRunnable(VideoPlayerPresenter player) {
            mPlayerRfr = new WeakReference<VideoPlayerPresenter>(player);
        }

        private boolean mIsRunning = false;

        public boolean isRunning() {
            return this.mIsRunning;
        }

        @Override
        public void run() {
            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                this.mIsRunning = false;
                return;
            }
            this.mIsRunning = true;
            mPlayerRfr.get().programNext();
            this.mIsRunning = false;
        }
    }
    //----------------------------------------------------------------------------------------------

    // 启动更新播放进度线程
    private void startUpdateProgressRunnable() {
        Logutil.i(TAG, "startUpdateProgressRunnable() ...");
        if (null != mUpateProgressRunnable) {
            _handler.removeCallbacks(mUpateProgressRunnable);
            mUpateProgressRunnable.prepare();
            _handler.post(mUpateProgressRunnable);
        }
    }

    // 停止更新播放进度线程
    private void stopUpdateProgressRunnable() {
        Logutil.i(TAG, "stopUpdateProgressRunnable() ...");
        if (null != mUpateProgressRunnable) {
            mUpateProgressRunnable.stop();
            _handler.removeCallbacks(mUpateProgressRunnable);
        }
    }

    // 更新播放进度线程
    private static class UpateProgressRunnable implements Runnable {
        WeakReference<VideoPlayerPresenter> mRfr;
        private boolean mIsStop = false;

        public UpateProgressRunnable(VideoPlayerPresenter mRfr) {
            this.mRfr = new WeakReference<VideoPlayerPresenter>(mRfr);
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
                Logutil.i(TAG, "UpateProgressRunnable stop run ...");
                return;
            }

            if (!mIsStop) {
                mRfr.get()._handler.postDelayed(this, 500);
                mRfr.get().updateProgress();
            }
        }
    }

    // 保存断点媒体信息
    private void saveLastMediaInfo() {
        //快速切换的时候，可能造成position = 0
        if(mViewRef.get().getCurrentPosition() != 0){
            PreferencesManager.saveLastVideoProgress(mViewRef.get().getCurrentPosition());
        }
    }

    // 启动播放媒体信息保存线程
    private void startPollingSaveInfoRunnable() {
        if (null != mPollingSaveInfoRunnable) {
            _handler.removeCallbacks(mPollingSaveInfoRunnable);
            mPollingSaveInfoRunnable.prepare();
            _handler.post(mPollingSaveInfoRunnable);
        }
    }

    // 停止播放媒体信息保存线程
    private void stopPollingSaveInfoRunnable() {
        if (null != mPollingSaveInfoRunnable) {
            mPollingSaveInfoRunnable.stop();
            _handler.removeCallbacks(mPollingSaveInfoRunnable);
        }
    }

    // 更新播放进度线程
    private static class PollingSaveInfoRunnable implements Runnable {
        WeakReference<VideoPlayerPresenter> mRfr;
        private boolean mIsStop = false;

        public PollingSaveInfoRunnable(VideoPlayerPresenter mRfr) {
            this.mRfr = new WeakReference<VideoPlayerPresenter>(mRfr);
        }

        public void prepare() {
            mIsStop = false;
        }

        public void stop() {
            mIsStop = true;
            mRfr.get().saveLastMediaInfo();
        }

        @Override
        public void run() {
            if (null == mRfr || null == mRfr.get()) {
                Logutil.i(TAG, "PollingSaveInfoRunnable stop run ...");
                return;
            }

            if (!mIsStop) {
                mRfr.get()._handler.postDelayed(this, 1000);
                mRfr.get().saveLastMediaInfo();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
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
            Logutil.i(TAG, "stopFastForwardRunnable() ...");
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
        private WeakReference<VideoPlayerPresenter> mPlayerRfr;
        // 是否停止快进 true:停止 ，false:不停止
        private volatile boolean mIsStopFastForward = true;
        private volatile boolean mIsRunning = false;
        private volatile boolean mFromAudioFocsLossEvent = false;
        // 单次10s快进
        private static final int STEP_UNIT_10S = 10000;// 10s=10,000ms
        private static final int DELAY_TIME_1S = 1000;


        public FastForwardRunnable(VideoPlayerPresenter player) {
            this.mPlayerRfr = new WeakReference<VideoPlayerPresenter>(player);
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
            mIsRunning = false;
            mIsStopFastForward = true;
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
                int steppingProgress = 0;
                if (mPlayerRfr.get().getForwardOrBackPosition()){
                     steppingProgress = curProgress + STEP_UNIT_10S;
                }else {
                     steppingProgress = curProgress + STEP_UNIT_10S * 2;
                }

                // 步进进度大于总进度时：1.直接设置总进度 2.结束快进操作
                if (steppingProgress >= totalProgress) {
                    steppingProgress = totalProgress;
                    // 禁止线程运行标识
                    this.mIsStopFastForward = true;
                    mPlayerRfr.get().onNextProgram();
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
            Logutil.i(TAG, "checkIsRestorePlay() mFromAudioFocsLossEvent=" + mFromAudioFocsLossEvent);

            if (mPlayerRfr.get().isPrepared()) {
                Logutil.i(TAG, "checkIsRestorePlay() 00");
                mPlayerRfr.get().onMuteVolumeEnable(false);
                if (!mFromAudioFocsLossEvent) {
                    Logutil.i(TAG, "checkIsRestorePlay() 0000");
                    mPlayerRfr.get().startUpdateProgressRunnable();
                    mPlayerRfr.get().startVideo();
                }
                this.mFromAudioFocsLossEvent = false;
            }
        }
    }

    //----------------------------------------------------------------------------------------------

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
        private WeakReference<VideoPlayerPresenter> mPlayerRfr;
        // 是否停止快退 true:停止 ，false:不停止
        private volatile boolean mIsStopFastBackward = true;
        private volatile boolean mIsRunning = false;
        private volatile boolean mFromAudioFocsLossEvent = false;
        // 单次10s快退
        private static final int STEP_UNIT_10S = 10000;// 10s=10,000ms
        private static final int DELAY_TIME_1S = 1000;
        private static final int MIN_PROGRESS = 0;


        public FastBackwardRunnable(VideoPlayerPresenter player) {
            this.mPlayerRfr = new WeakReference<VideoPlayerPresenter>(player);
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
            mIsRunning = false;
            mIsStopFastBackward = true;
        }

        @Override
        public void run() {
            this.mIsRunning = true;
            if (null == mPlayerRfr || null == mPlayerRfr.get()) {
                Logutil.w(TAG, "FastBackwardRunnable.run() null == mPlayerRfr !!!");
                return;
            }
            // 停止快退
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

                mPlayerRfr.get().onMuteVolumeEnable(false);
                if (!mFromAudioFocsLossEvent) {
                    mPlayerRfr.get().startVideo();
                    mPlayerRfr.get().startUpdateProgressRunnable();
                }
                this.mFromAudioFocsLossEvent = false;
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    /**
     * 判断是否能正常的快进
     * 某些特别视频的原因不能正常快进
     */
    int lastPosition = 0 ;
    private boolean getForwardOrBackPosition(){
        int curProgress = getCurrentProgress();
        boolean isCanSeekForwardOrBack = true;
        //上次快进的位置加上3秒后大于当前的位置时，视为不能正常快进
        if (lastPosition + 3 * 1000 >= curProgress && lastPosition != 0) {
            isCanSeekForwardOrBack = false;
        } else {
            isCanSeekForwardOrBack = true;
        }
        lastPosition = curProgress;
        return isCanSeekForwardOrBack;
    }
    /**
     *蓝牙状态监听
     * bug 8770 音频焦点再次获取到时差存在两秒
     */
    IBtCallStatus iBtCallStatus = new IBtCallStatus() {
        @Override
        public void btStateChange(int state) {
            //挂断电话。bug
            if (state == 7){
                Logutil.i("lcc","videoPresenter btCallOver ");
                //如果在前台
                boolean isTop = AppUtil.getActivityIsTop(MediaApplication.getContext(),VideoPlayerActivity.class.getName());
                if (isTop && !VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).hasAudioFocus() ){
                    Logutil.i("lcc","btCallOver requestAudioFocus");
                    VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).onRequestAudioFocus();
                    //没有播放，并且不是手动暂停
                    if (!VideoStateManager.getInstance().getVideoIsClickStop() && !mViewRef.get().isPlaying()){
                        _handler.post(new Runnable() {
                            @Override
                            public void run() {
                                restorePlay();
                            }
                        });

                    }
                }
            }else {
                //如果在前台
                boolean isTop = AppUtil.getActivityIsTop(MediaApplication.getContext(),VideoPlayerActivity.class.getName());
                if (isTop){
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopFastOrBackTask(true);
                        }
                    });
                }
            }

        }
    };

    /**+
     * 恢复播放
     */
    private synchronized void restorePlay(){
        VideoStateManager.getInstance().setAudioFocus_loss(false);
        notifyMediaPlayStateChange(true);
        startUpdateProgressRunnable();// 启动更新播放进度线程
        startPollingSaveInfoRunnable();// 启动播放媒体信息保存线程
        onStopKeyEventEnable(false);
        onStopHandBrakeEventEnable(false);
        if (!mViewRef.get().isPlaying()){
            startVideo();
        }

    }
    private IBackModeChange iBackModeChange = new IBackModeChange() {
        @Override
        public void backModeChanged(final boolean state) {
            if (!state){
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean isTop = AppUtil.getActivityIsTop(MediaApplication.getContext(),VideoPlayerActivity.class.getName());
                        Logutil.i("lcc","backMode over isTop "+isTop);
                        Logutil.i("lcc","backMode hasAudioFocus "+VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).hasAudioFocus());
                        if (isTop && !VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).hasAudioFocus() ){
                            VideoAudioFocusModel.getInstance(mViewRef.get().getContext()).onRequestAudioFocus();
                            //没有播放，并且不是手动暂停
                            if (!VideoStateManager.getInstance().getVideoIsClickStop() && !mViewRef.get().isPlaying()){
                                Logutil.i("lcc","back mode end ,restore playVideo");
                                restorePlay();
                            }
                        }
                    }
                });
            }else {
                //如果在前台
                boolean isTop = AppUtil.getActivityIsTop(MediaApplication.getContext(),VideoPlayerActivity.class.getName());
                if (isTop){
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopFastOrBackTask(true);
                        }
                    });
                }
            }
        }
    };

}
