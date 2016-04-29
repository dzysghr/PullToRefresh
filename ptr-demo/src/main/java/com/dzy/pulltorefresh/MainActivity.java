package com.dzy.pulltorefresh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dzy.pulltorefresh.adapter.CommenAdapter;
import com.dzy.pulltorefresh.adapter.ViewHolder;

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
        list.add("下拉松手刷新+刷新时不可拉动");
        list.add("超过刷新线刷新");
        list.add("水滴下拉");
        list.add("头部固定");
        list.add("内容固定-MaterialStyle");
        list.add("刷新完成强制返回");
        list.add("刷新时隐藏头部");
        list.add("viewPager共存");
        list.add("自动刷新+ScrollView");


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
                else if (position==2)
                    startActivity(new Intent(MainActivity.this,DropWaterActivity.class));
                else if (position==3)
                    startActivity(new Intent(MainActivity.this,PinHeaderActivity.class));
                else if (position==4)
                    startActivity(new Intent(MainActivity.this,PinContentActivity.class));
                else if (position==5)
                    startActivity(new Intent(MainActivity.this,ForceTopActivity.class));
                else if (position==6)
                    startActivity(new Intent(MainActivity.this,HideHeaderActivity.class));
                else if (position==7)
                    startActivity(new Intent(MainActivity.this,ViewpagerActivity.class));
                else if (position==8)
                    startActivity(new Intent(MainActivity.this,AutoRefreshActivity.class));

            }
        });

    }


}
