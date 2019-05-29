// IProxyMultimedia.aidl
package com.semisky.multimedia.aidl;


interface IProxyMultimedia {
    /**
    * 获取多媒体应用标识
    **/
    int getMultimediaAppFlag();
    /**
    * 是否为有效的应用标识
    * */
    boolean hasValidAppFlagWith(int appFlag);
    /**
    * 启动多媒体
    **/
    void onLaunchMultimedia();
    /**
    * 启动音乐多媒体音乐播放服务
    **/
    void onLaunchMusicPlayService();
    /**
    * 是否可以进入媒体音乐<br>
    *     @param mUsbFlag : 1 (代表USB1), 2 (代表USB2)
    **/
    boolean canEnterMusic(int mUsbFlag);
    /**
    * 是否可以进入媒体视频<br>
    *     @param mUsbFlag : 1 (代表USB1), 2 (代表USB2)
    **/
    boolean canEnterVideo(int mUsbFlag);
    /**
    * 是否可以进入媒体图片<br>
    *     @param mUsbFlag : 1 (代表USB1), 2 (代表USB2)
    **/
    boolean canEnterPhoto(int mUsbFlag);

}
