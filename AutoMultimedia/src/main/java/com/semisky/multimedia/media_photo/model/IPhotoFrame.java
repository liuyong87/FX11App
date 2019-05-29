package com.semisky.multimedia.media_photo.model;

import android.view.View;
import android.widget.ImageView;

import com.semisky.multimedia.media_photo.photoplayer.PhotoView;

/**
 * Created by Anter on 2018/8/4.
 */

public interface IPhotoFrame {

    void showBigPhoto(PhotoView img,String imgUrl);

    void showSmallPhoto(ImageView img, String imgUrl);

    void removeView(View view);

}
