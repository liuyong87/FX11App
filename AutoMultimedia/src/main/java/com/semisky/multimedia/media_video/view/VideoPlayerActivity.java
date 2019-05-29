package com.semisky.multimedia.media_video.view;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.semisky.multimedia.FactoryTestManager;
import com.semisky.multimedia.R;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.base_view.ToastCustomHint;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_video.videoplayer.VideoSurfaceView;

import java.io.FileDescriptor;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by LiuYong on 2018/8/7.
 */

public class VideoPlayerActivity extends TemplateVideoPlayerActivity implements
        OnClickListener,
        OnLongClickListener,
        OnTouchListener ,FactoryTestManager.OnFactoryTestVideoCommandListener{
    private static final String TAG = Logutil.makeTagLog(VideoPlayerActivity.class);

    private VideoSurfaceView mVideoSurfaceView;
    private ImageView iv_transition_view,
            iv_list,
            iv_prev,
            iv_switch,
            iv_next;
    private TextView tv_curTime,
            tv_totalTime;
    private SeekBar sb_video;
    private View videoBottombar, drivingVideoWarningView;
    private Button btnBack;


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.registerAudioFocusChange();
        mPresenter.onRequestAudioFocus();
        mPresenter.onStopKeyEventEnable(false);
        String title = getString(R.string.status_bar_video_title_text);
        mPresenter.setTitleToStatusBar(this.getClass().getName(), title);
        mPresenter.onStopHandBrakeEventEnable(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBindPresenter()) {
            if (mVideoSurfaceView.isPlaying()){
                mPresenter.pauseVideo();
            }
            mPresenter.onPauseUpdateProgress();
            mPresenter.onStopKeyEventEnable(true);
            mPresenter.onStopHandBrakeEventEnable(true);
            mPresenter.stopFastOrBackTask(true);
            closeDialog();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBindPresenter()){
            Log.i(TAG,"onStop() ...");
            mPresenter.removeFullScreenModeTimeoutRunnable();
        }
    }

    @Override
    protected void onDestroy() {
        if (isBindPresenter()) {
//            mPresenter.unRegisterAudioFocusChange();
//            mPresenter.onAbandonAudioFocus();
        }
        mPresenter.finishDelayed();
        FactoryTestManager.getInstances().onRegisterVideoCmmandListener();// 工厂测试
        super.onDestroy();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initViews() {
        this.mVideoSurfaceView = (VideoSurfaceView) findViewById(R.id.videoSurfaceView);
        this.iv_transition_view = (ImageView) findViewById(R.id.iv_transition_view);
        this.tv_curTime = (TextView) findViewById(R.id.tv_curTime);
        this.tv_totalTime = (TextView) findViewById(R.id.tv_totalTime);
        this.sb_video = (SeekBar) findViewById(R.id.sb_video);
        this.iv_list = (ImageView) findViewById(R.id.iv_list);
        this.iv_prev = (ImageView) findViewById(R.id.iv_prev);
        this.iv_switch = (ImageView) findViewById(R.id.iv_switch);
        this.iv_next = (ImageView) findViewById(R.id.iv_next);
        this.videoBottombar = findViewById(R.id.include_layout_bottombar);
        this.drivingVideoWarningView = findViewById(R.id.include_driving_video_warning_view);
        this.btnBack = (Button) findViewById(R.id.btn_back);
    }

    @Override
    protected void setListener() {
        this.mVideoSurfaceView.setOnCompletionListener(mPresenter.getOnCompletionListener());
        this.mVideoSurfaceView.setOnPreparedListener(mPresenter.getOnPreparedListener());
        this.mVideoSurfaceView.setOnInfoListener(mPresenter.getOnInfoListener());
        this.mVideoSurfaceView.setOnErrorListener(mPresenter.getOnErrorListener());
        this.mVideoSurfaceView.setOnSeekCompleteListener(mPresenter.getOnSeekCompleteListener());
        this.sb_video.setOnSeekBarChangeListener(mPresenter.getOnSeekBarChangeListener());
        this.mVideoSurfaceView.setOnClickListener(this);
        this.iv_list.setOnClickListener(this);
        this.iv_prev.setOnClickListener(this);
        this.iv_prev.setOnLongClickListener(this);
        this.iv_prev.setOnTouchListener(this);
        this.iv_switch.setOnClickListener(this);
        this.iv_next.setOnClickListener(this);
        this.iv_next.setOnLongClickListener(this);
        this.iv_next.setOnTouchListener(this);
        this.btnBack.setOnClickListener(this);
        FactoryTestManager.getInstances().registerVideoCmmandListener(this);//工厂测试
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void handlerIntent(Intent intent) {
        Logutil.i(TAG, "handlerIntent() ...");
        if (isBindPresenter()) {
            mPresenter.onHandlerIntent(intent);
        }
    }

    @Override
    protected void onLoadData() {
        if (isBindPresenter()) {
            mPresenter.onLoadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_list:
                if (isBindPresenter()) {
                    mPresenter.onEnterList();
                    overridePendingTransition(0,0);
                }
                break;
            case R.id.iv_prev:
                if (isBindPresenter()) {
                    mPresenter.onPrevProgram();
                }
                break;
            case R.id.iv_switch:
                if (isBindPresenter()) {
                    mPresenter.onSwitchPlayOrPause();
                }
                break;
            case R.id.iv_next:
                if (isBindPresenter()) {
                    mPresenter.onNextProgram();
                }
                break;
            case R.id.videoSurfaceView:
                if (isBindPresenter()) {
                    mPresenter.onSwitchScreentMode();
                }
                break;
            case R.id.btn_back:
                if (isBindPresenter()) {
                    mPresenter.btnBack();
                }
                break;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_prev:
                Logutil.i(TAG, "onLongClick() PREV ...");
                if (isBindPresenter()) {
                    mPresenter.pauseVideo(true);
                    mPresenter.setStopTouchEventEnableWhenPrevProgram(false);
                    mPresenter.onFastBackward();

                }
                break;
            case R.id.iv_next:
                Logutil.i(TAG, "onLongClick() NEXT ...");
                if (isBindPresenter()) {
                    mPresenter.pauseVideo(true);
                    mPresenter.setStopTouchEventEnableWhenNextProgram(false);
                    mPresenter.onFastForward();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.iv_prev:
                if (mPresenter.getStopTouchEventEnableWhenPrevProgram()) {

                } else if (MotionEvent.ACTION_DOWN == action) {
                    Logutil.i(TAG, "onTouch() ACTION_DOWN PREV ...");
                } else if (MotionEvent.ACTION_UP == action) {
                    Logutil.i(TAG, "onTouch() ACTION_UP PREV ...");
                    mPresenter.setStopTouchEventEnableWhenPrevProgram(true);
                    mPresenter.onStopFastBackward();
                }
                break;
            case R.id.iv_next:
                if (mPresenter.getStopTouchEventEnableWhenNextProgram()) {

                } else if (MotionEvent.ACTION_DOWN == action) {
                    Logutil.i(TAG, "onTouch() ACTION_DOWN NEXT ...");
                } else if (MotionEvent.ACTION_UP == action) {
                    Logutil.i(TAG, "onTouch() ACTION_UP NEXT ...");
                    mPresenter.setStopTouchEventEnableWhenNextProgram(true);
                    mPresenter.onStopFastForward();
                }
                break;
        }
        return false;
    }

    @Override
    public void onPlayVideo(String path, int progress, boolean isAutoPlay) {
        mVideoSurfaceView.setAutoPlayState(isAutoPlay);
        mVideoSurfaceView.setVideoPath(path);
        mVideoSurfaceView.seekTo(progress);
    }

    @Override
    public void onChangeSeekbarMaxProgress(int progress) {
        this.sb_video.setMax(progress);
    }

    @Override
    public void onShowProgramCurrentTime(String curTime) {
        tv_curTime.setText(curTime);
    }

    @Override
    public void onShowProgramTotalTime(String totalTime) {
        tv_totalTime.setText(totalTime);
    }

    @Override
    public void onShowProgramProgress(int progress) {
        sb_video.setProgress(progress);
    }

    @Override
    public void onSwitchTransitionBlackView(boolean enable) {
        this.iv_transition_view.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onSwitchWatchVideoWarningView(boolean enable) {
        this.drivingVideoWarningView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onSwitchPlayVideoExceptionWarningView(boolean enable) {
        if (enable) {
            int resText = R.string.tv_alert_media_play_error_text;
            ToastCustomHint.showSingleMsg(this, resText, ToastCustom.DELAY_TIME_5S);
        }
    }

    @Override
    public void onChangePlaySwitchStateView(boolean enable) {
        int resId = enable ? R.drawable.selector_video_btn_pause : R.drawable.selector_video_btn_play;
        iv_switch.setImageResource(resId);
    }

    @Override
    public void onChangeScreenMode(boolean isFullScreen) {
        if (isFullScreen) {
            videoBottombar.setVisibility(View.INVISIBLE);
        } else {
            videoBottombar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isPlaying() {
        if (null != mVideoSurfaceView) {
            return mVideoSurfaceView.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isPrepared() {
        if (null != mVideoSurfaceView) {
            return mVideoSurfaceView.isPrepared();
        }
        return false;
    }

    @Override
    public void onPauseVideo() {
        if (null != mVideoSurfaceView) {
            this.mVideoSurfaceView.pause();
        }
    }

    @Override
    public void onStartVideo() {
        if (null != mVideoSurfaceView) {
            this.mVideoSurfaceView.start();
        }
    }

    @Override
    public int getDuration() {
        if (null != mVideoSurfaceView) {
            return mVideoSurfaceView.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (null != mVideoSurfaceView) {
            return mVideoSurfaceView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public String getVideoPath() {
        return mVideoSurfaceView.getVideoPath();
    }

    @Override
    public void onSeekTo(int progress) {
        if (isBindPresenter()) {
            mVideoSurfaceView.seekTo(progress);
        }
    }

    @Override
    public void onMuteVolumeEnable(boolean isMute) {
        if (isBindPresenter()) {
            mVideoSurfaceView.muteVolumeEnable(isMute);
        }
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void setIsNeedPlay(boolean isNeedPlay) {
    }

    @Override
    public void closeDialog() {
        ToastCustomHint.closeDialog();
    }

    @Override
    public void backKey() {
        finish();
    }

    @Override
    public void setIsHasAudioFocus(boolean isHasAudioFocus) {
        mVideoSurfaceView.setAudioFocusState(isHasAudioFocus);
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.println("==============DUMP START=========================");
        if (null != args && args.length > 0) {
            if (args.length > 1 && "driving".equals(args[0])) {
                writer.println("==============RESULT arg1=" + args[0] + ",arg2=" + args[1]);
                if ("0".equals(args[1])) {
                    onSwitchWatchVideoWarningView(false);
                } else if ("1".equals(args[1])) {
                    onSwitchWatchVideoWarningView(true);
                }
            }
        }
        writer.println("==============DUMP END=========================");
    }

    /***
     * 工厂测试功能
     * @param cmd
     */
    @Override
    public void onVideoCommand(int cmd) {
        if (cmd == Definition.MediaCtrlConst.CMD_NEXT){
            if (isBindPresenter()) {
                mPresenter.onNextProgram();
            }
        }else if (cmd == Definition.MediaCtrlConst.CMD_PREV){
            if (isBindPresenter()) {
                mPresenter.onPrevProgram();
            }
        }else if (cmd == Definition.MediaCtrlConst.CMD_PAUSE || cmd ==Definition.MediaCtrlConst.CMD_START ){
            if (isBindPresenter()) {
                mPresenter.onSwitchPlayOrPause();
            }
        }
    }

}
