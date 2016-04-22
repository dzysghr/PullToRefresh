package com.dzy.pulltorefresh;

import android.os.Bundle;

public class DropWaterActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayout.mRefreshImmediately = true;
        mLayout.setHeaderView(new CircleHeaderView(this));
    }
}
