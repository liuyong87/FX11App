package com.semisky.multimedia.media_music.adpter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_list.MultimediaListManger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/30.
 */

public class MusicListRecyclerViewAdapter extends RecyclerView.Adapter<MusicListRecyclerViewAdapter.ViewHolder> {
    private List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
    private int mPositionWithCurrentPlayItem = -1;

    public MusicListRecyclerViewAdapter() {
        musicInfos = new ArrayList<MusicInfo>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_music_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_MusicName.setText(musicInfos.get(position).getTitle());

        String url = musicInfos.get(position).getUrl();
        String playingUrl = getPlayingUrl();

        int resItemBg = R.drawable.bg_transparent;// 默认为透明背景
        int showPlayingIconEnable = View.INVISIBLE;// 标识媒体播放ICON
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        holder.tv_MusicName.setEllipsize(truncateAt);
        holder.tv_MusicName.setText(musicInfos.get(position).getTitle());
        holder.tv_albumName.setText(musicInfos.get(position).getAlbum());
        holder.tv_artist.setText(musicInfos.get(position).getArtist());
        holder.tv_musicDuration.setText(musicInfos.get(position).getDuration() + "");

        if (url.equals(playingUrl)) {
            this.mPositionWithCurrentPlayItem = position;
            showPlayingIconEnable = View.VISIBLE;
//            holder.tv_playingFlag.setVisibility(showPlayingIconEnable);
        }


    }

    @Override
    public int getItemCount() {
        return musicInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playingFlag;
        ImageView albumCoverFlow;
        TextView tv_MusicName;
        TextView tv_artist;
        TextView tv_albumName;
        TextView tv_musicDuration;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
//            tv_playingFlag = itemView.findViewById(R.id.tv_icon_playing);
            albumCoverFlow = itemView.findViewById(R.id.im_album);
            tv_MusicName = itemView.findViewById(R.id.tv_musicName);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_albumName = itemView.findViewById(R.id.tv_albumName);
            tv_musicDuration = itemView.findViewById(R.id.tv_musicDuration);
            relativeLayout = itemView.findViewById(R.id.rl_horizontal_bar);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapterOnClick != null
                            && getAdapterPosition() > -1
                            && getAdapterPosition() < musicInfos.size()) {
                        adapterOnClick.onItemClick(musicInfos.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface AdapterOnClick {
        void onItemClick(MusicInfo musicInfo);
    }

    private AdapterOnClick adapterOnClick;

    public void setAdapterOnClick(AdapterOnClick onClick) {
        adapterOnClick = onClick;
    }

    // 获取当前播放URL
    private String getPlayingUrl() {
        return MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
    }

    public void setData(List<MusicInfo> musicInfos) {
        this.musicInfos.clear();
        if (musicInfos != null) {
            this.musicInfos.addAll(musicInfos);
        }
        notifyDataSetChanged();
    }
}
