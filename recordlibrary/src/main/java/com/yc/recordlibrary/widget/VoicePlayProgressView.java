package com.yc.recordlibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author by kabuqinuofu on 2018/3/15.
 */
@SuppressLint("AppCompatCustomView")
public class VoicePlayProgressView extends ImageView {

    private long maxLength;
    private long curLength;
    private boolean isShowProgress;
    private Paint paint = new Paint();
    private RectF rectF = new RectF();

    public VoicePlayProgressView(Context context) {
        super(context);
    }

    public VoicePlayProgressView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width != height) {
            height = Math.min(width, height);
            width = height;
        }
        this.rectF.left = 4.0f;
        this.rectF.top = 4.0f;
        this.rectF.right = (float) (width - 4);
        this.rectF.bottom = (float) (height - 4);
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.rgb(233, 233, 233));
        canvas.drawColor(0);
        this.paint.setStrokeWidth(8.0f);
        this.paint.setStyle(Paint.Style.STROKE);
        if (this.isShowProgress && this.curLength <= this.maxLength) {
            this.paint.setColor(Color.rgb(0, 153, 255));
            canvas.drawArc(this.rectF, -90.0f, 360.0f * (((float) this.curLength) / ((float) this.maxLength)), false, this.paint);
        }
    }

    public void setShowProgress(boolean showProgress) {
        this.isShowProgress = showProgress;
    }

    public boolean getIsShowProgress() {
        return this.isShowProgress;
    }

    public long getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(long j) {
        this.maxLength = j;
    }

    public long getCurLength() {
        return this.curLength;
    }

    public void setCurLength(long j) {
        this.curLength = j;
        invalidate();
    }

}
