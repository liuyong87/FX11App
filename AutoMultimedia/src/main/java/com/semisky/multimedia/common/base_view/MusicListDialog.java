package com.semisky.multimedia.common.base_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.media_music.adpter.MusicListRecyclerViewAdapter;
import com.semisky.multimedia.media_music.adpter.SpaceItemDecoration;

import java.util.List;

/**
 * Created by Administrator on 2019/5/7.
 */

public class MusicListDialog extends Dialog {
    private TextView tv_MusicDataCount;
    private RecyclerView recyclerView;
    private ImageView iv_cancel;
    private Context context;
    private MusicListRecyclerViewAdapter mMusicListAdapter;
    public MusicListDialog(@NonNull Context context) {
        super(context,R.style.DialogStyle);
        this.context = context;

    }

    public MusicListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected MusicListDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_list);
        initView();
        setCanceledOnTouchOutside(false);
    }
    private void initView(){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = 1200;
        window.setAttributes(params);
        tv_MusicDataCount = (TextView) findViewById(R.id.tv_count);
        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerList);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initRecycler();
    }
    private void initRecycler(){
        GridLayoutManager manager = new GridLayoutManager(context,3);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(50));
        mMusicListAdapter = new MusicListRecyclerViewAdapter();
        mMusicListAdapter.setAdapterOnClick(new MusicListRecyclerViewAdapter.AdapterOnClick() {
            @Override
            public void onItemClick(MusicInfo musicInfo) {
                dismiss();
                if (onItemOnClickListener != null){
                    onItemOnClickListener.onItemSelect(musicInfo);
                }

            }
        });
        recyclerView.setAdapter(mMusicListAdapter);
    }
    private OnItemOnClickListener  onItemOnClickListener;
    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
        this.onItemOnClickListener = onItemOnClickListener;
    }
    public interface OnItemOnClickListener{
        void onItemSelect(MusicInfo musicInfo);
    }
    public void setMusicData(List<MusicInfo> musicData){
        if (musicData!=null){
            mMusicListAdapter.setData(musicData);
        }

    }
}
