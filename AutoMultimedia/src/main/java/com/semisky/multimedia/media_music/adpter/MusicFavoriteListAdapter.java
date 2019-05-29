package com.semisky.multimedia.media_music.adpter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class MusicFavoriteListAdapter extends BaseAdapter implements OnClickListener,OnItemHighLightChangeCallback {
    private static final String TAG = Logutil.makeTagLog(MusicFavoriteListAdapter.class);
    private Handler _handler = new Handler(Looper.getMainLooper());
    private Context mContext;
    private List<MusicInfo> mList;
    private OnCancelFavoriteListener mOnCancelFavoriteListener;
    private int mCancelFavoritePos = -1;
    private OnItemHighLightChangeCallback mCallback = null;
    private int mPositionWithCurrentPlayItem = -1;

    @Override
    public void onItemHighLightChange() {
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
    }

    public interface OnCancelFavoriteListener {
        void onCancelFavoriteWith(String url);
    }

    public void setOnCancelFavoriteListener(OnCancelFavoriteListener l) {
        this.mOnCancelFavoriteListener = l;
    }


    public MusicFavoriteListAdapter(Context ctx) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_music_favorite_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        highLight(viewHolder,position);
        return convertView;
    }

    public class ViewHolder {
        private ImageView iv_icon_music, iv_icon_playing, iv_favorite_icon;
        private TextView tv_songName;
        private LinearLayout linearLayout_favorite;


        public ViewHolder(View itemView) {
            iv_icon_music = (ImageView) itemView.findViewById(R.id.iv_icon_music);
            tv_songName = (TextView) itemView.findViewById(R.id.tv_songName);
            iv_icon_playing = (ImageView) itemView.findViewById(R.id.iv_icon_playing);
            iv_favorite_icon = (ImageView) itemView.findViewById(R.id.iv_favorite_icon);
            linearLayout_favorite= (LinearLayout) itemView.findViewById(R.id.ll_layout_favorite);
        }
    }

    @Override
    public void onClick(View v) {
        mCancelFavoritePos = (Integer) (((ImageView) v).getTag());
        String url = getUrlWithListSpecifyPosistion(mCancelFavoritePos);
        Logutil.i(TAG, "onClick() ..." + mCancelFavoritePos);
        Logutil.i(TAG, "onClick() ..." + (null != url ? url : "unknown"));

        if (null != url && null != mOnCancelFavoriteListener) {
            mOnCancelFavoriteListener.onCancelFavoriteWith(url);
        }
    }

    // 获取列表指定item资源路径
    private String getUrlWithListSpecifyPosistion(int pos) {
        if (null != mList) {
            MusicInfo info = mList.get(pos);
            return info.getUrl();
        }
        return null;
    }

    public void onCancelFavorateItem() {
        Logutil.i(TAG, "onCancelFavorateItem() ...");
        mList.remove(mCancelFavoritePos);
        notifyDataSetChanged();
    }
    private void highLight(ViewHolder viewHolder,int position){
        String urlWithMusic = getPlayingUrl();
        int resItemBg = R.drawable.bg_transparent;// 默认为透明背景
        if (urlWithMusic != null && urlWithMusic.equals(mList.get(position).getUrl())) {
            resItemBg = R.drawable.list_item_highlight;
            viewHolder.linearLayout_favorite.setBackgroundResource(resItemBg);
        } else {
            viewHolder.linearLayout_favorite.setBackgroundResource(resItemBg);
        }
        viewHolder.tv_songName.setText(mList.get(position).getTitle());
        viewHolder.iv_favorite_icon.setTag(position);
        viewHolder.iv_favorite_icon.setOnClickListener(this);
    }

    // 获取当前播放URL
    private String getPlayingUrl() {
        return MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
    }
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
