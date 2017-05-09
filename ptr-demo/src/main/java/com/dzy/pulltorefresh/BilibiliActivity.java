package com.dzy.pulltorefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshListener;
import com.dzy.pulltorefresh.headerview.BilibiliHeader;

public class BilibiliActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilibili);
        final PullToRefreshLayout ptr = (PullToRefreshLayout) findViewById(R.id.ptrlayout);
        BilibiliHeader header = new BilibiliHeader(this);
        ptr.setHeader(header);

        ptr.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefreshStart() {
                ptr.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptr.succeedRefresh();
                    }
                },2000);
            }
        });

    }
}
