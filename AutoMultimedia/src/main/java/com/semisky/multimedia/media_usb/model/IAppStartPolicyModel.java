package com.semisky.multimedia.media_usb.model;

/**
 * Created by LiuYong on 2018/8/8.
 */

public interface IAppStartPolicyModel {

    public interface OnAutoStartAppStateListener{
        void onJumpTo(int appFlag,boolean isForeground);
    }

    /**
     * 注册多媒体自启动状态监听
     * @param l
     */
    public void setOnAutoStartAppStateListener(OnAutoStartAppStateListener l);

    /**
     * 反注册多媒体自启动状态监听
     */
    public void unRegisterOnAutoStartAppStateListener();

    /**
     * 用户手动启动多媒体应用
     */
    void onUserStartApp();

    /**
     自动跳转条件？
     第一级条件：
     A.是否有高优先级任务？
     (isHightPriorityRunning )
     OR
     B.是否有打断操作？
     （isInterruptionOPeration）
     OR
     C.是否首次已跳转？
     （mIsFirstJumpApp）
     OR
     D.是否禁止执行断点记忆操作？
     （mIsStopMedia）
     条件成立执行：禁止往下执行

     第二级条件：
     A.是否为图片AppFlag?
     条件成立执行：设置为APP_FLAG_MUISC
     第三级条件：
     A.AppFlag对于断点记忆是否存在？
     （isAvalidAppFlagWithBreakPointMemory）

     3.1.嵌套条件1：
     1.断点记忆存在。
     2.导航在前台。
     3.音乐标识为音乐标识。
     条件成立执行：音乐在后台播放

     3.2.嵌套条件2：
     1.断点记忆存在。
     2.导航在前台。
     3.非音乐标识。
     条件成立执行：不执行任何操作
     3.3.嵌套条件3：
     1.断点记忆存在。
     2.导航在不在前台。
     3.非音乐标识。
     条件成立执行：跳转断点记忆前台界面媒体播放操作

     第四级条件：
     4.1.嵌套条件1：
     1.音乐媒体数据大于零
     2.首个扫描音乐URL不等于空
     3.导航在前台
     条件成立执行：音乐在后台播放
     4.2.嵌套条件2：
     1.音乐媒体数据大于零
     2.首个扫描音乐URL不等于空
     3.导航不在前台
     条件成立执行：跳转前台音乐播放界面媒体播放
     4.3.嵌套条件3：
     1.音乐媒体数据大于零
     2.无首个扫描音乐URL
     3.导航不在前台
     条件成立执行：不执行任何操作
     第五级条件：
     A.进入列表条件（ fromScanDoneEvent ）
     1.以上四个条件不成立
     2.首个跳转APP标识为false
     3.扫描完成标识为真
     条件成立执行：跳转多媒体列表操作
     */
    void onAutoStartApp(boolean fromScanDoneEvent);

    /**
     * 释放资源
     */
    void onRelease();

}
