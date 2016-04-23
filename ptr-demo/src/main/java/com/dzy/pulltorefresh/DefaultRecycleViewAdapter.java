package com.dzy.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by dzysg on 2016/4/22 0022.
 */
public class DefaultRecycleViewAdapter extends RecyclerView.Adapter<DefaultRecycleViewAdapter.Holder>
{

    List<String> mDatas;
    Context mContext;

    public DefaultRecycleViewAdapter(Context context, List<String> list)
    {
        mContext = context;
        mDatas = list;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 20;
    }

    public static class Holder extends RecyclerView.ViewHolder
    {

        public Holder(View itemView)
        {
            super(itemView);
        }
    }
}
