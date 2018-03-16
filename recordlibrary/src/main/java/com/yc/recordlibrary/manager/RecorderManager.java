package com.yc.recordlibrary.manager;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * @author by kabuqinuofu on 2018/3/15.
 */
public class RecorderManager {

    private String mDir;
    private String mCurrentFilePath;
    private MediaRecorder mMediaRecorder;

    private long startTime;
    private static RecorderManager mInstance;

    private RecorderManager(String dir) {
        mDir = dir;
    }

    public interface RecordingStateListener {
        void start();

        void stop(long time);

        void cancle();
    }

    private RecordingStateListener mListener;

    public void setOnAudioStateListener(RecordingStateListener listener) {
        mListener = listener;
    }

    public static RecorderManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (RecorderManager.class) {
                if (mInstance == null) {
                    mInstance = new RecorderManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 准备
     */
    public void prepareAudio() {
        try {
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = generateFileName();

            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            //准备结束
            if (mListener != null) {
                mListener.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        return System.currentTimeMillis() + ".amr";
    }

    public void stop() {
        release();
        if (mListener != null) {
            mListener.stop((System.currentTimeMillis() - startTime) / 1000 + 1);
        }
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    private void release() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

}
