package com.semisky.multimedia;

/**
 * Created by lcc on 2018/10/9.
 */

public class FactoryTestManager {
    private static FactoryTestManager instances;
    private OnFactoryTestVideoCommandListener videoCommandListener;
    private OnFactoryTestPictureCommandListener pictureCommandListener;
    public static FactoryTestManager getInstances(){
        if (instances == null){
            instances=new FactoryTestManager();
        }
        return instances;
    }
    // 监听视频工厂测试命令接口
    public interface OnFactoryTestVideoCommandListener{
        void onVideoCommand(int cmd);
    }
    // 监听图片工厂测试命令接口
    public interface OnFactoryTestPictureCommandListener{
        void onPictureCommand(int cmd);
    }

    public void registerVideoCmmandListener(OnFactoryTestVideoCommandListener onFactoryTestVideoCommandListener){
        videoCommandListener=onFactoryTestVideoCommandListener;
    }
    public void onRegisterVideoCmmandListener(){
        videoCommandListener=null;
    }
    public void registerPictureCmmandListener(OnFactoryTestPictureCommandListener onFactoryTestPictureCommandListener){
        pictureCommandListener=onFactoryTestPictureCommandListener;
    }
    public void onRegisterPictureCmmandListener(){
        pictureCommandListener=null;
    }

    public void notifyVideoCommand(int cmd){
        if (videoCommandListener!=null){
            videoCommandListener.onVideoCommand(cmd);
        }
    }
    public void notifyPictureCommand(int cmd){
        if (pictureCommandListener!=null){
            pictureCommandListener.onPictureCommand(cmd);
        }
    }

}
