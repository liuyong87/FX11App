package com.semisky.multimedia.media_video.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_video.manager.VideoStateManager;
import com.semisky.multimedia.media_video.model.VideoAudioFocusModel;

import java.io.IOException;

/**
 * 视频播放核心组件视图
 */
public class VideoSurfaceView extends SurfaceView implements OnSeekCompleteListener {
    private static final String TAG = Logutil.makeTagLog(VideoSurfaceView.class);
    private static final boolean DEBUG = true;

    private Uri mUri;// 资源URI
    private Context mContext;// 上下文
    private int mDuration;// 资源总进度

    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;// 视频播放画布
    private MediaPlayer mMediaPlayer = null;// 媒体播放器

    private boolean mIsPrepared;// 媒体是否准备完成
    private int mVideoWidth;// 视频宽
    private int mVideoHeight;// 视频高
    private int mSurfaceWidth;// 画布宽
    private int mSurfaceHeight;// 画布高
    // MediaPlayer Listener
    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;
    private OnInfoListener mOnInfoListener;
    private OnErrorListener mOnErrorListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;

    private int mCurrentBufferPercentage;// 缓冲网络媒体资源
    private boolean mStartWhenPrepared;// 媒体准备完成可播放标识
    private int mSeekWhenPrepared;// 媒体准备完成设置进度
    private boolean mIsAutoPlay = true;// 是否自动播放标识(注标记清除场景:1.丢失永久音频焦点)
    private boolean isHasAudioFocus = true ;//是否有音频焦点

    public VideoSurfaceView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    /*Setter/Getter*/

    /**
     * 设置自动播放标识
     *
     * @param enable
     */
    public void setAutoPlayState(boolean enable) {
        this.mIsAutoPlay = enable;
    }
    /**
     * 媒体是否准备完成
     *
     * @return
     */
    public boolean isPrepared() {
        return mIsPrepared;
    }
    /**
     * 设置是否有音频焦点
     */
    public void setAudioFocusState(boolean isHasAudioFocus){
        this.isHasAudioFocus = isHasAudioFocus;
    }

    /**
     * 获取视频宽
     *
     * @return
     */
    public int getVideoWidth() {
        return mVideoWidth;
    }

    /**
     * 获取视频高
     *
     * @return
     */
    public int getVideoHeight() {
        return mVideoHeight;
    }

    /**
     * 设置画布宽、高
     *
     * @param width
     * @param height
     */
    public void setVideoScale(int width, int height) {
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;
        setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 初始化视图
     */
    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /**
     * 设置媒体资源路径
     *
     * @param path
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * 设置媒体资源URI
     *
     * @param uri
     */
    public void setVideoURI(Uri uri) {
        mUri = uri;
        mStartWhenPrepared = false;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    /**
     * 获取当前播放资源URI
     *
     * @return
     */
    public String getVideoPath() {
        return (mUri != null ? mUri.getPath() : null);
    }

    /**
     * 停止媒体播放器
     */
    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 初始化媒体播放器
     */
    private void openVideo() {

        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            Logutil.v(TAG, "openVideo() not ready for playback just yet, will try again later");
            return;
        }
        // Tell the music playback service to pause
        // TODO: these constants need to be published somewhere in the
        // framework.
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        Log.i(TAG, "mUri=" + mUri.toString());

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mIsPrepared = false;
            mStartWhenPrepared = true;
            Logutil.v(TAG, "reset duration to -1 in openVideo");
            mDuration = -1;
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
        } catch (IOException ex) {
            Log.w(TAG, "IOException Unable to open content: " + mUri);
            if (null != mOnErrorListener) {
                mOnErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_IO);
            }
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "IllegalArgumentException Unable to open content: " + mUri);
            if (null != mOnErrorListener) {
                mOnErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_IO);
            }
            return;
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (null != mOnSeekCompleteListener) {
            mOnSeekCompleteListener.onSeekComplete(mp);
        }
    }

    /**
     * 监听媒体资源播放信息接口实现
     */
    MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "===========>OnInfoListener.onInfo() what=" + what + ",extra=" + extra);
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            }
            return false;
        }
    };

    /**
     * 监听媒体资源视频内容尺寸接口实现
     */
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            if (mVideoWidth != 0 && mVideoHeight != 0) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
        }
    };

    /**
     * 监听媒体资源准备完成接口实现
     */
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {

            mIsPrepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            Log.i(TAG, "===================================================");
            Log.i(TAG, "onPrepared() mVideoWidth: " + mVideoWidth + ",mVideoHeight：" + mVideoHeight);
            Log.i(TAG, "onPrepared() mSeekWhenPrepared：" + mSeekWhenPrepared);
            Log.i(TAG, "onPrepared() mStartWhenPrepared：" + mStartWhenPrepared + ",mIsAutoPlay：" + mIsAutoPlay);
            Log.i(TAG, "===================================================");

            // 视频尺寸宽或高不为零
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);

                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                // 1.是否准备好资源
                // 2.是否允许播放（待加入）
                if (mStartWhenPrepared && !isPlaying() && isHasAudioFocus) {
                    if (VideoAudioFocusModel.getInstance(mContext).hasAudioFocus() && mIsAutoPlay && (!VideoStateManager.getInstance().getVideoIsClickStop() ||
                                         VideoStateManager.getInstance().getVideoIsLossFocus())) {
                        mMediaPlayer.start();
                        mStartWhenPrepared = false;
                        VideoStateManager.getInstance().setAudioFocus_loss(false);
                    }
                }
            }

            // 视频尺寸宽或高为零
            else {

                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                // 1.是否准备好资源
                // 2.是否允许播放（待加入）
                if (mStartWhenPrepared && !isPlaying() && isHasAudioFocus) {
                    if (VideoAudioFocusModel.getInstance(mContext).hasAudioFocus() && mIsAutoPlay && (!VideoStateManager.getInstance().getVideoIsClickStop() ||
                                        VideoStateManager.getInstance().getVideoIsLossFocus())) {
                        mMediaPlayer.start();
                        mStartWhenPrepared = false;
                        VideoStateManager.getInstance().setAudioFocus_loss(false);
                    }
                }
            }
        }
    };

    /**
     * 监听媒体资源播放完成接口实现
     */
    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {

            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    /**
     * 监听媒体资源播放异常接口实现
     */
    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }
            return true;
        }
    };

    /**
     * 监听媒体资源缓冲接口实现
     */
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };


    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        // 视频画布尺寸改变
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
            }
        }

        // 视频画布创建
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated() ...");
            mSurfaceHolder = holder;
            openVideo();// 当前Surface不可见时，会销毁当前播放视频，所以这个方法是用来恢复当前播放视频资源
        }

        // 视频画布销毁
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed() ...");
            mSurfaceHolder = null;

            if (mMediaPlayer != null) {
                mSeekWhenPrepared = mMediaPlayer.getCurrentPosition();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

    };


    /*  Setter/Getter Method*/
    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        this.mOnSeekCompleteListener = l;
    }

    /**
     * 注册媒体资源
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * 注册媒体资源播放完成监听
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * 注册媒体资源播放异常监听
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * 注册视频信息改变监听
     *
     * @param l
     */
    public void setOnInfoListener(MediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }


    /* Function Method*/

    /**
     * 播放操作
     */
    public void start() {
        //在播放的时候设置当前状态到中间件
    //    SemiskyIVIManager.getInstance().setCurrentAppStatus(AutoConstants.AppType.MEDIA_MUSIC,AutoConstants.CurrentAppStatus.STATE_PLAYING);
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            mStartWhenPrepared = false;
            mIsAutoPlay = true;
            VideoStateManager.getInstance().setVideoIsClickStop(false);
            Log.i(TAG, "start() ...");
        }
    }

    /**
     * 暂停播放器
     */
    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                Log.i(TAG, "pause() ...");
            }
        }
        mStartWhenPrepared = false;
    }

    /**
     * 获取总进度
     *
     * @return
     */
    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    /**
     * 获取当前播放进度
     *
     * @return
     */
    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 设置播放指定播放进度位置
     *
     * @param msec
     */
    public void seekTo(int msec) {
        Log.d(TAG, "seekTo() msec [ " + msec + " ]");
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    /**
     * 设置媒体播放器静音状态
     *
     * @param isMute
     */
    public void muteVolumeEnable(boolean isMute) {
        Logutil.i(TAG, "muteVolumeEnable() mIsPrepared=" + mIsPrepared);
        if(null == mMediaPlayer){
            return;
        }
        if (mIsPrepared) {
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
            Logutil.i(TAG, "muteVolumeEnable() ..." + volume);
        }
    }


}
