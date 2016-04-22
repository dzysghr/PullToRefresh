package com.dzy.pulltorefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.dzy.ptr.BaseHeaderView;

/**
 * Created by dzysg on 2016/4/17 0017.
 */
public class CircleHeaderView extends BaseHeaderView
{

    int BigRadius = 30;
    float BigCircleMargin = 15;

    float curBigRadius;
    float curSmallRadius;
    float curBigY;
    float curSmallY;

    Paint p;
    HeaderState mHeaderState;


    public CircleHeaderView(Context context)
    {
        super(context);
    }


    @Override
    public void init(Context context)
    {
        p = new Paint();
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);

    }


    @Override
    public int getMaxHeight()
    {
        return 200;
    }

    @Override
    public int getThresholdHeight()
    {
        return 180;
    }

    @Override
    public int getRefreshingHeight()
    {
        return (int)(2*(BigRadius+BigCircleMargin));
    }


    @Override
    public void StateChange(HeaderState state)
    {
        mHeaderState = state;
        if (mHeaderState == HeaderState.refreshing)
        {
            StartBacktoRefresh();
        }
        Log.d("state", "statechange  " + mHeaderState.toString());
    }

    @Override
    public void startRefresh()
    {

    }

    @Override
    public void onSucceedRefresh()
    {

    }

    @Override
    public void onFailRefresh()
    {

    }


    private boolean isAnimatingBackToRefresh = false;
    private static int BigCirlceDown = 0;
    private static int SmallCicleDown = 1;
    private static int backToRefresh = 2;
    int mState = 0;

    float mOffset;

    @Override
    public void onPositionChange(float offset)
    {
        Log.i("tag", "offset " + offset);

        mOffset = offset;
        if (mOffset <= 80)
            mState = 0;
        else
            mState = 1;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //Log.d("onDraw", "headerview ");
        canvas.drawColor(0xff22ff44);
        if (mHeaderState == HeaderState.drag)
        {
            if (mState == 0)
                drawMoveBig(canvas);
            else if (mState == 1)
            {
                drawBig(canvas);
                drawDragSmall(canvas);
                drawLine(canvas);
            }
        } else if (mHeaderState == HeaderState.refreshing)
        {
            if (isAnimatingBackToRefresh)
            {
                drawBig(canvas);
                drawSmall(canvas);
                drawLine(canvas);
            } else
            {
                //画正在刷新的动画
                drawBig(canvas);
            }

        }

    }

    private void StartBacktoRefresh()
    {
        final float tempRadiusDe = curBigRadius - curSmallRadius;
        final float deY = curSmallY - curBigY;

        ValueAnimator anim = ValueAnimator.ofFloat(deY, 0);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float val = (float) animation.getAnimatedValue();
                curSmallY = curBigY + val;
                curSmallRadius += 0.5;
                curSmallRadius = Math.min(curBigRadius, curSmallRadius);

                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                isAnimatingBackToRefresh = false;

            }
        });
        isAnimatingBackToRefresh = true;
        anim.start();
    }

    private void drawLine(Canvas canvas)
    {

        Path path = new Path();

        path.moveTo(getMeasuredWidth() / 2 - curBigRadius, curBigY);


        path.lineTo(getMeasuredWidth() / 2 + curBigRadius, curBigY);

        path.quadTo(getMeasuredWidth() / 2 + curSmallRadius - 2, (curSmallY + curBigY) / 2, getMeasuredWidth() / 2 + curSmallRadius, curSmallY);
        path.lineTo(getMeasuredWidth() / 2 - curSmallRadius, curSmallY);

        path.quadTo(getMeasuredWidth() / 2 - curSmallRadius + 2, (curSmallY + curBigY) / 2, getMeasuredWidth() / 2 - curBigRadius, curBigY);

        canvas.drawPath(path, p);
    }

    private void drawMoveBig(Canvas canvas)
    {
        canvas.drawCircle(getMeasuredWidth() / 2, getMaxHeight() - 10 - BigRadius, BigRadius, p);
    }

    private void drawDragSmall(Canvas canvas)
    {
        float offsetfrom = 2 * (BigRadius + BigCircleMargin);
        float offsetto = getThresholdHeight();

        curSmallRadius = BigRadius - 20 * (mOffset - offsetfrom) / (offsetto - offsetfrom);
        curSmallY = getMaxHeight() - BigCircleMargin - curSmallRadius;
        drawSmall(canvas);
    }

    private void drawBig(Canvas canvas)
    {

        //80 来自2*bigRadius+10*2    120来自 threshold - 80
        float offsetfrom = 2 * (BigRadius + BigCircleMargin);
        float offsetto = getThresholdHeight();

        curBigRadius = BigRadius - 5 * (mOffset - offsetfrom) / (offsetto - offsetfrom);
        float cx = getMeasuredWidth() / 2;
        curBigY = getMaxHeight() - mOffset + BigCircleMargin + BigRadius;


        canvas.drawCircle(cx, curBigY, curBigRadius, p);

    }


    private void drawSmall(Canvas canvas)
    {
        canvas.drawCircle(getMeasuredWidth() / 2, curSmallY, curSmallRadius, p);
    }

}
