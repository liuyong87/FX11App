package com.semisky.multimedia.media_photo.bean;


import com.semisky.multimedia.aidl.photo.PhotoInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片播放列表
 * Created by Anter on 2018/8/4.
 */

public class PhotoPlayList<T> {
    private List<PhotoInfo> mPhotoList = new ArrayList<PhotoInfo>();

    /**
     * 添加多媒体列表
     *
     * @param list
     */
    public void addList(List<PhotoInfo> list) {
        if (null != list) {
            mPhotoList.clear();
            mPhotoList.addAll(list);
        }
    }

    /**
     * 获取图片列表
     *
     * @return
     */
    public List<PhotoInfo> getList() {
        if (null != mPhotoList) {
            return this.mPhotoList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 图片列表总长度
     *
     * @return
     */
    public int getSize() {
        return (null != this.mPhotoList ? this.mPhotoList.size() : 0);
    }

    /**
     * 图片列表是否有数据
     *
     * @return
     */
    public boolean hasData() {
        return (null != this.mPhotoList && this.mPhotoList.size() > 0);
    }

    /**
     * 清除所有图片集合数据
     */
    public void clearAllData() {
        if (null != this.mPhotoList) {
            this.mPhotoList.clear();
        }
    }

    /**
     * 获取指定位置URL
     *
     * @param position
     * @return
     */
    public String getUrlOf(int position) {
        if (null != mPhotoList && position < mPhotoList.size()) {
            return mPhotoList.get(position).getFileUrl();
        }
        return null;
    }


}
