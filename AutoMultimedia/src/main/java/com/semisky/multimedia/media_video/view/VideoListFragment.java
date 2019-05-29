package com.semisky.multimedia.media_video.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_video.adapter.VideoListHorizontalAdapter;
import com.semisky.multimedia.media_video.presenter.VideoListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class VideoListFragment extends BaseFragment<IVideoListView<VideoInfo> ,VideoListPresenter<IVideoListView<VideoInfo>>> implements
        IVideoListView<VideoInfo>,VideoListHorizontalAdapter.OnHorizontalItemClickListener{
    private static final String TAG = Logutil.makeTagLog(VideoListFragment.class);
    private VideoListHorizontalAdapter mVideoListHorizontalAdapter;

    // RecyclerView 相关成员变量
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    private TextView tv_alert_list_empty;

    private int mUsbFlag = 1;

    public void setUsbFlag(int usbFlag){
        this.mUsbFlag = usbFlag;
    }

    public int getUsbFlag(){
        return mUsbFlag;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_video_list;
    }

    @Override
    protected void initViews() {
        tv_alert_list_empty = (TextView)mContentView.findViewById(R.id.tv_list_state_text);
        mRecyclerView = mContentView.findViewById(R.id.recyclerview);
    }

    @Override
    protected void setListener() {
        mVideoListHorizontalAdapter = new VideoListHorizontalAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mVideoListHorizontalAdapter);
        mVideoListHorizontalAdapter.setmOnHorizontalItemClickListener(this);
    }

    @Override
    protected void initData() {
        if(isBindPresenter()){
            mPresenter.setUsbFlag(mUsbFlag);
            mPresenter.onLoadData();
        }
    }

    @Override
    protected VideoListPresenter<IVideoListView<VideoInfo>> createPresenter() {
        return new VideoListPresenter();
    }

    @Override
    public void onFirstRefreshData(List<VideoInfo> dataList) {
        if(isBindPresenter()){
            Logutil.i(TAG,"onFirstRefreshData() ...");
            mVideoListHorizontalAdapter.updateList(dataList);
        }
    }

    @Override
    public void onRefreshData(List<VideoInfo> dataList) {
        if(isBindPresenter()){
            Logutil.i(TAG,"onRefreshData() ...");
            mVideoListHorizontalAdapter.updateList(dataList);
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        int isVisible = enable ? View.VISIBLE:View.GONE;
        tv_alert_list_empty.setVisibility(isVisible);
    }

    @Override
    public void onItemClick(View view, int position) {
        String url = mVideoListHorizontalAdapter.getList().get(position).getFileUrl();
        if(isBindPresenter()){
            mPresenter.onPlayList(url);
            getActivity().overridePendingTransition(0,0);
        }
    }

 /*   @Override
    public void onItemHighLightChange() {
        int item = mVideoListAdapter.getmPositionWithCurrentPlayItem();
//        lv_video.setSelection(item);
    }*/

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
        }
    }

    @Override
    public void onDestroyView() {
//        mVideoListAdapter.onRelease();
        super.onDestroyView();
    }
}
