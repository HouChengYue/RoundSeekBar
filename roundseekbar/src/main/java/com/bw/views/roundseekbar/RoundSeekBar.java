package com.bw.views.roundseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author houch
 * date: 2022/4/22
 **/
@SuppressWarnings("unused")
public class RoundSeekBar extends View {
    private static final String TAG = "RoundSeekBar";
    private int bgColor = 0xFF364F63;
    private int bgWidth = 36;
    private int countWidth = 40;
    private int dividerColor = 0xAA000000;
    private int endAngle = 360;
    private int endColor = 0xFFF45A5A;
    private int mMax = 100;
    private int progress = 99;
    private boolean showDivider = true;
    private int startAngle = 0;
    private int startColor = 0xFF11B0F8;
    private boolean canTouch = true;
    private int mMeasuredHeight = 200;
    private int mMeasuredWidth = 200;
    private Paint mBgPaint;
    private Paint mCountPaint;
    private Paint mDividerPaint;
    private Paint mHandlePaint;
    private float mX1;
    private float mY1;
    private float mX2;
    private float mY2;
    private ThreadPoolExecutor mPoolExecutor;
    private int mMinSide;
    private int mMaxWidth;
    private int offset = 10;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private final float half = 0.5f;
    private LinearGradient mLinearGradient;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public RoundSeekBar(Context context) {
        this(context, null);
    }

    public RoundSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RoundSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundSeekBar);
        bgColor = typedArray.getColor(R.styleable.RoundSeekBar_bgColor, bgColor);
        bgWidth = (int) typedArray.getDimension(R.styleable.RoundSeekBar_bgWidth, bgWidth);
        countWidth = (int) typedArray.getDimension(R.styleable.RoundSeekBar_countWidth, countWidth);
        dividerColor = typedArray.getColor(R.styleable.RoundSeekBar_dividerColor, dividerColor);

        endColor = typedArray.getColor(R.styleable.RoundSeekBar_endColor, endColor);
        mMax = typedArray.getInt(R.styleable.RoundSeekBar_Max, mMax);
        progress = typedArray.getInt(R.styleable.RoundSeekBar_progress, progress);
        progress = Math.min(progress, mMax);
        showDivider = typedArray.getBoolean(R.styleable.RoundSeekBar_showDivider, showDivider);
        startAngle = typedArray.getInt(R.styleable.RoundSeekBar_startAngle, startAngle);
        if (startAngle < 0) {
            startAngle = 0;
        }
        int maxAngle = 360;
        if (startAngle > maxAngle) {
            startAngle = maxAngle;
        }
        endAngle = typedArray.getInt(R.styleable.RoundSeekBar_endAngle, endAngle);
        if (endAngle < startAngle) {
            endAngle = startAngle;
        }
        if (endAngle > startAngle + maxAngle) {
            endAngle = startAngle + maxAngle;
        }
        startColor = typedArray.getColor(R.styleable.RoundSeekBar_startColor, startColor);
        canTouch = typedArray.getBoolean(R.styleable.RoundSeekBar_canTouch, canTouch);
        typedArray.recycle();
        iniPaint();
        mPoolExecutor = new ThreadPoolExecutor(1, 2, 300, TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(), runnable -> new Thread(runnable, TAG));
    }

    private void iniPaint() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(bgColor);
        mBgPaint.setStrokeWidth(bgWidth);
        mBgPaint.setStyle(Paint.Style.STROKE);

        mCountPaint = new Paint();
        mCountPaint.setAntiAlias(true);
        mCountPaint.setStrokeWidth(countWidth);
        mCountPaint.setStyle(Paint.Style.STROKE);
        mCountPaint.setStrokeCap(Paint.Cap.ROUND);

        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setColor(dividerColor);

        mHandlePaint = new Paint();
        mHandlePaint.setAntiAlias(true);
        mHandlePaint.setColor(Color.WHITE);
        mLinearGradient = new LinearGradient(0, 0, mMeasuredWidth, 0, startColor, endColor, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();
        mCountPaint.setShader(mLinearGradient);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    public void setTouchOffset(int offset) {
        this.offset = offset;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mMinSide = Math.min(mMeasuredWidth, mMeasuredHeight);
        mMaxWidth = Math.max(bgWidth, countWidth);
        canvas.drawArc(mMaxWidth * half, mMaxWidth * half,
                mMinSide - mMaxWidth * half, mMinSide - mMaxWidth * half,
                0, 360, false, mBgPaint);
        canvas.drawArc(mMaxWidth * half, mMaxWidth * half,
                mMinSide - mMaxWidth * half, mMinSide - mMaxWidth * half,
                startAngle + 90, (progress * 1f / mMax) * (endAngle - startAngle),
                false, mCountPaint);
        canvas.rotate(
                startAngle - 90, mMinSide * half, mMinSide * half);
        canvas.drawCircle(mMaxWidth * half, mMinSide * half, mMaxWidth * half - 6, mHandlePaint);
        canvas.save();

        if (showDivider) {
            for (int i = 1; i < progress; i++) {
                canvas.rotate((1f / mMax) * (endAngle - startAngle),
                        mMinSide * half, mMinSide * half);

                canvas.drawLine(0, mMinSide * half,
                        mMaxWidth, mMinSide * half,
                        mDividerPaint);
            }

        }
        canvas.restore();
        canvas.rotate(
                (progress * 1f / mMax) * (endAngle - startAngle), mMinSide * half, mMinSide * half);
        canvas.drawCircle(mMaxWidth * half, mMinSide * half, mMaxWidth * half - 6, mHandlePaint);
        post(() -> {
            Rect rect = new Rect();
            getHitRect(rect);
            rect.left += offset;
            rect.top += offset;
            rect.right += offset;
            rect.bottom += offset;
            TouchDelegate touchDelegate = new TouchDelegate(rect, this);
            ViewGroup parent = (ViewGroup) getParent();
            parent.setTouchDelegate(touchDelegate);
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canTouch) {
            return false;
        }
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mX1 = event.getX();
                mY1 = event.getY();
            }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                mX2 = event.getX();
                mY2 = event.getY();
                Log.i(TAG, "mX1:" + mX1 + " mY1:" + mY1 + " mX2:" + mX2 + " mY2:" + mY2);
                mPoolExecutor.submit(() -> {
                    float distance = (float) Math.sqrt(Math.pow(mX2 - mMinSide * half, 2) + Math.pow(mY2 - mMinSide * half, 2));
                    if (distance > (mMinSide * half - mMaxWidth - offset) && distance < mMinSide * half+offset) {
                        int angle = fitAngle(mMinSide * half, mMinSide * half, mX2, mY2);
                        int progress = (angle - startAngle) * mMax / (endAngle - startAngle);
                        if (progress < 0) {
                            return;
                        }
                        if (progress > mMax) {
                            return;
                        }
                        post(() -> {
                            setProgress(progress);
                            if (mOnSeekBarChangeListener != null) {
                                mOnSeekBarChangeListener.onProgressChanged(progress, true);
                            }
                        });
                    }
                });
                return true;
            }
            default:
                return performClick();

        }


    }


    /**
     * 获取两条线的夹角
     */

    public static int fitAngle(float centerX, float centerY, float x, float y) {
        double rotation = 0;
        double tmpDegree = Math.atan2(Math.abs(y - centerY), Math.abs(x - centerX)) * 180 / Math.PI;
        if (x > centerX && y < centerY) {
            //第一象限
            rotation = 270 - tmpDegree;

        } else if (x > centerX && y > centerY) {
            //第二象限
            rotation = 270 + tmpDegree;
        } else if (x < centerX && y > centerY) {
            //第三象限
            rotation = 90 - tmpDegree;
        } else if (x < centerX && y < centerY) {
            //第四象限
            rotation = 90 + tmpDegree;
        } else if (x - centerX == 0 && y < centerY) {
            rotation = 180;
        } else if (x - centerX == 0 && y > centerY) {
            rotation = 0;
        }
        return (int) rotation;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPoolExecutor.shutdown();
        mBgPaint = null;
        mCountPaint.setShader(null);
        mCountPaint = null;
        mDividerPaint = null;
    }

    /**
     * 设置进度Progress监听
     */
    public interface OnSeekBarChangeListener {
        /**
         * 进度改变
         *
         * @param progress 进度
         * @param fromUser 是否来至User
         */
        void onProgressChanged(int progress, boolean fromUser);
    }

}
