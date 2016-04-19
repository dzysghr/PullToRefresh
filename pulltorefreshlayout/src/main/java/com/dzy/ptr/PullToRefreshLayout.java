package com.dzy.ptr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 *
 * Created by dzysg on 2016/4/16 0016.
 */
public class PullToRefreshLayout extends FrameLayout implements  ValueAnimator.AnimatorUpdateListener
{


    View mChildView;
    BaseHeaderView mHeaderView;

    //超过刷新线马上刷新
    private boolean mRefreshImmediately = false;

    //开始刷新后马上回到刷新高度
    private boolean mUpToRefredshingImmediately = false;

    //下拉是否可以超过Header的高度
    private boolean canOverTheHeaderHeight = false;

    //如果正在刷新的时候也可以拉动
    private boolean canScrollWhenRefreshing = false;

    private int mHeaderHeight;
    private int mRefreshingHeight;
    private int mThresholdHeight;
    private boolean isRefreshing;
    private float startY;
    private float curY;
    float offsetY;
    private ValueAnimator mBackToTop;
    private ValueAnimator mBackToRefreshing;
    private ValueAnimator mFinshAndBack;
    private BaseHeaderView.HeaderState mHeaderState;

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(2);


    public PullToRefreshLayout(Context context)
    {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /** 设置头部
     * @param header {@link BaseHeaderView} BaseHeaderView
     */
    public void setHeaderView(BaseHeaderView header)
    {
        if (header == null)
            return;
        if (mHeaderView != null)
            throw new IllegalArgumentException("you can only set Headerview one time");

        mHeaderView = header;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.height = mHeaderView.getMaxHeight();
        mHeaderView.setLayoutParams(params);

        mHeaderHeight = mHeaderView.getMaxHeight();
        mRefreshingHeight = mHeaderView.getRefreshingHeight();
        mThresholdHeight = mHeaderView.getThresholdHeight();

        Log.d("tag", "MaxHeight " + mHeaderHeight + " refreshHeight : " + mRefreshingHeight + " ThresholdHeight :" + mThresholdHeight);


        mHeaderView.setTranslationY(-mHeaderHeight);
        addView(mHeaderView);
        setUpAnimation();

    }


    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        for(int i = 0; i < getChildCount(); i++)
        {
            if (getChildAt(i) instanceof BaseHeaderView)
                continue;
            mChildView = getChildAt(i);
        }

        if (getChildCount() > 2)
            throw new IllegalArgumentException("PullToRefreshLayout should only have one child view");
    }




    private void setUpAnimation()
    {
        //这个是下拉程度不够而返回顶部的动画
        mBackToTop = ValueAnimator.ofFloat(curY, 0);
        mBackToTop.setDuration(500);
        mBackToTop.addUpdateListener(this);

        //这个是下拉过了刷新线后，松开返回到正在刷新高度的动画
        mBackToRefreshing = ValueAnimator.ofFloat(offsetY, mRefreshingHeight);
        mBackToRefreshing.setInterpolator(new DecelerateInterpolator(2));
        mBackToRefreshing.setDuration(500);
        mBackToRefreshing.addUpdateListener(this);
        mBackToRefreshing.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                //上升到刷新高度后，开始通知header进行刷新动画
                changeState(BaseHeaderView.HeaderState.refreshing);
                mHeaderView.startRefresh();
            }
        });


        //这个是刷新完成后（无论成功失败），从正在刷新高度返回到顶部的隐藏动画，这个动画应该被headerview调用
        mFinshAndBack = ValueAnimator.ofFloat(mRefreshingHeight, 0);
        mFinshAndBack.setInterpolator(new DecelerateInterpolator(2));
        mFinshAndBack.setDuration(500);
        mFinshAndBack.addUpdateListener(this);
        mFinshAndBack.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                changeState(BaseHeaderView.HeaderState.hide);
            }
        });


        //返回顶部的动画由header去控制
        mHeaderView.setFinishLisenter(new BaseHeaderView.FinishLisenter()
        {
            @Override
            public void onRefreshFinish()
            {
                mFinshAndBack.start();
            }
        });

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        float val = (float) animation.getAnimatedValue();
        mChildView.setTranslationY(val);
        float offset = -mHeaderHeight + (float) animation.getAnimatedValue();
        mHeaderView.setTranslationY(offset);
        mHeaderView.onOffset(val);
        offsetY = val;
    }


    /**
     * 刷新完成
     */
    public void finishRefresh()
    {
        if (isRefreshing)
        {
            isRefreshing = false;
            changeState(BaseHeaderView.HeaderState.finish);
            mHeaderView.onFinishRefresh();
        }
    }

    public void failRefresh()
    {
        if (isRefreshing)
        {
            isRefreshing = false;
            changeState(BaseHeaderView.HeaderState.fail);
            mHeaderView.onFinishRefresh();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mHeaderView == null)
            return false;

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                curY = startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curY = ev.getY();
                float dy = curY - startY;
                //如果是向下滑而且列表不可以再向下滑，则拦截下滑事件
                if (dy > 0 && !canChildScrollUp())
                {
                    Log.i("tag", "cannot scroll up");
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mHeaderView == null || mChildView == null)
            return false;

        if (isRefreshing)
            return false;

        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                curY = event.getY();
                //dy表示手指滑动的距离
                float dy = curY - startY;

                dy = Math.max(0, dy);           //dy不可以为负防止 内容跑到顶部以上去

                // TODO: 2016/4/16 0016 这里的1.5是阻尼系数
                offsetY  = dy/1.5f;

                //如果下拉不可以超过header的高度
                if (!canOverTheHeaderHeight)
                    offsetY = Math.min(mHeaderHeight, offsetY);

                //如果超过刷新线就要立即刷新的话
                if (mRefreshImmediately&&offsetY>mThresholdHeight)
                {
                    changeState(BaseHeaderView.HeaderState.refreshing);
                    mHeaderView.startRefresh();
                    isRefreshing =true;

                    //如果开始刷新后需要立即返回到刷新高度的话
                    if (mUpToRefredshingImmediately)
                    {
                        mBackToRefreshing.setFloatValues(offsetY, mRefreshingHeight);
                        mBackToRefreshing.start();
                    }
                    return true;
                }

                mChildView.setTranslationY(offsetY);
                mHeaderView.setTranslationY(-mHeaderHeight + offsetY);
                mHeaderView.onOffset(offsetY);


                //如果超过刷新阈值
                if (offsetY < mThresholdHeight)
                    changeState(BaseHeaderView.HeaderState.drag);
                else
                    changeState(BaseHeaderView.HeaderState.over);

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                offsetY = mChildView.getTranslationY();
                //如果下拉程度不达到刷新线
                if (offsetY < mThresholdHeight)
                {
                    //自动升回顶部,隐藏
                    mHeaderView.StateChange(BaseHeaderView.HeaderState.drag);
                    mBackToTop.setFloatValues(offsetY, 0);
                    mBackToTop.start();
                } else
                {
                    //从超过刷新线升到正在刷新的高度
                    changeState(BaseHeaderView.HeaderState.release);
                    mBackToRefreshing.setFloatValues(offsetY,mRefreshingHeight);
                    mBackToRefreshing.start();
                    isRefreshing = true;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void changeState(BaseHeaderView.HeaderState state)
    {
        if (mHeaderState!=state)
        {
            mHeaderView.StateChange(state);
            mHeaderState = state;
        }
    }

    private boolean canChildScrollUp()
    {
        if (mChildView == null)
            return false;
        else
            return mChildView.canScrollVertically(-1);
    }

}
