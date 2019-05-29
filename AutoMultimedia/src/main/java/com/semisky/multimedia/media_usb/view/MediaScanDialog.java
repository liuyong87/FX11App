package com.semisky.multimedia.media_usb.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semisky.multimedia.R;

/**
 * 媒体扫描弹窗
 * Created by Anter on 2018/8/6.
 */

public class MediaScanDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView tv_scan_status;
    private View contentView;
    private RelativeLayout rl_dialog_view;
    private View.OnClickListener mOnClickListener;


    public MediaScanDialog(Context context) {
        super(context, R.style.DialogStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        WindowManager.LayoutParams lParams = getWindow().getAttributes();
        lParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        lParams.gravity = Gravity.CENTER;
        // lParams.dimAmount = 0.9f;
        getWindow().setAttributes(lParams);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //        this.setCanceledOnTouchOutside(true);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        contentView = inflater.inflate(R.layout.dialog_media_scan, null, false);
        setContentView(contentView);
        initWidget();

    }

    private void initWidget() {
        tv_scan_status = (TextView) contentView.findViewById(R.id.tv_scan_status);
        rl_dialog_view = (RelativeLayout) contentView.findViewById(R.id.rl_dialog_view);
        rl_dialog_view.setOnClickListener(this);
    }

    /**
     * U盘未挂载提示信息
     */
    public void alertUSBUnmounted() {
        tv_scan_status.setText(R.string.tv_scan_status_unmount_usb_text);
    }
    /**
     * U盘扫描未完成提示信息
     */
    public void alertUSBFailure(){
        tv_scan_status.setText(R.string.tv_scan_status_failure_usb_text);
    }

    /**
     * 正在加载U盘数据提示信息
     */
    public void alertUSBLoadding() {
        tv_scan_status.setText(R.string.tv_scan_status_loadding_text);
    }


    public void setmOnClickListener(View.OnClickListener l){
        this.mOnClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if(null != mOnClickListener){
            mOnClickListener.onClick(v);
        }
    }
}
