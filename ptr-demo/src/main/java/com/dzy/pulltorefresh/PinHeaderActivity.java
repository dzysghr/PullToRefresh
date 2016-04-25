package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.adapter.DefaultRecycleViewAdapter;
import com.dzy.pulltorefresh.headerview.PinHeaderView;

public class PinHeaderActivity extends AppCompatActivity
{

    protected RecyclerView mRecyclerView;
    protected PullToRefreshLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_header);


        mRecyclerView = (RecyclerView) findViewById(R.id.review);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new DefaultRecycleViewAdapter(this,null));

        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);
        mLayout.setPinHeader(true);
        mLayout.setHeader(new PinHeaderView(this));



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
