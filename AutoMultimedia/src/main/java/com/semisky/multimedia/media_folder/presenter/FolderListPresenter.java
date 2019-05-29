package com.semisky.multimedia.media_folder.presenter;

import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaFileType;
import com.semisky.multimedia.common.interfaces.OnLoadDataListener;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_folder.model.FolderDataModel;
import com.semisky.multimedia.media_folder.model.IFolderDataModel;
import com.semisky.multimedia.media_folder.view.IForlderListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class FolderListPresenter<V extends IForlderListView<FolderInfo>> extends BasePresenter<V> implements IFolderListPresenter {
    private static final String TAG = Logutil.makeTagLog(FolderListPresenter.class);
    private IFolderDataModel mFolderDataModel;

    public FolderListPresenter() {
        mFolderDataModel = new FolderDataModel();
    }

    @Override
    public void onLoadData() {
        mFolderDataModel.onLoadData(new OnLoadDataListener<FolderInfo>() {
            @Override
            public void onLoadData(final List<FolderInfo> dataList) {
                Logutil.i(TAG, "onLoadData() ..." + (null != dataList ? dataList.size() : 0));
                if (isBindView()) {
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isBindView()){
                                boolean hasData = null != dataList && dataList.size() > 0 ? true : false;
                                mViewRef.get().onAlertEmptyListTextVisible(!hasData);
                                if (hasData) {
                                    mViewRef.get().onRefreshData(dataList);
                                }
                            }

                        }
                    });
                }
            }
        }, Definition.PATH_USB1/* + Definition.PATH_USB1_DIR_EXTEND*/);

    }

    @Override
    public void onOpenDir(final String dir) {
        Logutil.i(TAG, "onOpenDir() ..." + dir);
        mFolderDataModel.onLoadData(new OnLoadDataListener<FolderInfo>() {
            @Override
            public void onLoadData(final List<FolderInfo> dataList) {
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isBindView()){
                            if (isTopLevelDir(dir)) {
                                mViewRef.get().onRefreshData(dataList);
                            } else {
                                mViewRef.get().onRefreshData(makeBackDirList(dataList, dir));
                            }
                        }
                    }
                });

            }
        }, dir);
    }

    // 是否为顶级目录
    private boolean isTopLevelDir(String dir) {
        boolean isTopLevelDir = false;
        if (null != dir) {
            if (Definition.PATH_USB1.equals(dir)) {
                isTopLevelDir = true;
            } else if (Definition.PATH_USB2.equals(dir)) {
                isTopLevelDir = true;
            }
        }
        Logutil.i(TAG, "isTopLevelDir() ..." + isTopLevelDir);
        return isTopLevelDir;
    }

    private List<FolderInfo> makeBackDirList(List<FolderInfo> folderInfos, String dir) {
        Logutil.i(TAG, "makeBackDirList() folderInfos.size=" + folderInfos.size() + ", dir=" + dir);
        if (null != folderInfos) {
            if (!folderInfos.isEmpty()) {
                String preDir = dir.substring(0, dir.lastIndexOf("/"));
                Logutil.i(TAG, "makeBackDirList() preDir =" + preDir);
                FolderInfo folderInfo = new FolderInfo();
                folderInfo.setType(MediaFileType.TYPE_BACK_DIR);
                folderInfo.setUrl(preDir);
                folderInfo.setName(new File(dir).getName());

                List<FolderInfo> list = new ArrayList<FolderInfo>();
                list.add(folderInfo);
                list.addAll(list.size(), folderInfos);
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void onPlayList(int appFlag, String url) {
        if (appFlag >= Definition.AppFlag.TYPE_MUSIC && appFlag <= Definition.AppFlag.TYPE_PHOTO) {
            AppUtil.enterPlayerView(appFlag, url);
            mViewRef.get().getmActivity().finish();
        }
    }

    @Override
    public void setTitleToUI(String name, String title) {
        SemiskyIVIManager.getInstance().setTitleName(name,title);
    }
}
