package com.dzy.pulltorefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dzy.ptr.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity
{

    protected ListView mLv;
    protected PullToRefreshLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mLv = (ListView) findViewById(R.id.lv);
        mLayout = (PullToRefreshLayout) findViewById(R.id.ptrlayout);


        mLayout.setHeaderView(new HeaderView(this));


        List<String> list = new ArrayList<String>();
        for(int i=0;i<20;i++)
            list.add(""+i);


        mLv.setAdapter(new CommenAdapter<String>(this,list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder = ViewHolder.getViewHolder(position,convertView,parent,R.layout.list_item_layout,mContext);
                holder.setTextView(R.id.tvTitile,"title "+mDatas.get(position));
                return holder.getConvertView();
            }
        });
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
