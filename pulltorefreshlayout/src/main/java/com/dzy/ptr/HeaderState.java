package com.dzy.ptr;

public class HeaderState
{
    public static final int  drag = 0; //正在下拉,且高度处于刷新线之前
    public static final int over = 1; //下拉超过刷新线
    public static final int release = 2; //从超过刷新线返回到刷新线
    public static final int refreshing = 3;//正在刷新线
    public static final int finish = 4;//刷新完成-正在返回顶部
    public static final int fail = 5;//刷新失败-正在返回顶部
    public static final int  hide = 6; //返回顶部，完全隐藏
}