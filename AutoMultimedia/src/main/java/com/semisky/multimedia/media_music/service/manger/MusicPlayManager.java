package com.semisky.multimedia.media_music.service.manger;

/**
 * Created by lcc on 2018/10/25.
 */

public class MusicPlayManager {
    private static MusicPlayManager musicPlayManager;
    private boolean isClickStopMusic;//是否为手动暂停播放
    public static MusicPlayManager getInstance(){
        if (musicPlayManager == null){
            musicPlayManager = new MusicPlayManager();
        }
        return musicPlayManager;
    }

    public void setIsClickStopMusicPlay(boolean isClickStopMusic){
        this.isClickStopMusic = isClickStopMusic;
    }
    public boolean getIsClickStopMusicPlay(){
        return isClickStopMusic;
    }

}
