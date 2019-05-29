package com.semisky.multimedia.media_music.LrcView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.semisky.multimedia.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by chenhongrui on 2019/4/10
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
public class LrcView extends View {

    private static final String TAG = "LrcView";

    /**
     * 拖动线隐藏数据
     */
    private static final long TIMELINE_KEEP_TIME = 4 * DateUtils.SECOND_IN_MILLIS;
    private static final long ADJUST_DURATION = 100;

    private List<LrcEntity> mLrcEntityList = new ArrayList<>();

    /**
     * 拖动歌词时左侧播放按钮图片
     */
    private Drawable mPlayDrawable;

    /**
     * 歌词
     */
    private TextPaint mLrcPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 时间线
     */
    private TextPaint mTimePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 时间文字
     */
    private Paint.FontMetrics mTimeFontMetrics;

    /**
     * 时间文字大小
     */
    private int mTimeTextWidth = 0;

    /**
     * 播放按钮大小
     */
    private int mDrawableWidth = 0;

    /**
     * 时间线颜色
     */
    private int mTimelineColor;

    /**
     * 拖动歌词时右侧时间字体颜色
     */
    private int mTimeTextColor;

    /**
     * 左右歌词的间距
     */
    private int mLrcPadding = 0;

    /**
     * 滚动的距离
     */
    private float mOffset = 0f;

    /**
     * 歌词起点位置，默认从屏幕中央
     */
    private int mLrcOriginPosition = 0;

    /**
     * 歌词间距
     */
    private float mDividerHeight = 0f;

    /**
     * 当前播放的行数
     */
    private int mCurrentLine;

    /**
     * 动画执行时间
     */
    private long mAnimationDuration;

    /**
     * 非当前行歌词字体颜色
     */
    private int mNormalTextColor;

    /**
     * 当前行歌词字体颜色
     */
    private int mCurrentTextColor;

    /**
     * 拖动歌词时选中歌词的字体颜色
     */
    private int mTimelineTextColor;

    /**
     * 是否显示拖动线
     */
    private boolean isShowTimeline;

    /**
     * 是否在滑动中
     */
    private boolean isFling;

    /**
     * 默认歌词
     */
    private String mDefaultLabel;

    private ValueAnimator mAnimator;

    private ILoadLrcCallback iLoadLrcCallback;

    private GestureDetector mGestureDetector;

    private Scroller mScroller;

    private boolean isTouching;

    private int centerY;

    private StaticLayout mDefaultStaticLayout;

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化属性
     */
    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
        mNormalTextColor = typedArray.getColor(R.styleable.LrcView_lrcNormalTextColor,
                getResources().getColor(R.color.lrc_normal_text_color));
        mCurrentTextColor = typedArray.getColor(R.styleable.LrcView_lrcCurrentTextColor,
                getResources().getColor(R.color.lrc_current_text_color));
        mTimelineTextColor = typedArray.getColor(R.styleable.LrcView_lrcTimelineTextColor,
                getResources().getColor(R.color.lrc_timeline_text_color));
        mDividerHeight = typedArray.getFloat(R.styleable.LrcView_lrcDividerHeight,
                getResources().getDimension(R.dimen.lrc_divider_height));
        mAnimationDuration = typedArray.getInt(R.styleable.LrcView_lrcAnimationDuration,
                getResources().getInteger(R.integer.lrc_animation_duration));
        mLrcPadding = typedArray.getInt(R.styleable.LrcView_lrcPadding,
                getResources().getInteger(R.integer.lrc_padding));
        mPlayDrawable = typedArray.getDrawable(R.styleable.LrcView_lrcPlayDrawable);
        mPlayDrawable = (mPlayDrawable == null) ? getResources().getDrawable(R.drawable.play) : mPlayDrawable;
        mTimelineColor = typedArray.getColor(R.styleable.LrcView_lrcTimelineColor,
                getResources().getColor(R.color.lrc_timeline_color));
        mTimeTextColor = typedArray.getColor(R.styleable.LrcView_lrcTimeTextColor,
                getResources().getColor(R.color.lrc_time_text_color));
        float timeTextSize = typedArray.getDimension(R.styleable.LrcView_lrcTimeTextSize,
                getResources().getDimension(R.dimen.lrc_time_text_size));
        float strokeWidth = typedArray.getDimension(R.styleable.LrcView_lrcTimelineHeight,
                getResources().getDimension(R.dimen.lrc_timeline_height));
        mDefaultLabel = typedArray.getString(R.styleable.LrcView_lrcLabel);
        mDefaultLabel = TextUtils.isEmpty(mDefaultLabel) ? getContext().getString(R.string.lrc_label) : mDefaultLabel;
        float mLrcTextSize = typedArray.getFloat(R.styleable.LrcView_lrcTextSize,
                getResources().getDimension(R.dimen.lrc_text_size));

        typedArray.recycle();

        mDrawableWidth = (int) getResources().getDimension(R.dimen.lrc_drawable_width);
        mTimeTextWidth = (int) getResources().getDimension(R.dimen.lrc_time_width);

        mLrcPaint.setTextSize(mLrcTextSize);
        mLrcPaint.setTextAlign(Paint.Align.LEFT);

        mTimePaint.setTextSize(timeTextSize);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        mTimePaint.setStrokeWidth(strokeWidth);
        mTimePaint.setStrokeCap(Paint.Cap.ROUND);

        mTimeFontMetrics = mTimePaint.getFontMetrics();

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext());
    }

    /**
     * 加载歌词
     */
    public void initLrc(List<LrcEntity> lrcEntries) {

        reset();

        if (lrcEntries != null && !lrcEntries.isEmpty()) {
            mLrcEntityList.addAll(lrcEntries);
        }

        initEntryList();
    }

    private void initEntryList() {
        if (getHeight() == 0 && iLoadLrcCallback != null) {
            iLoadLrcCallback.lrcLoading();
            return;
        }

        for (LrcEntity lrcEntity : mLrcEntityList) {
            lrcEntity.init(mLrcPaint, (int) getLrcWidth());
        }

        centerY = getHeight() / 2;
        mLrcOriginPosition = centerY;
        mOffset = centerY;

        if (iLoadLrcCallback != null) {
            iLoadLrcCallback.lrcLoadSuccess();
            invalidate();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mDefaultStaticLayout = StaticLayout.Builder.obtain(mDefaultLabel, 0, mDefaultLabel.length(), mLrcPaint, (int) getLrcWidth())
                    .setAlignment(Layout.Alignment.ALIGN_CENTER).build();
        } else {
            mDefaultStaticLayout = new StaticLayout(mDefaultLabel, mLrcPaint, (int) getLrcWidth(), Layout.Alignment.ALIGN_CENTER,
                    1f, 0f, false);
        }
    }

    /**
     * 歌词宽度
     */
    private float getLrcWidth() {
        return Math.abs(getWidth() - mLrcPadding * 2);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initEntryList();
            //加入播放按钮和时间text
            int l = (mTimeTextWidth - mDrawableWidth) / 2;
            int t = getHeight() / 2 - mDrawableWidth / 2;
            int r = l + mDrawableWidth;
            int b = t + mDrawableWidth;
            mPlayDrawable.setBounds(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 无歌词文件
        if (mLrcEntityList.isEmpty()) {
            mLrcPaint.setColor(mCurrentTextColor);
            drawText(canvas, mDefaultStaticLayout, centerY);
            return;
        }

        int centerLine = getCenterLine();

        Log.d(TAG, "onDraw: " + isShowTimeline);
        //是否显示时间线
        if (isShowTimeline) {
            mPlayDrawable.draw(canvas);

            mTimePaint.setColor(mTimelineColor);
            canvas.drawLine(mTimeTextWidth, centerY, getWidth() - mTimeTextWidth, centerY, mTimePaint);

            mTimePaint.setColor(mTimeTextColor);
            String timeText = Utils.formatTime(mLrcEntityList.get(centerLine).getTime());
            float timeX = getWidth() - mTimeTextWidth / 2f;
            float timeY = centerY - (mTimeFontMetrics.descent + mTimeFontMetrics.ascent) / 2;
            canvas.drawText(timeText, timeX, timeY, mTimePaint);
        }

        //移动到相应的位置(默认移动到中间位置)
        canvas.translate(0, mOffset);

        //歌词显示的位置
        float y = 0;
        for (int i = 0; i < mLrcEntityList.size(); i++) {
            if (i > 0) {
                y += getLrcHeightOffset(i);
            }

            if (i == mCurrentLine) {
                mLrcPaint.setColor(mCurrentTextColor);
            } else if (isShowTimeline && i == centerLine) {
                mLrcPaint.setColor(mTimelineTextColor);
            } else {
                mLrcPaint.setColor(mNormalTextColor);
            }

            drawText(canvas, mLrcEntityList.get(i).getStaticLayout(), y);
        }
    }

    /**
     * 画歌词
     *
     * @param y 歌词中心 Y 坐标
     */
    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(mLrcPadding, y - staticLayout.getHeight() / 2f);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * 主动刷新
     */
    public void updateTime(long time) {
        if (mLrcEntityList.isEmpty()) {
            return;
        }

        int line = findShowLine(time);
        if (line != mCurrentLine) {
            mCurrentLine = line;
            if (!isShowTimeline) {
                scrollTo(line);
            } else {
                invalidate();
            }
        }
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private int findShowLine(long time) {
        int left = 0;
        int right = mLrcEntityList.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = mLrcEntityList.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= mLrcEntityList.size() || time < mLrcEntityList.get(middle + 1).getTime()) {
                    return middle;
                }
                left = middle + 1;
            }
        }

        return 0;
    }

    /**
     * 滚动到某一行
     */
    private void scrollTo(int line) {
        scrollTo(line, mAnimationDuration);
    }

    private void scrollTo(int line, long duration) {
        float offset = getOffset(line);
        endAnimation();

        mAnimator = ValueAnimator.ofFloat(mOffset, offset);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    private void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    /**
     * 实时获取当前播放
     * TODO 是否可以优化
     */
    private int getCenterLine() {
        int centerLine = 0;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < mLrcEntityList.size(); i++) {
            if (Math.abs(mOffset - getOffset(i)) < minDistance) {
                minDistance = Math.abs(mOffset - getOffset(i));
                centerLine = i;
            }
        }
        return centerLine;
    }

    /**
     * 获取当前歌词所处的位置
     */
    private float getOffset(int line) {
        if (mLrcEntityList.get(line).getOffset() == Float.MIN_VALUE) {
            float offset = mLrcOriginPosition;
            for (int i = 1; i <= line; i++) {
                offset -= getLrcHeightOffset(i);
            }
            mLrcEntityList.get(line).setOffset(offset);
        }

        return mLrcEntityList.get(line).getOffset();
    }

    private void reset() {
        endAnimation();
        mScroller.forceFinished(true);
        isShowTimeline = false;
        isTouching = false;
        isFling = false;
        removeCallbacks(hideTimelineRunnable);
        mLrcEntityList.clear();
        mOffset = 0;
        mCurrentLine = 0;
        invalidate();
    }

    /**
     * 文字高度上一行和下一行文字之和的一半+文字间距
     */
    private float getLrcHeightOffset(int i) {
        return (mLrcEntityList.get(i - 1).getHeight() + mLrcEntityList.get(i).getHeight()) / 2f + mDividerHeight;
    }

    private int endX = 0;
    private int endY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endX = (int) event.getX();
                endY = (int) event.getY();
                //1.拖动时间线，放手不点击按钮
                //2.拖动时间线，放手点击按钮
                if (!mLrcEntityList.isEmpty() && !isFling) {
                    postHideTimeRunnable();
                }
                isTouching = false;
            default:
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private void postHideTimeRunnable() {
        adjustCenter();
        removeCallbacks(hideTimelineRunnable);
        postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
    }

    /**
     * 不让拖动线马上隐藏
     */
    private Runnable hideTimelineRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mLrcEntityList.isEmpty() && isShowTimeline) {
                isShowTimeline = false;
                scrollTo(mCurrentLine);
            }
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            isTouching = true;
            if (!mLrcEntityList.isEmpty()) {
                mScroller.forceFinished(true);
                removeCallbacks(hideTimelineRunnable);
                isShowTimeline = true;
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mLrcEntityList.isEmpty()) {
                mOffset += -distanceY;
                //校验合法性
                mOffset = Math.min(mOffset, getOffset(0));
                mOffset = Math.max(mOffset, getOffset(mLrcEntityList.size() - 1));
                invalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            boolean contains = mPlayDrawable.getBounds().contains(endX, endY);
            Log.d(TAG, "onSingleTapConfirmed: " + contains);
            if (!mLrcEntityList.isEmpty() && contains) {
                int centerLine = getCenterLine();
                long time = mLrcEntityList.get(centerLine).getTime();
                if (iLoadLrcCallback != null && iLoadLrcCallback.onPlayTime(time)) {
                    isShowTimeline = false;
                    removeCallbacks(hideTimelineRunnable);
                    mCurrentLine = centerLine;
                    invalidate();
                    return true;
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!mLrcEntityList.isEmpty()) {
                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 0,
                        (int) getOffset(mLrcEntityList.size() - 1), (int) getOffset(0));
                isFling = true;
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    /**
     * 将中心行微调至正中心
     */
    private void adjustCenter() {
        scrollTo(getCenterLine(), ADJUST_DURATION);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrY();
            invalidate();
        }

        if (isFling && mScroller.isFinished()) {
            isFling = false;
            if (!mLrcEntityList.isEmpty() && !isTouching) {
                postHideTimeRunnable();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(hideTimelineRunnable);
    }

    public void setOnLrcLoadingListener(ILoadLrcCallback lrcLoadingListener) {
        this.iLoadLrcCallback = lrcLoadingListener;
    }

    // johnliu add

    /**
     * 是否有歌词
     * @return
     */
    public boolean hasLyric(){
        return (null != mLrcEntityList && !mLrcEntityList.isEmpty());
    }
}
