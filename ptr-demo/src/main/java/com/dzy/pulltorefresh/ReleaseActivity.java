package com.dzy.pulltorefresh;

import android.os.Bundle;

import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

public class ReleaseActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLayout.setHeader(new ArrowHeaderView(this));
        mLayout.setRefreshImmediately(false);

        mLayout.setRefreshLinstener(new RefreshLinstener() {
            @Override
            public void onRefreshStart()
            {
                LoadDatas();
            }
        });


    }

    public void LoadDatas()
    {
        mLayout.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                mList.clear();
                for(int i=0;i<20;i++)
                    mList.add(""+i);
                mAdapter.notifyDataSetChanged();
                mLayout.succeedRefresh();
            }
        },2000);
    }
}
