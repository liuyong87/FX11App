package com.semisky.multimedia.media_music.presenter;

import android.view.View;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.MusicDataRetrieve;
import com.semisky.multimedia.common.utils.USBCheckUtil;
import com.semisky.multimedia.media_list.bean.FolderInfo;
import com.semisky.multimedia.media_list.bean.MusicAlbumInfo;
import com.semisky.multimedia.media_list.bean.MusicArtistInfo;
import com.semisky.multimedia.media_music.model.IMusicDataModel;
import com.semisky.multimedia.media_music.model.IMusicDataModel.OnLoadDataListener;
import com.semisky.multimedia.media_music.model.MusicDataModel;
import com.semisky.multimedia.media_music.view.IMusicListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MusicListPresenter<V extends IMusicListView<MusicInfo>> extends BasePresenter<V> implements IMusicListPresenter {
    private static final String TAG = Logutil.makeTagLog(MusicListPresenter.class);
    private IMusicDataModel mMusicDataModel;
    private int mUsbFlag = 0;
    private List<MusicInfo> allMusic;
    private List<MusicInfo> artist = new ArrayList<MusicInfo>();
    private List<MusicInfo> album = new ArrayList<MusicInfo>();
    private List<MusicInfo> folder = new ArrayList<MusicInfo>();


    public MusicListPresenter(int mUsbFlag) {
        this.mUsbFlag = mUsbFlag;
        this.mMusicDataModel = new MusicDataModel(mUsbFlag);
        mMusicDataModel.registerOnRefreshDataListener(mOnRefreshDataListener);
        USBManager.getInstance().registerOnUSBStateChangeListener(onUSBStateChangeListener);

    }

    private USBManager.OnUSBStateChangeListener onUSBStateChangeListener = new USBManager.OnUSBStateChangeListener() {
        @Override
        public void onChangeState(String usbPath, int stateCode) {
            if (!isBindView()) {
                return;
            }
            switch (stateCode) {
                case USBManager.STATE_USB_REMOVED:
                    if (mUsbFlag == AppUtil.conversionUsbPathToUsbFlag(usbPath)) {
                        showTextUsbDisconnected();
                    }
                    break;
                case USBManager.STATE_USB_MOUNTED:
                    if (mUsbFlag == AppUtil.conversionUsbPathToUsbFlag(usbPath)) {
                        showTextUsbLoading();
                    }
                    break;
            }

        }
    };

    @Override
    public void onLoadData() {
        if (mUsbFlag == Definition.FLAG_USB1) {

            if (!USBCheckUtil.isUdiskExist(Definition.PATH_USB1)) {
                showTextUsbDisconnected();
                return;
            }
        } else if (mUsbFlag == Definition.FLAG_USB2) {
            if (!USBCheckUtil.isUdiskExist(Definition.PATH_USB2)) {
                showTextUsbDisconnected();
                return;
            }
        }

        mMusicDataModel.onLoadMusicInfoList(new OnLoadDataListener<MusicInfo>() {
            @Override
            public void onLoadData(List<MusicInfo> dataList) {
                refreshFirstData(dataList);
            }
        }, mUsbFlag);
    }

    private IMusicDataModel.OnRefreshDataListener mOnRefreshDataListener = new IMusicDataModel.OnRefreshDataListener() {
        @Override
        public void onUpdateData(final List<MusicInfo> dataList, boolean isScanEnd) {
            refreshData(dataList, isScanEnd);
        }
    };


    // 刷新媒体数据列表
    private void refreshData(final List<MusicInfo> dataList, final boolean isScanEnd) {
        if (isBindView()) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mViewRef == null || mViewRef.get() == null) {
                        //bug 极限操作 可能造成mViewRef 为空
                        return;
                    }
                    if (null != dataList && dataList.size() > 0) {
                        allMusic = dataList;
                        mViewRef.get().onRefreshData(dataList);
                        mViewRef.get().onAlertEmptyListTextVisible(false);
                        return;
                    }
                    if (isScanEnd) {
                        mViewRef.get().onAlertEmptyListTextVisible(true);
                    } else {
                        showTextUsbLoading();
                    }

                }
            });
        }

    }

    // 刷新首次媒体数据列表
    private void refreshFirstData(final List<MusicInfo> dataList) {
        if (isBindView()) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != dataList && dataList.size() > 0) {
                        allMusic = dataList;
                        mViewRef.get().onFirstRefreshData(dataList);
                        mViewRef.get().onAlertEmptyListTextVisible(false);
                        return;
                    }
                    if (mMusicDataModel.isMediaScanFinished()) {
                        mViewRef.get().onAlertEmptyListTextVisible(true);
                    } else {
                        showTextUsbLoading();
                    }

                }
            });
        }

    }

    @Override
    public void onListPlay(String url) {
        if (isBindView()) {
            Logutil.i(TAG, "onListPlay() ..." + url);
            AppActivityManager
                    .getInstance()
                    .onStopColseAcitvityWith(Definition.AppFlag.TYPE_MUSIC)
                    .onCloseOrtherActivity();
            AppUtil.enterPlayerView(Definition.AppFlag.TYPE_MUSIC, url);
        }
    }

    @Override
    public void setTitleToUI(String name, String title) {
        SemiskyIVIManager.getInstance().setTitleName(name, title);
    }

    @Override
    public void onMusicInfoChange(int change) {
        if (isBindView()) {
            mViewRef.get().showMusicInfo(change);
            backRestoreMusicInfo(mViewRef.get().getSelectoyType());
            mViewRef.get().showMusicOtherInfo(View.GONE);
        }
    }

    @Override
    public void onMusicOtherInfo(int change) {
        if (isBindView()) {
            mViewRef.get().showMusicOtherInfo(change);
            mViewRef.get().showMusicInfo(View.GONE);
        }
    }

    @Override
    public void setAllMusicInfo() {
        if (isBindView()) {
            mViewRef.get().showAllMusicInfo(getAllMusic());
        }
    }

    @Override
    public void setArtistInfo() {
        if (isBindView()) {
            mViewRef.get().showArtistMusicInfo(getArtist());
        }
    }

    @Override
    public void setAlbumInfo() {
        if (isBindView()) {
            mViewRef.get().showAlbumMusicInfo(getAlbum());
        }
    }

    @Override
    public void setFolder() {
        if (isBindView()) {
            mViewRef.get().showFolder(getFolder());
        }

    }

    private void showTextUsbDisconnected() {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    mViewRef.get().showTextUsbDisconnected();
                }
            }
        });
    }

    private void showTextUsbLoading() {
        if (isBindView()) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().showTextUsbLoading();
                    }
                }
            });
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        mMusicDataModel.unregisterOnRefreshDataListener();
        USBManager.getInstance().unregisterOnUSBStateChangeListener(onUSBStateChangeListener);
    }

    private List<MusicInfo> getAllMusic() {
        return allMusic;
    }

    private List<MusicArtistInfo> getArtist() {
        artist.clear();
        artist.addAll(allMusic);
        List<MusicArtistInfo> list = MusicDataRetrieve.getInstance().getArtist(artist);
        return list;
    }

    private List<MusicAlbumInfo> getAlbum() {
        album.clear();
        album.addAll(allMusic);
        List<MusicAlbumInfo> map = MusicDataRetrieve.getInstance().getAlbum(album);
        return map;
    }

    private List<FolderInfo> getFolder() {
        folder.clear();
        folder.addAll(allMusic);
        List<FolderInfo> folderInfos = MusicDataRetrieve.getInstance().getFolderInfo(folder);
        return folderInfos;
    }

    private void backRestoreMusicInfo(int type) {
        if (type == 0) {
            if (isBindView()) {
                mViewRef.get().showArtistMusicInfo(getArtist());
            }
        } else if (type == 1) {
            if (isBindView()) {
                mViewRef.get().showAlbumMusicInfo(getAlbum());
            }
        } else if (type == 2) {
            if (isBindView()) {
                mViewRef.get().showFolder(getFolder());
            }
        }
    }


}
