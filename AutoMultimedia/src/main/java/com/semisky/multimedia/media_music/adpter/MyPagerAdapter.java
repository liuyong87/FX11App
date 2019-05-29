package com.semisky.multimedia.media_music.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/6.
 */

public class MyPagerAdapter extends PagerAdapter {
    private OnItemClickListener mOnItemClickListener;
    private List<MusicInfo> musicInfos =new ArrayList<MusicInfo>();
    private Context context;
    public MyPagerAdapter(Context context){
        this.context = context;
    }


    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }


    public void setOnItemClickListener(OnItemClickListener l){
        this.mOnItemClickListener = l;
    }

    @Override
    public int getCount() {
        return musicInfos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_pager_item, null);
        ImageView iv = view.findViewById(R.id.iv_album);
//        iv.setImageResource(R.drawable.music_album_photo_def);
        iv.setBackgroundResource(R.drawable.music_album_photo_def);
        container.addView(view);
        view.setTag(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mOnItemClickListener){
                    mOnItemClickListener.onItemClick(v,(int)v.getTag());
                }
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public void setData(List<MusicInfo>musicInfos){
        this.musicInfos.clear();
        if (musicInfos != null){
            this.musicInfos = musicInfos;
        }
        notifyDataSetChanged();
    }
    public int getCurrentItemPosition(String url){
        for (int i = 0;i < musicInfos.size(); i++){
            if (musicInfos.get(i).getUrl().equals(url)){
                return i;
            }
        }
        return 0;
    }
}
