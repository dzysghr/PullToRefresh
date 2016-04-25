package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.pulltorefresh.headerview.MaterialHeader;

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
        mLayout.setPinContent(true);
        mLayout.setHeader(new MaterialHeader(this));


        List<BlankFragment> fragments = new ArrayList<BlankFragment>();

        fragments.add(BlankFragment.newInstance("one"));
        fragments.add(BlankFragment.newInstance("two"));
        fragments.add(BlankFragment.newInstance("three"));

        DefaultPageAdapter adapter = new DefaultPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
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
