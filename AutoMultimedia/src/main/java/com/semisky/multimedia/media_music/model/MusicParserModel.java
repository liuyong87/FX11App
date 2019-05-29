package com.semisky.multimedia.media_music.model;

import android.media.MediaMetadataRetriever;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.EncodingUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.LrcView.LrcEntity;
import com.semisky.multimedia.media_music.LrcView.LrcParseUtil;
import com.semisky.multimedia.media_music.LrcView.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/23.
 */

public class MusicParserModel implements IMusicParserModel<MusicInfo> {
    private static final String TAG = Logutil.makeTagLog(MusicParserModel.class);
    private volatile MediaMetadataRetriever mmr;


    @Override
    public void parserMusicInfo(final String url, final OnMediaParserListener<MusicInfo> l) {
        Logutil.i(TAG,"parserMusicInfo() ..."+(null != url?url:"unkown !!!"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                l.onMediaInfo(getMusicInfo(url));
            }
        }).start();
    }


    private synchronized MusicInfo getMusicInfo(String url) {
        MusicInfo info = null;
        if (null != mmr) {
            safeCloseMediaMetadataRetriever(mmr);
        }

        mmr = new MediaMetadataRetriever();
        info = new MusicInfo();

        if (null == url || !new File(url).exists()) {
            Logutil.e(TAG, "getMusicInfo() URL == NULL");
            return info;
        }

        if (isValidDataSource(mmr, url)) {
            info.setUrl(url);
            info.setUsbFlag(getUsbFlag(url));
            info.setTitle(getSongName(url));
            info.setArtist(getArtist(mmr));
            info.setAlbum(getAlbum(mmr));
        }else {
            info.setUrl(url);
            info.setUsbFlag(getUsbFlag(url));
            info.setTitle(getSongName(url));
            info.setArtist(null);
            info.setAlbum(null);
        }

        safeCloseMediaMetadataRetriever(mmr);

        return info;
    }

    // 获取歌名
    private String getSongName(String url) {
        String songName = null;
        try {
            songName = url.substring(url.lastIndexOf(File.separator) + 1);
            songName = encodingString(songName);
        } catch (Exception e) {
            Logutil.e(TAG, "getSongName() FAIL !!!");
            e.printStackTrace();
        }
        Logutil.i(TAG, "getSongName() ..." + songName);
        return songName;
    }


    // U盘标识
    private int getUsbFlag(String url){
        int usbFlag = -1;
        if(null == url){
            Logutil.e(TAG,"getUsbFlag() FAIL !!! ,usbFlag="+usbFlag+",url="+url);
            usbFlag = -1;
        }

        if(url.startsWith(Definition.PATH_USB1)){
            return Definition.FLAG_USB1;
        }else if(url.startsWith(Definition.PATH_USB2)){
            return Definition.FLAG_USB2;
        }
        Logutil.e(TAG,"getUsbFlag() FAIL !!! , usbFlag="+usbFlag+",url="+url);
        return usbFlag;
    }


    // 获取媒体专辑
    private String getAlbum(MediaMetadataRetriever mmr) {
        String album = null;
        try {
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            album = encodingString(album);
        } catch (Exception e) {
            Logutil.e(TAG, "getAlbum() FAIL !!!");
            e.printStackTrace();
        }
        Logutil.i(TAG, "getAlbum() ..." + album);
        return album;
    }

    // 获取媒体歌手
    private String getArtist(MediaMetadataRetriever mmr) {
        String artist = null;
        try {
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            artist = encodingString(artist);//解码
        } catch (Exception e) {
            Logutil.e(TAG, "getArtist() FAIL !!!");
            e.printStackTrace();
        }
        Logutil.i(TAG, "getArtist() ..." + artist);
        return artist;
    }

    private void safeCloseMediaMetadataRetriever(MediaMetadataRetriever mmr) {
        if (null != mmr) {
            try {
                mmr.release();
                mmr = null;
            } catch (Exception e) {
                Logutil.w(TAG, "----------------------safeCloseMediaMetadataRetriever()");
            }
        }
    }

    // 资源URL是否有效
    private boolean isValidDataSource(MediaMetadataRetriever mmr, String uri) {
        boolean isValidDataSource = false;
        try {
            mmr.setDataSource(uri);
            isValidDataSource = true;
        } catch (Exception e) {
            e.printStackTrace();
            safeCloseMediaMetadataRetriever(mmr);
        }
        Logutil.i(TAG, "isValidDataSource() ..." + isValidDataSource);
        return isValidDataSource;
    }
    /**
     * 字符解码
     */
    private String encodingString(String s){
        String encodeString=EncodingUtil.getEncodeString(s,null);
        return encodeString;
    }
}
