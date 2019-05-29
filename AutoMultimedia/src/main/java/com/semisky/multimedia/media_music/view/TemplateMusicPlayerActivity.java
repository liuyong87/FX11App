package com.semisky.multimedia.media_music.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_view.BaseActivity;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.presenter.MusicPlayerPresenter;

import java.util.List;

/**
 * 音乐播放器模板Acitivity
 * Created by Anter on 2018/7/30.
 */

public abstract class TemplateMusicPlayerActivity extends BaseActivity<IMusicPlayerView, MusicPlayerPresenter<IMusicPlayerView>> implements IMusicPlayerView {

    protected abstract int getLayoutResID();

    protected abstract void initViews();

    protected abstract void setListener();

    protected abstract void loadData();


    @Override
    protected MusicPlayerPresenter<IMusicPlayerView> createPresenter() {
        return new MusicPlayerPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        initViews();
        setListener();
        handlerIntent(super.getIntent());
        loadData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        super.setIntent(intent);
        handlerIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_MUSIC);// 保存断点记忆应用标识
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbindService();
        super.onDestroy();
    }

    @Override
    public void onShowProgramName(String programName) {

    }

    @Override
    public void onShowProgramArtistName(String artistName) {

    }

    @Override
    public void onShowProgramAlbumName(String albumName) {

    }



    @Override
    public void onShowProgramCurrentTime(String curTime) {

    }


    @Override
    public void onShowProgramProgress(int progress) {

    }

    @Override
    public void onSwitchFavoriteView(boolean enable) {

    }

    @Override
    public void onUpdateDuration(int duration) {

    }
}
