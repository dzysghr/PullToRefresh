package com.dzy.pulltorefresh.headerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.ptr.BaseHeaderView;

/**
 * 这个水滴刷新效果不是很完善，代码写得比较乱
 * Created by dzysg on 2016/4/17 0017.
 */
public class DropWaterHeader extends BaseHeaderView
{

    int BigRadius = 30;
    float BigCircleMargin = 20;

    float curBigRadius;
    float curSmallRadius;
    float curBigY;
    float curSmallY;

    Paint p;
    HeaderState mHeaderState;
    ProgressBar mPb;
    TextView mTv;

    public DropWaterHeader(Context context)
    {
        super(context);
    }


    @Override
    public void init(Context context)
    {
        p = new Paint();
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);
        mPb = new ProgressBar(getContext());
        LayoutParams params = new LayoutParams(60,60);
        params.bottomMargin = 10;
        params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

        mTv = new TextView(getContext());
        LayoutParams textparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textparams.bottomMargin = 10;
        textparams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        mTv.setTextSize(12);


        addView(mPb,params);
        addView(mTv,textparams);


        mTv.setVisibility(View.INVISIBLE);
        mPb.setVisibility(View.INVISIBLE);
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

        if (mHeaderState==HeaderState.hide)
        {
            mPb.setVisibility(View.INVISIBLE);
            mTv.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void startRefresh()
    {

    }

    @Override
    public void onSucceedRefresh()
    {
        mTv.setVisibility(View.VISIBLE);
        mTv.setText("刷新成功");
        mPb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailRefresh()
    {
        mTv.setVisibility(View.VISIBLE);
        mTv.setText("刷新失败");
        mPb.setVisibility(View.INVISIBLE);
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
        if (mOffset <= 2*(BigRadius+BigCircleMargin))
            mState = 0;
        else
            mState = 1;


        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widht,getMaxHeight());
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
            }
        }

    }

    private void StartBacktoRefresh()
    {
        final float tempRadiusDe = curBigRadius - curSmallRadius;
        final float deY = curSmallY - curBigY;

        ValueAnimator anim = ValueAnimator.ofFloat(deY, 0);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float val = (float) animation.getAnimatedValue();
                curSmallY = curBigY + val;
                curSmallRadius += 1;
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
                mPb.setVisibility(View.VISIBLE);
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
        canvas.drawCircle(getMeasuredWidth() / 2, getMaxHeight() - BigCircleMargin - BigRadius, BigRadius, p);
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