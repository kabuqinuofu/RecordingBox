package com.yc.recordlibrary.box;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.recordlibrary.R;
import com.yc.recordlibrary.manager.MediaManager;
import com.yc.recordlibrary.manager.RecorderManager;
import com.yc.recordlibrary.widget.VoicePlayProgressView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author by kabuqinuofu on 2018/3/15.
 */
public class RecordNoticeBox {

    private Dialog mDialog;
    private Context mContext;
    private RecordListener mListener;

    private String mFileDir;

    private Button mUpView;
    private Button mDeleteView;
    private TextView mTimeView;
    private TextView mTagRemind;
    private ImageView mVoiceLeft;
    private ImageView mVoiceRight;
    private LinearLayout mDeleteLayout;
    private RecorderManager recordVoiceManager;

    private Timer timer;
    private long recorderSecondsElapsed;
    private AnimationDrawable leftAnimationDrawable;
    private AnimationDrawable rightAnimationDrawable;

    private long mTime;
    private int mLimitTime;
    private MyHandler myHandler;

    /**
     * 录制状态0，准备录音状态,1 正在录制,2录制完成之后，可预览状态，3录制完成之后，预览播放状态
     */
    private int recodingState = 0;
    private final int 准备录制 = 0;
    private final int 正在录制 = 1;
    private final int 完成录制 = 2;
    private final int 录制播放 = 3;

    private VoicePlayProgressView mVoicePlayProgressView;

    RecordNoticeBox(RecordNoticeBoxBuilder builder, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.record_notice_box, null);
        mDialog = new Dialog(context, R.style.ActionBoxStyle);
        mDialog.setContentView(view);
        mDialog.setCancelable(builder.getCancelable());
        mDialog.setCanceledOnTouchOutside(builder.getCanceledOnTouchOutside());

        mVoiceLeft = view.findViewById(R.id.voice_time_left);
        mVoiceRight = view.findViewById(R.id.voice_time_right);
        mTimeView = view.findViewById(R.id.voice_time);
        mTagRemind = view.findViewById(R.id.tvTagRemind);
        mDeleteLayout = view.findViewById(R.id.delete_layout);
        mUpView = view.findViewById(R.id.btnUpOk);
        mDeleteView = view.findViewById(R.id.btnCancal);
        mVoicePlayProgressView = view.findViewById(R.id.vPlayProgressView);

        mContext = context;
        mFileDir = builder.getFileDir();
        mLimitTime = builder.getLimitTime();
        mListener = builder.getRecordListener();
        myHandler = new MyHandler(this);

        mUpView.setOnClickListener(new OnClickListener());
        mDeleteView.setOnClickListener(new OnClickListener());
        mVoicePlayProgressView.setOnClickListener(new OnClickListener());
        mVoicePlayProgressView.setShowProgress(false);
        File file = mContext.getExternalFilesDir(mFileDir);
        recordVoiceManager = RecorderManager.getInstance(file != null ? file.getAbsolutePath() : mContext.getFilesDir() + File.separator + mFileDir);
        recordVoiceManager.setOnAudioStateListener(new mRecordingStateListener());

        Window window = mDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.START | Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.x = 0;
            lp.y = 0;
            lp.width = builder.getScreenWidth();
            window.setAttributes(lp);
        }
    }

    class mRecordingStateListener implements RecorderManager.RecordingStateListener {
        @Override
        public void start() {
            recodingState = 正在录制;
            setState();
        }

        @Override
        public void stop(long time) {
            mTime = time;
            if (mTime <= 1) {
                recodingState = 准备录制;
                setState();
                Toast.makeText(mContext, R.string.record_short, Toast.LENGTH_SHORT).show();
            } else {
                recodingState = 完成录制;
                setState();
            }
        }

        @Override
        public void cancle() {

        }
    }

    class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                int i = view.getId();
                if (i == R.id.vPlayProgressView) {
                    if (recodingState == 准备录制) {
                        recordVoiceManager.prepareAudio();
                        startTimer();
                    } else if (recodingState == 正在录制) {
                        recordVoiceManager.stop();
                    } else if (recodingState == 完成录制) {
                        mVoicePlayProgressView.setShowProgress(true);
                        mVoicePlayProgressView.setMaxLength(mTime);
                        recodingState = 录制播放;
                        setState();
                        startTimer();
                        MediaManager.playSound(recordVoiceManager.getCurrentFilePath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                recodingState = 2;
                                setState();
                                mVoicePlayProgressView.setShowProgress(false);
                                mVoicePlayProgressView.setMaxLength(0);
                                mVoicePlayProgressView.setCurLength(0);
                            }
                        });
                    } else if (recodingState == 录制播放) {
                        mVoicePlayProgressView.setShowProgress(false);
                        mVoicePlayProgressView.setMaxLength(0);
                        mVoicePlayProgressView.setCurLength(0);
                        MediaManager.pause();
                        recodingState = 完成录制;
                        setState();
                    }

                } else if (i == R.id.btnUpOk) {
                    if (mListener != null)
                        mListener.finish(recordVoiceManager.getCurrentFilePath(), mTime);
                    dismiss();

                } else if (i == R.id.btnCancal) {
                    if (mListener != null)
                        mListener.cancle();
                    dismiss();
                }
            } else {
                Toast.makeText(mContext, R.string.no_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setState() {
        if (recodingState == 准备录制) {
            cancelAnim();
            mTagRemind.setText(R.string.click_record);
            mTimeView.setText(formatSeconds(0));
            mTagRemind.setVisibility(View.VISIBLE);
            mDeleteLayout.setVisibility(View.INVISIBLE);
            mVoicePlayProgressView.setImageResource(R.drawable.icon_record_voice_start);
        } else if (recodingState == 正在录制) {
            executeAnim();
            mTagRemind.setText(R.string.click_finish_record);
            mDeleteLayout.setVisibility(View.INVISIBLE);
            mVoicePlayProgressView.setImageResource(R.drawable.icon_record_voice_stop);
        } else if (recodingState == 完成录制) {
            stopTimer();
            cancelAnim();
            mTimeView.setVisibility(View.VISIBLE);
            mTimeView.setText(formatSeconds(mTime));
            mTagRemind.setVisibility(View.INVISIBLE);
            mDeleteLayout.setVisibility(View.VISIBLE);
            mVoicePlayProgressView.setImageResource(R.drawable.icon_voice_play_start);
        } else if (recodingState == 录制播放) {
            executeAnim();
            mTimeView.setVisibility(View.VISIBLE);
            mTagRemind.setVisibility(View.INVISIBLE);
            mVoicePlayProgressView.setImageResource(R.drawable.icon_record_voice_stop);
        }
    }

    private String formatSeconds(long seconds) {
        return getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private String getTwoDecimalsValue(long value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }

    private void cancelAnim() {
        if (leftAnimationDrawable != null) {
            leftAnimationDrawable.stop();
        }
        if (rightAnimationDrawable != null) {
            rightAnimationDrawable.stop();
        }
        mVoiceLeft.setImageResource(R.drawable.voice_left_01);
        mVoiceRight.setImageResource(R.drawable.voice_left_01);
    }

    private void executeAnim() {
        mVoiceLeft.setImageResource(R.drawable.voice_left_animation);
        mVoiceRight.setImageResource(R.drawable.voice_right_animation);
        leftAnimationDrawable = (AnimationDrawable) mVoiceLeft.getDrawable();
        leftAnimationDrawable.start();
        rightAnimationDrawable = (AnimationDrawable) mVoiceRight.getDrawable();
        rightAnimationDrawable.start();
    }

    private void startTimer() {
        recorderSecondsElapsed = 0;
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    static class MyHandler extends Handler {

        WeakReference<RecordNoticeBox> weakReference;

        MyHandler(RecordNoticeBox recordNoticeBox) {
            weakReference = new WeakReference<>(recordNoticeBox);
        }

        @Override
        public void handleMessage(Message msg) {
            RecordNoticeBox recordNoticeBox = weakReference.get();
            if (recordNoticeBox != null) {
                if (recordNoticeBox.recodingState == recordNoticeBox.正在录制) {
                    //如果录制时长大于指定限制时长，强制停止录制
                    if (recordNoticeBox.recorderSecondsElapsed >= recordNoticeBox.mLimitTime) {
                        recordNoticeBox.recordVoiceManager.stop();
                    }
                    recordNoticeBox.mTimeView.setText(recordNoticeBox.formatSeconds(recordNoticeBox.recorderSecondsElapsed));
                } else if (recordNoticeBox.recodingState == recordNoticeBox.录制播放) {
                    recordNoticeBox.mTimeView.setText(recordNoticeBox.formatSeconds(recordNoticeBox.recorderSecondsElapsed));
                    recordNoticeBox.mVoicePlayProgressView.setCurLength(recordNoticeBox.recorderSecondsElapsed);
                }
            }

        }
    }

    private void updateTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (recodingState == 正在录制) {
                    recorderSecondsElapsed++;
                    myHandler.sendEmptyMessage(正在录制);
                } else if (recodingState == 录制播放) {
                    recorderSecondsElapsed++;
                    myHandler.sendEmptyMessage(录制播放);
                }
            }
        }).run();
    }

    public static RecordNoticeBoxBuilder newBox(Context context) {
        return new RecordNoticeBoxBuilder(context);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            stopTimer();
            MediaManager.release();
            mDialog.dismiss();
        }
    }
}