package com.semisky.multimedia.media_music.bean;

import com.semisky.multimedia.aidl.music.MusicInfo;


/**
 * Created by Administrator on 2019/5/6.
 */
public class MusicAlbumPicture {
   private MusicInfo musicInfo;

    public MusicInfo getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(MusicInfo musicInfo) {
        this.musicInfo = musicInfo;
    }

    public byte[] getAlbumBytes() {
        return albumBytes;
    }

    public void setAlbumBytes(byte[] albumBytes) {
        this.albumBytes = albumBytes;
    }

    private byte[] albumBytes;
   public MusicAlbumPicture(MusicInfo musicInfo,byte[] bytes){
       this.musicInfo=musicInfo;
       this.albumBytes = bytes;
   }


}
