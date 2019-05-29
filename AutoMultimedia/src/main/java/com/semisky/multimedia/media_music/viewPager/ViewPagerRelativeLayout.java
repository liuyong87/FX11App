package com.semisky.multimedia.media_music.viewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author by chenhongrui on 2019-05-06
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
public class ViewPagerRelativeLayout extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private ViewPager mPager;

    public ViewPagerRelativeLayout(Context context) {
        super(context);
    }

    public ViewPagerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        try {
            mPager = (ViewPager) getChildAt(0);
            mPager.addOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mPager.dispatchTouchEvent(event);
    }

    @Override
    public void onPageSelected(int position) {
        if (pageItemChangeListener != null) {
            pageItemChangeListener.onPageItemSelected(mPager.getChildAt(position), position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) {
        if (pageItemChangeListener != null) {
            pageItemChangeListener.onPageScrollStateChanged(position);
        }
    }

    private IPageItemChangeListener pageItemChangeListener;

    public void AddPageItemChangeListener(IPageItemChangeListener iPageItemChangeListener) {
        this.pageItemChangeListener = iPageItemChangeListener;
    }
}
