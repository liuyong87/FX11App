package com.semisky.multimedia.media_music.viewPager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author by chenhongrui on 2019-05-06
 *
 * 内容摘要: 处理点击item跳转
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
public class ClipViewPager extends ViewPager {

    private static final String TAG = "ClipViewPager";
    private final static float DISTANCE = 10;
    //缩放大小
    public final static float minScale = 0.8f;
    //移动大小
    public final static float maxTranslationX = 80;
    //旋转大小
    public final static float maxRotate = 35;
    private float downX;
    private float downY;

    public ClipViewPager(Context context) {
        super(context);
    }

    public ClipViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = ev.getX();
            downY = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {

            float upX = ev.getX();
            float upY = ev.getY();
            //如果 up的位置和down 的位置 距离 > 设置的距离,则事件继续传递,不执行下面的点击切换事件
            if (Math.abs(upX - downX) > DISTANCE || Math.abs(upY - downY) > DISTANCE) {
                return super.dispatchTouchEvent(ev);
            }

            View view = viewOfClickOnScreen(ev);
            if (view != null) {
                int index = (Integer) view.getTag();
                if (getCurrentItem() != index) {
                    setCurrentItem(index);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private View viewOfClickOnScreen(MotionEvent ev) {
        int childCount = getChildCount();
        int currentIndex = getCurrentItem();
        int[] location = new int[2];
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            int position = (Integer) v.getTag();
            v.getLocationOnScreen(location);

            int minX = location[0];
            int minY = location[1];

            int maxX = location[0] + v.getWidth();
            int maxY = location[1] + v.getHeight();

//            Log.d(TAG, "viewOfClickOnScreen: --------第 " + i + "---------");
//            Log.d(TAG, "viewOfClickOnScreen:position " + position);
//            Log.d(TAG, "viewOfClickOnScreen:currentIndex " + currentIndex);
//            Log.d(TAG, "viewOfClickOnScreen: -----------------");

//            Log.d(TAG, "viewOfClickOnScreen: 修正前的宽度 " + v.getWidth());
//            Log.d(TAG, "viewOfClickOnScreen: 修正前的maxX " + maxX);
            //修正因为动画导致的点击区域的变化 平移 缩放
            int offset = (int) (v.getWidth() * (1 - minScale)) / 2;
            if (position < currentIndex) {
                maxX = (int) (maxX - maxTranslationX - offset);
                minX = (int) (minX - maxTranslationX + offset);
            } else if (position == currentIndex) {
//                minX += v.getWidth() * Math.abs(1 - minScale);
            } else if (position > currentIndex) {
                maxX = (int) (maxX - maxTranslationX - offset);
                minX = (int) (minX - maxTranslationX + offset);
            }

//            Log.d(TAG, "viewOfClickOnScreen: 修正后的宽度 " + offset);
            float x = ev.getRawX();
            float y = ev.getRawY();

//            Log.d(TAG, "viewOfClickOnScreen:x " + x + " minX " + minX + " maxX " + maxX);
//            Log.d(TAG, "viewOfClickOnScreen:y " + y + " minY " + minY + " maxY " + maxY);
            if ((x > minX && x < maxX) && (y > minY && y < maxY)) {
//                Log.d(TAG, "viewOfClickOnScreen:当前点击了 " + v.getTag());
                return v;
            }
        }
        return null;
    }
}