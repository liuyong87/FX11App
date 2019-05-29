package com.semisky.multimedia.media_music.adpter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
/**
 * Created by Administrator on 2019/5/11.
 */

public class SpaceItemDecorationFolder extends RecyclerView.ItemDecoration {
    private int space = 0;
    public SpaceItemDecorationFolder(int space){
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) == 0 ){
            outRect.left = 0;
        }else {
            outRect.left = space;
        }
    }
}
