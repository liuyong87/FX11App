package com.semisky.multimedia.media_video.presenter;

import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.AppFlag;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_video.model.IVideoDataModel;
import com.semisky.multimedia.media_video.model.IVideoDataModel.OnRefreshDataListener;
import com.semisky.multimedia.media_video.model.VideoDataModel;
import com.semisky.multimedia.media_video.view.IVideoListView;

import java.util.List;


/**
 * Created by LiuYong on 2018/8/9.
 */

public class VideoListPresenter<V extends IVideoListView<VideoInfo>> extends BasePresenter<V> implements IVideoListPresenter {
    private static final String TAG = Logutil.makeTagLog(VideoListPresenter.class);
    private IVideoDataModel mVideoDataModel;
    private int mUsbFlag = -1;

    public VideoListPresenter() {
        mVideoDataModel = new VideoDataModel();
        mVideoDataModel.registerOnRefreshDataListener(mOnRefreshDataListener);
    }

    @Override
    public void setUsbFlag(int usbFlag) {
        this.mUsbFlag = usbFlag;
    }

    @Override
    public int getUsbFlag() {
        return this.mUsbFlag;
    }

    @Override
    public void onPlayList(String url) {
        Logutil.i(TAG, "onPlayList() ..." + url);
        if (isBindView()) {
            AppActivityManager
                    .getInstance()
                    .onStopColseAcitvityWith(AppFlag.TYPE_VIDEO)
                    .onCloseOrtherActivity();
            AppUtil.enterPlayerView(AppFlag.TYPE_VIDEO, url);
        }
    }

    @Override
    public void setTitleToUI(String name, String title) {
        SemiskyIVIManager.getInstance().setTitleName(name, title);
    }

    @Override
    public void onLoadData() {
        mVideoDataModel.onLoadVideoInfoList(new IVideoDataModel.OnLoadDataListener<VideoInfo>() {
            @Override
            public void onLoadData(List<VideoInfo> dataList) {
                refreshFirstData(dataList);
            }
        },mUsbFlag);
    }

    private OnRefreshDataListener mOnRefreshDataListener = new OnRefreshDataListener() {
        @Override
        public void onUpdateData(final List<VideoInfo> dataList) {
            refreshData(dataList);
        }
    };

    // 刷新媒体列表数据
    private void refreshData(final List<VideoInfo> dataList) {

        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    mViewRef.get().onAlertEmptyListTextVisible(true);
                    if (null != dataList && dataList.size() > 0) {
                        mViewRef.get().onRefreshData(dataList);
                        mViewRef.get().onAlertEmptyListTextVisible(false);
                        return;
                    }
                    mViewRef.get().onAlertEmptyListTextVisible(true);
                }
            }
        });
    }

    // 刷新媒体列表首次数据
    private void refreshFirstData(final List<VideoInfo> dataList) {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    if (null != dataList && dataList.size() > 0) {
                        mViewRef.get().onFirstRefreshData(dataList);
                        mViewRef.get().onAlertEmptyListTextVisible(false);
                        return;
                    }
                    mViewRef.get().onAlertEmptyListTextVisible(true);
                }
            }
        });
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        mVideoDataModel.unregisterOnRefreshDataListener();
    }
}
