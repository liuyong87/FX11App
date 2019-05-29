package com.semisky.multimedia.media_music.view;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_list.bean.FolderInfo;
import com.semisky.multimedia.media_list.bean.MusicAlbumInfo;
import com.semisky.multimedia.media_list.bean.MusicArtistInfo;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */
 

public interface IMusicListView<E> {
    /**
     * 首次刷新数据
     *
     * @param dataList
     */
    void onFirstRefreshData(List<E> dataList);

    /**
     * 动态刷新数据
     *
     * @param dataList
     */
    void onRefreshData(List<E> dataList);

    /**
     * 当集合空数据时警示提示
     *
     * @param enable
     */
    void onAlertEmptyListTextVisible(boolean enable);

    /**
     *USB未连接
     */
    void showTextUsbDisconnected();
    /**
     *USB连接
     */
    void showTextUsbLoading();
    /**
     * 获取usb标记
     */
    int getUsbFlagIndex();

    /**
     * 显示音乐信息
     */
    void showMusicInfo(int gone);
    /**
     * 显示专辑，歌手，文件夹下的音乐信息
     */
    void showMusicOtherInfo(int gone);
    /**
     * 获取进入专辑，歌手，文件夹信息前的数据flag
     */
    int getSelectoyType();
    /**
     * 显示所有歌曲信息
     */
    void showAllMusicInfo(List<MusicInfo> list);
    /**
     * 显示根歌手的音乐信息
     */
    void showArtistMusicInfo(List<MusicArtistInfo> musicInfo);
    /**
     * 显示专辑下的音乐信息
     */
    void showAlbumMusicInfo(List<MusicAlbumInfo> musicInfo);
    /**
     * 显示文件夹信息
     */
    void showFolder(List<FolderInfo> folderInfos);

}
