package com.semisky.multimedia.media_photo.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.base_view.ToastCustomHint;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.ScreenModeUtil;
import com.semisky.multimedia.media_photo.adapter.PhotoPlayerAdapter;

import java.util.List;

/**
 * Created by Anter on 2018/8/4.
 */

public class PhotoPlayerActivity extends TemplatePhotoPlayerActivity implements View.OnClickListener, View.OnTouchListener,View.OnLongClickListener {
    private static final String TAG = Logutil.makeTagLog(PhotoPlayerActivity.class);
    private ImageView iv_list, iv_rotate,iv_set_wallpaper;
    private ViewPager view_pager;
    private PhotoPlayerAdapter mPhotoPlayerAdapter;
    private RelativeLayout photo_play_control_bar;
    private TextView tv_photo_name;

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_photo_player;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBindPresenter()) {
            String title = getString(R.string.status_bar_photo_title_text);
            mPresenter.setTitleToStatusBar(this.getClass().getName(), title);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPhotoPause();
    }

    @Override
    protected void initViews() {
        mPhotoPlayerAdapter = new PhotoPlayerAdapter(this);
        iv_list = (ImageView) findViewById(R.id.iv_list);
        iv_rotate = (ImageView) findViewById(R.id.iv_rotate);
        iv_set_wallpaper = (ImageView) findViewById(R.id.iv_set_wallpaper);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        photo_play_control_bar = (RelativeLayout) findViewById(R.id.photo_play_control_bar);
        tv_photo_name = (TextView)findViewById(R.id.tv_photo_name);

        view_pager.setAdapter(mPhotoPlayerAdapter);
    }


    @Override
    protected void setListener() {
        mPhotoPlayerAdapter.setmOnClickListener(mCurPhotoOnClickListener);
        mPhotoPlayerAdapter.setmOnTouchListener(this);
        iv_list.setOnClickListener(this);
        iv_list.setOnLongClickListener(this);
        iv_list.setOnTouchListener(this);
        iv_rotate.setOnClickListener(this);
        iv_rotate.setOnLongClickListener(this);
        iv_rotate.setOnTouchListener(this);
        view_pager.setOnPageChangeListener(mPresenter.getOnPageChangeListener());
        view_pager.setOnTouchListener(mPresenter.getOnTouchListener());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void handlerIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        Logutil.i(TAG, "handlerIntent() url = " + url);
        if (isBindPresenter()) {
            mPresenter.onHandlerIntent(intent);
        }
    }

    @Override
    public void onLoadData() {
      /*  if (isBindPresenter()) {
            mPresenter.onLoadData();
        }*/
    }

    @Override
    public void onRefreshPhotoList(List<PhotoInfo> list) {
        Logutil.i(TAG, "onRefreshPhotoList() list=" + list.size());
        if (null != mPhotoPlayerAdapter) {
            mPhotoPlayerAdapter.updateList(list);
        }
    }

    @Override
    public int getCurrentItem() {
        if (null != mPhotoPlayerAdapter) {
            return view_pager.getCurrentItem();
        }
        return 0;
    }

    @Override
    public void onShowSpecifyPhoto(int pos) {
        view_pager.setCurrentItem(pos, false);
    }

    @Override
    public void onShowSingleMessage(int resId) {
        ToastCustomHint.showSingleMsg(this, resId, ToastCustom.DELAY_TIME_2S);
    }

    @Override
    public void onScreenShowModeChange(boolean enable) {
        int visibleEnable = enable ? View.GONE : View.VISIBLE;
        photo_play_control_bar.setVisibility(visibleEnable);
        tv_photo_name.setVisibility(visibleEnable);
    }

    @Override
    public void onChangeRotatePhoto(int curItem) {
        mPhotoPlayerAdapter.onRotate(curItem);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            int viewId = v.getId();
            if (viewId == R.id.iv_prev || viewId == R.id.iv_list || viewId == R.id.iv_next || viewId == R.id.iv_switch || viewId == R.id.iv_rotate ){
                if (isBindPresenter()){
                    mPresenter.startFullScreenModeTimeoutRunnable();

                }
            }else {
                if (isBindPresenter()) {
                    mPresenter.touchPausePlay();
                    mPresenter.onPhotoPause();
                }
            }

        }
        return false;
    }

    @Override
    public void onResetCurrentPhoto(int curPosition) {
        mPhotoPlayerAdapter.onRestoreOriginalPhoto(curPosition);
    }

    @Override
    public void onChangeSwitchView(boolean isPlay) {
    }

    @Override
    public int getResIdByChangeFirstPhotoText() {
        return R.string.tv_alert_change_first_photo_text;
    }

    @Override
    public int getResIdByChangeLastPhotoText() {
        return R.string.tv_alert_change_last_photo_text;
    }

    @Override
    public int getIsFullShow() {
        return photo_play_control_bar.getVisibility();
    }

    @Override
    public void onChangePhotoNameText(String photoName) {
        tv_photo_name.setText(photoName);
    }


    @Override
    public void onClick(View v) {
        if(!isBindPresenter()){
            return;
        }
        mPresenter.onReTimingWhenNormalScreentTouchPlayWidget();
        switch (v.getId()) {
            case R.id.iv_list:
                mPresenter.onPhotoPause();
                mPresenter.onEnterList();
                break;
            case R.id.iv_rotate:
                mPresenter.onPhotoPause();
                mPresenter.onRotate();
                break;
            case R.id.iv_set_wallpaper:

                break;
        }
    }

    private View.OnClickListener mCurPhotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBindPresenter()) {
                mPresenter.onSwitchScreentMode();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop() ...");
        if(!isBindPresenter()){
            return;
        }
        mPresenter.removeFullScreenModeTimeoutRunnable();//长按清除延时任务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logutil.i(TAG, "onDestroy() ...");
    }

    @Override
    public boolean onLongClick(View v) {
        mPresenter.removeFullScreenModeTimeoutRunnable();//长按清除延时任务
        return false;
    }
}
