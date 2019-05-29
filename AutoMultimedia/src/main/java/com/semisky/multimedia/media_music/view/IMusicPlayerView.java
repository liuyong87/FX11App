package com.semisky.multimedia.media_music.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_music.LrcView.LrcEntity;

import java.util.List;

/**
 * Created by Anter on 2018/7/30.
 */

public interface IMusicPlayerView {
    Context getContext();

    /**
     * 显示节目名字
     *
     * @param programName
     */
    void onShowProgramName(String programName);

    /**
     * 显示歌手名字
     *
     * @param artistName
     */
    void onShowProgramArtistName(String artistName);

    /**
     * 显示专辑名字
     *
     * @param albumName
     */
    void onShowProgramAlbumName(String albumName);

    /**
     * 总的时间
     *
     * @param totalTime
     */
    void onShowProgramTotalTime(String totalTime);


    /**
     * 显示当前播放时间进度
     *
     * @param curTime
     */
    void onShowProgramCurrentTime(String curTime);


    /**
     * 更新播放进度
     *
     * @param progress
     */
    void onShowProgramProgress(int progress);

    /**
     * 媒体播放异常时的警示信息视图
     *
     * @param enable true:显示视图 ,false:隐藏视图
     */
    void onSwitchPlayProgramExceptionWarningView(boolean enable);

    /**
     * 收藏歌曲显示状态设置
     */
    void onSwitchFavoriteView(boolean enable);

    /**
     * 更新进度条总进度
     *
     * @param duration
     */
    void onUpdateDuration(int duration);

    /**
     * 播放状态改变
     *
     * @param isPlay
     */
    void onChangePlayState(boolean isPlay);

    /**
     * 播放模式改变
     *
     * @param playMode 播放模式
     */
    void onChangePlayMode(int playMode);

    /**
     * 退出音乐播放界面
     */
    void onFinish();

    /**
     * 刷新数据
     */
    void refreshMusicData(List<MusicInfo> data);

    /**
     * 更新专辑图的位置
     */
    void refreshPlayingPosition(String url);

    /**
     * 播放控制按钮显示状态
     */
    void onPlayShowState(int state);

    /**
     * 歌词改变
     *
     * @param lrcList
     */
    void onChangedLyric(List<LrcEntity> lrcList);

    /**
     * 歌曲控件显示与隐藏
     *
     * @param isShow
     */
    void onLrcViewVisible(boolean isShow);

    /**
     * 是否有歌词
     */
    boolean hasLyric();

    /**
     * 专辑控件显示与隐藏
     * @param isShow
     */
    void onAlbumViewVisible(boolean isShow);

    /**
     * 专辑控件是显示状态
     * @return
     */
    boolean isAlbumViewVisible();


}
