package com.semisky.multimedia.media_music.bean;

import android.media.MediaMetadataRetriever;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.constants.Definition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/6.
 */
public class MusicAlbumParse {
    private static MusicAlbumParse albumParse;
    List<MusicAlbumPicture> musicAlbumPicturesUsbOne = new ArrayList<>();
    List<MusicAlbumPicture> musicAlbumPicturesUsbTwo = new ArrayList<>();
    public static MusicAlbumParse getInstance(){
        if (albumParse == null){
            synchronized (MusicAlbumParse.class){
                if (albumParse == null){
                    albumParse = new MusicAlbumParse();
                }
            }
        }
        return albumParse;
    }

    public List<MusicAlbumPicture> getAlbumPicture(List<MusicInfo> musicInfos,int usbFlag){
       if (usbFlag == Definition.FLAG_USB1){
           for (int i = 0;i < musicAlbumPicturesUsbOne.size();i++){
           }
       }
       return null;

    }
    private List<MusicAlbumPicture> getAlbumPicture(MusicInfo musicInfos){

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        try {

        }catch (Exception e){

        }
        return null;

    }
  public void clearData(int usbFlag){
        if (usbFlag == Definition.FLAG_USB1){
            if (musicAlbumPicturesUsbOne !=null){
                musicAlbumPicturesUsbOne.clear();
            }
        }else if (usbFlag == Definition.FLAG_USB2){
            if (musicAlbumPicturesUsbTwo !=null){
                musicAlbumPicturesUsbTwo.clear();
            }

        }
  }
}
