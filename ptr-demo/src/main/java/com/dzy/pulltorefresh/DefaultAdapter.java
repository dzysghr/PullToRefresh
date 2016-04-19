package com.dzy.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DefaultAdapter extends RecyclerView.Adapter<DefaultAdapter.ToolbarViewHolder> {


    private Context mContext;
    private List<String> mDatas;
    private LayoutInflater mInflater;

    public DefaultAdapter(Context context, List<String> list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatas = list;

    }

    @Override
    public ToolbarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_layout, parent, false);

        ToolbarViewHolder myViewHolder = new ToolbarViewHolder(view);
        myViewHolder.title = (TextView) view.findViewById(R.id.tvTitile);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ToolbarViewHolder holder, int position) {
        holder.title.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ToolbarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;


        public ToolbarViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            Log.d("tag","position "+getAdapterPosition());
        }
    }

}
