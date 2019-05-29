package com.semisky.multimedia.common.base_view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.semisky.multimedia.R;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LiuYong on 2018/8/14.
 */

public class ToastCustom {
    private static long LENGTH_SHORT = 500;
    private static long LENGTH_LONG = 2000;
    public static long DELAY_TIME_1S = 1000;
    public static long DELAY_TIME_2S = 2000;
    public static long DELAY_TIME_5S = 5000;
    private static Handler mSingleMsgHandler = new Handler(Looper.getMainLooper());
    private static View mSigleMsgView = null;
    private static TextView mSigleMsgText = null;
    private static Toast mSigleMsgToast = null;
    private static Timer timer = null;

    public static void showSingleMsg(Context context, int msg, long delayTime) {
        if (null == mSigleMsgView) {
            mSigleMsgView = LayoutInflater.from(context).inflate(R.layout.toast_single_message_layout, null);
            mSigleMsgText = (TextView) mSigleMsgView.findViewById(R.id.tv_show_msg);
            mSigleMsgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDialog();
                }
            });
        }
        mSigleMsgText.setText(msg);
        mSingleMsgHandler.removeCallbacks(mSingleMsgRunnable);
        if (mSigleMsgToast == null) {
            mSigleMsgToast = new Toast(context);
            mSigleMsgToast.setDuration(Toast.LENGTH_LONG);
            mSigleMsgToast.setGravity(Gravity.FILL, 0, 0);
            mSigleMsgToast.setView(mSigleMsgView);
        }
        requstFocus(mSigleMsgToast);
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mSigleMsgToast != null) {
                    mSigleMsgToast.show();
                }
            }
        }, 2500);

        mSingleMsgHandler.removeCallbacks(mSingleMsgRunnable);
        mSingleMsgHandler.postDelayed(mSingleMsgRunnable, delayTime);
        mSigleMsgToast.show();
    }

    private static Runnable mSingleMsgRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSigleMsgToast != null) {
                mSigleMsgToast.cancel();
                mSigleMsgToast = null;
            }

        }
    };

    /**
     * 关闭显示弹窗
     */
    public static void closeDialog() {
        if (mSigleMsgToast != null) {
            mSigleMsgToast.cancel();
            mSigleMsgToast = null;
        }

    }

    // 设置Toast可以获取焦点事件
    private static void requstFocus(Toast mToast) {
        Object mTN;
        try {
            mTN = getField(mToast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getField(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
