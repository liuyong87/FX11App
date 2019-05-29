package com.semisky.multimedia.media_music.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_view.MusicListDialog;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.base_view.ToastCustomHint;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;
import com.semisky.multimedia.media_music.LrcView.LrcEntity;
import com.semisky.multimedia.media_music.LrcView.LrcView;
import com.semisky.multimedia.media_music.adpter.MyPagerAdapter;
import com.semisky.multimedia.media_music.viewPager.ClipViewPager;
import com.semisky.multimedia.media_music.viewPager.IPageItemChangeListener;
import com.semisky.multimedia.media_music.viewPager.ViewPagerRelativeLayout;

import java.util.List;

/**
 * Created by Anter on 2018/8/2.
 */

public class MusicPlayerActivity extends TemplateMusicPlayerActivity implements OnClickListener, MusicListDialog.OnItemOnClickListener {
    private static final String TAG = Logutil.makeTagLog(MusicPlayerActivity.class);
    private TextView
            tv_songName,
            tv_artistName,
            tv_albumName,
            tv_curTime,
            tv_totalTime;
   // private ImageView /*iv_lrc_switch, iv_sound,iv_listPlaying, iv_list iv_play_mode*/ iv_switch;
    private ImageButton iv_lrc_switch,iv_sound,iv_list,iv_listPlaying,iv_play_mode,iv_switch;
    private SeekBar sb_music;
    private LrcView lrcView;
    private ViewPagerRelativeLayout rlContainer;
    private ClipViewPager viewPager;
    private MusicListDialog musicListDialog;
    private MyPagerAdapter myPagerAdapter;


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        Logutil.i(TAG, "getLayoutResID() ...");
        return R.layout.activity_music_player;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void handlerIntent(Intent intent) {
        if (isBindPresenter()) {
            mPresenter.onHandlerIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logutil.i(TAG, "Activity onResume() ...");
        if (isBindPresenter()) {
            mPresenter.playButtonGoneAndVisible(View.VISIBLE);
            mPresenter.onResumeRequestAudio();
            String title = getString(R.string.status_bar_music_title_text);
            mPresenter.setTitleToStatusBar(this.getClass().getName(), title, AutoConstants.AppStatus.RUN_FOREGROUND);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logutil.i(TAG, "Activity onPause() ...");
        //activity pause时，app状态设置为后台状态
        if (isBindPresenter()) {
            String title = getString(R.string.status_bar_music_title_text);
            mPresenter.setTitleToStatusBar(this.getClass().getName(), title, AutoConstants.AppStatus.RUN_BACKGROUND);
        }
    }

    @Override
    protected void initViews() {
        Logutil.i(TAG, "initViews() ...");
        tv_songName = (TextView) findViewById(R.id.tv_musicName);
        tv_artistName = (TextView) findViewById(R.id.tv_artistName);
        tv_albumName = (TextView) findViewById(R.id.tv_albumName);
        tv_curTime = (TextView) findViewById(R.id.tv_currentTime);
        tv_totalTime = (TextView) findViewById(R.id.tv_totalTime);
        sb_music = (SeekBar) findViewById(R.id.sb_music);
        iv_sound = (ImageButton) findViewById(R.id.iv_sound);
        iv_list = (ImageButton) findViewById(R.id.iv_list);
        iv_listPlaying = (ImageButton) findViewById(R.id.iv_playing_list);
        iv_play_mode = (ImageButton) findViewById(R.id.iv_play_mode);
        iv_lrc_switch = (ImageButton) findViewById(R.id.iv_lrc_switch);
        iv_switch = (ImageButton) findViewById(R.id.iv_switch);
        lrcView = (LrcView) findViewById(R.id.lrc_view);

        // 音乐专辑组合相关控件----STAR
        viewPager = (ClipViewPager) findViewById(R.id.view_pager);
        rlContainer = (ViewPagerRelativeLayout) findViewById(R.id.rl_container);
        rlContainer.AddPageItemChangeListener(mIPageItemClickListener);
        viewPager.setPageTransformer(true, new Anim());
        myPagerAdapter = new MyPagerAdapter(this);
        myPagerAdapter.setOnItemClickListener(mOnItemClickListener);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        // ---------END


    }

    private MyPagerAdapter.OnItemClickListener mOnItemClickListener = new MyPagerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (!isBindPresenter()) {
                return;
            }
            Log.i(TAG, "onItemClick() ..." + position);
            mPresenter.playButtonGoneAndVisible(View.VISIBLE);
            mPresenter.onSwitchPlayOrPause();
        }
    };

    private IPageItemChangeListener mIPageItemClickListener = new IPageItemChangeListener() {

        @Override
        public void onPageItemSelected(View view, int position) {
            Log.i(TAG, "onPageItemSelected() ..." + position);
            mPresenter.onPlayPosition(position);
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }

    };

    @Override
    protected void setListener() {
        Logutil.i(TAG, "setListener() ...");
        sb_music.setOnSeekBarChangeListener(mPresenter.getOnSeekBarChangeListener());
        iv_list.setOnClickListener(this);
        iv_listPlaying.setOnClickListener(this);
        iv_play_mode.setOnClickListener(this);
        iv_sound.setOnClickListener(this);
        iv_lrc_switch.setOnClickListener(this);
        tv_songName.setSelected(true);
        iv_switch.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        if (isBindPresenter()) {
            mPresenter.loadData();
        }

    }

    @Override
    public void onClick(View v) {
        if(!isBindPresenter()){
            return;
        }
        switch (v.getId()) {
            case R.id.iv_list:
                mPresenter.onEnterList();
                overridePendingTransition(0,0);
                break;
            case R.id.iv_play_mode:
                mPresenter.onSwitchPlayMode();
                break;
            case R.id.iv_playing_list:
                showMusicList();
                break;
            case R.id.iv_sound:
                mPresenter.setSound();
                break;
            case R.id.iv_lrc_switch:
                mPresenter.reqShowLrc();
                break;
            case R.id.iv_switch:
                mPresenter.onSwitchPlayOrPause();
                break;
        }
    }


    // IMusicPlayerView

    @Override
    public void onShowProgramName(String programName) {
        super.onShowProgramName(programName);
        programName = programName != null ? programName : getString(R.string.tv_songName_text);
        tv_songName.setText(programName);
    }

    @Override
    public void onShowProgramArtistName(String artistName) {
        super.onShowProgramArtistName(artistName);
        artistName = artistName != null ? artistName : getString(R.string.tv_artistName_text);
        tv_artistName.setText(artistName);
    }

    @Override
    public void onShowProgramAlbumName(String albumName) {
        super.onShowProgramAlbumName(albumName);
        if (albumName == null || albumName.length() <= 0) {
            tv_albumName.setText(R.string.tv_albumName_text);
        } else {
            tv_albumName.setText(albumName);
        }
    }

    @Override
    public void onShowProgramTotalTime(String totalTime) {
        tv_totalTime.setText("/" + totalTime);

    }

    @Override
    public void onSwitchFavoriteView(boolean enable) {
        super.onSwitchFavoriteView(enable);
    }

    @Override
    public void onShowProgramCurrentTime(String curTime) {
        super.onShowProgramCurrentTime(curTime);
        tv_curTime.setText(curTime);
    }

    @Override
    public void onUpdateDuration(int duration) {
        super.onUpdateDuration(duration);
        sb_music.setMax(duration);
    }

    @Override
    public void onShowProgramProgress(int progress) {
        super.onShowProgramProgress(progress);
        sb_music.setProgress(progress);
        lrcView.updateTime(progress);
    }

    @Override
    public void onChangePlayState(boolean isPlay) {
        int resId = isPlay ? R.drawable.btn_pause_normal : R.drawable.btn_play_normal;
        iv_switch.setImageResource(resId);
    }

    @Override
    public void onChangePlayMode(int playMode) {
        int resId = R.drawable.selector_btn_playmode_cycle;
        switch (playMode) {
            case PlayMode.LOOP:
                resId = R.drawable.selector_btn_playmode_cycle;
                break;
            case PlayMode.SHUFFLE:
                resId = R.drawable.selector_btn_playmode_random;
                break;
            case PlayMode.SINGLE:
                resId = R.drawable.selector_btn_playmode_single;
                break;
        }
        iv_play_mode.setImageResource(resId);
    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    public void refreshMusicData(List<MusicInfo> data) {
        //todo 获取到数据
        myPagerAdapter.setData(data);
    }

    @Override
    public void refreshPlayingPosition(String url) {
        if (myPagerAdapter != null) {
            int position = myPagerAdapter.getCurrentItemPosition(url);
            Log.i(TAG, "refreshPlayingPosition() ======> position : " + position);
            viewPager.setCurrentItem(position);
        }

    }

    @Override
    public void onPlayShowState(int state) {
        iv_switch.setVisibility(state);
    }

    @Override
    public void onSwitchPlayProgramExceptionWarningView(boolean enable) {
        if (enable) {
            int resText = R.string.tv_alert_media_play_error_text;
            ToastCustomHint.showSingleMsg(this, resText, ToastCustom.DELAY_TIME_2S);
        }
    }

    private void showMusicList() {
        if (musicListDialog == null) {
            musicListDialog = new MusicListDialog(this);
        }
        musicListDialog.show();
        musicListDialog.setMusicData(mPresenter.getMusicData());
        musicListDialog.setOnItemOnClickListener(this);
    }

    @Override
    public void onItemSelect(MusicInfo musicInfo) {
        mPresenter.onPlayingListUrl(musicInfo.getUrl());
    }

    @Override
    public void onChangedLyric(List<LrcEntity> lrcList) {
        lrcView.initLrc(lrcList);
    }

    @Override
    public void onLrcViewVisible(boolean isShow) {
        lrcView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        iv_lrc_switch.setSelected(isShow);
    }

    @Override
    public void onAlbumViewVisible(boolean isShow) {
        rlContainer.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean hasLyric() {
        return lrcView.hasLyric();
    }

    @Override
    public boolean isAlbumViewVisible() {
        return rlContainer.isShown();
    }
}
