package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.headerview.ArrowHeaderView;

import java.util.ArrayList;
import java.util.List;

public class ViewpagerActivity extends AppCompatActivity
{
    ViewPager mViewPager;
    PullToRefreshLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);
        //mLayout.setPinContent(true);
        mLayout.setHeader(new ArrowHeaderView(this));

        mLayout.setHasHorizentalChild(true);
        mLayout.setCanScrollWhenRefreshing(false);
        //mLayout.setForceToTopWhenFinish(true);


        List<BlankFragment> fragments = new ArrayList<BlankFragment>();

        fragments.add(BlankFragment.newInstance("one"));
        fragments.add(BlankFragment.newInstance("two"));
        fragments.add(BlankFragment.newInstance("three"));

        DefaultPageAdapter adapter = new DefaultPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

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
                },2000);
            }
        });

    }




    public static class DefaultPageAdapter extends FragmentPagerAdapter
    {

        List<BlankFragment> mFragments;

        public DefaultPageAdapter(FragmentManager fm,List<BlankFragment> fragments)
        {
            super(fm);
            mFragments = fragments;
        }
        @Override
        public Fragment getItem(int position)
        {
            return mFragments.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }
    }
}
