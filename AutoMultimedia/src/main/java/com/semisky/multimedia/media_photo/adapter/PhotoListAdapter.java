package com.semisky.multimedia.media_photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.media_photo.model.IPhotoFrame;
import com.semisky.multimedia.media_photo.model.PhotoFrameBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class PhotoListAdapter extends BaseAdapter {
    private Context mContext;
    private List<PhotoInfo> mList;
    private LayoutInflater mInflater;
    private IPhotoFrame mPhotoFrame;

    public PhotoListAdapter(Context ctx) {
        this.mContext = ctx;
        this.mList = new ArrayList<PhotoInfo>();
        this.mInflater = LayoutInflater.from(ctx);
        mPhotoFrame = PhotoFrameBuilder.create();
    }

    public void updateList(List<PhotoInfo> list) {
        if (null != list) {
            this.mList.clear();
            this.mList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = this.mInflater.inflate(R.layout.adapter_item_photo_list, parent, false);
            viewHolder.iv_photo_item = (ImageView)convertView.findViewById(R.id.iv_photo_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mPhotoFrame.showSmallPhoto(viewHolder.iv_photo_item, mList.get(position).getFileUrl());
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_photo_item;
    }
}
