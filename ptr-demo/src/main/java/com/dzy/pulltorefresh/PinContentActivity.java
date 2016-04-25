package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.headerview.MaterialHeader;

public class PinContentActivity extends AppCompatActivity
{

    PullToRefreshLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_content);

        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);

        mLayout.setPinContent(true);
        mLayout.setHeader(new MaterialHeader(this));

        mLayout.setRefreshLinstener(new RefreshLinstener()
        {
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
