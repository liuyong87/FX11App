package com.semisky.multimedia.media_photo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.media_photo.model.IPhotoFrame;
import com.semisky.multimedia.media_photo.model.PhotoFrameBuilder;

import java.util.ArrayList;
import java.util.List;

public class PhotoListHorizontalAdapter extends RecyclerView.Adapter<PhotoListHorizontalAdapter.ViewHolder> {

    private OnHorizontalItemClickListener mOnHorizontalItemClickListener;
    private List<PhotoInfo> mList = new ArrayList<PhotoInfo>();
    private IPhotoFrame mPhotoFrame;


    public PhotoListHorizontalAdapter(){
        this.mPhotoFrame = PhotoFrameBuilder.create();
    }

    public interface OnHorizontalItemClickListener {
        void onItemClick(View view,int position);
    }

    public void setOnHorizontalItemClickListener(OnHorizontalItemClickListener l){
        this.mOnHorizontalItemClickListener = l;
    }

    public void updateList(List<PhotoInfo> list) {
        if (null == list) {
            return;
        }
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public List<PhotoInfo> getList(){
        return this.mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_horizontal_photo_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mPhotoFrame.showSmallPhoto(holder.iv_photo_item, mList.get(position).getFileUrl());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_photo_item;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_photo_item = (ImageView)itemView.findViewById(R.id.iv_photo_item);
            iv_photo_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != mOnHorizontalItemClickListener){
                mOnHorizontalItemClickListener.onItemClick(v,getLayoutPosition());
            }
        }
    }

}
