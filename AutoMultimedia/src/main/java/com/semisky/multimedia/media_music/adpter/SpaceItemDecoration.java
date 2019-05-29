package com.semisky.multimedia.media_music.adpter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2019/5/2.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space = 0;
    public SpaceItemDecoration(int space){
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1 || parent.getChildLayoutPosition(view) == 2){
            outRect.left = 0;
        }else {
            outRect.left = space;
        }
    }
}
