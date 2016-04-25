package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class ForceTopActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLayout.setHeader(new ArrowHeaderView(this));
        mLayout.mRefreshImmediately = true;
        mLayout.mForceToTopWhenFinish = true;
        mLayout.setRefreshLinstener(new RefreshLinstener() {
            @Override
            public void onRefreshStart()
            {
                mLayout.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mLayout.succeedRefresh();

                    }
                }, 2000);
            }
        });
    }
}
