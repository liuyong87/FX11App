package com.semisky.multimedia.media_list.bean;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/5/5.
 */

public class MusicAlbumInfo {
    private String artistName;
    private String albumName;
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

    public String getAlbumName() {
        return albumName;
    }

    public MusicAlbumInfo(String artistName, String albumName, List<MusicInfo>list){
        this.artistName = artistName;
        this.albumName = albumName;
        this.list = list;
    }
}
