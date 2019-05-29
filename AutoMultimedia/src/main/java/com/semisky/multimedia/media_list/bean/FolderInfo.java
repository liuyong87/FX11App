package com.semisky.multimedia.media_list.bean;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/5/10.
 */
public class FolderInfo {
    private String folderName;

    private List<MusicInfo> musicInfos;

    public  FolderInfo(String folderName,List<MusicInfo>list){
        this.folderName = folderName;
        this.musicInfos = list;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<MusicInfo> getMusicInfos() {
        return musicInfos;
    }

    public void setMusicInfos(List<MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
    }



}
