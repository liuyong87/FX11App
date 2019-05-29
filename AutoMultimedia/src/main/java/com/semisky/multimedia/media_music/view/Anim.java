package com.semisky.multimedia.media_music.view;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import static com.semisky.multimedia.media_music.viewPager.ClipViewPager.maxRotate;
import static com.semisky.multimedia.media_music.viewPager.ClipViewPager.maxTranslationX;
import static com.semisky.multimedia.media_music.viewPager.ClipViewPager.minScale;

/**
 * Created by Administrator on 2019/5/6.
 */

public class Anim implements PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setPivotY(page.getHeight() / 2f);
        if (position <= -1) { // [-Infinity,-1)
            page.setRotationY(maxRotate);
            page.setPivotX(0);
            page.setScaleX(minScale);
            page.setScaleY(minScale);
            page.setTranslationX(-position * maxTranslationX);
        } else if (position < 1) { // [-1,1]
            page.setRotationY(-position * maxRotate);
            if (position < 0) {//[0,-1]
                page.setPivotX(0);
                page.setScaleX(1 + position * (1 - minScale));
                page.setScaleY(1 + position * (1 - minScale));
                page.setTranslationX(-position * maxTranslationX);
            } else {//[1,0]
                page.setPivotX(page.getWidth());
                page.setScaleX(1 - position * (1 - minScale));
                page.setScaleY(1 - position * (1 - minScale));
                page.setTranslationX(-position * maxTranslationX);
            }
        } else { // (1,+Infinity]
            page.setRotationY(-1 * maxRotate);
            page.setPivotX(page.getWidth());
            page.setScaleX(minScale);
            page.setScaleY(minScale);
            page.setTranslationX(-position * maxTranslationX);
        }
    }
}
