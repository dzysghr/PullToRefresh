package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class AutoRefreshActivity extends AppCompatActivity
{

    PullToRefreshLayout mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_refresh);
        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);
        mLayout.setHeader(new ArrowHeaderView(this));

        mLayout.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                mLayout.autoRefresh();
            }
        },500);


        mLayout.setRefreshLinstener(new RefreshLinstener() {
            @Override
            public void onRefreshStart()
            {
                mLayout.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        mLayout.succeedRefresh();
                    }
                },3000);
            }
        });
    }
}
