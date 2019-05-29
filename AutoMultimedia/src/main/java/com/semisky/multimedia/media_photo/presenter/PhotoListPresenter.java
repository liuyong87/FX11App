package com.semisky.multimedia.media_photo.presenter;

import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.media_photo.model.IPhotoDataModel;
import com.semisky.multimedia.media_photo.model.PhotoDataModel;
import com.semisky.multimedia.media_photo.view.IPhotoListView;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class PhotoListPresenter<V extends IPhotoListView<PhotoInfo>> extends BasePresenter<V> implements IPhotoListPresenter {
    private IPhotoDataModel mPhotoDataModel;
    private int mUsbFlag = -1;


    // Constructor
    public PhotoListPresenter() {
        mPhotoDataModel = new PhotoDataModel();
        mPhotoDataModel.registerOnRefreshDataListener(mOnRefreshDataListener);
    }

    @Override
    public void setUsbFlag(int usbFlag) {
        this.mUsbFlag = usbFlag;
        if(null != mPhotoDataModel){
            mPhotoDataModel.setUsbFlag(mUsbFlag);
        }
    }

    @Override
    public int getUsbFlag() {
        return this.mUsbFlag;
    }


    @Override
    public void onLoadData() {
        mPhotoDataModel.onLoadPhotoInfoList(mUsbFlag,new IPhotoDataModel.OnLoadDataListener<PhotoInfo>() {
            @Override
            public void onLoadData(List<PhotoInfo> photoInfoList) {
                refreshData(photoInfoList);
            }
        });
    }


    private IPhotoDataModel.OnRefreshDataListener mOnRefreshDataListener = new IPhotoDataModel.OnRefreshDataListener() {
        @Override
        public void onUpdateData(List<PhotoInfo> dataList) {
            refreshData(dataList);
        }
    };

    // 刷新媒体数据
    private void refreshData(final List<PhotoInfo> photoInfoList) {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    if (null != photoInfoList && photoInfoList.size() > 0) {
                        mViewRef.get().onRefreshData(photoInfoList);
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
        mPhotoDataModel.unregisterOnRefreshDataListener();
    }

    @Override
    public void onListPlay(String url) {
        AppActivityManager
                .getInstance()
                .onStopColseAcitvityWith(Definition.AppFlag.TYPE_PHOTO)
                .onCloseOrtherActivity();
        AppUtil.enterPlayerView(Definition.AppFlag.TYPE_PHOTO, url);
    }

    @Override
    public void setTitleToUI(String name, String title) {
        SemiskyIVIManager.getInstance().setTitleName(name,title);
    }
}
