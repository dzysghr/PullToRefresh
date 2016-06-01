# PullToRefresh
通用的可自定义头部下拉刷新布局，只实现下拉逻辑，不实现下拉头部视觉效果，功能设计参考[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)

# 主要功能特性
 
 * 下拉松手刷新
 * 下拉刷新
 * 头部固定
 * 内容固定
 * 刷新时是否可拖动头部
 * 刷新完成是否强制返回
 * 刷新时是否隐藏头部
 * ViewPager等横向滑动控件共存
 * 自动刷新

# Demo
 * 下拉松手刷新
 
![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E4%B8%8B%E6%8B%89%E9%87%8A%E6%94%BE%E5%88%B7%E6%96%B0_clip.gif)
 
* 超过刷新线刷新

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E8%B6%85%E8%BF%87%E5%88%B7%E6%96%B0%E7%BA%BF%E5%88%B7%E6%96%B0_clip.gif)

* 头部固定

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E5%A4%B4%E9%83%A8%E5%9B%BA%E5%AE%9A_clip.gif)

* 内容固定

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E5%86%85%E5%AE%B9%E5%9B%BA%E5%AE%9A_clip.gif)

* 刷新时隐藏头部

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E9%9A%90%E8%97%8F%E5%A4%B4%E9%83%A8_clip.gif)

* viewpager等横向滑动兼容

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E6%A8%AA%E5%90%91_clip.gif)

* 刷新完成强制返回

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E5%BC%BA%E5%88%B6%E8%BF%94%E5%9B%9E_clip.gif)

* 自动刷新

![](https://github.com/dzysghr/PullToRefresh/raw/master/gif/%E8%87%AA%E5%8A%A8%E5%88%B7%E6%96%B0_clip.gif)

 
# 功能配置

* 释放刷新or下拉刷新(默认为`false`，释放刷新)

```
mPullToRefreshLayout.setRefreshImmediately(boolean b);
```

* 头部固定(默认为`false`)
```
mPullToRefreshLayout.setPinHeader(true);
```

* 内容固定(默认为`false`,建议同时关闭`setCanScrollWhenRefreshing`)
```
mPullToRefreshLayout.setPinContent(true);
```

* 刷新完成强制返回(默认为`false`)
```
mPullToRefreshLayout.setForceToTopWhenFinish(true);
```

* 刷新时是否可拖动头部(默认为`true`)
```
mPullToRefreshLayout.setCanScrollWhenRefreshing(false);
```

* 开启横向滑动处理(默认为`false`，不处理横向逻辑)

```
mPullToRefreshLayout.setHasHorizontalChild(true);
```


* 上升动画时间（默认为`500ms`）

```
mPullToRefreshLayout.setAnimDuration(int animDuration)
```

* 滑动阻尼，默认为`2`

```
mPullToRefreshLayout.setResistance(float resistance)
```

# 自定义头部

1. 继承View or ViewGroup ，并实现HeaderController接口，详见demo
```
public interface HeaderController
{

    /**
     * 触发刷新的下拉高度,单位px,小于等于你的header高度
     * @return 触发刷新的下拉高度
     */
    int getThresholdHeight();

    /**
     * 正在刷新时的高度,单位px，小于等于你的header高度
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
     * @param offset 当头部不固定时表示位置偏移量，当头部内容固定时表示头部露出的高度
     *               范围为下拉时从 0 到 header.getMeasureHeight,上升反之。
    void onPositionChange(float offset);

    /** 当 Controller被设置到PullToRefreshLayout时调用
     * @param layout 关联的PullToRefreshLayout
     */
    void attachLayout(PullToRefreshLayout layout);

}
```

2. 为PullToRefhreshLayout设置头部
```
mLayout.setHeader(new CustomHeader(context));
```
# 注意事项

* PullToRefreshLayout只能有一个直接子View，
* 如果子view是一个可以scroll的控件（比如ListView、RecycleView、ScrollView）,PullToRefreshLayout可以直接判断子view是否已经滑到顶部，但当子view为一个XXLayout内部包含一个ListView时


```
    PullToRefreshLayout
         FrameLauout
             ListView
 ```
 PullToRefreshLayout是无法判断Listview是否滑到顶的，此时需要自己实现何时应该开启下拉
 ```
 mPullToRefreshLayout.setScrollableListener(new ScrollCondition() {
            @Override
            public boolean canRefresh()
            {
                if(canscroll)
                    return false;
                return true;
            }
        });
 ```
