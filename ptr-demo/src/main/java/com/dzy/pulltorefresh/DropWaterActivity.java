package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.pulltorefresh.headerview.DropWaterHeader;

public class DropWaterActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayout.setRefreshImmediately(true);
        mLayout.setHeader(new DropWaterHeader(this));
    }
}
