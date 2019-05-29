package com.semisky.multimedia.media_music.adpter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.MultimediaListManger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MusicListAdapter extends BaseAdapter implements OnItemHighLightChangeCallback {
    private static final String TAG = Logutil.makeTagLog(MusicListAdapter.class);
    private Handler _handler = new Handler(Looper.getMainLooper());
    private OnItemHighLightChangeCallback mCallback = null;

    private Context mContext;
    private List<MusicInfo> mList;
    private int mPositionWithCurrentPlayItem = -1;


    public MusicListAdapter(Context ctx) {
        this.mContext = ctx;
        mList = new ArrayList<MusicInfo>();
        MultimediaListManger.getInstance().addCallback(this);
    }

    public void updateList(List<MusicInfo> list) {
        synchronized (this.mList) {
            mList.clear();
            this.mList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    private List<MusicInfo> getmList() {
        synchronized (mList) {
            return this.mList;
        }
    }

    public void addCallback(OnItemHighLightChangeCallback callback) {
        this.mCallback = callback;
    }

    private void notifyItemHighLightChange() {
        if (null != mCallback) {
            mCallback.onItemHighLightChange();
        }
    }

    /**
     * 当前播放条目位置
     *
     * @return
     */
    public int getmPositionWithCurrentPlayItem() {
        return this.mPositionWithCurrentPlayItem;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_music_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        refreshItemContent(viewHolder, position);

        return convertView;
    }

    public class ViewHolder {
        private ImageView iv_icon_music, iv_icon_playing;
        private TextView tv_songName;
        private LinearLayout ll_item_layout;


        public ViewHolder(View itemView) {
            iv_icon_music = (ImageView) itemView.findViewById(R.id.iv_icon_music);
            tv_songName = (TextView) itemView.findViewById(R.id.tv_songName);
            iv_icon_playing = (ImageView) itemView.findViewById(R.id.iv_icon_playing);
            ll_item_layout = (LinearLayout) itemView.findViewById(R.id.ll_item_layout);
        }
    }

    // 刷新条目内容
    private void refreshItemContent(ViewHolder viewHolder, int pos) {

        viewHolder.tv_songName.setText(mList.get(pos).getTitle());

        String url = mList.get(pos).getUrl();
        String playingUrl = getPlayingUrl();

        int resItemBg = R.drawable.bg_transparent;// 默认为透明背景
        int showPlayingIconEnable = View.INVISIBLE;// 标识媒体播放ICON
        TruncateAt truncateAt = TruncateAt.END;

        if (url.equals(playingUrl)) {
            this.mPositionWithCurrentPlayItem = pos;

            resItemBg = R.drawable.list_item_highlight;
            showPlayingIconEnable = View.VISIBLE;
            truncateAt = TruncateAt.MARQUEE;

            viewHolder.tv_songName.setEllipsize(truncateAt);
            viewHolder.tv_songName.setMarqueeRepeatLimit(-1);
            viewHolder.tv_songName.setSelected(true);
            viewHolder.tv_songName.setHorizontallyScrolling(true);

            viewHolder.iv_icon_playing.setVisibility(showPlayingIconEnable);
            viewHolder.ll_item_layout.setBackgroundResource(resItemBg);
        }else {
            viewHolder.tv_songName.setEllipsize(truncateAt);
            viewHolder.iv_icon_playing.setVisibility(showPlayingIconEnable);
            viewHolder.ll_item_layout.setBackgroundResource(resItemBg);
        }

    }

    // 获取当前播放URL
    private String getPlayingUrl() {
        return MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
    }

    @Override
    public void onItemHighLightChange() {
        String musicUrl = MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
        _handler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                mPositionWithCurrentPlayItem = getPostionWithCurrentPlayingUrl();
                if (mPositionWithCurrentPlayItem >= 0 && mPositionWithCurrentPlayItem < mList.size()) {
                    notifyItemHighLightChange();
                }
            }
        });
        Logutil.i(TAG, "onItemHighLightChange() musicUrl=" + (null != musicUrl ? musicUrl : "unkown !!!"));
        Logutil.i(TAG, "onItemHighLightChange() mPositionWithCurrentPlayItem=" + mPositionWithCurrentPlayItem);
    }

    // 获取当前播放媒体P
    private int getPostionWithCurrentPlayingUrl() {
        if (null != mList && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                String url = mList.get(i).getUrl();
                if (url.equals(getPlayingUrl())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 释放资源
     */
    public void onRelease() {
        MultimediaListManger.getInstance().unRegisterCallback(this);
        this.mList.clear();
        this.mList = null;
        this.mContext = null;
        this._handler.removeCallbacksAndMessages(null);
        this._handler = null;
        this.mCallback = null;
    }
}
