package com.yc.recordlibrary.box;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author by kabuqinuofu on 2018/3/15.
 */
public class RecordNoticeBoxBuilder {

    private int mScreenWidth;

    private Context context;

    private boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;

    private int mLimitTime;
    private String mFileDir;
    private RecordListener mListener;

    RecordNoticeBoxBuilder(Context context) {
        if (context == null) {
            throw new NullPointerException("Context may not be null");
        }
        this.context = context;
        mScreenWidth = getScreenWidth(context);
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public RecordNoticeBoxBuilder setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public boolean getCancelable() {
        return mCancelable;
    }

    public RecordNoticeBoxBuilder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mCanceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    public boolean getCanceledOnTouchOutside() {
        return mCanceledOnTouchOutside;
    }

    /**
     * @param fileDir 文件夹名称
     * @return
     */
    public RecordNoticeBoxBuilder setFileDir(String fileDir) {
        mFileDir = fileDir;
        return this;
    }

    public String getFileDir() {
        return mFileDir;
    }

    /**
     * @param limitTime 规定最长录制时间
     * @return
     */
    public RecordNoticeBoxBuilder setLimitTime(int limitTime) {
        mLimitTime = limitTime;
        return this;
    }

    public int getLimitTime() {
        return mLimitTime;
    }

    public RecordNoticeBoxBuilder setRecordListener(RecordListener listener) {
        mListener = listener;
        return this;
    }

    public RecordListener getRecordListener() {
        return mListener;
    }

    public RecordNoticeBox create() {
        return new RecordNoticeBox(this, context);
    }

    public static int getScreenWidth(Context context) {
        context = context.getApplicationContext();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        assert manager != null;
        manager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

}
