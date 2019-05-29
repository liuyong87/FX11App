package com.semisky.multimedia.media_photo.manager;

/**
 * Created by Administrator on 2018/11/26.
 */

public class PhotoManager {
    private boolean isClickStop = false ;//是否为手动暂停
    private boolean mIsFullScreen = false;// 默认非全屏
    private static PhotoManager photoManager = null;
    public static PhotoManager getPhotoManager(){
        if (photoManager == null){
            photoManager = new PhotoManager();
        }
        return photoManager;
    }
    public void setClickStop(boolean is){
        isClickStop = is;
    }
    public boolean getClickStop(){
        return isClickStop;
    }
    public void setIsFullScreen(boolean isFullScreen){
        mIsFullScreen = isFullScreen ;
    }
    public boolean getIsFullScreen(){
        return mIsFullScreen ;
    }
    public void cleanState(){
        photoManager = null;
    }

}
