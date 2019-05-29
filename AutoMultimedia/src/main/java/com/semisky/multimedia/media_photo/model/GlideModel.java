package com.semisky.multimedia.media_photo.model;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.semisky.multimedia.R;
import com.semisky.multimedia.media_photo.photoplayer.PhotoView;

/**
 * Created by Anter on 2018/8/4.
 */

public class GlideModel implements IPhotoFrame {

    @Override
    public void showBigPhoto(PhotoView img, String imgUrl) {
        Glide.with(img.getContext())
                .load(imgUrl)
                .skipMemoryCache(true)
                .error(R.drawable.photo_icon_error)
                .into(img);
    }

    @Override
    public void showSmallPhoto(ImageView img, String imgUrl) {
        Glide.with(img.getContext())
                .load(imgUrl)
                .placeholder(R.drawable.photo_icon_def)
                .error(R.drawable.photo_icon_error)
                .into(img);
    }

    @Override
    public void removeView(View view) {
        Glide.clear(view);
    }
}
