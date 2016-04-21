package com.dzy.pulltorefresh;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ViewHolder {
    private SparseArray<View> mViews;

    private int mPosition;
    private View mConvertView;

    public ViewHolder(int position, ViewGroup parent,@LayoutRes int layoutId, Context context) {

        mPosition = position;
        mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(int position, View convertView, ViewGroup parent, int layoutId, Context context) {

        if (convertView == null) {
            return new ViewHolder(position, parent, layoutId, context);
        } else {
            return (ViewHolder) convertView.getTag();
        }

    }

    public View getConvertView() {
        return mConvertView;
    }


    /**
     * 根据控件id从item的layout获得控件对象
     *
     * @param viewid 控件id
     * @return 控件对象
     */
    public <T extends View> T getView(@IdRes int viewid) {
        View v = mViews.get(viewid);

        if (v == null) {
            v = mConvertView.findViewById(viewid);
            mViews.put(viewid, v);
        }

        return (T) v;
    }


    /**
     * 为TextView控件设置text属性
     *
     * @param id   控件id
     * @param text text内容
     */
    public void setTextView(@IdRes int id, String text) {
        TextView tv = getView(id);

        tv.setText(text);
    }

    public void setProgress(@IdRes int id, int progress) {
        ProgressBar pb = getView(id);
        pb.setProgress(progress);
    }


}
