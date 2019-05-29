package com.semisky.multimedia.media_music.presenter;

import android.util.Log;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.model.IMusicDataModel;
import com.semisky.multimedia.media_music.model.MusicDataModel;
import com.semisky.multimedia.media_music.model.ProxyMusicPlayerModel;
import com.semisky.multimedia.media_music.view.IMusicFavoriteListView;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */
public class MusicFavoriteListPresenter<V extends IMusicFavoriteListView<MusicInfo>> extends BasePresenter<V> implements IMusicFavoriteListPresenter {
    private static final String TAG = Logutil.makeTagLog(MusicFavoriteListPresenter.class);
    private IMusicDataModel mMusicDataModel;
    private ProxyMusicPlayerModel proxyMusicPlayerModel;

    public MusicFavoriteListPresenter() {
//        mMusicDataModel = new MusicDataModel();
//        proxyMusicPlayerModel = ProxyMusicPlayerModel.getInstance();

    }

    @Override
    public void onLoadData() {

    }



    @Override
    public void onListPlay(String url) {
        if(isBindView()){
            Logutil.i(TAG,"onListPlay() ..." + url);
            AppActivityManager
                    .getInstance()
                    .onStopColseAcitvityWith(Definition.AppFlag.TYPE_MUSIC)
                    .onCloseOrtherActivity();
            AppUtil.enterPlayerView(Definition.AppFlag.TYPE_MUSIC,url);
        }
    }

    @Override
    public void onCancelFavoriteWith(String url) {
        proxyMusicPlayerModel.onDeleteFavorite();


    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        mMusicDataModel.unregisterOnRefreshDataListener();
    }
}
