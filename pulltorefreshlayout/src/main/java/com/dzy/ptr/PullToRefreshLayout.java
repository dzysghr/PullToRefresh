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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * 下拉刷新布局
 * Created by dzysg on 2016/4/16 0016.
 */
public class PullToRefreshLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener
{
    // TODO: 2016/4/19 0019 增加自动刷新功能
    // TODO: 2016/4/19 0019 未向外部暴露各个接口
    // TODO: 2016/4/19 0019 刷新完成时强制上升了！！！！！！
    View mChildView;
    BaseHeaderView mHeaderView;


    //实现
    //超过刷新线马上刷新
    public boolean mRefreshImmediately = true;

    // TODO: 2016/4/21 0021 考虑到代码复杂度、功能实用性，未实现
    //开始刷新后不等松手马上回到刷新高度
    public boolean mUpToRefredshingImmediately = false;

    //实现
    //下拉是否可以超过Header的高度
    public boolean canOverTheHeaderHeight = false;

    //实现
    //如果正在刷新的时候也可以拉动
    public boolean canScrollWhenRefreshing = true;


    // TODO: 2016/4/21 0021 头部是随着下拉向下移动还是随着下拉逐渐显露出来
    public boolean mHeaderMove = false;

    //刷新回调，当刷新发生时会回调该接口
    private RefreshLinstener mRefreshLinstener;

    private ScrollCondition mCondition;

    private int mHeaderHeight;
    private int mRefreshingHeight;
    private int mThresholdHeight;

    //是否正在刷新
    private boolean isRefreshing;

    //是否已经刷新完成
    private boolean isFinish;

    //手指是否还在屏幕上
    private boolean isOnTouch = false;


    private float startY;

    //lastPos 是最近一次抬手或者动画完成时的ChildView的偏移量
    private float LastPos = 0;


    private ValueAnimator mBackToTop;
    private ValueAnimator mBackToRefreshing;
    private ValueAnimator mFinshAndBack;
    private BaseHeaderView.HeaderState mHeaderState;

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(2);


    private float offsetY;


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


        addView(mHeaderView);
        bringChildToFront(mHeaderView);
        setUpAnimation();

    }


    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        layoutChildren();
    }

    private void layoutChildren()
    {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null)
        {

            final int left = paddingLeft;
            final int top = paddingTop + (int) offsetY - mHeaderHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();

            Log.d("onLayout", "left " + left + " top " + top + " right " + right + " bottom " + bottom);
            mHeaderView.layout(left, top, right, bottom);

        }
        if (mChildView != null)
        {

            final int left = paddingLeft;
            final int top = paddingTop + (int) offsetY;
            final int right = left + mChildView.getMeasuredWidth();
            final int bottom = top + mChildView.getMeasuredHeight();
            mChildView.layout(left, top, right, bottom);
        }
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
        mBackToRefreshing = new ValueAnimator();
        mBackToRefreshing.setInterpolator(decelerateInterpolator);
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
        mFinshAndBack = new ValueAnimator();
        mFinshAndBack.setInterpolator(decelerateInterpolator);
        mFinshAndBack.setDuration(600);
        mFinshAndBack.addUpdateListener(this);
        mFinshAndBack.addListener(new AnimatorListenerAdapter()
        {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                changeState(BaseHeaderView.HeaderState.hide);
                isFinish = false;
            }
        });


    }


    /**
     * 所有的位移动画都调用这个方法，用来更新位置
     *
     * @param animation 当前动画
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        int val = (int) animation.getAnimatedValue();
        performOffsetTo(val);
        LastPos = val;
    }


    /**
     * 刷新成功
     */
    public void succeedRefresh()
    {
        isFinish = true;
        if (isRefreshing)
        {
            isRefreshing = false;
            changeState(BaseHeaderView.HeaderState.finish);
            mHeaderView.onSucceedRefresh();
            //如果手指还没放开就不能开始上升的动画
            if (!isOnTouch)
            {

                if (offsetY != 0)
                {
                    mFinshAndBack.setIntValues((int)offsetY, 0);
                    mFinshAndBack.start();
                } else
                    changeState(BaseHeaderView.HeaderState.hide);
            }

        }

    }

    /**
     * 刷新失败
     */
    public void failRefresh()
    {
        isFinish = true;
        if (isRefreshing)
        {
            isRefreshing = false;
            changeState(BaseHeaderView.HeaderState.fail);
            mHeaderView.onFailRefresh();
            if (!isOnTouch)
            {

                if (offsetY != 0)
                {
                    mFinshAndBack.setIntValues((int)offsetY, 0);
                    mFinshAndBack.start();
                } else
                    changeState(BaseHeaderView.HeaderState.hide);
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {

        // TODO: 2016/4/19 0019 逻辑太长，待分解
        if (mHeaderView == null)
            return super.dispatchTouchEvent(ev);
        //如果刷新时不可以再拉动头部
        if (isRefreshing && !canScrollWhenRefreshing)
            return super.dispatchTouchEvent(ev);

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (isOnTouch)
                    break;
                else
                {
                    startY = ev.getY();
                    isOnTouch = true;
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                float curY = ev.getY();
                if (canChildScrollUp())
                {
                    startY = curY;
                }
                //如果列表不可以再向上滑，则拦截事件
                else
                {
                    //dy为滑动距离，被childview消费的不算
                    float dy = curY - startY;
                    //getNestedScrollAxes()
                    //1.5是阻尼系数，为了产生韧性效果
                    dy = dy / 1.5f;

                    //lastPos 是最后一次抬手或者动画完成时的偏移量
                    float newOffset = LastPos + dy;

                    Log.d("offset", "dy :" + dy + "  LastPos :" + LastPos + " newPot :" + newOffset+" start "+startY);

                    //newOffset等于0说明header已经刚好完全隐藏了，小于0时应该传下层去处理move事件
                    if (newOffset < 0)
                    {
                        if (LastPos == 0f)
                            return super.dispatchTouchEvent(ev);
                        else
                        {
                            //在理想状态下，当header从显示变为隐藏时，newOffset从正渐变为0，当newOffset为0时，mChildView和mHeaderView的offset都应该为0，
                            //但是由于浮点值不一定会出现刚好为0的情况，可能直接从0.12变为-0.02，所以这里强制设为0
                            //performOffsetTo(0);
                            LastPos = 0f;


                            //down事件被下层接收，向上移动，拦截部分move来隐藏header，当头部完全隐藏后的move应该传递到下层，
                            // 造成下层接收到的down和move位置断层，会出现下层瞬移的现象，所以这里手动发
                            // 一个down事件给子view，覆盖之前的down,以后直接将move事件传递下去
                            MotionEvent e = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_DOWN, ev.getX(), ev.getY(), ev.getMetaState());
                            return super.dispatchTouchEvent(e);
                        }
                    }

                    // TODO: 2016/4/20 0020 还是有点问题，但不明显，慢慢优化
                    MotionEvent cancelEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, ev.getX(), ev.getY(), ev.getMetaState());
                    super.dispatchTouchEvent(cancelEvent);
                    //Log.i("tag", "move offset");
                    moveTo(newOffset);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isOnTouch = false;

                LastPos = offsetY;
                //Log.d("tag", "ACTION_CANCEL or UP, offsetY= "+offsetY);

                //如果header已经完全隐藏了，则由子view去处理action_up和cancel事件
                if (offsetY <= 0)
                    break;

                if (isFinish)
                {
                    mHeaderView.StateChange(BaseHeaderView.HeaderState.finish);
                    mFinshAndBack.setIntValues((int)offsetY, 0);
                    if (mFinshAndBack.isRunning())
                        mFinshAndBack.cancel();
                    mFinshAndBack.start();
                    return true;
                }


                //如果下拉程度不达到刷新线
                if (offsetY < mThresholdHeight)
                {
                    //如果当前不是正在刷新，则回到顶部隐藏header
                    if (!isRefreshing)
                    {
                        mHeaderView.StateChange(BaseHeaderView.HeaderState.drag);
                        //自动升回顶部,隐藏
                        mBackToTop.setIntValues((int)offsetY, 0);
                        mBackToTop.start();
                        return true;
                    }
                    //如果正在刷新，且当前位置大于正在刷新高度
                    else if (offsetY > mRefreshingHeight)
                    {
                        //升到正在刷新高度
                        mBackToRefreshing.setIntValues((int)offsetY, mRefreshingHeight);
                        if (mBackToRefreshing.isRunning())
                            mBackToRefreshing.cancel();
                        mBackToRefreshing.start();
                        return true;
                    } else  //这时候是普通的点击事件，传给下层
                        break;

                } else//如果下拉达到了刷新线
                {
                    //从超过刷新线升到正在刷新的高度
                    mBackToRefreshing.setIntValues((int)offsetY, mRefreshingHeight);
                    if (mBackToRefreshing.isRunning())
                        mBackToRefreshing.cancel();
                    mBackToRefreshing.start();

                    //如果当前不是正在刷新，则触发释放状态，触发刷新事件
                    if (!isRefreshing)
                    {
                        changeState(BaseHeaderView.HeaderState.release);
                        isRefreshing = true;
                        if (mRefreshLinstener != null)
                            mRefreshLinstener.onRefreshingStart();
                    }
                    return true;
                }
        }

        if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP)
            Log.e("tag", "child view get cancel");
        return super.dispatchTouchEvent(ev);
    }


    private void moveTo(float to)
    {

        //如果下拉不可以超过header的高度
        if (!canOverTheHeaderHeight)
            to = Math.min(mHeaderHeight, to);

        performOffsetTo(to);
        mHeaderView.onPositionChange(to);


        //如果超过刷新线就要立即刷新的话
        if (mRefreshImmediately && to > mThresholdHeight && !isFinish)
        {
            isRefreshing = true;
            changeState(BaseHeaderView.HeaderState.refreshing);
            mHeaderView.startRefresh();

            if (mRefreshLinstener != null)
                mRefreshLinstener.onRefreshingStart();


            //如果开始刷新后需要立即返回到刷新高度的话
            if (mUpToRefredshingImmediately)
            {
                mBackToRefreshing.setIntValues((int)to, mRefreshingHeight);
                mBackToRefreshing.start();
            }
            return;
        }


        //不是正在刷新也不是已经完成
        if (!isRefreshing && !isFinish)
        {
            //如果超过刷新阈值
            if (to < mThresholdHeight)
                changeState(BaseHeaderView.HeaderState.drag);
            else
                changeState(BaseHeaderView.HeaderState.over);
        }


    }


    private void performOffsetTo(float to)
    {

        int x = (int) (to - offsetY);
        Log.d("x","x  "+x);

        if (x==0)
            return;
        mChildView.offsetTopAndBottom(x);
        mHeaderView.offsetTopAndBottom(x);

        offsetY = to;
    }

    /**
     * 通知Header状态改变
     *
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

    /**
     * 判断子控件能否向上滑（下拉）
     *
     * @return 能则返回true
     */
    private boolean canChildScrollUp()
    {
        //如果
        if (mCondition != null)
            return mCondition.canScroll();

        return mChildView != null && mChildView.canScrollVertically(-1);
    }

    /*
    从下面开始都是getter、setter
     */

    public RefreshLinstener getRefreshLinstener()
    {
        return mRefreshLinstener;
    }

    public void setRefreshLinstener(RefreshLinstener refreshLinstener)
    {
        mRefreshLinstener = refreshLinstener;
    }

    public ScrollCondition getCondition()
    {
        return mCondition;
    }

    public void setCondition(ScrollCondition condition)
    {
        mCondition = condition;
    }
}
