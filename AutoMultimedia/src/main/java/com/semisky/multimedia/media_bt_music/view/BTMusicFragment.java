package com.semisky.multimedia.media_bt_music.view;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.semisky.multimedia.R;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_bt_music.presenter.BTMusicPresenter;

public class BTMusicFragment extends BaseFragment<IBTMusicView, BTMusicPresenter<IBTMusicView>>
        implements IBTMusicView, View.OnClickListener {
    private static final String TAG = Logutil.makeTagLog(BTMusicFragment.class);

    private Button btn_bt_connect_switch;
    private TextView tv_bt_status,tv_bt_list;
    private View ll_bt_connect_status;

    @Override
    protected BTMusicPresenter<IBTMusicView> createPresenter() {
        return new BTMusicPresenter();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_bt_music;
    }

    @Override
    protected void initViews() {
        this.btn_bt_connect_switch = (Button) mContentView.findViewById(R.id.btn_bt_connect_switch);
        this.tv_bt_status = (TextView) mContentView.findViewById(R.id.tv_bt_status);
        this.tv_bt_list = (TextView) mContentView.findViewById(R.id.tv_bt_list);
        ll_bt_connect_status = mContentView.findViewById(R.id.ll_bt_connect_status);

    }

    @Override
    protected void setListener() {
        this.btn_bt_connect_switch.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (!isBindPresenter()) {
            return;
        }
        mPresenter.checkBtMusicConnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isBindPresenter()){
            return;
        }
        mPresenter.onViewResume();
        mPresenter.checkBtMusicConnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!isBindPresenter()){
            return;
        }
        mPresenter.onViewStop();
    }

    @Override
    public void onBTConnectionStatusChanged(boolean isConnect) {
        Log.i(TAG,"onBTConnectionStatusChanged() isConnect : "+isConnect);
        int resId = isConnect ?
                R.string.btn_bt_connection_text : R.string.btn_bt_disconnection_text;
        tv_bt_status.setText(resId);

        if(isConnect){

            ll_bt_connect_status.setVisibility(View.GONE);// 未连接视图隐藏
            tv_bt_list.setVisibility(View.VISIBLE);// 显示已连接，显示蓝牙无列表数据
        }else {
            ll_bt_connect_status.setVisibility(View.VISIBLE);
            tv_bt_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isBindPresenter()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_bt_connect_switch:
                mPresenter.reqBtMusicConnect();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (isBindPresenter()) {
            mPresenter.destory();
        }
        super.onDestroyView();
    }

}
