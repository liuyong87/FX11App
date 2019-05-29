package com.semisky.multimedia.media_photo.model;

import android.view.View;
import android.widget.ImageView;

import com.semisky.multimedia.media_photo.photoplayer.PhotoView;

/**
 * Created by Anter on 2018/8/4.
 */

public class PhotoFrameBuilder implements IPhotoFrame {
    private static PhotoFrameBuilder _INSTANCE;
    private IPhotoFrame mPhotoFrame;

    private PhotoFrameBuilder() {
        this.mPhotoFrame = new GlideModel();
    }

    public static PhotoFrameBuilder create() {
        if (null == _INSTANCE) {
            _INSTANCE = new PhotoFrameBuilder();
        }
        return _INSTANCE;
    }

    @Override
    public void showBigPhoto(PhotoView img, String imgUrl) {
        mPhotoFrame.showBigPhoto(img, imgUrl);
    }

    @Override
    public void showSmallPhoto(ImageView img, String imgUrl) {
        mPhotoFrame.showSmallPhoto(img, imgUrl);
    }

    @Override
    public void removeView(View view) {
        mPhotoFrame.removeView(view);
    }
}
