package com.bw.views.chooseview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author houch
 * date: 2022/4/24
 **/
public class ChooseView extends View {
    private static final String TAG = "ChooseView";
    private List<ChooseItem> chooseItems;
    private ChooseItem mChooseItem;
    private Paint mPaint;
    private boolean isExpand = false;
    private int mMeasuredWidth = 40;
    private int mStrokeColor = 0xffffffff;
    private int mStrokeWidth = 2;
    private onItemChooseListener mOnItemChooseListener;
    private ThreadPoolExecutor mExecutor;
    private Bitmap mChooseBitmap;
    private Rect mRectChoose;
    private Paint mBgPaint;
    private int startY = 0;
    private int mWidth;
    private int mHeight;
    private List<Bitmap> mBitmaps;
    private int mPadding = 0;


    public ChooseView(Context context) {
        super(context);
    }

    public ChooseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
        chooseItems = new ArrayList<>();
        mExecutor = new ThreadPoolExecutor(1, 2, 300L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                runnable -> new Thread(runnable, TAG));
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        mStrokeColor = strokeColor;
        mBgPaint.setColor(mStrokeColor);
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        mBgPaint.setStrokeWidth(mStrokeWidth);
    }

    public void setChooseItems(@NonNull List<ChooseItem> chooseItems) {
        this.chooseItems = chooseItems;
        if (!chooseItems.isEmpty()) {
            this.mChooseItem = chooseItems.get(0);
            mChooseBitmap = BitmapFactory.decodeResource(getResources(), mChooseItem.getRes());
            mWidth = mChooseBitmap.getWidth();
            mHeight = mChooseBitmap.getHeight();
            int left = getPaddingLeft();
            int righ = getPaddingRight();
            int top = getPaddingTop();
            int bottom = getPaddingBottom();
            mPadding = Math.max(Math.max(left, righ), Math.max(top, bottom));
            mRectChoose = new Rect((int) (0.5f * (mMeasuredWidth)) + mPadding,
                    (int) (0.5f * (mMeasuredWidth) - mPadding),
                    (int) (0.5f * (mMeasuredWidth) - mPadding),
                    (int) (0.5f * (mMeasuredWidth)) + mPadding);
            mBitmaps = new ArrayList<>();
            for (int i = 0; i < chooseItems.size(); i++) {
                ChooseItem chooseItem = chooseItems.get(i);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), chooseItem.getRes());
                mBitmaps.add(bitmap);
            }
        }
        invalidate();
    }


    public void setOnItemChooseListener(onItemChooseListener onItemChooseListener) {
        this.mOnItemChooseListener = onItemChooseListener;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.STROKE);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isExpand) {
            if (mChooseBitmap != null) {
                canvas.translate(0, startY);
                canvas.drawBitmap(mChooseBitmap, null, mRectChoose, mPaint);
            }
        } else {
            mBgPaint.setAlpha(100);
            mBgPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(0.5f * mStrokeWidth, 0.5f * mStrokeWidth,
                    mMeasuredWidth - (0.5f * mStrokeWidth),
                    (mMeasuredWidth * (chooseItems.size() + 1)) - (0.5f * mStrokeWidth),
                    0.5f * mMeasuredWidth, 0.5f * mMeasuredWidth, mBgPaint);
            mBgPaint.setAlpha(60);
            mBgPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(0.5f * mStrokeWidth, 0,
                    mMeasuredWidth - (0.5f * mStrokeWidth), mMeasuredWidth * (chooseItems.size() + 1),
                    0.5f * mMeasuredWidth, 0.5f * mMeasuredWidth, mBgPaint);
            canvas.drawLine(0.5f * (mMeasuredWidth - mWidth), startY,
                    0.5f * (mMeasuredWidth + mWidth), startY, mBgPaint);
            canvas.translate(0, startY);
            canvas.drawBitmap(mChooseBitmap, null, mRectChoose, mPaint);
            if (mBitmaps != null) {
                for (int i = 0; i < mBitmaps.size(); i++) {
                    canvas.translate(0, -mMeasuredWidth);
                    Bitmap bitmap = mBitmaps.get(i);
                    canvas.drawBitmap(bitmap, null, mRectChoose, mPaint);
                }
            }
            post(() -> {
                Rect rect = new Rect();
                getHitRect(rect);
                TouchDelegate touchDelegate = new TouchDelegate(rect, this);
                ((View) getParent()).setTouchDelegate(touchDelegate);
            });
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mExecutor.submit(() -> {
            if (mPadding == 0) {
                mRectChoose = new Rect((int) (0.5f * (mMeasuredWidth - mWidth)),
                        (int) (0.5f * (mMeasuredWidth - mHeight)),
                        (int) (0.5f * (mMeasuredWidth + mWidth)),
                        (int) (0.5f * (mMeasuredWidth + mHeight)));

            } else {
                mRectChoose = new Rect(mPadding,
                        mPadding,
                        mMeasuredWidth - mPadding,
                        mMeasuredWidth - mPadding);
            }

        });
        startY = mMeasuredWidth * chooseItems.size();
        setMeasuredDimension(mMeasuredWidth, mMeasuredWidth * (chooseItems.size() + 1));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            float y = event.getY();
            if (y > startY && y < startY + mMeasuredWidth) {
                isExpand = !isExpand;
                if (!isExpand) {
                    if (mOnItemChooseListener != null) {
                        mOnItemChooseListener.onItemChoose(mChooseItem);
                    }
                }
                invalidate();
                return true;
            }
            if (isExpand) {
                if (0 < y && y < startY) {
                    int position = (int) (y / mMeasuredWidth) + 1;
                    mChooseItem = chooseItems.get(chooseItems.size() - position);
                    mChooseBitmap = mBitmaps.get(chooseItems.size() - position);
                    if (mOnItemChooseListener != null) {
                        mOnItemChooseListener.onItemChoose(mChooseItem);
                    }
                    isExpand = false;
                    invalidate();
                    return true;

                }
            }

        }

        return super.onTouchEvent(event);

    }

    public interface onItemChooseListener {
        void onItemChoose(ChooseItem chooseItem);
    }

}
