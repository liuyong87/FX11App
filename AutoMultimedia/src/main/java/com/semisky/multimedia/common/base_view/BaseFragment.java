package com.semisky.multimedia.common.base_view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semisky.multimedia.common.base_presenter.BasePresenter;

/**
 * Created by LiuYong on 2018/8/9.
 */

public abstract class BaseFragment<V ,P extends BasePresenter<V>> extends Fragment {

    protected abstract P createPresenter();

    protected abstract int getLayoutResID();

    protected abstract void initViews();

    protected abstract void setListener();

    protected abstract void initData();

    public P mPresenter;

    protected View mContentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutResID(),container,false);
        initViews();
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.onAttachView((V)this);
        setListener();
        initData();
    }


    @Override
    public void onDestroyView() {
        mPresenter.onDetachView();
        super.onDestroyView();
    }

    protected boolean isBindPresenter(){
        return (null != mPresenter);
    }
}
