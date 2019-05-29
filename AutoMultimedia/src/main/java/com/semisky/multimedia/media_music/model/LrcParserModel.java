package com.semisky.multimedia.media_music.model;

import android.util.Log;
import android.util.SparseArray;

import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.LrcView.LrcEntity;
import com.semisky.multimedia.media_music.LrcView.LrcParseUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class LrcParserModel {
    private static final String TAG = Logutil.makeTagLog(LrcParserModel.class);
    private static LrcParserModel _INSTANCE;
    private OnLyricParseListener mOnLyricParseListener;
    private LrcParseRunnable mLrcParseRunnable;

    private static HashMap<String,List<LrcEntity>> mLrcEntityCatchMap = new HashMap<>();


    private LrcParserModel() {
        this.mLrcParseRunnable = new LrcParseRunnable();
    }

    public static LrcParserModel getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new LrcParserModel();
        }
        return _INSTANCE;
    }

    public void setOnLyricParseListener(OnLyricParseListener l) {
        this.mOnLyricParseListener = l;
    }


    /**
     * 解析歌词
     *
     * @param lrcUrl
     */
    public void parseLrc(String lrcUrl) {
        if (null != mLrcParseRunnable && mLrcParseRunnable.isRunning()) {
            mLrcParseRunnable.stop();
            mLrcParseRunnable = new LrcParseRunnable();
        }
        mLrcParseRunnable.setmLrcUrl(lrcUrl);
        mLrcParseRunnable.setOnLyricParseListener(mOnLyricParseListener);
        new Thread(mLrcParseRunnable).start();
    }

    static class LrcParseRunnable implements Runnable {
        private OnLyricParseListener mOnLyricParseListener;
        private volatile boolean isInterrupt = false;
        private String mLrcUrl;
        private boolean mIsRunning = false;

        public void setmLrcUrl(String lrcUrl) {
            this.mLrcUrl = lrcUrl;
        }

        public boolean isRunning() {
            return false;
        }

        public void stop() {
            this.isInterrupt = true;
        }

        public void setOnLyricParseListener(OnLyricParseListener l) {
            this.mOnLyricParseListener = l;
        }

        @Override
        public void run() {

            mIsRunning = true;
            if (null == mLrcUrl || !new File(mLrcUrl).exists()) {
                if (null != mOnLyricParseListener) {
                    mOnLyricParseListener.onLyricNotExists();
                    mIsRunning = false;
                }
                return;
            }

            List<LrcEntity> lrcList = null;

            if(!mLrcEntityCatchMap.containsValue(mLrcUrl)){
                mLrcEntityCatchMap.clear();
                lrcList = LrcParseUtil.parseLrc(new File(mLrcUrl));
            }else {
                lrcList = mLrcEntityCatchMap.get(mLrcUrl);
            }

            if (isInterrupt) {
                Log.w(TAG, "Interrupt current lyric parser ..." + mLrcUrl);
                mIsRunning = false;
                return;
            }

            if (null == lrcList || lrcList.size() <= 0) {
                mIsRunning = false;
                if (null != mOnLyricParseListener) {
                    mOnLyricParseListener.onLyricParseFail();
                }
                Log.d(TAG, "onLyricParseFail() ..." + mLrcUrl);
                return;
            }

            if (null != mOnLyricParseListener) {
                mOnLyricParseListener.onLyricParseSuccess(lrcList);
                mIsRunning = false;
                Log.d(TAG, "onLyricParseSuccess() ..." + mLrcUrl);
            }
        }
    }

    public interface OnLyricParseListener {
        void onLyricNotExists();

        void onLyricParseFail();

        void onLyricParseSuccess(List<LrcEntity> lrcList);
    }


}
