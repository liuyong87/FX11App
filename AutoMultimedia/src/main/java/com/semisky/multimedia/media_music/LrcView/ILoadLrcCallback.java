package com.semisky.multimedia.media_music.LrcView;

/**
 * @author by chenhongrui on 2019/4/10
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
public interface ILoadLrcCallback {

    /**
     * 歌词加载中
     */
    void lrcLoading();

    /**
     * 歌词加载成功
     */
    void lrcLoadSuccess();

    /**
     * 歌词加载失败
     */
    void lrcLoadFile();

    /**
     * 播放当前时间片段
     */
    boolean onPlayTime(long time);
}
