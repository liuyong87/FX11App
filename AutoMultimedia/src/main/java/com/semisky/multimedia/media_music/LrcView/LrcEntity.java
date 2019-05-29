/*
 * Copyright (C) 2017 wangchenyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.semisky.multimedia.media_music.LrcView;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class LrcEntity implements Comparable<LrcEntity> {

    private long time;
    private String text;
    private StaticLayout staticLayout;
    private float offset = Float.MIN_VALUE;

    public LrcEntity(long time, String text) {
        this.time = time;
        this.text = text;
    }

    void init(TextPaint paint, int width) {
        Layout.Alignment align = Layout.Alignment.ALIGN_CENTER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = StaticLayout.Builder.obtain(text, 0, text.length(), paint, width).setAlignment(align).build();
        } else {
            staticLayout = new StaticLayout(text, paint, width, align, 1f, 0f, false);
        }
    }

    long getTime() {
        return time;
    }

    StaticLayout getStaticLayout() {
        return staticLayout;
    }

    int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    float getOffset() {
        return offset;
    }

    void setOffset(float offset) {
        this.offset = offset;
    }

    @Override
    public int compareTo(LrcEntity entry) {
        if (entry == null) {
            return -1;
        }
        return (int) (time - entry.getTime());
    }
}
