package com.semisky.multimedia.common.base_presenter;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

public class BasePresenter<V> {
    protected WeakReference<V> mViewRef;
    protected Handler _handler = new Handler(Looper.getMainLooper());


    public void onAttachView(V view) {
        if (!isBindView()) {
            mViewRef = new WeakReference<V>(view);
        }
    }

    public void onDetachView() {
        this.mViewRef.clear();
        this.mViewRef = null;
    }

    public boolean isBindView() {
        return (null != mViewRef && null != this.mViewRef.get());
    }

}
