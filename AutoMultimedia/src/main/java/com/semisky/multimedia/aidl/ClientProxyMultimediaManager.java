package com.semisky.multimedia.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.semisky.multimedia.common.utils.Logutil;


/**
 * 多媒体管理类
 * Created by LiuYong on 2018/9/3.
 */

public class ClientProxyMultimediaManager {
    private static String TAG = "ClientProxyMultimediaManager";
    private Context mContext;
    private IProxyMultimedia mProxyMultimedia;

    // 多媒体服务包名与类名
    private static final String SERVICE_PKG = "com.semisky.multimedia";
    private static final String SERVICE_CLZ = "com.semisky.multimedia.media_multimedia.MultimediaService";
    // 多媒体Activity隐式意图
    private static final String ACTION_ACTIVITY_MUSIC = "com.semisky.action.VIEW_MUSIC_PLAYTER";
    private static final String ACTION_ACTIVITY_VIDEO = "com.semisky.action.VIEW_VIDEO_PLAYTER";
    private static final String ACTION_ACTIVITY_PHOTO = "com.semisky.action.VIEW_PHOTO_PLAYTER";
    private static final String ACTION_ACTIVITY_MUSICLIST ="com.semisky.action.VIEW_MULTIMEDIA_LIST";
    //APP FLAG
    public static final int APP_FLAG_MUSIC = 1;
    public static final int APP_FLAG_VIDEO = 2;
    public static final int APP_FLAG_PHOTO = 3;
    public static final int APP_FLAG_INVALID = -1;// 无效应用标识


    private static ClientProxyMultimediaManager _INSTANCE;

    private ClientProxyMultimediaManager() {

    }

    public static ClientProxyMultimediaManager getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new ClientProxyMultimediaManager();
        }
        return _INSTANCE;
    }


    /**
     * 多媒体服务是否连接
     *
     * @return
     */
    public boolean isConnected() {
        return (null != mProxyMultimedia);
    }

    public void bindService() {
        Log.i(TAG, "bindService() ...");
        Intent bindIntent = new Intent();
        bindIntent.setClassName(SERVICE_PKG, SERVICE_CLZ);
        mContext.bindService(bindIntent, mConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unbindService() {
        Log.i(TAG, "unbindService() ...");
        Intent bindIntent = new Intent();
        bindIntent.setClassName(SERVICE_PKG, SERVICE_CLZ);
        mContext.unbindService(mConn);
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mProxyMultimedia = IProxyMultimedia.Stub.asInterface(service);
            Log.i(TAG, "Multimedia onServiceConnected() ..." + name.getClassName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Multimedia onServiceDisconnected() ..." + name.getClassName());
            mProxyMultimedia = null;
        }
    };

    /**
     * 注入上下文
     *
     * @param ctx
     */
    public void init(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * 获取多媒体应用标识
     *
     * @return
     * @throws RemoteException
     */
    public int getMultimediaAppFlag() {
        if (isConnected()) {
            Log.i(TAG, "getMultimediaAppFlag() ...");
            try {
                return mProxyMultimedia.getMultimediaAppFlag();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 指定应用标识是否有效
     *
     * @param appFlag
     * @return
     */
    public boolean hasValidAppFlagWith(int appFlag) {
        if (isConnected()) {
            Log.i(TAG, "hasValidAppFlagWith() ..." + appFlag);
            try {
                return mProxyMultimedia.hasValidAppFlagWith(appFlag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 启动多媒体
     */
    public void onLaunchMultimedia() {
        if (isConnected()) {
            Log.i(TAG, "onLaunchMultimedia() ...");
            try {
                mProxyMultimedia.onLaunchMultimedia();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动多媒体音乐播放服务
     */
    public void onLaunchMusicPlayService() {
        if (isConnected()) {
            Log.i(TAG, "onLaunchMusicPlayService() ...");
            try {
                mProxyMultimedia.onLaunchMusicPlayService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    // utils

    /**
     * 启动界面
     *
     * @param appFlag
     */
    public void jumpTo(int appFlag,boolean startList) {
        String action = null;
        Intent intent = new Intent();
        switch (appFlag) {
            case APP_FLAG_MUSIC:
                if (startList){
                    action = ACTION_ACTIVITY_MUSIC;
                }else {
                    action = ACTION_ACTIVITY_MUSICLIST;
                }

                break;
            case APP_FLAG_VIDEO:
                action = ACTION_ACTIVITY_VIDEO;
                break;
            case APP_FLAG_PHOTO:
                action = ACTION_ACTIVITY_PHOTO;
                break;
        }
        if (null != action) {
            intent.setAction(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = intent.resolveActivity(mContext.getPackageManager());
            if (null != cn) {
                String clz = cn.getClassName();
                Log.w(TAG, "jumpTo() CLZ=" + clz);
                mContext.startActivity(intent);
            } else {
                Log.w(TAG, "jumpTo action fail !!! , action=" + action);
            }
        }

    }
}
