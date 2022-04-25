package com.bw.views.chooseview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * description:
 *
 * @author houch
 * date: 2022/4/24
 **/
public class ClipViewConstraintLayout extends ConstraintLayout {
    private static final String TAG = "ClipViewConstraintLayou";

    private ClipViewConstraintLayout mConstraintLayout;

    public ClipViewConstraintLayout(@NonNull Context context) {
        this(context, null);
    }

    public ClipViewConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipViewConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ClipViewConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int nleft = 0;
    private int ntop = 0;
    private int nright = 0;
    private int nbuttom = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        post(() -> {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int cLeft = child.getLeft();
                nleft = Math.min(nleft, Math.min(0, cLeft));
                int cTop = child.getTop();
                ntop = Math.min(ntop, Math.min(0, cTop));
                int cRight = child.getRight();
                nright = Math.min(nright, Math.min(0, cRight));
                int cBottom = child.getBottom();
                nbuttom = Math.min(nbuttom, Math.min(0, cBottom));
            }
            Rect rect = new Rect();
            getHitRect(rect);
            rect.left += nleft;
            rect.top += ntop;
            rect.right += nright;
            rect.bottom += nbuttom;
            TouchDelegate touchDelegate = new TouchDelegate(rect, this);
            ViewGroup parent = (ViewGroup) getParent();
            parent.setTouchDelegate(touchDelegate);
            parent.setClipChildren(false);

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取坐标相对屏幕的位置
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        View child;
        //检测坐标是否落在对应的子布局内
        if ((child = checkChildTouch(rawX, rawY)) != null) {
            //若是则将坐标值修改为子布局中心点
            int outLocation[] = new int[2];
            child.getLocationOnScreen(outLocation);
            event.setLocation(rawX - outLocation[0], rawY - outLocation[1]);
            //分发事件给子布局
            return child.dispatchTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    private View checkChildTouch(float x, float y) {
        int outLocation[] = new int[2];
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                //获取View 在屏幕上的可见坐标
                child.getLocationOnScreen(outLocation);
                //点击坐标是否落在View 的可见区域，若是则将事件分发给它
                boolean hit = (x >= outLocation[0] && y > outLocation[1]
                        && x <= outLocation[0] + child.getWidth() && y <= outLocation[1] + child.getHeight());
                if (hit) {
                    return child;
                }
            }
        }
        return null;
    }

}
