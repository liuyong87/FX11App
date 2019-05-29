package com.semisky.multimedia.media_photo.presenter;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.interfaces.IBackModeChange;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.ScreenModeUtil;
import com.semisky.multimedia.common.utils.USBCheckUtil;
import com.semisky.multimedia.media_photo.bean.PhotoPlayList;
import com.semisky.multimedia.media_photo.manager.PhotoManager;
import com.semisky.multimedia.media_photo.model.IPhotoDataModel;
import com.semisky.multimedia.media_photo.model.PhotoDataModel;
import com.semisky.multimedia.media_photo.view.IPhotoPlayerView;
import com.semisky.multimedia.media_photo.view.PhotoListActivity;

import java.io.File;
import java.util.List;


/**
 * Created by Anter on 2018/8/4.
 */

public class PhotoPlayerPresenter<V extends IPhotoPlayerView<PhotoInfo>> extends BasePresenter<V> implements IPhotoPlayerPresenter {
    private static final String TAG = Logutil.makeTagLog(PhotoPlayerPresenter.class);
    private PhotoPlayList<PhotoInfo> mPhotoPlayList;
    private AutoPlayRunnable mAutoPlayRunnable;
    private IPhotoDataModel mPhotoDataModel;

    private boolean mIsAutoPlay = false;// 是否自动播放
    private boolean mIsListPlay = false;// 是否列表播放
    private String mUserListSelectUrl = null; // 用户选择列表播放URL
    private boolean isPlayState = false;// 在暂停前记录是否为播放状态

    private int mUsbFlag = Definition.FLAG_USB_INVALID;


    public PhotoPlayerPresenter() {
        this.mPhotoPlayList = new PhotoPlayList();
        this.mAutoPlayRunnable = new AutoPlayRunnable();
        this.mPhotoDataModel = new PhotoDataModel();
        this.mPhotoDataModel.registerOnRefreshDataListener(OnRefreshDataListener);
        SemiskyIVIManager.getInstance().registerBackModeChanged(getIBackModeChanged());
    }

    @Override
    public void setUsbFlag(int usbFlag) {
        this.mUsbFlag = usbFlag;
    }

    @Override
    public int getUsbFlag() {
        return this.mUsbFlag;
    }

    private IPhotoDataModel.OnRefreshDataListener OnRefreshDataListener = new IPhotoDataModel.OnRefreshDataListener() {
        @Override
        public void onUpdateData(List<PhotoInfo> dataList) {
            refreshData(dataList);
        }
    };

    @Override
    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return this.mOnPageChangeListener;
    }

    @Override
    public View.OnTouchListener getOnTouchListener() {
        return this.onTouchListener;
    }

    @Override
    public void onHandlerIntent(Intent intent) {
        String url = null;

        initUsbFlag();
        if (null != intent) {
            url = intent.getStringExtra("url");
        }

        if (null != url) {
            PreferencesManager.saveLastPhotoUrl(AppUtil.conversionUrlToUsbFlag(url),url);
            if (mPhotoPlayList.hasData()) {
                onPlayListPhoto(url);
            }
        } else {
            if(!mPhotoPlayList.hasData()){
                this.onLoadData();
            }

        }
        if (mViewRef.get().getIsFullShow() == View.GONE) {
            PhotoManager.getPhotoManager().setIsFullScreen(true);
        } else {
            PhotoManager.getPhotoManager().setIsFullScreen(false);
            startFullScreenModeTimeoutRunnable();
        }

    }

    /**
     * 初始化
     */
    private void initUsbFlag() {
        String lastUrl = PreferencesManager.getLastPhotoUrl(PreferencesManager.getLastPhotoSourceUsbFlag());
        if (null != lastUrl) {
            if (lastUrl.startsWith(Definition.PATH_USB1)) {

                boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
                this.mUsbFlag = isMountedUsb1 ? Definition.FLAG_USB1 : Definition.FLAG_USB_INVALID;

                if (!isMountedUsb1) {
                    boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
                    this.mUsbFlag = isMountedUsb2 ? Definition.FLAG_USB2 : Definition.FLAG_USB_INVALID;
                }
            } else if (lastUrl.startsWith(Definition.PATH_USB2)) {
                boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
                this.mUsbFlag = isMountedUsb2 ? Definition.FLAG_USB2 : Definition.FLAG_USB_INVALID;

                if (!isMountedUsb2) {
                    boolean isMountedUsb = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
                    this.mUsbFlag = isMountedUsb ? Definition.FLAG_USB1 : Definition.FLAG_USB_INVALID;
                }
            }
        }
        Log.i(TAG, "initUsbFlag() ..." + mUsbFlag);
    }

    // 恢复播放图片
    private void onRefreshPhotoPlayPostion() {
        String lastUrl = PreferencesManager.getLastPhotoUrl(PreferencesManager.getLastPhotoSourceUsbFlag());
        boolean isExistsWithLastUrl = (null != lastUrl && new File(lastUrl).exists());
        boolean hasData = mPhotoPlayList.hasData();
        Logutil.i(TAG, "onRefreshPhotoPlayPostion() lastUrl" + lastUrl);
        Logutil.i(TAG, "onRefreshPhotoPlayPostion() hasData" + hasData);
        Logutil.i(TAG, "onRefreshPhotoPlayPostion() isExistsWithLastUrl" + isExistsWithLastUrl);

        if (isExistsWithLastUrl && hasData) {
            int pos = getPhotoPosition(lastUrl);
            if (pos != -1) {
                mViewRef.get().onShowSpecifyPhoto(pos);
            }
        }
    }

    @Override
    public void onLoadData() {
        Logutil.i(TAG,"onLoadData() ...");
        if (isBindView()) {
            mPhotoDataModel.onLoadPhotoInfoList(mUsbFlag, new IPhotoDataModel.OnLoadDataListener<PhotoInfo>() {
                @Override
                public void onLoadData(List<PhotoInfo> photoInfoList) {
                    Logutil.i(TAG,"onLoadData() ..."+(null != photoInfoList?photoInfoList.size():0));
                    refreshData(photoInfoList);
//                    startFullScreenModeTimeoutRunnable();// 延时全屏模式显示
                    //如果不是手动暂停才会恢复播放
                    if (!PhotoManager.getPhotoManager().getClickStop()) {
                        onAutoPlayPhoto();
                    }
                }
            });
        }
    }

    private void refreshData(final List<PhotoInfo> photoInfoList) {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    if (null != photoInfoList && photoInfoList.size() > 0) {
                        mViewRef.get().onRefreshPhotoList(photoInfoList);
                        mPhotoPlayList.addList(photoInfoList);
                        onRefreshPhotoPlayPostion();
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onPrevProgram() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            // 切换上一张图片前恢复当前预览图片
            onResetCurrentPhoto();

            // 切换上一张图片
            int curItem = mViewRef.get().getCurrentItem();
            int prevItem = (curItem - 1);
            prevItem = prevItem < 0 ? mPhotoPlayList.getSize() - 1 : prevItem;
            mViewRef.get().onShowSpecifyPhoto(prevItem);
            onAlertWhenChangeLastPhoto(prevItem);

        }
    }

    @Override
    public void onNextProgram() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            // 切换下一张图片前恢复当前预览图片
            onResetCurrentPhoto();
            // 切换下一张图片
            int nextItem = (mViewRef.get().getCurrentItem() + 1);
            nextItem = nextItem > (mPhotoPlayList.getSize() - 1) ? 0 : nextItem;
            Logutil.i(TAG, "onNextProgram() ..." + nextItem + ",SIZE=" + (mPhotoPlayList.getSize()));
            mViewRef.get().onShowSpecifyPhoto(nextItem);
            onAlertWhenChangeFirstPhoto(nextItem);
        }
    }

    @Override
    public void onSwitchPlayOrPause() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            Logutil.i(TAG, "mIsAutoPlay=" + mIsAutoPlay);
            onResetCurrentPhoto();
            if (!mIsAutoPlay) {
                PhotoManager.getPhotoManager().setClickStop(false);//恢复播放，状态为初始状态
                startAutoPlayRunnable();
            } else {
                PhotoManager.getPhotoManager().setClickStop(true);//手动停止播放
                stopAutoPlayRunnable();
            }
            mViewRef.get().onChangeSwitchView(mIsAutoPlay);
        }
    }

    @Override
    public void onPhotoPause() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            mViewRef.get().onChangeSwitchView(false);
            stopAutoPlayRunnable();
        }
    }

    @Override
    public void onRotate() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            mViewRef.get().onChangeRotatePhoto(mViewRef.get().getCurrentItem());
        }
    }

    @Override
    public void onAutoPlayPhoto() {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView() && mPhotoPlayList.hasData()) {
                    startAutoPlayRunnable();
                    mViewRef.get().onChangeSwitchView(mIsAutoPlay);

                }
            }
        });
    }

    @Override
    public void onSwitchScreentMode() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            if (PhotoManager.getPhotoManager().getIsFullScreen()) {
                onChangeNoramlScreenMode();
                startFullScreenModeTimeoutRunnable();
            } else {
                onChangeFullScreenMode();
                removeFullScreenModeTimeoutRunnable();
            }
        }
    }

    // 切换到非全屏显示模式
    private void onChangeNoramlScreenMode() {
        if (isBindView()) {
            PhotoManager.getPhotoManager().setIsFullScreen(false);
            mViewRef.get().onScreenShowModeChange(PhotoManager.getPhotoManager().getIsFullScreen());
            ScreenModeUtil.changeNormalScreenMode(mViewRef.get().getContext());
            SemiskyIVIManager.getInstance().showBottomBar();
        }
    }

    // 切换到全屏显示模式
    private void onChangeFullScreenMode() {
        if (isBindView()) {
            PhotoManager.getPhotoManager().setIsFullScreen(true);
            mViewRef.get().onScreenShowModeChange(PhotoManager.getPhotoManager().getIsFullScreen());
            ScreenModeUtil.chanageFullScreenMode(mViewRef.get().getContext());
            SemiskyIVIManager.getInstance().dismissBottomBar();
        }
    }

    // 清除延时全屏线程
    public void removeFullScreenModeTimeoutRunnable() {
        _handler.removeCallbacks(mDelayChangeFullScreenModeRunnable);
    }

    // 延时全屏模式显示（延时5s）
    public void startFullScreenModeTimeoutRunnable() {
        _handler.removeCallbacks(mDelayChangeFullScreenModeRunnable);
        _handler.postDelayed(mDelayChangeFullScreenModeRunnable, 5000);
    }

    // 延时全屏
    private Runnable mDelayChangeFullScreenModeRunnable = new Runnable() {
        @Override
        public void run() {
            onSwitchScreentMode();
        }
    };

    @Override
    public void onEnterList() {
        if (isBindView()) {
            Intent it = new Intent();
            it.setClass(MediaApplication.getContext(), PhotoListActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MediaApplication.getContext().startActivity(it);
        }
    }

    @Override
    public void onResetCurrentPhoto() {
        if (isBindView() && mPhotoPlayList.hasData()) {
            int curItem = mViewRef.get().getCurrentItem();
            mViewRef.get().onResetCurrentPhoto(curItem);
        }
    }

    @Override
    public void onPlayListPhoto(String userSelectUrl) {
        if (isBindView() && mPhotoPlayList.hasData()) {
            mIsAutoPlay = true;
            int userSelectUrlPos = getPhotoPosition(userSelectUrl);
            Logutil.i(TAG, "onPlayListPhoto() ..." + userSelectUrlPos);
            if (userSelectUrlPos != -1) {
                mIsAutoPlay = false;
                mViewRef.get().onShowSpecifyPhoto(userSelectUrlPos);
            }
        }
    }

    // 在图片集合中找出用户选择的图片条目位置
    private int getPhotoPosition(String userSelectUrl) {
        for (int i = 0; i < mPhotoPlayList.getSize(); i++) {
            String url = mPhotoPlayList.getList().get(i).getFileUrl();
            if (userSelectUrl.equals(url)) {
                return i;
            }
        }
        return -1;
    }


    private void startAutoPlayRunnable() {
        if (null == this.mAutoPlayRunnable) {
            this.mAutoPlayRunnable = new AutoPlayRunnable();
        }
        mIsAutoPlay = true;
        this.mAutoPlayRunnable.prepare();
        _handler.postDelayed(this.mAutoPlayRunnable, 5000);
    }

    private void stopAutoPlayRunnable() {
        if (null != this.mAutoPlayRunnable) {
            this.mIsAutoPlay = false;
            this.mAutoPlayRunnable.stop();
            _handler.removeCallbacks(this.mAutoPlayRunnable);
        }
    }

    private class AutoPlayRunnable implements Runnable {
        private boolean mIsStop = false;

        public void stop() {
            this.mIsStop = true;
        }

        public void prepare() {
            this.mIsStop = false;
        }

        @Override
        public void run() {
            Logutil.i(TAG, "AutoPlayRunnable run() ..." + mIsStop);
            if (!this.mIsStop) {
                onNextProgram();
                _handler.postDelayed(this, 5000);
            }
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        stopAutoPlayRunnable();
        if (null != mPhotoPlayList) {
            mPhotoPlayList.clearAllData();
        }
        this.mPhotoDataModel.unregisterOnRefreshDataListener();
        SemiskyIVIManager.getInstance().unRegisterBackModeChanged(getIBackModeChanged());
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            Logutil.i(TAG, "onPageSelected() ..." + position);
            if (isBindView()) {
                onResetCurrentPhoto();
                String curPhotoUrl = mPhotoPlayList.getUrlOf(position);
                if (null != curPhotoUrl) {
                    PreferencesManager.saveLastPhotoUrl(AppUtil.conversionUrlToUsbFlag(curPhotoUrl),curPhotoUrl);
                    String fileName = mPhotoPlayList.getList().get(position).getFileName();
                    if (null != fileName) {
                        mViewRef.get().onChangePhotoNameText(fileName);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    int x1, x2, isOne;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_MOVE == event.getAction()) {
                if (isOne == 0) {
                    x1 = (int) event.getX();
                }
                isOne++;
            } else if (MotionEvent.ACTION_UP == event.getAction()) {
                int position = mViewRef.get().getCurrentItem();//获取当前的position
                x2 = (int) event.getX();
                if (x2 - x1 >= 100 && position == 0 && mPhotoPlayList.getSize() > 1) {
                    onPrevProgram();
                } else if (x1 - x2 >= 100 && position != 0 && position == mPhotoPlayList.getSize() - 1) {
                    onNextProgram();
                    return true;
                }
                isOne = 0;
            }
            return false;
        }
    };

    // 切换到首张图片提示
    private void onAlertWhenChangeFirstPhoto(int curItem) {
        if (isBindView()) {
            if (0 == curItem) {
                // 提示“列表结束，回到第一张”
                mViewRef.get().onShowSingleMessage(mViewRef.get().getResIdByChangeFirstPhotoText());
            }
        }
    }

    // 切换到最后一张图片提示
    private void onAlertWhenChangeLastPhoto(int curItem) {
        if (isBindView()) {
            int lastItem = mPhotoPlayList.getSize() - 1;
            if (curItem == lastItem) {
                // 提示“列表结束，回到最后一张”
                mViewRef.get().onShowSingleMessage(mViewRef.get().getResIdByChangeLastPhotoText());
            }
        }
    }

    @Override
    public void setTitleToStatusBar(String clz, String title) {
        SemiskyIVIManager.getInstance().setAppStatus(clz, title, AutoConstants.AppStatus.RUN_FOREGROUND);
    }

    @Override
    public void onReTimingWhenNormalScreentTouchPlayWidget() {
        if (!PhotoManager.getPhotoManager().getIsFullScreen()) {
            startFullScreenModeTimeoutRunnable();
        }

    }

    @Override
    public IBackModeChange getIBackModeChanged() {
        return iBackModeChange;
    }

    @Override
    public void touchPausePlay() {
        PhotoManager.getPhotoManager().setClickStop(true); //手动停止
    }

    private IBackModeChange iBackModeChange = new IBackModeChange() {
        @Override
        public void backModeChanged(final boolean state) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (AppUtil.getActivityIsTop(MediaApplication.getContext(), "com.semisky.multimedia.media_photo.view.PhotoPlayerActivity")) {
                        if (state && mIsAutoPlay) {
                            isPlayState = true;//接收到车事件，暂停播放时记录之前为播放状态
                            onPhotoPause();
                        } else if (isPlayState && !state) {
                            startAutoPlayRunnable();
                            isPlayState = false;
                            mViewRef.get().onChangeSwitchView(mIsAutoPlay);
                        }
                    }
                }
            });


        }
    };

}
