package com.dzy.pulltorefresh;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.ptr.RefreshLinstener;
import com.dzy.pulltorefresh.adapter.DefaultRecycleViewAdapter;
import com.dzy.pulltorefresh.headerview.MaterialHeader;


public class DefaultFragment extends Fragment
{

    private static final String ARG_PARAM1 = "param1";
    RecyclerView mRecyclerView;
    private PullToRefreshLayout mLayout;

    public DefaultFragment()
    {

    }


    public static DefaultFragment newInstance(String param1)
    {
        DefaultFragment fragment = new DefaultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view =inflater.inflate(R.layout.fragment_blank, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.review);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new DefaultRecycleViewAdapter(getContext(),null));

        mLayout = (PullToRefreshLayout) view.findViewById(R.id.ptrlayout);

        mLayout.setHeader(new MaterialHeader(getContext()));
        mLayout.setPinContent(true);
        mLayout.setHasHorizontalChild(true);
        mLayout.setCanScrollWhenRefreshing(false);

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
                },3000);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }



}
