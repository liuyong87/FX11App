package com.semisky.multimedia.media_folder.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaFileType;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.MultimediaListManger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/29.
 */

public class FolderListAdapter extends BaseAdapter implements OnItemHighLightChangeCallback {
    private static final String TAG = Logutil.makeTagLog(FolderListAdapter.class);
    private Context mCtx;
    private List<FolderInfo> mList;
    private Handler _handler = new Handler(Looper.getMainLooper());
    private OnItemHighLightChangeCallback mCallback = null;
    private int mPositionWithCurrentPlayItem = -1;

    public FolderListAdapter(Context ctx) {
        this.mCtx = ctx;
        this.mList = new ArrayList<FolderInfo>();
        MultimediaListManger.getInstance().addCallback(this);
    }

    public void updateList(List<FolderInfo> list) {
        if (null != list && list.size() > 0) {
            this.mList.clear();
            this.mList.addAll(list);
            notifyDataSetChanged();
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
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.adapter_item_folder_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 展示条目
        showItem(viewHolder, position);
        return convertView;
    }

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

    public class ViewHolder {
        private ImageView iv_icon_type;
        private TextView tv_name;
        private LinearLayout linearLayout_folder;

        public ViewHolder(View itemView) {
            iv_icon_type = (ImageView) itemView.findViewById(R.id.iv_icon_type);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            linearLayout_folder = (LinearLayout) itemView.findViewById(R.id.ll_layout_folder);
        }
    }

    private void showItem(ViewHolder viewHolder, int posistion) {
        int resId = getImageResId(mList.get(posistion).getType());
        if (resId != -1) {
            viewHolder.iv_icon_type.setImageResource(resId);
        }
        String urlWithMusic = MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
        String urlWithVideo = MultimediaListManger.getInstance().getmPlayingUrlWithVideo();
        int resItemBg = R.drawable.bg_transparent;// 默认为透明背景
        if (urlWithMusic != null && urlWithMusic.equals(mList.get(posistion).getUrl())
                || (urlWithVideo !=null && urlWithVideo.equals(mList.get(posistion).getUrl()))) {
            resItemBg = R.drawable.list_item_highlight;
            viewHolder.linearLayout_folder.setBackgroundResource(resItemBg);
        } else {
            viewHolder.linearLayout_folder.setBackgroundResource(resItemBg);
        }
        viewHolder.tv_name.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.tv_name.setText(mList.get(posistion).getName()+"　　");
    }

    private int getImageResId(int fileType) {
        int resId = -1;
        switch (fileType) {
            case MediaFileType.TYPE_BACK_DIR:
                resId = R.drawable.list_icon_pre_folder;
                break;
            case MediaFileType.TYPE_FOLDER:
                resId = R.drawable.list_icon_folder;
                break;
            case MediaFileType.TYPE_MUSIC:
                resId = R.drawable.list_icon_music;
                break;
            case MediaFileType.TYPE_VIDEO:
                resId = R.drawable.list_icon_video;
                break;
            case MediaFileType.TYPE_PHOTO:
                resId = R.drawable.list_icon_photo;
                break;
        }
        return resId;
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
    // 获取当前播放URL
    private String getPlayingUrl() {
        return MultimediaListManger.getInstance().getmPlayingUrlWithMusic();
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
        this.mCtx = null;
        this._handler.removeCallbacksAndMessages(null);
        this._handler = null;
        this.mCallback = null;
    }
}
