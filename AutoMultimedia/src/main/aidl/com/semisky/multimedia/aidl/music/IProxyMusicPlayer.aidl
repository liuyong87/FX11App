package com.semisky.multimedia.aidl.music;

import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;

interface IProxyMusicPlayer{
	/**
	* 注册曲目改变监听接口
	**/
	void registerCallback(IProxyProgramChangeCallback callback);
	/**
	* 反注册曲目改变监听接口
	**/
	void unregisterCallback(IProxyProgramChangeCallback callback);
	/**
	* 获取歌曲名
	**/
	String getSongName();
	/**
	* 获取歌手名
	**/
	String getArtistName();
	/**
	* 获取专辑名
	**/
	String getAlbumName();
	/**
	* 获取媒体总进度
	**/
	int getTotalPorgress();
	/**
	* 获取媒体当前进度
	**/
	int getCurrentProgress();
	/**
	* 获取当前播放曲目位置
	**/
	String getCurProgramPos();
	/**
	* 是否收藏
	**/
	boolean isFavorite();
	/**
	* 获取播放模式
	**/
	int getPlayMode();
	/**
	* 是否播放
	**/
	boolean isPlaying();
	/*----------- play control -----------*/
	/**
	* 切换下一曲目
	**/
	void onSwitchNext();
	/**
	* 切换上一曲目
	**/
	void onSwitchPrev();
	/**
	* 切换快进
	**/
	void onSwitchFastForward();
    /**
     * 停止快进
     **/
    void onSwitchStopFastForward();
	/**
	* 切换快退
	**/
	void onSwitchFastBackward();
     /**
     * 停止快退
     **/
    void onSwitchStopFastBackward();
	/**
	* 切换播放或暂停
	**/
	void onSwitchPlayOrPause();
	/**
    * 恢复播放操作（前置条件：之前是播放状态）
    **/
	void onSwitchResumePlay();
	/**
	* 切换播放模式
	**/
	void onSwitchPlayMode();
	/**
	* 切换收藏
	**/
	void onSwitchFavorite();
	/**
    * 设置播放器进度
    **/
	void onSeekTo(int progress);
	/**
    * 设置更新进度线程使能状态
    **/
	void onUpdateProgressWithThreadEnabled(boolean enabled);
	/**
	* 删除收藏，通知到播放界面
	**/
	void onDeleteFavorite();
	/**
	* 通知service申请音频焦点
	**/
	void notifyServiceRequestAudio();
	/**
	* 获取歌词URL
	**/
	String getCurrentLyricUrl();

}