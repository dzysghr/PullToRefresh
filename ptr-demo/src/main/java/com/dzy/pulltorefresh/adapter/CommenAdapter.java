package com.dzy.pulltorefresh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


public abstract class CommenAdapter<T> extends BaseAdapter
{
	protected List<T> mDatas;
	protected Context mContext;
	protected LayoutInflater mInflater;
	
	
	
	public CommenAdapter(Context context,List<T> list)
	{
		mContext = context;
		mDatas = list;
		mInflater = LayoutInflater.from(context);
	}



	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	

	

}
