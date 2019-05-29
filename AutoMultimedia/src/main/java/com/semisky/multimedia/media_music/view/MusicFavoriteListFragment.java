package com.semisky.multimedia.media_music.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.media_music.adpter.MusicFavoriteListAdapter;
import com.semisky.multimedia.media_music.presenter.MusicFavoriteListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MusicFavoriteListFragment extends BaseFragment<IMusicFavoriteListView<MusicInfo>,MusicFavoriteListPresenter<IMusicFavoriteListView<MusicInfo>>>
        implements IMusicFavoriteListView<MusicInfo>,
        AdapterView.OnItemClickListener,MusicFavoriteListAdapter.OnCancelFavoriteListener,OnItemHighLightChangeCallback{

    private ListView lv_favorite;
    private TextView tv_alert_list_empty;
    private MusicFavoriteListAdapter mMusicFavoriteListAdapter;

    @Override
    protected MusicFavoriteListPresenter<IMusicFavoriteListView<MusicInfo>> createPresenter() {
        return new MusicFavoriteListPresenter();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_music_favorite_list;
    }

    @Override
    protected void initViews() {
        this.lv_favorite = (ListView)mContentView.findViewById(R.id.lv_favorite);
        this.tv_alert_list_empty = (TextView)mContentView.findViewById(R.id.tv_list_state_text);

        this.mMusicFavoriteListAdapter = new MusicFavoriteListAdapter(getActivity());
        this.lv_favorite.setAdapter(mMusicFavoriteListAdapter);
    }

    @Override
    protected void setListener() {
        SemiskyIVIManager.getInstance().setTitleName(this.getClass().getName(),getString(R.string.status_bar_favorite_list_title_text));
        lv_favorite.setOnItemClickListener(this);
        mMusicFavoriteListAdapter.setOnCancelFavoriteListener(this);
        mMusicFavoriteListAdapter.addCallback(this);
    }

    @Override
    protected void initData() {
        if(isBindPresenter()){
            mPresenter.onLoadData();

        }
    }

    @Override
    public void onRefreshData(List<MusicInfo> dataList) {
        if(isBindPresenter()){
            mMusicFavoriteListAdapter.updateList(dataList);
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        if(isBindPresenter()){
            int isVisible = enable ? View.VISIBLE:View.GONE;
            tv_alert_list_empty.setVisibility(isVisible);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isBindPresenter()){
            String url = ((MusicInfo)mMusicFavoriteListAdapter.getItem(position)).getUrl();
            mPresenter.onListPlay(url);
        }
    }

    @Override
    public void onCancelFavoriteWith(String url) {
        mPresenter.onCancelFavoriteWith(url);
    }

    @Override
    public void onRefreshCancelFavoriteItemView() {
        mMusicFavoriteListAdapter.onCancelFavorateItem();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            SemiskyIVIManager.getInstance().setTitleName(this.getClass().getName(),getString(R.string.status_bar_favorite_list_title_text));
        }
    }

    @Override
    public void onItemHighLightChange() {
        int item = mMusicFavoriteListAdapter.getmPositionWithCurrentPlayItem();
        lv_favorite.setSelection(item);
    }

    @Override
    public void onDestroyView() {
        if (null!=mMusicFavoriteListAdapter){
            mMusicFavoriteListAdapter.onRelease();
        }
        super.onDestroyView();

    }
}
