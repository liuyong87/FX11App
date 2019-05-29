package com.semisky.multimedia.media_music.viewPager;

import android.view.View;

public interface IPageItemChangeListener {

    /**
     * 滚动结束
     */
    void onPageItemSelected(View view, int position);

    /**
     * 开始滚动
     */
    void onPageScrollStateChanged(int position);
}
