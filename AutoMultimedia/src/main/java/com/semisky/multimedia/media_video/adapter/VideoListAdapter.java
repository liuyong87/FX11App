package com.semisky.multimedia.media_video.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.MultimediaListManger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LiuYong on 2018/8/9.
 */

public class VideoListAdapter extends BaseAdapter {
    private static final String TAG = Logutil.makeTagLog(VideoListAdapter.class);
    private Context mContext;
    private List<VideoInfo> mList;
    private OnItemHighLightChangeCallback mCallback;
    private int mPositionWithCurrentPlayItem = -1;
    private Handler _handler = new Handler(Looper.getMainLooper());


    public VideoListAdapter(Context ctx) {
        this.mContext = ctx;
        this.mList = new ArrayList<VideoInfo>();
    }

    public void updateList(List<VideoInfo> list) {
        if (null == list) {
            return;
        }
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addCallback(OnItemHighLightChangeCallback callback) {
        this.mCallback = callback;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_video_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        refreshItemContent(viewHolder, position);
        return convertView;
    }


    public class ViewHolder {
        private TextView tv_videoName;
        private LinearLayout ll_item_layout;

        public ViewHolder(View itemView) {
            tv_videoName = (TextView) itemView.findViewById(R.id.tv_videoName);
            ll_item_layout = (LinearLayout) itemView.findViewById(R.id.ll_item_layout);
        }
    }

    private void refreshItemContent(ViewHolder viewHolder, int pos) {
        String url = mList.get(pos).getFileUrl();
        int resItemBg = R.drawable.bg_transparent;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        if (url.equals(getPlayingUrl())) {
            resItemBg = R.drawable.list_item_highlight;
            viewHolder.ll_item_layout.setBackgroundResource(resItemBg);
            truncateAt = TextUtils.TruncateAt.MARQUEE;
            viewHolder.tv_videoName.setEllipsize(truncateAt);
            viewHolder.tv_videoName.setMarqueeRepeatLimit(-1);
            viewHolder.tv_videoName.setSelected(true);
            viewHolder.tv_videoName.setHorizontallyScrolling(true);
        }else {
            viewHolder.ll_item_layout.setBackgroundResource(resItemBg);
            viewHolder.tv_videoName.setEllipsize(truncateAt);
        }
        viewHolder.tv_videoName.setText(mList.get(pos).getFileName());

    }

    /**
     * 通知当前播放高亮条目更新
     */
    public void notifyItemHighLightChange() {
        Logutil.i(TAG, "notifyItemHighLightChange() ...");
        notifyDataSetChanged();
        _handler.post(new Runnable() {
            @Override
            public void run() {
                mPositionWithCurrentPlayItem = getPostionWithCurrentPlayingUrl();

                Logutil.i(TAG, "notifyItemHighLightChange() ..." + mPositionWithCurrentPlayItem);
                Logutil.i(TAG, "notifyItemHighLightChange() url" + getPlayingUrl());
                if (mPositionWithCurrentPlayItem != -1) {
                    if (null != mCallback) {
                        mCallback.onItemHighLightChange();
                    }
                }
            }
        });

    }

    // 获取当前播放媒体P
    private int getPostionWithCurrentPlayingUrl() {
        if (null != mList && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                String url = mList.get(i).getFileUrl();
                if (url.equals(getPlayingUrl())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String getPlayingUrl() {
        return MultimediaListManger.getInstance().getmPlayingUrlWithVideo();
    }

    public void onRelease() {
        this.mList.clear();
        this.mContext = null;
        this.mCallback = null;
        _handler.removeCallbacksAndMessages(null);
        _handler = null;
    }


}
