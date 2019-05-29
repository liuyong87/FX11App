package com.semisky.multimedia.media_multimedia;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.FactoryTestManager;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.model.FX11AppStartPolicyModel;


/**
 * Created by LiuYong on 2018/9/3.
 */

public class MultimediaService extends Service {
    private static final String TAG = Logutil.makeTagLog(MultimediaService.class);

    @Override
    public void onCreate() {
        super.onCreate();
        Logutil.i(TAG, "onCreate() ...");
        LocalProxyMultimediaImpl.getInstance().init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logutil.i(TAG, "onStartCommand() ...startId=" + startId);
        handIntent(intent); //出厂测试命令处理
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logutil.i(TAG, "onBind() ...");

        return LocalProxyMultimediaImpl.getInstance();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logutil.i(TAG, "onUnbind() ...");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logutil.i(TAG, "onDestroy() ...");
    }

    //出厂测试，不属于正常使用功能
    private void handIntent(Intent intent) {
        Log.i(TAG, "handIntent: factory Test: " + intent.getAction());
        String action = intent.getAction();
        if (null == action) {
            Log.i(TAG, "handIntent() action = null");
            return;
        }

        // 来自中间件意图事件
        if (AutoConstants.ACTION_MODE.equals(action)) {
            // 多媒体启动管理类
            FX11AppStartPolicyModel.getInstance().handlerAppStartEvent(intent);

        } else if (action.equals(Definition.FACTORY_TEST_ACTION_MUSIC)) {
            //音乐
            int value = intent.getIntExtra(Definition.MediaCtrlConst.PARAM_CMD, -1);
            if (value != -1) {
                startMusicService(value);
            }

        } else if (action.equals(Definition.FACTORY_TEST_ACTION_VIDEO)) {
            //视频
            int value = intent.getIntExtra(Definition.MediaCtrlConst.PARAM_CMD, -1);
            if (value != -1) {
                FactoryTestManager.getInstances().notifyVideoCommand(value);
            }

        } else if (action.equals(Definition.FACTORY_TEST_ACTION_PICTURE)) {
            //图片
            int value = intent.getIntExtra(Definition.MediaCtrlConst.PARAM_CMD, -1);
            if (value != -1) {
                FactoryTestManager.getInstances().notifyPictureCommand(value);
            }

        }
    }

    //启动音乐服务
    private void startMusicService(int value) {
        Log.i(TAG, "startMusicService: factory music test: " + value);
        Intent intent = new Intent();
        intent.setAction(Definition.MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
        intent.setClassName(Definition.MediaCtrlConst.SERVICE_PKG, Definition.MediaCtrlConst.SERVICE_CLZ);
        intent.putExtra(Definition.MediaCtrlConst.PARAM_CMD, value);
        startService(intent);
    }


}
