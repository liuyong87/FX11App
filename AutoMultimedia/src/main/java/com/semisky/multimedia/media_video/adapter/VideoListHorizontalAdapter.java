package com.semisky.multimedia.media_video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.utils.Logutil;

import java.util.ArrayList;
import java.util.List;

public class VideoListHorizontalAdapter  extends RecyclerView.Adapter<VideoListHorizontalAdapter.MediaViewHolder>{

    private static final String TAG = Logutil.makeTagLog(VideoListHorizontalAdapter.class);
    private Context mContext;
    private List<VideoInfo> mList = new ArrayList<VideoInfo>();

    private OnHorizontalItemClickListener mOnHorizontalItemClickListener;

    public interface OnHorizontalItemClickListener {
        void onItemClick(View view,int position);
    }


    public void setmOnHorizontalItemClickListener(OnHorizontalItemClickListener l){
        this.mOnHorizontalItemClickListener = l;
    }

    public VideoListHorizontalAdapter(Context ctx){
        this.mContext = ctx;
    }


    public void updateList(List<VideoInfo> list) {
        if (null == list) {
            return;
        }
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public List<VideoInfo> getList(){
        return this.mList;
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_horizontal_video_list, parent, false);
        return new MediaViewHolder(view,mOnHorizontalItemClickListener);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        holder.tv_video_name.setText(mList.get(position).getFileName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // ViewHolder
    class MediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnHorizontalItemClickListener mOnHorizontalItemClickListener;
        private ImageView iv_video_thumb;
        private TextView tv_video_name;
        public MediaViewHolder(View itemView , OnHorizontalItemClickListener l) {
            super(itemView);
            this.mOnHorizontalItemClickListener = l;
            itemView.setOnClickListener(this);
            this.iv_video_thumb = (ImageView) itemView.findViewById(R.id.iv_video_thumb);
            this.tv_video_name = (TextView) itemView.findViewById(R.id.tv_video_name);
        }

        @Override
        public void onClick(View v) {
            if(null != mOnHorizontalItemClickListener){
                mOnHorizontalItemClickListener.onItemClick(v,getLayoutPosition());
            }
        }
    }
}
