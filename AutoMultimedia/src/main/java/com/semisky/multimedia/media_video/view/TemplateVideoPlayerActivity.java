package com.semisky.multimedia.media_video.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.semisky.multimedia.common.base_view.BaseActivity;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.media_video.presenter.VideoPlayerPresenter;

/**
 * Created by LiuYong on 2018/8/7.
 */

public abstract class TemplateVideoPlayerActivity extends BaseActivity<IVideoPlayerView, VideoPlayerPresenter<IVideoPlayerView>> implements IVideoPlayerView {

    protected abstract int getLayoutResID();

    protected abstract void initViews();

    protected abstract void setListener();

    protected abstract void onLoadData();

    protected abstract void handlerIntent(Intent intent);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(getLayoutResID());
        initViews();
        setListener();
        onLoadData();
        handlerIntent(super.getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlerIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_VIDEO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected VideoPlayerPresenter createPresenter() {
        return new VideoPlayerPresenter();
    }

    @Override
    public void onShowProgramName(String programName) {

    }
}
