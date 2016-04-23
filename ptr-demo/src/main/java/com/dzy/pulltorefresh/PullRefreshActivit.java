package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class PullRefreshActivit extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLayout.setHeaderView(new ArrowHeaderView(this));
        mLayout.mRefreshImmediately = true;
    }
}
