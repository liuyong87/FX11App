package com.semisky.multimedia.common.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.MultimediaListActivity;
import com.semisky.multimedia.media_music.view.MusicPlayerActivity;
import com.semisky.multimedia.media_photo.view.PhotoPlayerActivity;
import com.semisky.multimedia.media_video.view.VideoPlayerActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiuYong on 2018/9/13.
 */

public class AppActivityManager {
    private static final String TAG = Logutil.makeTagLog(AppActivityManager.class);
    private static AppActivityManager _INSTANCE;
    private int mStopCloseActivityFlag = Definition.AppFlag.TYPE_INVALID;
    private static Map<Integer, Class<?>> mActivityStacksMap = new HashMap<Integer, Class<?>>();

    static {
        mActivityStacksMap.put(Definition.AppFlag.TYPE_MUSIC, MusicPlayerActivity.class);
        mActivityStacksMap.put(Definition.AppFlag.TYPE_VIDEO, VideoPlayerActivity.class);
        mActivityStacksMap.put(Definition.AppFlag.TYPE_PHOTO, PhotoPlayerActivity.class);
        mActivityStacksMap.put(Definition.AppFlag.TYPE_LIST, MultimediaListActivity.class);
    }

    private AppActivityManager() {

    }

    public static AppActivityManager getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new AppActivityManager();
        }
        return _INSTANCE;
    }


    public AppActivityManager onStopColseAcitvityWith(int appFlag) {
        this.mStopCloseActivityFlag = appFlag;
        return this;
    }

    public AppActivityManager onCloseOrtherActivity() {
        if (mActivityStacksMap.size() > 0) {
            for (Map.Entry<Integer, Class<?>> entry : mActivityStacksMap.entrySet()) {
                if (entry.getKey() == mStopCloseActivityFlag) {
                    continue;
                } else {
                    Class clz = entry.getValue();
                }
            }
        }
        this.mStopCloseActivityFlag = Definition.AppFlag.TYPE_INVALID;
        return this;
    }

    /**
     * 获取Activity栈指定下标界面
     *
     * @param mContext
     * @param taskActivityByIndex
     * @param maxRunningTask
     * @return
     */
    public String getTastActivity(Context mContext, int taskActivityByIndex,
                                  int maxRunningTask) {
        String curActivityName = null;
        ActivityManager mActivityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> mRunningTaskInfo = mActivityManager
                .getRunningTasks(maxRunningTask);
        if (mRunningTaskInfo.size() <= 0) {
            return "";
        }
        ActivityManager.RunningTaskInfo cinfo = mRunningTaskInfo
                .get(taskActivityByIndex);
        ComponentName mComponentName = cinfo.topActivity;
        curActivityName = mComponentName.getClassName();
        return curActivityName;
    }

    /**
     * 当前界面是否在前台
     *
     * @param
     * @param clz
     * @return
     */
    public boolean isTopActivity(String clz) {
        return checkTopActivity(MediaApplication.getContext(), clz, 0, 1);
    }

    // 检查当前界面是否在前台
    private boolean checkTopActivity(Context mContext, String clz,
                                     int taskActivityByIndex, int maxRunningTask) {
        String curActivityName = null;
        ActivityManager mActivityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> mRunningTaskInfo = mActivityManager
                .getRunningTasks(maxRunningTask);
        ActivityManager.RunningTaskInfo cinfo = mRunningTaskInfo
                .get(taskActivityByIndex);
        ComponentName mComponentName = cinfo.topActivity;
        curActivityName = mComponentName.getClassName();

        if (null != clz || !("".equals(clz))) {
            if (clz.equals(curActivityName)) {
                return true;
            }
        }

        return false;
    }


}
