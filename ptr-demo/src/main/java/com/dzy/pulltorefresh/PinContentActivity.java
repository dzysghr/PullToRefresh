package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class PinContentActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLayout.mPinContent = true;
        mLayout.setHeaderView(new ArrowHeaderView(this));

    }
}
