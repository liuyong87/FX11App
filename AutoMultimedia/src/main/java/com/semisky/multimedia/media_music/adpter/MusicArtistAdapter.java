package com.semisky.multimedia.media_music.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_list.bean.MusicArtistInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/5.
 */

public class MusicArtistAdapter extends RecyclerView.Adapter<MusicArtistAdapter.ViewHolder>{

    private List<MusicArtistInfo> listHashMap = new ArrayList<MusicArtistInfo>();
    public MusicArtistAdapter (){

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_music_artist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.tv_artistName.setText(listHashMap.get(position).getArtistName());
      holder.tv_artistCount.setText(listHashMap.get(position).getList().size()+"首歌曲");

    }

    @Override
    public int getItemCount() {
        return listHashMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView albumCoverFlow ;
        TextView tv_artistName;
        TextView tv_artistCount;
        RelativeLayout relativeLayout ;

        public ViewHolder(View itemView) {
            super(itemView);
            albumCoverFlow = itemView.findViewById(R.id.im_album);
            tv_artistName = itemView.findViewById(R.id.tv_artistName);
            tv_artistCount = itemView.findViewById(R.id.tv_artist_count);
            relativeLayout = itemView.findViewById(R.id.rl_artist_info);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (adapterOnClick!=null){
                      adapterOnClick.onItemClick(listHashMap.get(getAdapterPosition()).getList());
                  }
                }
            });
        }
    }
    public void setData(List<MusicArtistInfo> musicInfos){
        this.listHashMap.clear();
        if (musicInfos!=null){
            this.listHashMap.addAll(musicInfos);
        }
         notifyDataSetChanged();
    }
    public interface AdapterOnClickArtist{
        void onItemClick(List<MusicInfo> musicInfo);
    }
    private AdapterOnClickArtist adapterOnClick;
    public void setAdapterOnClick(AdapterOnClickArtist onClick){
        adapterOnClick = onClick;
    }
}
