package com.semisky.multimedia.media_photo.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.semisky.multimedia.R;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;

public class PhotoListActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = Logutil.makeTagLog(PhotoListActivity.class);

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment[] mFragments = new Fragment[2];// Fragments array
    private String[] mFragmentsTag = new String[2];// Fragments tag array
    private Button[] mFragmentListTabUI = new Button[2];
    private int mCurrentFragmentIndex = Definition.MediaListConst.FRAGMENT_LIST_USB1_BY_PHOTO;

    // Fragment tag
    private static final String FRAGMENT_USB1_LIST_TAG = PhotoListFragment.class.getSimpleName() + "ByUsb1";
    private static final String FRAGMENT_USB2_LIST_TAG = PhotoListFragment.class.getSimpleName() + "ByUsb2";
    // Fragment index
    private static final int FRAGMENT_USB1_LIST_INDEX = Definition.MediaListConst.FRAGMENT_LIST_USB1_BY_PHOTO;
    private static final int FRAGMENT_USB2_LIST_INDEX = Definition.MediaListConst.FRAGMENT_LIST_USB2_BY_PHOTO;

    private Button
            btn_usb1,
            btn_usb2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        initViews();
        setListener();
        handlerIntent(super.getIntent());
    }

    private void initViews() {
        btn_usb1 = (Button) findViewById(R.id.btn_usb1);
        btn_usb2 = (Button) findViewById(R.id.btn_usb2);
    }

    private void setListener() {
        btn_usb1.setOnClickListener(this);
        btn_usb2.setOnClickListener(this);
    }

    private void handlerIntent(Intent intent) {
        int fragmentFlag = intent.getIntExtra(Definition.MediaListConst.FRAGMENT_FLAG,
                Definition.MediaListConst.FRAGMENT_LIST_USB1_BY_VIDEO);
        Log.i(TAG, "handlerIntent() ..." + fragmentFlag);
        initShowFragment(fragmentFlag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_usb1:
                switchFragment(FRAGMENT_USB1_LIST_INDEX);
                break;
            case R.id.btn_usb2:
                switchFragment(FRAGMENT_USB2_LIST_INDEX);
                break;
        }
    }

    // 初始显示界面
    private void initShowFragment(int fragmentFlag) {
        this.mCurrentFragmentIndex = fragmentFlag;
        this.mFragmentManager = getSupportFragmentManager();
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        // Fragment tag add to array
        mFragmentsTag[FRAGMENT_USB1_LIST_INDEX] = FRAGMENT_USB1_LIST_TAG;
        mFragmentsTag[FRAGMENT_USB2_LIST_INDEX] = FRAGMENT_USB2_LIST_TAG;
        // Fragment instance add to array
        mFragments[FRAGMENT_USB1_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_USB1_LIST_TAG);
        mFragments[FRAGMENT_USB2_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_USB2_LIST_TAG);
        // add ui
        mFragmentListTabUI[FRAGMENT_USB1_LIST_INDEX] = btn_usb1;
        mFragmentListTabUI[FRAGMENT_USB2_LIST_INDEX] = btn_usb2;

        if (null == mFragments[FRAGMENT_USB1_LIST_INDEX]) {
            PhotoListFragment photoListFragmentByUsb1 = new PhotoListFragment();
            photoListFragmentByUsb1.setUsbFlag(Definition.FLAG_USB1);
            mFragments[FRAGMENT_USB1_LIST_INDEX] = photoListFragmentByUsb1;

        }

        if (null == mFragments[FRAGMENT_USB2_LIST_INDEX]) {
            PhotoListFragment photoListFragmentByUsb2 = new PhotoListFragment();
            photoListFragmentByUsb2.setUsbFlag(Definition.FLAG_USB2);
            mFragments[FRAGMENT_USB2_LIST_INDEX] = photoListFragmentByUsb2;
        }

        if (FRAGMENT_USB1_LIST_INDEX == fragmentFlag) {
            setSelectorBackground(btn_usb1);
        } else if (FRAGMENT_USB2_LIST_INDEX == fragmentFlag) {
            setSelectorBackground(btn_usb2);
        }
        // 展示当前Fragment界面
        if (!this.mFragments[fragmentFlag].isAdded()) {
            mFragmentTransaction.add(R.id.fragment_container, mFragments[fragmentFlag], mFragmentsTag[fragmentFlag]);
            mFragmentTransaction.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
    }

    private void setSelectorBackground(Button btn) {

        if (btn.getId() == R.id.btn_usb1) {// btn1选中
            Log.i(TAG, "setSelectorBackground() BTN1");
            btn_usb1.setBackgroundResource(R.drawable.sidebar_btn_pressed);
            Drawable drawable = getResources().getDrawable(R.drawable.common_btn_usb1_pressed);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_usb1.setCompoundDrawables(drawable, null, null, null);

            btn_usb1.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn_usb1.setBackground(null);
            btn_usb1.setTextColor(getResources().getColor(R.color.colorArtist));

            Drawable drawable = getResources().getDrawable(R.drawable.common_btn_usb1_normal);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_usb1.setCompoundDrawables(drawable, null, null, null);
        }

        if (btn.getId() == R.id.btn_usb2) {// btn2选中
            Log.i(TAG, "setSelectorBackground() BTN2");
            Drawable drawable = getResources().getDrawable(R.drawable.common_btn_usb2_pressed);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_usb2.setCompoundDrawables(drawable, null, null, null);
            btn_usb2.setBackgroundResource(R.drawable.sidebar_btn_pressed);
            btn_usb2.setTextColor(getResources().getColor(R.color.white));
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.common_btn_usb2_normal);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_usb2.setCompoundDrawables(drawable, null, null, null);

            btn_usb2.setBackground(null);
            btn_usb2.setTextColor(getResources().getColor(R.color.colorArtist));
        }
    }

    private void switchFragment(int fragmentFlag) {
        Log.i(TAG, "switchFragment() ...fragmentFlag : " + fragmentFlag + ",mCurrentFragmentIndex : " + mCurrentFragmentIndex);
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (fragmentFlag == mCurrentFragmentIndex) {
            return;
        }

        if (null == mFragments[fragmentFlag]) {
            Log.i(TAG, "switchFragment() Fragment == NULL");
            return;
        }

        if (null == mFragmentManager.findFragmentByTag(mFragmentsTag[fragmentFlag]) && !mFragments[fragmentFlag].isAdded()) {
            mFragmentTransaction.add(R.id.fragment_container, mFragments[fragmentFlag], mFragmentsTag[fragmentFlag]);
        }
        mFragmentTransaction.hide(mFragments[mCurrentFragmentIndex]).show(mFragments[fragmentFlag]);
        mFragmentTransaction.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();
        setSelectorBackground(mFragmentListTabUI[fragmentFlag]);
        this.mCurrentFragmentIndex = fragmentFlag;
    }

}
