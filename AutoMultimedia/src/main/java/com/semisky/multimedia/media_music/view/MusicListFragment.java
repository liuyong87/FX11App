package com.semisky.multimedia.media_music.view;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.bean.FolderInfo;
import com.semisky.multimedia.media_list.bean.MusicAlbumInfo;
import com.semisky.multimedia.media_list.bean.MusicArtistInfo;
import com.semisky.multimedia.media_music.adpter.MusicAlbumAdapter;
import com.semisky.multimedia.media_music.adpter.MusicArtistAdapter;
import com.semisky.multimedia.media_music.adpter.MusicFolderAdapter;
import com.semisky.multimedia.media_music.adpter.MusicListRecyclerViewAdapter;
import com.semisky.multimedia.media_music.adpter.SpaceItemDecoration;
import com.semisky.multimedia.media_music.presenter.MusicListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */
public class MusicListFragment extends BaseFragment<IMusicListView<MusicInfo>, MusicListPresenter<IMusicListView<MusicInfo>>> implements
        IMusicListView<MusicInfo>,
        OnItemHighLightChangeCallback, View.OnClickListener {
    private static final String TAG = Logutil.makeTagLog(MusicListFragment.class);
    private MusicListRecyclerViewAdapter mMusicListAdapter;
    private MusicArtistAdapter artistAdapter;
    private MusicAlbumAdapter  albumAdapter;
    private MusicFolderAdapter folderAdapter;
    private RecyclerView lv_music;
    private TextView tv_list_state_text;
    private TextView tv_allMusic, tv_artist, tv_album, tv_folder,tv_musicOtherMusic;
    private ImageView im_search;
    private TextView tv_count;
    private RelativeLayout rl_horizontal_bar,rlMusicOtherInfo;
    private ImageView goPlaying,iv_back;
    TextView[] textViews;
    private int touchFlag = 0;
    private int selectorType = 0;// 0 =》歌手，1 =》专辑，2 =》文件夹
    GridLayoutManager manager;


    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_music_list;
    }

    @Override
    protected void initViews() {
        mMusicListAdapter = new MusicListRecyclerViewAdapter();
        textViews = new TextView[4];
        tv_list_state_text = (TextView) mContentView.findViewById(R.id.tv_list_state_text);
        lv_music = (RecyclerView) mContentView.findViewById(R.id.recyclerList);
        tv_allMusic = (TextView) mContentView.findViewById(R.id.all_music);
        textViews[0] = tv_allMusic;
        tv_artist = (TextView) mContentView.findViewById(R.id.artist);
        textViews[1] = tv_artist;
        tv_album = (TextView) mContentView.findViewById(R.id.album);
        textViews[2] = tv_album;
        tv_folder = (TextView) mContentView.findViewById(R.id.folder);
        textViews[3] = tv_folder;
        tv_count = (TextView) mContentView.findViewById(R.id.tv_count);
        im_search = (ImageView) mContentView.findViewById(R.id.image_search);
        rl_horizontal_bar = (RelativeLayout) mContentView.findViewById(R.id.rl_horizontal_bar);
        goPlaying = (ImageView) mContentView.findViewById(R.id.im_go_playing);
        touchFlag = R.id.all_music;
        rlMusicOtherInfo = (RelativeLayout) mContentView.findViewById(R.id.rl_musicOtherInfo) ;
        iv_back = (ImageView) mContentView.findViewById(R.id.iv_back) ;
        tv_musicOtherMusic = (TextView) mContentView.findViewById(R.id.tv_otherInfo);
        setTextColor(tv_allMusic);
    }

    @Override
    protected void setListener() {
        manager = new GridLayoutManager(getActivity(), 3);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.lv_music.addItemDecoration(new SpaceItemDecoration(104));
        this.lv_music.setLayoutManager(manager);
        mMusicListAdapter.setAdapterOnClick(onClick);
        this.lv_music.setAdapter(mMusicListAdapter);
        tv_allMusic.setOnClickListener(this);
        tv_artist.setOnClickListener(this);
        tv_album.setOnClickListener(this);
        tv_folder.setOnClickListener(this);
        im_search.setOnClickListener(this);
        goPlaying.setOnClickListener(this);
        iv_back.setOnClickListener(this);
//        mMusicListAdapter.addCallback(this);
//        this.lv_music.setOnItemClickListener(this);
    }


    @Override
    protected void initData() {
        if (isBindPresenter()) {
            mPresenter.onLoadData();
        }
    }

    @Override
    protected MusicListPresenter<IMusicListView<MusicInfo>> createPresenter() {
        return new MusicListPresenter(getUsbFlagIndex());
    }

    @Override
    public void onFirstRefreshData(List<MusicInfo> dataList) {
        if (isBindPresenter()) {
            this.mMusicListAdapter.setData(dataList);
            //  mMusicListAdapter.onItemHighLightChange();
        }
    }

    @Override
    public void onRefreshData(List<MusicInfo> dataList) {
        if (isBindPresenter()) {
            this.mMusicListAdapter.setData(dataList);
//            mMusicListAdapter.onItemHighLightChange(); // bug 7398 . 如果数据正在更新中，滑动列表时将会自动显示到高亮的位置
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        int isVisible = enable ? View.VISIBLE : View.GONE;
        if (enable) {
            tv_list_state_text.setText(getString(R.string.tv_alert_music_list_empty_text));
            setViewsVisibility(View.GONE);
        } else {
            if (rlMusicOtherInfo.getVisibility() == View.GONE){
                setViewsVisibility(View.VISIBLE);
            }
        }
        tv_list_state_text.setVisibility(isVisible);
    }

    @Override
    public void showTextUsbDisconnected() {
        tv_list_state_text.setText(getString(R.string.tv_usb_disconnect));
        tv_list_state_text.setVisibility(View.VISIBLE);
        setViewsVisibility(View.GONE);
        cleanData();

    }

    @Override
    public void showTextUsbLoading() {
        tv_list_state_text.setText(getString(R.string.tv_usb_loading));
        tv_list_state_text.setVisibility(View.VISIBLE);
        setViewsVisibility(View.GONE);
    }


    @Override
    public int getUsbFlagIndex() {
        Bundle bundle = getArguments();
        int usbFlag = bundle.getInt(Definition.MediaListConst.FRAGMENT_FLAG);
        Log.i(TAG, "getUsbFlagIndex = " + usbFlag);
        return usbFlag;
    }

    @Override
    public void showMusicInfo(int gone) {
        rl_horizontal_bar.setVisibility(gone);
    }

    @Override
    public void showMusicOtherInfo(int gone) {
        rlMusicOtherInfo.setVisibility(gone);
    }

    @Override
    public int getSelectoyType() {
        return selectorType;
    }

    @Override
    public void showAllMusicInfo(List<MusicInfo> list) {
        if (null == mMusicListAdapter){
            mMusicListAdapter =new MusicListRecyclerViewAdapter();
            mMusicListAdapter.setAdapterOnClick(onClick);
        }
        setRecyclerViewManager();
        lv_music.setAdapter(mMusicListAdapter);
        mMusicListAdapter.setData(list);
        setTextColor(textViews[0]);

    }

    @Override
    public void showArtistMusicInfo(List<MusicArtistInfo> musicInfo) {
        if (null == artistAdapter){
            artistAdapter = new MusicArtistAdapter();
            artistAdapter.setAdapterOnClick(adapterOnClickArtist);
        }
        setRecyclerViewManager();
        lv_music.setAdapter(artistAdapter);
        artistAdapter.setData(musicInfo);
        setTextColor(textViews[1]);

    }

    @Override
    public void showAlbumMusicInfo(List<MusicAlbumInfo> musicInfo) {
        if (null == albumAdapter){
            albumAdapter = new MusicAlbumAdapter();
            albumAdapter.setAdapterOnClick(adapterOnClickAlbum);
        }
        setRecyclerViewManager();
        lv_music.setAdapter(albumAdapter);
        albumAdapter.setData(musicInfo);
        setTextColor(textViews[2]);

    }

    @Override
    public void showFolder(List<FolderInfo> folderInfos) {
        if (null == folderAdapter){
            folderAdapter = new MusicFolderAdapter();
            folderAdapter.setAdapterOnClick(adapterOnClickFolder);
        }
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.lv_music.setLayoutManager(manager);
        lv_music.setAdapter(folderAdapter);
        folderAdapter.setData(folderInfos);
    }


    @Override
    public void onItemHighLightChange() {
//        int item = mMusicListAdapter.getmPositionWithCurrentPlayItem();
//        lv_music.setSelection(item);

    }

    @Override
    public void onDestroyView() {
        if (null != mMusicListAdapter) {

        }
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.setTitleToUI(this.getClass().getName(), getString(R.string.status_bar_music_list_title_text));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_music:
                if (v.getId() == touchFlag){
                    return;
                }
                mPresenter.setAllMusicInfo();
                break;
            case R.id.artist:
                if (v.getId() == touchFlag){
                    return;
                }
                mPresenter.setArtistInfo();
                break;
            case R.id.album:
                if (v.getId() == touchFlag){
                    return;
                }
                mPresenter.setAlbumInfo();
                break;
            case R.id.folder:
                if (v.getId() == touchFlag){
                    return;
                }
                mPresenter.setFolder();
                setTextColor(textViews[3]);
                break;
            case R.id.image_search:
                break;
            case R.id.im_go_playing:
                AppUtil.enterPlayerView(Definition.AppFlag.TYPE_MUSIC);
                getActivity().finish();
                getActivity().overridePendingTransition(0,0);
                break;
            case R.id.iv_back:
                mPresenter.onMusicInfoChange(View.VISIBLE);
                break;
        }
    }

    private MusicListRecyclerViewAdapter.AdapterOnClick onClick = new MusicListRecyclerViewAdapter.AdapterOnClick() {
        @Override
        public void onItemClick(MusicInfo musicInfo) {
              getActivity().finish();
              mPresenter.onListPlay(musicInfo.getUrl());

        }
    };
    private MusicArtistAdapter.AdapterOnClickArtist adapterOnClickArtist =new MusicArtistAdapter.AdapterOnClickArtist() {
        @Override
        public void onItemClick(List<MusicInfo> musicInfo) {
            selectorType = 0;
            mPresenter.onMusicOtherInfo(View.VISIBLE);
            tv_musicOtherMusic.setText(musicInfo.get(0).getArtist());
            lv_music.setAdapter(mMusicListAdapter);
            mMusicListAdapter.setData(musicInfo);


        }
    };
    private MusicAlbumAdapter.AdapterOnClickAlbum adapterOnClickAlbum =new MusicAlbumAdapter.AdapterOnClickAlbum() {
        @Override
        public void onItemClick(List<MusicInfo> musicInfo) {
            selectorType = 1;
            mPresenter.onMusicOtherInfo(View.VISIBLE);
            tv_musicOtherMusic.setText(musicInfo.get(0).getArtist());
            lv_music.setAdapter(mMusicListAdapter);
            mMusicListAdapter.setData(musicInfo);
        }
    };

    private MusicFolderAdapter.AdapterOnClickFolder adapterOnClickFolder = new MusicFolderAdapter.AdapterOnClickFolder() {
        @Override
        public void onItemClick(List<MusicInfo> musicInfo,String folderName) {
            selectorType = 2;
            mPresenter.onMusicOtherInfo(View.VISIBLE);
            tv_musicOtherMusic.setText(folderName);
            setRecyclerViewManager();
            lv_music.setAdapter(mMusicListAdapter);
            mMusicListAdapter.setData(musicInfo);

        }
    };

    /**
     * view 显示字体颜色
     *
     * @param view
     */
    private void setTextColor(TextView view) {
        touchFlag = view.getId();
        view.setTextColor(getResources().getColor(R.color.white));
        for (TextView view1 : textViews) {
            if (view1.getId() != view.getId()) {
                view1.setTextColor(getResources().getColor(R.color.colorArtist));
            }
        }
    }

    /**
     * view 是否可见
     * @param visibility
     */

    private void setViewsVisibility(int visibility) {
        rl_horizontal_bar.setVisibility(visibility);
        goPlaying.setVisibility(visibility);
        tv_count.setVisibility(visibility);
    }

    private void cleanData(){
        if (mMusicListAdapter != null){
            mMusicListAdapter.setData(null);
        }
        if (artistAdapter != null){
            artistAdapter.setData(null);
        }
        if (albumAdapter !=null){
            albumAdapter.setData(null);
        }
    }
    private void setRecyclerViewManager(){
         lv_music.setLayoutManager(manager);
    }

}
