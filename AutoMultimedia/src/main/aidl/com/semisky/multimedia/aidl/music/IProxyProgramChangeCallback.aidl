package com.semisky.multimedia.aidl.music;

interface IProxyProgramChangeCallback{
	/**
	* 改变歌曲名
	**/
	void onChangeSongName(String songName);
	/**
	* 改变歌手名
	**/
	void onChangeArtistName(String artistName);
	/**
	* 改变专辑名
	**/
	void onChangeAlbumName(String albumName);
	/**
	* 改变总进度
	**/
	void onChangeTotalProgress(int progress);
	/**
	* 当前播放改变
	**/
	void onChangeCurrentProgress(int progress);
	/**
	* 播放曲目位置改变
	**/
	void onChangeCurProgramPos(String programPos);
	/**
	* 播放模式改变
	**/
	void onChangePlayMode(int playMode);
	/**
	* 播放状态改变
	**/
	void onChangePlayStatus(boolean playStatus);
	/**
	* 收藏改变
	**/
	void onChangeFavorite(boolean isFavorite);
    /**
    * 媒体资源准备完成
    **/
    void onMediaPrepareCompleted();
	/**
	* 播放错误
	**/
	void onPlayError(int what);
	/**
	* 播放曲目资源路径改变
	**/
	void onChangeUrl(String url);

}