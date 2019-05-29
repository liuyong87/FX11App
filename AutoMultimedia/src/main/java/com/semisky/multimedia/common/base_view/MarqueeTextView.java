package com.semisky.multimedia.common.base_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by LiuYong on 2018/9/19.
 */

public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context) {
        super(context);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
