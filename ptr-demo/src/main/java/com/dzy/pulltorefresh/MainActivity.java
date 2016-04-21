package com.dzy.pulltorefresh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    ListView mLv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLv = (ListView) findViewById(R.id.lv);


        List<String> list = new ArrayList<String>();
        list.add("下拉松手刷新");
        list.add("超过刷新线刷新");


        mLv.setAdapter(new CommenAdapter<String>(this, list)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder = ViewHolder.getViewHolder(position, convertView, parent, R.layout.list_item_layout, mContext);
                holder.setTextView(R.id.tvTitile, mDatas.get(position));
                return holder.getConvertView();
            }
        });

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (position==0)
                    startActivity(new Intent(MainActivity.this,ReleaseActivity.class));
                else if (position==1)
                    startActivity(new Intent(MainActivity.this,PullRefreshActivit.class));
            }
        });

    }


}
