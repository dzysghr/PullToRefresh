package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dzy.ptr.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    RecyclerView mRecyclerView;
    PullToRefreshLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.review);
        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);


        //mLayout.setHeaderView(new CircleHeaderView(this));
        mLayout.setHeaderView(new HeaderView(this));


        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 40; i++) {
            list.add(i+"");
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new DefaultAdapter(this, list));

    }



    public void onOKClick(View v)
    {
        mLayout.finishRefresh();

    }

    public void onFailClick(View v)
    {
        mLayout.failRefresh();
    }


}
