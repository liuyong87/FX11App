package com.semisky.multimedia.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;


import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;

import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anter on 2018/7/28.
 */

public class MediaApplication extends Application {
    private static final String TAG = Logutil.makeTagLog(MediaApplication.class);
    public static List<Activity> mActivitys = Collections.synchronizedList(new LinkedList<Activity>());
    private static Context mContext;
    private static int currentUSB = 1;

    public static Context getContext() {
        return mContext;
    }

    public static void setCurrentUSB(int usb){
        currentUSB = usb;
    }
    public static int getCurrentUSB(){
        return currentUSB;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Logutil.i(TAG, "onCreate() ...");
        mContext = this.getApplicationContext();
        registerActivityListener();
        MediaStorageAccessProxyModel.getInstance().onAttachContext(mContext);
        if (!MediaStorageAccessProxyModel.getInstance().isConnection()) {
            MediaStorageAccessProxyModel.getInstance().bindService();
        }
        SemiskyIVIManager.getInstance().bindBottombarService();
    }
    

    
    

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
        Logutil.d(TAG, "activityList:size:" + mActivitys.size());
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
        Logutil.d(TAG, "activityList:size:" + mActivitys.size());
    }


    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    /**
                     *  监听到 Activity创建事件 将该 Activity 加入list
                     */
                    pushActivity(activity);

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null == mActivitys && mActivitys.isEmpty()) {
                        return;
                    }
                    if (mActivitys.contains(activity)) {
                        /**
                         *  监听到 Activity销毁事件 将该Activity 从list中移除
                         */
                        popActivity(activity);
                    }
                }
            });
        }
    }



}
