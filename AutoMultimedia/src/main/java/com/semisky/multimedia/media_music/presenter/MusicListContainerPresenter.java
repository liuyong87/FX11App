package com.semisky.multimedia.media_music.presenter;

import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.media_music.model.ProxyMusicPlayerModel;
import com.semisky.multimedia.media_music.view.IMusicListContainerView;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;

public class MusicListContainerPresenter<V extends IMusicListContainerView> extends BasePresenter<V> implements IMusicListContainerPresenter{

    private MediaStorageAccessProxyModel mDataModel;
    private ProxyMusicPlayerModel mPlayer;

    // Controctor
   public MusicListContainerPresenter(){
       mDataModel = MediaStorageAccessProxyModel.getInstance();
       mPlayer = ProxyMusicPlayerModel.getInstance();
   }

    @Override
    public void handlerSidebarEventByUsb1() {
        if(!isBindView()){
            return;
        }
       /* boolean isMountedByUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
        int listSize = mDataModel.queryMusicsSize(Definition.FLAG_USB1);

        if(isMountedByUsb1 && listSize > 0){
            // 切换USB1源
            mPlayer.changeAudioSource(Definition.FLAG_USB1);
            return;
        }*/
        mViewRef.get().showContentByUsb1();

    }


    @Override
    public void handlerSidebarEventByUsb2() {
        if(!isBindView()){
            return;
        }
        mViewRef.get().showContentByUsb2();
    }

    @Override
    public void handlerSidebarEventByBt() {
        if(!isBindView()){
            return;
        }
        mViewRef.get().showContentByBt();
    }


}
