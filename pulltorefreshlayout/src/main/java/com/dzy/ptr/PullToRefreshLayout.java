package com.dzy.ptr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * 下拉刷新布局
 * Created by dzysg on 2016/4/16 0016.
 */
public class PullToRefreshLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener
{
    // TODO: 2016/4/19 0019 增加自动刷新功能
    // TODO: 2016/4/19 0019 未向外部暴露各个接口

    View mChildView;
    HeaderController mUIController;
    View mHeader;


    //实现
    //超过刷新线马上刷新，比如 QQ
    public boolean mRefreshImmediately = false;

    // TODO: 2016/4/21 0021 考虑到功能实用性，未实现
    //开始刷新后不等松手马上回到刷新高度
    public boolean mUpToRefredshingImmediately = false;

    //实现
    //下拉是否可以超过Header的高度
    public boolean canOverTheHeaderHeight = false;

    //实现
    //如果正在刷新的时候也可以拉动
    public boolean canScrollWhenRefreshing = true;

    //实现，刷新完成不等松手马上升到顶部 ，网易新闻
    public boolean mForceToTopWhenFinish = false;

    // TODO: 2016/4/22 0022 刷新时不显示头部，微信朋友圈
    public boolean mBackToTopWhenRefresh = false;


    //实现，内容向下偏移，头部固定逐渐显示
    public boolean mPinHeader = false;

    //实现，内容固定，头部向下偏移，显示在内容上层
    public boolean mPinContent = false;


    //刷新回调，当刷新发生时会回调该接口
    private RefreshLinstener mRefreshLinstener;

    private ScrollCondition mCondition;

    //header高度
    private int mHeaderHeight;
    //正在刷新时的高度
    private int mRefreshingHeight;
    //下拉时触发刷新的高度
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

    //当前偏移量
    private float offsetY;

    private MotionEvent mLastEvent;


    private boolean mHasSendCancel = false;

    private ValueAnimator mBackToTop;
    private ValueAnimator mBackToRefreshing;
    private ValueAnimator mFinishAndBack;
    private HeaderState mHeaderState;
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
     * @param header  应该实现{@link HeaderController}
     */
    public void setHeader(View header)
    {
        if (header == null)
            return;
        if (mHeader != null)
            throw new IllegalArgumentException("you can only set Headerview one time");

        mHeader = header;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeader.setLayoutParams(params);

        if (header instanceof HeaderController)
            mUIController = (HeaderController) header;
        else
        {
            throw new IllegalArgumentException("the headerview should implement HeaderController interface");
        }

        mRefreshingHeight = mUIController.getRefreshingHeight();
        mThresholdHeight = mUIController.getThresholdHeight();

        //Log.d("tag", "MaxHeight " + mHeaderHeight + " refreshHeight : " + mRefreshingHeight + " ThresholdHeight :" + mThresholdHeight);
        addView(mHeader);
        setUpAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        android.util.Log.e("tag", "onMeasure");
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
        int top = 0;
        int right = 0;
        int bottom = 0;
        int left = paddingLeft;
        if (mUIController != null)
        {

            //如果header是固定模式
            if (mPinHeader)
            {
                top = paddingTop;
            } else
            {
                top = paddingTop + (int) offsetY - mHeader.getMeasuredHeight();
            }
            right = left + mHeader.getMeasuredWidth();
            bottom = top + mHeader.getMeasuredHeight();

            Log.d("onLayout", "left " + left + " top " + top + " right " + right + " bottom " + bottom);
            Log.d("onLayout", "header view getMeasuredWidth "+ mHeader.getMeasuredHeight());

            mHeader.layout(left, top, right, bottom);

            mHeaderHeight = mHeader.getMeasuredHeight();
            mThresholdHeight = mUIController.getThresholdHeight();
            mRefreshingHeight = mUIController.getRefreshingHeight();
        }

        if (mChildView != null)
        {

            if (mPinContent)
                top = paddingTop;
            else
            {
                top = paddingTop + (int) offsetY;
            }
            left = paddingLeft;
            right = left + mChildView.getMeasuredWidth();
            bottom = top + mChildView.getMeasuredHeight();
            mChildView.layout(left, top, right, bottom);

            if (mPinHeader)
                bringChildToFront(mChildView);
        }
    }


    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        for(int i = 0; i < getChildCount(); i++)
        {
            if (getChildAt(i) instanceof HeaderController)
                continue;
            mChildView = getChildAt(i);
        }

        if (getChildCount() > 2)
            throw new IllegalArgumentException("PullToRefreshLayout should only have one child view");
    }


    /**
     * 初始化各个动画
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
                changeState(HeaderState.refreshing);
                mUIController.startRefresh();
            }
        });


        //这个是刷新完成后（无论成功失败），从正在刷新高度返回到顶部的隐藏动画，这个动画应该被headerview调用
        mFinishAndBack = new ValueAnimator();
        mFinishAndBack.setInterpolator(decelerateInterpolator);
        mFinishAndBack.setDuration(600);
        mFinishAndBack.addUpdateListener(this);
        mFinishAndBack.addListener(new AnimatorListenerAdapter()
        {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                changeState(HeaderState.hide);
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
            changeState(HeaderState.finish);
            mUIController.onSucceedRefresh();

            //如果是强制升回顶部
            if (mForceToTopWhenFinish)
            {
                startY = mLastEvent.getY();
                notityFinishAndBack();
                return;
            }
            //手指还没放开就不能开始上升的动画
            if(!isOnTouch)
            {
                if (offsetY != 0)
                {
                    notityFinishAndBack();
                } else
                    changeState(HeaderState.hide);
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
            changeState(HeaderState.fail);
            mUIController.onFailRefresh();

            //如果是强制升回顶部
            if (mForceToTopWhenFinish)
            {
                startY = mLastEvent.getY();
                notityFinishAndBack();
                return;
            }
            if (!isOnTouch)
            {
                if (offsetY != 0)
                {
                    notityFinishAndBack();

                } else
                    changeState(HeaderState.hide);
            }
        }
    }

    public void notityFinishAndBack()
    {
        mFinishAndBack.setIntValues((int) offsetY, 0);
        mFinishAndBack.start();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {

        // TODO: 2016/4/19 0019 逻辑太长，待分解
        if (mUIController == null)
            return super.dispatchTouchEvent(ev);


        //如果刷新时不可以再拉动头部
        if (isRefreshing && !canScrollWhenRefreshing)
            return super.dispatchTouchEvent(ev);

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (!isOnTouch)
                {
                    cancelAnimIfNeed();
                    startY = ev.getY();
                    isOnTouch = true;
                    mHasSendCancel = false;
                    super.dispatchTouchEvent(ev);
                }
                return true;//无论任何时候都要返回true，否则后续事件收不到
            case MotionEvent.ACTION_MOVE:
                mLastEvent = ev;
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
                    //1.5是阻尼系数，为了产生韧性效果,下拉时才会有
                    dy = dy / 2f;

                    //lastPos 是最后一次抬手或者动画完成时的偏移量
                    float newOffset = LastPos + (int) dy;

                    Log.d("offset", "dy :" + dy + "  LastPos :" + LastPos + " newPot :" + newOffset + " start " + startY);

                    //newOffset等于0说明header已经刚好完全隐藏了，小于0时应该传下层去处理move事件
                    if (newOffset < 0)
                    {
                        if (!mHasSendCancel)
                        {
                            Log.e("tag", "!mHasSendCancel");
                            return super.dispatchTouchEvent(ev);
                        } else
                        {

                            LastPos = 0;
                            performOffsetTo(0);
                            Log.e("tag", "send down event");
                            //down事件被下层接收，向上移动，拦截部分move来隐藏header，当头部完全隐藏后,move应该传递到下层，
                            // 造成下层接收到的down和move位置断层，会出现下层瞬移的现象，所以这里手动发
                            // 一个down事件给子view，覆盖之前的down,以后直接将move事件传递下去
                            MotionEvent e = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_DOWN, ev.getX(), ev.getY(), ev.getMetaState());
                            mHasSendCancel = false;
                            return super.dispatchTouchEvent(e);
                        }
                    }

                    if (!mHasSendCancel)
                    {
                        Log.e("tag", "send cancel event");

                        //down事件被下层接收，造成下层控件显示按下效果（比如listview按下时Item颜色加深），如果此后要拦截move事件，就发一个cancel事件让下层view取消按下的效果
                        MotionEvent cancelEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, ev.getX(), ev.getY(), ev.getMetaState());
                        super.dispatchTouchEvent(cancelEvent);
                        mHasSendCancel = true;
                    }

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
                    mUIController.StateChange(HeaderState.finish);
                    mFinishAndBack.setIntValues((int) offsetY, 0);
                    if (mFinishAndBack.isRunning())
                        mFinishAndBack.cancel();
                    mFinishAndBack.start();
                    return true;
                }


                //如果下拉程度不达到刷新线
                if (offsetY < mThresholdHeight)
                {
                    //如果当前不是正在刷新，则回到顶部隐藏header
                    if (!isRefreshing)
                    {
                        mUIController.StateChange(HeaderState.drag);
                        //自动升回顶部,隐藏
                        mBackToTop.setIntValues((int) offsetY, 0);
                        mBackToTop.start();
                        return true;
                    }
                    //如果正在刷新，且当前位置大于正在刷新高度
                    else if (offsetY > mRefreshingHeight)
                    {
                        //升到正在刷新高度
                        mBackToRefreshing.setIntValues((int) offsetY, mRefreshingHeight);
                        if (mBackToRefreshing.isRunning())
                            mBackToRefreshing.cancel();
                        mBackToRefreshing.start();
                        return true;
                    } else  //这时候是普通的点击事件，传给下层
                        break;

                } else//如果下拉达到了刷新线
                {
                    //从超过刷新线升到正在刷新的高度
                    mBackToRefreshing.setIntValues((int) offsetY, mRefreshingHeight);
                    if (mBackToRefreshing.isRunning())
                        mBackToRefreshing.cancel();
                    mBackToRefreshing.start();

                    //如果当前不是正在刷新，则触发释放状态，触发刷新事件
                    if (!isRefreshing)
                    {
                        changeState(HeaderState.release);
                        isRefreshing = true;
                        if (mRefreshLinstener != null)
                            mRefreshLinstener.onRefreshStart();
                    }
                    return true;
                }
        }

        if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP)
            Log.e("tag", "child view get cancel");
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            Log.e("tag", "child view get Down");
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 将内容和头部移动到指定的位置
     *
     * @param to 偏移量，px
     */
    private void moveTo(float to)
    {

        //如果下拉不可以超过header的高度
        if (!canOverTheHeaderHeight)
            to = Math.min(mHeaderHeight, to);

        performOffsetTo(to);


        //如果超过刷新线就要立即刷新的话
        if (mRefreshImmediately && to > mThresholdHeight && !isFinish)
        {
            isRefreshing = true;
            changeState(HeaderState.refreshing);
            mUIController.startRefresh();

            if (mRefreshLinstener != null)
                mRefreshLinstener.onRefreshStart();


            //如果开始刷新后需要立即返回到刷新高度的话
            if (mUpToRefredshingImmediately)
            {
                mBackToRefreshing.setIntValues((int) to, mRefreshingHeight);
                mBackToRefreshing.start();
            }
            return;
        }


        //不是正在刷新也不是已经完成
        if (!isRefreshing && !isFinish)
        {
            //如果超过刷新阈值
            if (to < mThresholdHeight)
                changeState(HeaderState.drag);
            else
                changeState(HeaderState.over);
        }
    }


    private void performOffsetTo(float to)
    {

        int change = (int) (to - offsetY);
        Log.d("change", "performOffsetTo  " + change);

        if (change == 0)
            return;
        if (!mPinHeader)
            mHeader.offsetTopAndBottom(change);

        if (!mPinContent)
            mChildView.offsetTopAndBottom(change);

        mUIController.onPositionChange(to);
        invalidate();
        offsetY = offsetY + change;
    }

    private void cancelAnimIfNeed()
    {
        if (mBackToTop.isRunning())
            mBackToTop.cancel();
        if (mBackToRefreshing.isRunning())
            mBackToRefreshing.cancel();
        if (mFinishAndBack.isRunning())
            mFinishAndBack.cancel();
    }

    /**
     * 通知Header状态改变
     *
     * @param state 状态枚举 {@link com.dzy.ptr.HeaderState}
     */
    private void changeState(HeaderState state)
    {
        if (mHeaderState != state)
        {
            mUIController.StateChange(state);
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
        //如果用户自己实现判断逻辑，则以用户的逻辑为准
        if (mCondition != null)
            return mCondition.canScrollUp();

        if (mChildView == null)
            return true;

        if (android.os.Build.VERSION.SDK_INT < 14)
        {
            if (mChildView instanceof AbsListView)
            {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else
            {
                return mChildView.getScrollY() > 0;
            }
        } else
        {
            return mChildView.canScrollVertically(-1);
        }
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

    public ScrollCondition getScrollableListener()
    {
        return mCondition;
    }

    public void setScrollableListener(ScrollCondition condition)
    {
        mCondition = condition;
    }
}
