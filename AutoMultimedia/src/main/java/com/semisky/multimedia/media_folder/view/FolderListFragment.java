package com.semisky.multimedia.media_folder.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.constants.Definition.AppFlag;
import com.semisky.multimedia.common.constants.Definition.MediaFileType;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.media_folder.adapter.FolderListAdapter;
import com.semisky.multimedia.media_folder.presenter.FolderListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class FolderListFragment extends BaseFragment<IForlderListView<FolderInfo>, FolderListPresenter<IForlderListView<FolderInfo>>> implements IForlderListView<FolderInfo>, AdapterView.OnItemClickListener,OnItemHighLightChangeCallback {

    private ListView lv_folder;
    private TextView tv_alert_list_empty;
    private FolderListAdapter mFolderListAdapter;


    @Override
    protected FolderListPresenter<IForlderListView<FolderInfo>> createPresenter() {
        return new FolderListPresenter();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tv_alert_list_empty.setText(R.string.tv_alert_folder_list_empty_text);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_folder_list;
    }

    @Override
    protected void initViews() {
        this.lv_folder = (ListView) mContentView.findViewById(R.id.lv_folder);
        this.tv_alert_list_empty = (TextView) mContentView.findViewById(R.id.tv_list_state_text);

        this.mFolderListAdapter = new FolderListAdapter(getActivity());
        this.mFolderListAdapter.addCallback(this);
        this.lv_folder.setAdapter(mFolderListAdapter);
    }

    @Override
    protected void setListener() {
        mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_folder_list_title_text));
        this.lv_folder.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        if (isBindPresenter()) {
            mPresenter.onLoadData();
        }

    }

    @Override
    public void onRefreshData(List<FolderInfo> list) {
        if (isBindPresenter()) {
            this.mFolderListAdapter.updateList(list);
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        if (isBindPresenter()) {
            int isVisible = enable ? View.VISIBLE : View.GONE;
            tv_alert_list_empty.setVisibility(isVisible);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FolderInfo info = ((FolderInfo) mFolderListAdapter.getItem(position));
        String url = info.getUrl();

        switch (info.getType()) {
            case MediaFileType.TYPE_BACK_DIR:
                mPresenter.onOpenDir(url);
                break;
            case MediaFileType.TYPE_FOLDER:
                mPresenter.onOpenDir(url);
                break;
            case MediaFileType.TYPE_MUSIC:
                mPresenter.onPlayList(AppFlag.TYPE_MUSIC,url);
                break;
            case MediaFileType.TYPE_VIDEO:
                mPresenter.onPlayList(AppFlag.TYPE_VIDEO,url);
                break;
            case MediaFileType.TYPE_PHOTO:
                mPresenter.onPlayList(AppFlag.TYPE_PHOTO,url);
                break;
        }
    }

    @Override
    public Activity getmActivity() {
        return getActivity();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_folder_list_title_text));
        }
    }

    @Override
    public void onItemHighLightChange() {
        int item = mFolderListAdapter.getmPositionWithCurrentPlayItem();
        lv_folder.setSelection(item);

    }

    @Override
    public void onDestroyView() {
        if (null!=mFolderListAdapter){
            mFolderListAdapter.onRelease();
        }
        super.onDestroyView();
    }
}
