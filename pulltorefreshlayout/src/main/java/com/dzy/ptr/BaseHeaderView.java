package com.dzy.ptr;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * 这是下拉布局的头部基类，继承这个类来自定义各种各样的头部动画
 * Created by dzysg on 2016/4/16 0016.
 */
public abstract class BaseHeaderView extends FrameLayout
{

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

    public BaseHeaderView(Context context)
    {
        super(context);
        init(context);
        setWillNotDraw(false);
    }


    /** 初始化函数，在构造函数中被调用。
     */
    public abstract void init(Context context);


    /** 返回头部能下拉的最大高度,单位px
     * @return 返回头部能下拉的最大高度
     */
    public abstract int getMaxHeight();


    /** 触发刷新的下拉高度,单位px
     * @return 触发刷新的下拉高度
     */
    public abstract int getThresholdHeight();


    /**正在刷新时的高度,单位px，这个值应该小于等于 MaxHeight
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
     *  刷新成功时，此方法被调用
     */
    public abstract void onSucceedRefresh();


    /**
     *  刷新失败时，此方法被调用
     */
    public abstract void onFailRefresh();



    /**
     * 发生拖拽时时此方法会被PullToRefreshLayout调用，可以通过这个偏移量和当前的状态来决定动画的样子
     * @param offset 当头部不固定时表示位置偏移量，当头部内容固定时表示头部露出的高度，范围为下拉时从 0 到 MaxHeight,上升反之。
     */
    public abstract void onPositionChange(float offset);


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width =MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        Log.d("BaseHeader", "BaseHeader onMeasure  width " + width + "   height " + height);

    }
}
