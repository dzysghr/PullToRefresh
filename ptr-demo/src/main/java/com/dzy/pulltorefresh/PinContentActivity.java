package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.pulltorefresh.headerview.MaterialHeader;

public class PinContentActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLayout.setPinContent(true);
        mLayout.setHeader(new MaterialHeader(this));

    }
}
