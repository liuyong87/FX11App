package com.semisky.multimedia.media_photo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.media_photo.model.IPhotoFrame;
import com.semisky.multimedia.media_photo.model.PhotoFrameBuilder;
import com.semisky.multimedia.media_photo.photoplayer.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anter on 2018/8/4.
 */

public class PhotoPlayerAdapter extends PagerAdapter implements View.OnClickListener, View.OnTouchListener {
    private OnClickListener mOnClickListener = null;
    private OnTouchListener mOnTouchListener = null;
    private Context mContext;
    private LayoutInflater inflater;
    private List<PhotoInfo> mPhotoList;
    private PhotoView[] photoViews;
    private IPhotoFrame mPhotoFrame;

    public PhotoPlayerAdapter(Context ctx){
        this.mContext = ctx;
        this.inflater = LayoutInflater.from(this.mContext);
        mPhotoList = new ArrayList<PhotoInfo>();
        mPhotoFrame = PhotoFrameBuilder.create();
    }

    public void updateList( List<PhotoInfo> list){
        photoViews = new PhotoView[list.size()];
        mPhotoList.clear();
        mPhotoList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void setmOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void setmOnTouchListener(OnTouchListener listener) {
        this.mOnTouchListener = listener;
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        photoView.enable();
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mPhotoFrame.showBigPhoto(photoView,mPhotoList.get(position).getFileUrl());

        photoViews[position] = photoView;
        photoView.setOnClickListener(this);
        photoView.setOnTouchListener(this);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        photoViews[position] = null;
        mPhotoFrame.removeView((View) object);
    }

    @Override
    public void onClick(View v) {
        if (null != mOnClickListener) {
            mOnClickListener.onClick(v);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null != mOnTouchListener) {
            mOnTouchListener.onTouch(v, event);
        }
        return false;
    }

    // utils
    // 旋转图片
    public void onRotate(int position) {
        if (photoViews != null && photoViews[position] != null){
            photoViews[position].handRotate(-90);
        }
    }

    // 缩放图片
    public void onScale(int position, float scaleFactor) {
        photoViews[position].handScale(scaleFactor);
    }

    // 恢复到原始图片
    public void onRestoreOriginalPhoto(int position){
        if (photoViews[position]!=null){
            photoViews[position].reduction();
        }
    }


}
