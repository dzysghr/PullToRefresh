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
public class PullToRefreshLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener
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
    private boolean canScrollWhenRefreshing = true;

    private int mHeaderHeight;
    private int mRefreshingHeight;
    private int mThresholdHeight;
    private boolean isRefreshing;
    private float startY;
    private float curY;
    private float LastOffset = 0;
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


    /**
     * 设置头部
     *
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


    /**
     * 初始化各个动画，动画的数值不是传达室
     */
    private void setUpAnimation()
    {
        //这个是下拉程度不够而返回顶部的动画
        mBackToTop = new ValueAnimator();
        mBackToTop.setDuration(500);
        mBackToTop.addUpdateListener(this);

        //这个是下拉过了刷新线后，松开返回到正在刷新高度的动画
        mBackToRefreshing =new ValueAnimator();
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
        mFinshAndBack =new ValueAnimator();
        mFinshAndBack.setInterpolator(new DecelerateInterpolator(2));
        mFinshAndBack.setDuration(600);
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
                if (mChildView.getTranslationY()!=0)
                {
                    mFinshAndBack.setFloatValues(mChildView.getTranslationY(),0);
                    mFinshAndBack.start();
                }
                else
                    changeState(BaseHeaderView.HeaderState.hide);
            }
        });

    }


    /** 所有的位移动画都调用这个方法，用来更新位置
     * @param animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        float val = (float) animation.getAnimatedValue();
        mChildView.setTranslationY(val);
        float offset = -mHeaderHeight + (float) animation.getAnimatedValue();
        mHeaderView.setTranslationY(offset);
        mHeaderView.onPositionChange(val);
        LastOffset = val;
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
    public boolean dispatchTouchEvent(MotionEvent ev)
    {

        if (mHeaderView == null)
            return super.dispatchTouchEvent(ev);
        //如果刷新时不可以再拉动头部
        if (isRefreshing&&!canScrollWhenRefreshing)
            return super.dispatchTouchEvent(ev);

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curY = ev.getY();
                float dy = curY - startY;
                //如果列表不可以再向上滑，则拦截事件
                if (!canChildScrollUp())
                {

                    //lastoffset 是最后一次抬手或者动画完成时的偏移量
                    dy = dy / 1.5f;
                    float newPos = LastOffset +dy;
                    Log.d("tag","dy :"+dy+"  LastOffset :"+ LastOffset +" newPot :"+newPos);
                    if (newPos<=0)
                    {
                        // 这里的
                        LastOffset = 0;
                        //MotionEvent e = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_DOWN, ev.getX(), ev.getY(), ev.getMetaState());
                        // TODO: 2016/4/19 0019 bug，down事件不拦截被下层接收，然后拦截部分move，然后再传递部分move到下层，造成下层接收到的down和move位置断层
                        return super.dispatchTouchEvent(ev);
                    }
                    moveTo(newPos);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                offsetY = mChildView.getTranslationY();
                LastOffset = offsetY;
                if (offsetY == 0)
                    break;

                //如果下拉程度不达到刷新线
                if (offsetY < mThresholdHeight)
                {
                    //如果当前不是正在刷新，则回到顶部隐藏header
                    if (!isRefreshing)
                    {
                        mHeaderView.StateChange(BaseHeaderView.HeaderState.drag);
                        //自动升回顶部,隐藏
                        mBackToTop.setFloatValues(offsetY, 0);
                        mBackToTop.start();

                    }
                    //如果正在刷新，且当前位置大于正在刷新高度
                    else if (offsetY>mRefreshingHeight)
                    {
                        //自动升回顶部,隐藏
                        mBackToRefreshing.setFloatValues(offsetY, mRefreshingHeight);
                        if (mBackToRefreshing.isRunning())
                            mBackToRefreshing.cancel();
                        mBackToRefreshing.start();
                    }
                    return true;
                }
                else
                {
                    //从超过刷新线升到正在刷新的高度
                    mBackToRefreshing.setFloatValues(offsetY, mRefreshingHeight);
                    if (mBackToRefreshing.isRunning())
                        mBackToRefreshing.cancel();
                    mBackToRefreshing.start();

                    //如果当前不是正在刷新，则触发释放状态
                    if (!isRefreshing)
                    {
                        changeState(BaseHeaderView.HeaderState.release);
                        isRefreshing = true;
                    }
                    return true;
                }
        }
        return super.dispatchTouchEvent(ev);
    }


    private void moveTo(float to)
    {

        //如果下拉不可以超过header的高度
        if (!canOverTheHeaderHeight)
            to = Math.min(mHeaderHeight, to);

        //如果超过刷新线就要立即刷新的话
        if (mRefreshImmediately && to > mThresholdHeight)
        {
            changeState(BaseHeaderView.HeaderState.refreshing);
            mHeaderView.startRefresh();
            isRefreshing = true;

            //如果开始刷新后需要立即返回到刷新高度的话
            if (mUpToRefredshingImmediately)
            {
                mBackToRefreshing.setFloatValues(LastOffset, mRefreshingHeight);
                mBackToRefreshing.start();
            }
            return;
        }

        OffsetTo(to);

        if (!isRefreshing)
        {
            //如果超过刷新阈值
            if (to < mThresholdHeight)
                changeState(BaseHeaderView.HeaderState.drag);
            else
                changeState(BaseHeaderView.HeaderState.over);
        }
    }

    private void OffsetTo(float to)
    {
        mChildView.setTranslationY(to);
        mHeaderView.setTranslationY(-mHeaderHeight + to);
        mHeaderView.onPositionChange(to);
    }

    /** 通知Header状态改变
     * @param state 状态枚举 {@link com.dzy.ptr.BaseHeaderView.HeaderState}
     */
    private void changeState(BaseHeaderView.HeaderState state)
    {
        if (mHeaderState != state)
        {
            mHeaderView.StateChange(state);
            mHeaderState = state;
        }
    }


    /** 判断子控件能否向上滑
     * @return 能则返回true
     */
    private boolean canChildScrollUp()
    {
        if (mChildView == null)
            return false;
        else
            return mChildView.canScrollVertically(-1);
    }

}
