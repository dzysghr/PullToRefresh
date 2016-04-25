package com.dzy.pulltorefresh.headerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.dzy.ptr.HeaderController;
import com.dzy.ptr.HeaderState;
import com.dzy.ptr.PullToRefreshLayout;

/**
 *  Material风格的下拉刷新
 * Created by dzysg on 2016/4/24 0024.
 */
public class MaterialHeader extends View implements HeaderController
{

    MaterialProgressDrawable mDrawable;
    HeaderState mState = HeaderState.drag;
    ValueAnimator mScaleAnim ;
    PullToRefreshLayout mPullToRefreshLayout;
    float mScale = 1;

    public MaterialHeader(Context context)
    {
        super(context);
        init(context);
    }

    public void init(Context context)
    {
        mDrawable = new MaterialProgressDrawable(context,this);
        mDrawable.setCallback(this);
        mDrawable.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        mDrawable.setBackgroundColor(Color.WHITE);
        mDrawable.setAlpha(255);
        mDrawable.updateSizes(MaterialProgressDrawable.DEFAULT);
        mDrawable.setArrowScale(1);
        mDrawable.showArrow(true);


        mScaleAnim = ValueAnimator.ofFloat(1,0);
        mScaleAnim.setDuration(200);
        mScaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mScaleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                mPullToRefreshLayout.notityFinishAndBack();
            }
        });

    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr == mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }


    @Override
    public int getThresholdHeight()
    {
        return 180;
    }

    @Override
    public int getRefreshingHeight()
    {
        return 150;
    }

    @Override
    public void StateChange(HeaderState state)
    {
        if (state==HeaderState.hide)
        {
            mDrawable.stop();
            mDrawable.showArrow(true);
            mDrawable.setStartEndTrim(0,0);
            mScale = 1;
        }

        mState =state;
    }

    @Override
    public void startRefresh()
    {
        mScale = 1;
        mDrawable.start();
    }

    @Override
    public void onSucceedRefresh()
    {
        mDrawable.stop();
        mScaleAnim.start();

    }

    @Override
    public void onFailRefresh()
    {
        mDrawable.stop();
        mScaleAnim.start();
    }



    @Override
    public void onPositionChange(float offset)
    {
        if (mState==HeaderState.drag||mState==HeaderState.over)
        {
            float progress = offset/200;
            mDrawable.setStartEndTrim(0f, progress *0.8f);
            mDrawable.setProgressRotation(progress*0.7f);

        }
    }

    @Override
    public void attachLayout(PullToRefreshLayout layout)
    {
        mPullToRefreshLayout =  layout;
        mPullToRefreshLayout.setHandleToTopAnim(true);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        final int saveCount = canvas.save();
        Rect rect = mDrawable.getBounds();
        int l = getPaddingLeft() + (getMeasuredWidth() - mDrawable.getIntrinsicWidth()) / 2;
        canvas.translate(l,200-mDrawable.getIntrinsicHeight());
        canvas.scale(mScale,mScale, rect.exactCenterX(), rect.exactCenterY());
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        final int size = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, size, size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),200);
    }
}
