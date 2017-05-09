package com.dzy.pulltorefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshListener;
import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class HideHeaderActivity extends AppCompatActivity
{

    PullToRefreshLayout mLayout;
    TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_header);
        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);
        mLayout.setHeader(new ArrowHeaderView(this));
        mLayout.setHideWhenRefresh(true);
        mLayout.setRefreshImmediately(true);
        mLayout.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefreshStart()
            {
                mTv.setText("Refreshing... ");
                mLayout.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        mTv.setText("finished refresh");
                        mLayout.succeedRefresh();
                    }
                },2000);

            }
        });

        mTv = (TextView) findViewById(R.id.tv);



    }
}
