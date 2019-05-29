package com.semisky.multimedia.media_list.bean;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/5/5.
 */

public class MusicArtistInfo {
    private String artistName;
    private List<MusicInfo> list;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public List<MusicInfo> getList() {
        return list;
    }

    public void setList(List<MusicInfo> list) {
        this.list = list;
    }

    public MusicArtistInfo(String artistName,List<MusicInfo>list){
        this.artistName = artistName;
        this.list = list;
    }

}
