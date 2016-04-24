package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class ReleaseActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        mLayout.setHeader(new ArrowHeaderView(this));
        mLayout.mRefreshImmediately = false;

    }
}
