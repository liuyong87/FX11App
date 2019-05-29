package com.semisky.multimedia.media_music.model;

import com.semisky.multimedia.media_music.LrcView.LrcEntity;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/23.
 */

public interface IMusicParserModel<E> {

    interface OnMediaParserListener<E> {
        void onMediaInfo(E info);
    }


    void parserMusicInfo(String url, OnMediaParserListener<E> l);

}
