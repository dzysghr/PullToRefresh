package com.dzy.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 这是下拉布局的头部基类，继承这个类来自定义各种各样的头部动画
 * Created by dzysg on 2016/4/16 0016.
 */
public abstract class BaseHeaderView extends FrameLayout
{

    protected FinishLisenter mFinishLisenter;

    public enum HeaderState
    {

        drag, //正在下拉,且高度处于刷新线之前
        over, //下拉超过刷新线
        release, //从超过刷新线返回到刷新线
        refreshing,//正在刷新线
        finish,//刷新完成-正在返回顶部
        fail,//刷新失败-正在返回顶部
        hide //返回顶部，完全隐藏
    }

    interface FinishLisenter
    {
        void onRefreshFinish();
    }

    public BaseHeaderView(Context context)
    {
        this(context, null);
    }

    public BaseHeaderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BaseHeaderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init(context, attrs, defStyleAttr);

    }


    public abstract void init(Context context, AttributeSet attrs, int defStyleAttr);


    /** 返回头部能下拉的最大高度,单位px
     * @return 返回头部能下拉的最大高度
     */
    public abstract int getMaxHeight();


    /** 触发刷新的下拉高度,单位px
     * @return 触发刷新的下拉高度
     */
    public abstract int getThresholdHeight();


    /**正在刷新时的高度,单位px，这个值应该小于等于{@link #getMaxHeight()}
     * @return 正在刷新时的高度
     */
    public abstract int getRefreshingHeight();



    /** 当header的状态改变时会调用
     * @param state 状态
     */
    public abstract void StateChange(HeaderState state);

    /**
     * 开始刷新时被调用，这在个方法实现正在刷新时的动画
     */
    public abstract void startRefresh();


    /**
     *  刷新成功时，此方法被调用，在此方法实现刷新完成之后，header向上隐藏之前的动画
     *  完成此动画后，应该手动调用{@link #notifyUpToTop()}来隐藏header
     */
    public abstract void onFinishRefresh();


    /**
     *  刷新失败时，此方法被调用，在此方法实现刷新完成之后，header向上隐藏之前的动画
     *  完成此动画后，应该手动调用{@link #notifyUpToTop()}来隐藏header
     */
    public abstract void onFailRefresh();



    /**
     *  在“刷新完成” 动画结束后应该调用此方法隐藏Header
     */
    public void notifyUpToTop()
    {
        if (mFinishLisenter != null)
            mFinishLisenter.onRefreshFinish();
    }


    /**
     * 当位置变化时此方法会被PullToRefreshLayout调用，可以通过这个偏移量和当前的状态来决定动画的样子
     * @param offset 位置偏移量，当下拉时从 0 到 MaxHeight,上升反之
     */
    public abstract void onOffset(float offset);


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        setMeasuredDimension(width,getMeasuredHeight());
//    }

    final public void setFinishLisenter(FinishLisenter finishLisenter)
    {
        mFinishLisenter = finishLisenter;
    }

}
