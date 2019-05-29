package com.semisky.multimedia.media_photo.adapter;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by lcc on 2018/10/25.
 */

public class PhotoGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        glideBuilder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
