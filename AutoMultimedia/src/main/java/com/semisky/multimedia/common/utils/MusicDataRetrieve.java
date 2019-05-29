package com.semisky.multimedia.common.utils;

import android.util.Log;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_list.bean.FolderInfo;
import com.semisky.multimedia.media_list.bean.MusicAlbumInfo;
import com.semisky.multimedia.media_list.bean.MusicArtistInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/5/5.
 */


public class MusicDataRetrieve {
    public static MusicDataRetrieve musicDataRetrieve = null;
    public static MusicDataRetrieve getInstance(){
        if (musicDataRetrieve == null){
            synchronized (MusicDataRetrieve.class){
                if (musicDataRetrieve == null){
                    musicDataRetrieve = new MusicDataRetrieve();
                }
            }
        }
        return musicDataRetrieve;
    }
    public List<MusicArtistInfo> getArtist(List<MusicInfo> list){
        if (list == null || list.size()<=0) {
            return null;
        }
        List<MusicArtistInfo> musicArtistInfos = new ArrayList<MusicArtistInfo>();
        musicArtistInfos.clear();
        String artistName = null;
        List<String> strings =new ArrayList<>();
        strings.clear();
        for (int i = 0;i < list.size();i++) {
            artistName = list.get(i).getArtist();
            if (strings.contains(artistName)) {
            } else {
                strings.add(artistName);
            }
        }

        for (int i = 0;i < strings.size();i++){
            String artist = strings.get(i);
            List<MusicInfo> musicInfoList = getArtistList(list,artist);
            if (musicInfoList.size() > 0){
                musicArtistInfos.add(new MusicArtistInfo(artist,musicInfoList));
            }
        }
        return musicArtistInfos;
    }
    private List<MusicInfo> getArtistList(List<MusicInfo> list,String name){
        List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
        musicInfos.clear();
        for (int i = 0;i<list.size();i++){
            String artistName = list.get(i).getArtist();
            if (artistName.equals(name)){
                 musicInfos.add(list.get(i));
            }
        }
        return musicInfos;

    }

    public List<MusicAlbumInfo> getAlbum(List<MusicInfo> list){
        if (list == null || list.size()<=0) {
            return null;
        }
        List<MusicAlbumInfo> hashMap = new ArrayList<MusicAlbumInfo>();
        hashMap.clear();
        String albumName = null;
        List<String> strings =new ArrayList<>();
        strings.clear();
        for (int i = 0;i < list.size();i++){
            albumName = list.get(i).getAlbum();
            if (!strings.contains(albumName)){
                strings.add(albumName);
            }
        }
        for (int i = 0;i < strings.size();i++){
            String album = strings.get(i);
            List<MusicInfo> list1 = getAlbumList(list,album);
            hashMap.add(new MusicAlbumInfo(list1.get(0).getArtist(),album,list1));
        }

        return hashMap;
    }
    private List<MusicInfo> getAlbumList(List<MusicInfo> list,String name){
        List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
        musicInfos.clear();
        for (int i = 0;i<list.size();i++){
            String artistName = list.get(i).getAlbum();
            if (artistName.equals(name)){
                musicInfos.add(list.get(i));
            }
        }
        return musicInfos;

    }

    public List<FolderInfo> getFolderInfo(List<MusicInfo> musicInfoList){
        if (musicInfoList == null || musicInfoList.size()<=0) {
            return null;
        }
        List<FolderInfo> list = new ArrayList<FolderInfo>();
        list.clear();
        String folderName = null;
        List<String> strings =new ArrayList<>();
        strings.clear();
        for (int i = 0;i < musicInfoList.size();i++){
            folderName = musicInfoList.get(i).getFileFolder();
            if (!strings.contains(folderName)){
                strings.add(folderName);
            }
        }
        for (int i = 0;i < strings.size();i++){
            String folder = strings.get(i);
            List<MusicInfo> musicInfos = getFolderList(musicInfoList,folder);
            if (musicInfos.size() > 0){
                list.add(new FolderInfo(folder,musicInfos));
            }

        }
        Log.i("lcc", "getFolderInfo: "+list.size());
        return list;
    }
    private List<MusicInfo> getFolderList(List<MusicInfo> list,String name){
        List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
        musicInfos.clear();
        for (int i = 0;i<list.size();i++){
            String FolderName = list.get(i).getFileFolder();
            if (FolderName.equals(name)){
                musicInfos.add(list.get(i));
            }
        }
        return musicInfos;

    }

}
