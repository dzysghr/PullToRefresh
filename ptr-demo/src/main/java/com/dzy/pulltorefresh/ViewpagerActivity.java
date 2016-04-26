package com.dzy.pulltorefresh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewpagerActivity extends AppCompatActivity
{
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);


        List<DefaultFragment> fragments = new ArrayList<DefaultFragment>();

        fragments.add(DefaultFragment.newInstance("one"));
        fragments.add(DefaultFragment.newInstance("two"));
        fragments.add(DefaultFragment.newInstance("three"));

        DefaultPageAdapter adapter = new DefaultPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

    }




    public static class DefaultPageAdapter extends FragmentPagerAdapter
    {

        List<DefaultFragment> mFragments;

        public DefaultPageAdapter(FragmentManager fm,List<DefaultFragment> fragments)
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
