package com.dzy.ptr;

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