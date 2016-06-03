package com.dzy.pulltorefresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SwipeLaoutActivity extends AppCompatActivity
{

    SwipeRefreshLayout mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_laout);
        mLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

    }
}
