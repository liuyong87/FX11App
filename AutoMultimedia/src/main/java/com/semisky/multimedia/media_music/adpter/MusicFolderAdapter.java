package com.semisky.multimedia.media_music.adpter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_list.bean.FolderInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/10.
 */
public class MusicFolderAdapter extends RecyclerView.Adapter<MusicFolderAdapter.ViewHolder> {
    private List<FolderInfo> listHashMap = new ArrayList<FolderInfo>();
    public MusicFolderAdapter(){

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_music_folder,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String folderName = listHashMap.get(position).getFolderName();
        Log.i("lcc",folderName);
        File file = new File(folderName.substring(0,folderName.length()));
        if (file.exists()){
            holder.tv_folderName.setText(file.getName());
        }


    }

    @Override
    public int getItemCount() {
        return listHashMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_folderName;
        RelativeLayout relativeLayout ;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_folderName = itemView.findViewById(R.id.tv_folderName);
            relativeLayout = itemView.findViewById(R.id.rl_folderInfo);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapterOnClick!=null){
                        adapterOnClick.onItemClick(listHashMap.get(getAdapterPosition()).getMusicInfos(),listHashMap.get(getAdapterPosition()).getFolderName());
                    }
                }
            });
        }
    }
    public void setData(List<FolderInfo> musicInfos){
        this.listHashMap.clear();
        if (musicInfos!=null){
            this.listHashMap.addAll(musicInfos);
        }
        notifyDataSetChanged();
    }
    public interface AdapterOnClickFolder{
        void onItemClick(List<MusicInfo> musicInfo,String folderName);
    }
    private AdapterOnClickFolder adapterOnClick;
    public void setAdapterOnClick(AdapterOnClickFolder onClick){
        adapterOnClick = onClick;
    }
}
