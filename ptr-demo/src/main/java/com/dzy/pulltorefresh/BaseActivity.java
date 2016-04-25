package com.dzy.pulltorefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.pulltorefresh.adapter.CommenAdapter;
import com.dzy.pulltorefresh.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity
{

    protected ListView mLv;
    protected PullToRefreshLayout mLayout;
    List<String> mList;
    CommenAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mLv = (ListView) findViewById(R.id.lv);
        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);


        mList = new ArrayList<String>();
        mList.add("");
        mList.add("");
        mList.add("");

        mAdapter = new CommenAdapter<String>(this, mList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder = ViewHolder.getViewHolder(position,convertView,parent,R.layout.list_item_layout,mContext);
                holder.setTextView(R.id.tvTitile,"title "+mDatas.get(position));
                return holder.getConvertView();
            }
        };
        mLv.setAdapter(mAdapter);
    }


    public void onOKClick(View v)
    {
        mLayout.succeedRefresh();

    }

    public void onFailClick(View v)
    {
        mLayout.failRefresh();
    }
}
