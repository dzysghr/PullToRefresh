package com.dzy.ptr;

/**
 * 这是下拉布局的头部基类，继承这个类来自定义各种各样的头部动画
 * header会以width为Match_parent，height为Wrap_content的参数被加到ptrlayout
 * 如果需要自定义宽高，需要重写onMeasure
 * Created by dzysg on 2016/4/16 0016.
 */
public interface HeaderController
{

    // TODO: 2016/4/24 0024 完善注释
    /**
     * 触发刷新的下拉高度,单位px
     *
     * @return 触发刷新的下拉高度
     */
    int getThresholdHeight();


    /**
     * 正在刷新时的高度,单位px，这个值应该小于等于 MaxHeight
     *
     * @return 正在刷新时的高度
     */
    int getRefreshingHeight();


    /**
     * 当header的状态改变时会调用
     *
     * @param state 状态
     */
    void StateChange(HeaderState state);

    /**
     * 开始刷新时被调用，这在个方法实现正在刷新时的动画
     */
    void startRefresh();


    /**
     * 刷新成功时，此方法被调用
     */
    void onSucceedRefresh();


    /**
     * 刷新失败时，此方法被调用
     */
    void onFailRefresh();


    /**
     * 发生拖拽时时此方法会被PullToRefreshLayout调用，可以通过这个偏移量和当前的状态来决定动画的样子
     *
     * @param offset 当头部不固定时表示位置偏移量，当头部内容固定时表示头部露出的高度，范围为下拉时从 0 到 MaxHeight,上升反之。
     */
    void onPositionChange(float offset);

}
