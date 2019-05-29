package com.semisky.multimedia.common.base_view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.semisky.multimedia.common.base_presenter.BasePresenter;


public abstract class BaseActivity<V, P extends BasePresenter<V>> extends FragmentActivity {

	protected P mPresenter;

	protected abstract int getLayoutResID();

	protected abstract P createPresenter();

	protected abstract void initViews();
	protected abstract void setListener();
	protected abstract void initData();

	protected abstract void handlerIntent(Intent intent);

	protected boolean isBindPresenter() {
		return (null != this.mPresenter);
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResID());
		mPresenter = createPresenter();
		mPresenter.onAttachView((V) this);

		initViews();
		setListener();
		initData();
		handlerIntent(super.getIntent());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPresenter.onDetachView();
	}

}
