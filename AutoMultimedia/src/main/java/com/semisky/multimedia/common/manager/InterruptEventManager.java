package com.semisky.multimedia.common.manager;

import com.semisky.multimedia.common.utils.Logutil;

/**
 * 高优先级任务管理
 * Created by LiuYong on 2018/8/8.
 */

public class InterruptEventManager {
    private static final String TAG = Logutil.makeTagLog(InterruptEventManager.class);
    private static InterruptEventManager _INSTANCE;
    private boolean mInterruptEventFromBTCall = false;// 来自电话打断事件
    private boolean mInterruptEventFromParking = false;// 来自泊车打断事件
    private boolean mInterruptEventFromSystemUpgrade = false;// 来自系统升级打断事件
    private boolean mInterruptEventFromMode = false;//来着mode打断事件
    private boolean mInterruptEventFromHome = false;//来自home键打断事件
    private boolean mInterruptEventFromNavi = false;//来自快捷导航打断事件
    private boolean mInterruptEventFromAudio = false;// 来自快捷收音机打断事件；
    private boolean formHighPriorityAppRunning = false;//来自高优先级的持续影响
    private boolean isFormLauncherStart = false;// 是否允许launcher启动App


    private InterruptEventManager() {
    }

    public static InterruptEventManager getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new InterruptEventManager();
        }
        return _INSTANCE;
    }

    /**
     * 设置来自电话打断事件状态
     *
     * @param state
     */
    public void setInterruptEventFromBTCall(boolean state) {
        this.mInterruptEventFromBTCall = state;
    }

    /**
     * 设置来自泊车打断事件状态
     *
     * @param state
     */
    public void setInterruptEventFromParking(boolean state) {
        this.mInterruptEventFromParking = state;
    }

    /**
     * 设置来自系统升级打断事件状态
     *
     * @param state
     */
    public void setmInterruptEventFromSystemUpgrade(boolean state) {
        this.mInterruptEventFromSystemUpgrade = state;
    }
    /**
     * 设置来自mode 按键打断事件
     */
    public void setInterruptEventFromMode(boolean state){
        this.mInterruptEventFromMode = state;
    }
    /**
     * 设置来自home 按键打断事件
     */
    public void setInterruptEventFromHome(boolean state){
        this.mInterruptEventFromHome = state;
    }
    /**
     * 设置来自Navi 按键打断事件
     */
    public void setInterruptEventFromNavi(boolean state){
        this.mInterruptEventFromNavi = state;
    }
    /**
     * 设置来自audio 按键打断事件
     */
    public void setInterruptEventFromAudio(boolean state){
        this.mInterruptEventFromAudio = state;
    }
    /**
     * 获取车机面板导航快捷键打断事件
     */
    public boolean getInterruptEventFromNavi(){
        return mInterruptEventFromNavi;
    }
    /**
     * 设置高优先级持续影响。（在高优先级应用界面插入U盘，再退出当前高优先级应用，会进入多媒体，未能持续抑制多媒体启动）
     */
    public void setHighPriorityAppRunning(Boolean state){
        formHighPriorityAppRunning = state;
    }

    /**
     * 设置是否允许launcher 启动
     */
    public void setmFromLauncherStart(boolean b){
        isFormLauncherStart = b ;
    }
    /**
     * 是否允许launcher启动
     */
    public boolean getIsFormLauncherStart(){
        return isFormLauncherStart;
    }

    /**
     * 获取来自la
     */
    /**
     * 是否有打断事件操作
     *
     * @return
     */
    public boolean hasInterruptEvent() {
        Logutil.i(TAG, "===============");
        Logutil.i(TAG, "hasInterruptionEvent() mInterruptEventFromBTCall=" + mInterruptEventFromBTCall);
        Logutil.i(TAG, "hasInterruptionEvent() mInterruptEventFromParking=" + mInterruptEventFromParking);
        Logutil.i(TAG, "hasInterruptionEvent() mInterruptEventFromSystemUpgrade=" + mInterruptEventFromSystemUpgrade);
        Logutil.i(TAG, "hasInterruptionEvent() mInterruptEventFromAudio=" + mInterruptEventFromAudio);
        Logutil.i(TAG, "hasInterruptionEvent() mInterruptEventFromHome=" + mInterruptEventFromHome);
        Logutil.i(TAG, "hasInterruptionEvent() mInterruptEventFromMode=" + mInterruptEventFromMode);
        Logutil.i(TAG, "hasInterruptionEvent() formHighPriorityAppRunning=" + formHighPriorityAppRunning);
        Logutil.i(TAG, "===============");
        return (mInterruptEventFromBTCall || mInterruptEventFromParking || mInterruptEventFromSystemUpgrade || mInterruptEventFromMode
                || mInterruptEventFromAudio || mInterruptEventFromNavi || mInterruptEventFromHome || formHighPriorityAppRunning);
    }


}
