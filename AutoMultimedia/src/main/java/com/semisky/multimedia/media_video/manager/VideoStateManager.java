package com.semisky.multimedia.media_video.manager;

/**
 * Created by lcc on 2018/11/5.
 */

public class VideoStateManager {
    private static VideoStateManager videoStateManager;
    private boolean videoIsClickStop = false;
    private boolean audioFocus_loss = false ;//是否为永久丢失焦点
    public static VideoStateManager getInstance(){
        if (videoStateManager == null){
            videoStateManager = new VideoStateManager();
        }
        return videoStateManager;
    }
    public void setVideoIsClickStop(boolean is){
        videoIsClickStop = is;
    }
    public boolean getVideoIsClickStop (){
        return videoIsClickStop;
    }
    public void setAudioFocus_loss(boolean loss){
        audioFocus_loss = loss;
    }
    public boolean getVideoIsLossFocus(){
        return audioFocus_loss;
    }
    public void clean(){
        videoStateManager = null;
    }
}
