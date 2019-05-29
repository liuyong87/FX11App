package com.semisky.multimedia.media_photo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.common.base_view.BaseActivity;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.media_photo.presenter.PhotoPlayerPresenter;

/**
 * 本地图片模板视图
 * Created by Anter on 2018/8/4.
 */

public abstract class TemplatePhotoPlayerActivity extends BaseActivity<IPhotoPlayerView<PhotoInfo>, PhotoPlayerPresenter<IPhotoPlayerView<PhotoInfo>>> implements IPhotoPlayerView<PhotoInfo> {

    protected abstract int getLayoutResID();

    protected abstract void initViews();

    protected abstract void setListener();

    protected abstract void handlerIntent(Intent intent);

    @Override
    protected PhotoPlayerPresenter<IPhotoPlayerView<PhotoInfo>> createPresenter() {
        return new PhotoPlayerPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(getLayoutResID());
        initViews();
        setListener();
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
        PreferencesManager.saveLastAppFlag(Definition.AppFlag.TYPE_PHOTO);
//        onLoadData();
    }

}
