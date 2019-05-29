package com.semisky.multimedia.media_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaListConst;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_bt_music.view.BTMusicFragment;
import com.semisky.multimedia.media_music.view.MusicListFragment;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MultimediaListActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = Logutil.makeTagLog(MultimediaListActivity.class);

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment[] mFragments = new Fragment[3];// Fragments array
    private String[] mFragmentsTag = new String[3];// Fragments tag array
    private RelativeLayout[] mFragmentListTabUI = new RelativeLayout[3];
    private int mCurrentFragmentIndex = 0;
    // Fragments tag
    private static final String FRAGMENT_MUSIC_LIST_USB1_TAG = MusicListFragment.class.getSimpleName() + "usb1";
    private static final String FRAGMENT_MUSIC_LIST_USB2_TAG = MusicListFragment.class.getSimpleName() + "usb2";
    private static final String FRAGEMNT_BT_MUSIC_LIST_TAG = BTMusicFragment.class.getCanonicalName();

    // Fragments index in the array
    private static final int FRAGMENT_MUSIC_LIST_USB1_INDEX = MediaListConst.FRAGMENT_LIST_USB1_MUSIC;
    private static final int FRAGMENT_MUSIC_LIST_USB2_INDEX = MediaListConst.FRAGMENT_LIST_USB2_MUSIC;
    private static final int FRAGMENT_BT_MUSIC_LIST_INDEX = MediaListConst.FRAGMENT_LIST_BT_MUSIC;


    private TextView
            tv_usb_one,
            tv_usb_two,
            tv_bt_;
    private RelativeLayout rl_bt, rl_usbOne, rl_usbTwo;
    private ImageView iv_bt_, iv_usb_one, iv_usb_two;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_multimedia_list);
        super.onCreate(savedInstanceState);
        initViews();
        setListener();
        handlerIntent();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(intent.getIntExtra(MediaListConst.FRAGMENT_FLAG, MediaListConst.FRAGMENT_LIST_USB1_MUSIC));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(home, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }


    protected void initViews() {
        iv_usb_one = (ImageView) findViewById(R.id.iv_usb_one);
        iv_usb_two = (ImageView) findViewById(R.id.iv_usb_two);
        iv_bt_ = (ImageView) findViewById(R.id.iv_bt);
        tv_bt_ = (TextView) findViewById(R.id.tv_bt);
        tv_usb_one = (TextView) findViewById(R.id.tv_usb_one);
        tv_usb_two = (TextView) findViewById(R.id.tv_usb_two);
        rl_bt = (RelativeLayout) findViewById(R.id.rl_bt);
        rl_usbOne = (RelativeLayout) findViewById(R.id.rl_usb_one);
        rl_usbTwo = (RelativeLayout) findViewById(R.id.rl_usb_two);
    }

    protected void setListener() {
        rl_bt.setOnClickListener(this);
        rl_usbOne.setOnClickListener(this);
        rl_usbTwo.setOnClickListener(this);
        mFragmentListTabUI[FRAGMENT_MUSIC_LIST_USB1_INDEX] = rl_usbOne;
        mFragmentListTabUI[FRAGMENT_MUSIC_LIST_USB2_INDEX] = rl_usbTwo;
        mFragmentListTabUI[FRAGMENT_BT_MUSIC_LIST_INDEX] = rl_bt;
    }

    private void handlerIntent() {
        Intent intent = getIntent();
        int fragmentFlag = intent.getIntExtra(MediaListConst.FRAGMENT_FLAG, MediaListConst.FRAGMENT_LIST_USB1_MUSIC);
        initShowFragment(fragmentFlag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_usb_one:
                switchFragment(FRAGMENT_MUSIC_LIST_USB1_INDEX);
                setSelectorBackground(rl_usbOne, iv_usb_one, tv_usb_one);

                MediaApplication.setCurrentUSB(Definition.FLAG_USB1);
                break;
            case R.id.rl_usb_two:
                switchFragment(FRAGMENT_MUSIC_LIST_USB2_INDEX);
                setSelectorBackground(rl_usbTwo, iv_usb_two, tv_usb_two);
                MediaApplication.setCurrentUSB(Definition.FLAG_USB2);
                break;
            case R.id.rl_bt:
                if (SemiskyIVIManager.getInstance().isBtConnected()) {
                    AppUtil.jumpToBTMusic();
                    overridePendingTransition(0,0);
                }else {
                    switchFragment(FRAGMENT_BT_MUSIC_LIST_INDEX);
                }
                setSelectorBackground(rl_bt, iv_bt_, tv_bt_);
                break;

        }
    }

    /**
     * 初始显示页面列表
     *
     * @param fragmentFlag
     */
    private void initShowFragment(int fragmentFlag) {
        Log.i(TAG, "initShowFragment() fragmentFlag :" + fragmentFlag);
        this.mCurrentFragmentIndex = fragmentFlag;
        this.mFragmentManager = getSupportFragmentManager();
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        // Fragment tag add to array
        mFragmentsTag[FRAGMENT_MUSIC_LIST_USB1_INDEX] = FRAGMENT_MUSIC_LIST_USB1_TAG;
        mFragmentsTag[FRAGMENT_MUSIC_LIST_USB2_INDEX] = FRAGMENT_MUSIC_LIST_USB2_TAG;
        mFragmentsTag[FRAGMENT_BT_MUSIC_LIST_INDEX] = FRAGEMNT_BT_MUSIC_LIST_TAG;

        // Fragment instance add to array
        mFragments[FRAGMENT_MUSIC_LIST_USB1_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_MUSIC_LIST_USB1_TAG);
        mFragments[FRAGMENT_MUSIC_LIST_USB2_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_MUSIC_LIST_USB2_TAG);
        mFragments[FRAGMENT_BT_MUSIC_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGEMNT_BT_MUSIC_LIST_TAG);


        if (null == mFragments[FRAGMENT_MUSIC_LIST_USB1_INDEX]) {
            this.mFragments[FRAGMENT_MUSIC_LIST_USB1_INDEX] = new MusicListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(MediaListConst.FRAGMENT_FLAG, Definition.FLAG_USB1);
            this.mFragments[FRAGMENT_MUSIC_LIST_USB1_INDEX].setArguments(bundle);

        }
        if (null == mFragments[FRAGMENT_MUSIC_LIST_USB2_INDEX]) {
            this.mFragments[FRAGMENT_MUSIC_LIST_USB2_INDEX] = new MusicListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(MediaListConst.FRAGMENT_FLAG, Definition.FLAG_USB2);
            this.mFragments[FRAGMENT_MUSIC_LIST_USB2_INDEX].setArguments(bundle);

        }

        if (null == mFragments[FRAGMENT_BT_MUSIC_LIST_INDEX]) {
            this.mFragments[FRAGMENT_BT_MUSIC_LIST_INDEX] = new BTMusicFragment();
        }

        for(Fragment fragment:mFragments){
            if(fragment.isVisible()){
                mFragmentTransaction.hide(fragment);
            }
        }


        if (!this.mFragments[fragmentFlag].isAdded()) {
            mFragmentTransaction.add(R.id.fragment_container, mFragments[fragmentFlag], mFragmentsTag[fragmentFlag]);
            mFragmentTransaction.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }

        if(!mFragments[fragmentFlag].isVisible()){
            Log.i(TAG,"SHOW ...");
            mFragmentTransaction.show(mFragments[fragmentFlag]);
        }

        mFragmentTransaction.show(mFragments[fragmentFlag]);
        this.mFragmentListTabUI[fragmentFlag].setSelected(true);
        if (fragmentFlag == MediaListConst.FRAGMENT_LIST_USB1_MUSIC) {
            setSelectorBackground(rl_usbOne, iv_usb_one, tv_usb_one);
        } else if (fragmentFlag == MediaListConst.FRAGMENT_LIST_USB2_MUSIC) {
            setSelectorBackground(rl_usbTwo, iv_usb_two, tv_usb_two);
        } else if (fragmentFlag == MediaListConst.FRAGMENT_LIST_BT_MUSIC) {
            setSelectorBackground(rl_bt, iv_bt_, tv_bt_);
        }
    }

    /**
     * 用户切换列表
     *
     * @param position
     */
    private void switchFragment(int position) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (position == mCurrentFragmentIndex) {
            return;
        }

        if (!mFragments[position].isAdded() && null == mFragmentManager.findFragmentByTag(mFragmentsTag[position])) {
            mFragmentTransaction.add(R.id.fragment_container, mFragments[position], mFragmentsTag[position]);
        }
        mFragmentTransaction.hide(mFragments[mCurrentFragmentIndex]).show(mFragments[position]);
        mFragmentTransaction.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();
        this.mFragmentListTabUI[mCurrentFragmentIndex].setSelected(false);
        this.mFragmentListTabUI[position].setSelected(true);
        this.mCurrentFragmentIndex = position;

    }

    private void setSelectorBackground(RelativeLayout layout, ImageView imageView, TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.white));

        if (layout.getId() == rl_usbOne.getId()) {// 选中
            rl_usbOne.setBackgroundResource(R.drawable.sidebar_btn_pressed);
            iv_usb_one.setImageResource(R.drawable.common_btn_usb1_pressed);
        } else {// 未选中
            rl_usbOne.setBackground(null);
            iv_usb_one.setImageResource(R.drawable.common_btn_usb1_normal);
            tv_usb_one.setTextColor(getResources().getColor(R.color.colorArtist));
        }


        if (layout.getId() == rl_usbTwo.getId()) {// 选中
            rl_usbTwo.setBackgroundResource(R.drawable.sidebar_btn_pressed);
            iv_usb_two.setImageResource(R.drawable.common_btn_usb2_pressed);
        } else {// 未选中
            rl_usbTwo.setBackground(null);
            iv_usb_two.setImageResource(R.drawable.common_btn_usb2_normal);
            tv_usb_two.setTextColor(getResources().getColor(R.color.colorArtist));

        }

        if (layout.getId() == R.id.rl_bt) {// 选中
            rl_bt.setBackgroundResource(R.drawable.sidebar_btn_pressed);
            iv_bt_.setImageResource(R.drawable.btn_bt_pressed);
        } else {// 未选中
            rl_bt.setBackground(null);
            iv_bt_.setImageResource(R.drawable.btn_bt_normal);
            tv_bt_.setTextColor(getResources().getColor(R.color.colorArtist));

        }
    }

    BroadcastReceiver home = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(home);
    }


}
