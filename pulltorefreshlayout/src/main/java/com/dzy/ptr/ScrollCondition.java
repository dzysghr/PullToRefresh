package com.dzy.ptr;

/**
 *  判断是否还可以进行下拉，如果实现了这个接口则用这个接口决定，否则用默认的实现。
 *  调用逻辑参考 {@link PullToRefreshLayout#canChildScrollUp()}
 * Created by dzysg on 2016/4/21 0021.
 */
public interface ScrollCondition
{
    boolean canRefresh();
}
