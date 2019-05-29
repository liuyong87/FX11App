package com.semisky.multimedia.media_photo.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_photo.adapter.PhotoListHorizontalAdapter;
import com.semisky.multimedia.media_photo.presenter.PhotoListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class PhotoListFragment extends BaseFragment<IPhotoListView<PhotoInfo>, PhotoListPresenter<IPhotoListView<PhotoInfo>>> implements IPhotoListView<PhotoInfo>
        , PhotoListHorizontalAdapter.OnHorizontalItemClickListener {
    private static final String TAG = Logutil.makeTagLog(PhotoListFragment.class);
    private PhotoListHorizontalAdapter mPhotoListHorizontalAdapter;
    private RecyclerView mRecyclerView;
    private TextView tv_alert_list_empty,tv_count_num;

    private int mUsbFlag = 1;

    public void setUsbFlag(int usbFlag){
        this.mUsbFlag = usbFlag;
    }

    public int getUsbFlag(){
        return mUsbFlag;
    }

    @Override
    protected PhotoListPresenter<IPhotoListView<PhotoInfo>> createPresenter() {
        return new PhotoListPresenter();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_photo_list;
    }

    @Override
    protected void initViews() {
        tv_alert_list_empty = (TextView)mContentView.findViewById(R.id.tv_list_state_text);
        tv_count_num = (TextView)mContentView.findViewById(R.id.tv_count_num);

        mRecyclerView = (RecyclerView)mContentView.findViewById(R.id.recyclerview);
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    protected void setListener() {
//        mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_photo_list_title_text));
        mPhotoListHorizontalAdapter = new PhotoListHorizontalAdapter();
        mRecyclerView.setAdapter(mPhotoListHorizontalAdapter);
        mPhotoListHorizontalAdapter.setOnHorizontalItemClickListener(this);
    }


    @Override
    public void onItemClick( View view, int position) {
        if (isBindPresenter()) {
            Logutil.i(TAG, "onItemClick() position=" + position);
            String url =  mPhotoListHorizontalAdapter.getList().get(position).getFileUrl();
            Logutil.i(TAG, "onItemClick() url=" + url);
            mPresenter.onListPlay(url);
        }
    }

    @Override
    protected void initData() {
        if (isBindPresenter()) {
            mPresenter.setUsbFlag(mUsbFlag);
            mPresenter.onLoadData();
        }
    }

    @Override
    public void onRefreshData(List<PhotoInfo> dataList) {
        if (isBindPresenter()) {

            mPhotoListHorizontalAdapter.updateList(dataList);
            tv_count_num.setText(dataList.size()+"");
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        int isVisible = enable ? View.VISIBLE : View.GONE;
        tv_alert_list_empty.setVisibility(isVisible);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_photo_list_title_text));
        }
    }
}
