package com.dzy.pulltorefresh.headerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.ptr.HeaderController;
import com.dzy.ptr.HeaderState;
import com.dzy.ptr.PullToRefreshLayout;
import com.dzy.pulltorefresh.R;

/**
 *  这个是固定头部的例子
 * Created by dzysg on 2016/4/23 0023.
 */
public class PinHeaderView extends FrameLayout implements HeaderController
{

    TextView mTV;
    ProgressBar mPb;
    ImageView mImageView;

    private RotateAnimation mArrowUpAnim;
    private RotateAnimation mArrowDownAnim;
    private int mLastState = HeaderState.drag;



    public PinHeaderView(Context context)
    {
        super(context);
        init(context);
    }

    public void init(Context context)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.pin_headerlayout,this);
        mTV = (TextView) v.findViewById(R.id.tv);
        mPb = (ProgressBar) v.findViewById(R.id.pb);
        mImageView = (ImageView) v.findViewById(R.id.iv_arrow);

        mPb.setVisibility(View.INVISIBLE);

        mArrowUpAnim = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mArrowUpAnim.setInterpolator(new LinearInterpolator());
        mArrowUpAnim.setDuration(300);
        mArrowUpAnim.setFillAfter(true);

        mArrowDownAnim = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mArrowDownAnim.setInterpolator(new LinearInterpolator());
        mArrowDownAnim.setDuration(300);
        mArrowDownAnim.setFillAfter(true);
    }


    @Override
    public int getThresholdHeight()
    {
        return (int) getContext().getResources().getDisplayMetrics().density * 100;
    }

    @Override
    public int getRefreshingHeight()
    {
        return (int) getContext().getResources().getDisplayMetrics().density * 80;
    }

    @Override
    public void StateChange(int state)
    {
        if (state==HeaderState.over)
        {
            mImageView.startAnimation(mArrowUpAnim);
            mTV.setText("松开刷新");
        }
        else if (state == HeaderState.release||state==HeaderState.refreshing)
        {
            mImageView.clearAnimation();
            mImageView.setVisibility(INVISIBLE);
            mPb.setVisibility(VISIBLE);
            mTV.setVisibility(INVISIBLE);

        }
        else if (state==HeaderState.finish)
        {
            mPb.setVisibility(INVISIBLE);
            mTV.setVisibility(VISIBLE);
            mTV.setText("刷新成功");
        }
        else if(state==HeaderState.fail)
        {
            mPb.setVisibility(INVISIBLE);
            mTV.setVisibility(VISIBLE);
            mTV.setText("刷新失败");
        }
        else if (state==HeaderState.hide)
        {
            mImageView.setVisibility(VISIBLE);
            mArrowUpAnim.reset();
        }

        else//drag
        {
            if (mLastState==HeaderState.over)
            {
                mImageView.clearAnimation();
                mImageView.startAnimation(mArrowDownAnim);
            }
            mTV.setText("下拉刷新");
            mPb.setVisibility(INVISIBLE);

        }
        mLastState = state;
    }

    @Override
    public void startRefresh()
    {

    }

    @Override
    public void onSucceedRefresh()
    {

    }

    @Override
    public void onFailRefresh()
    {

    }

    @Override
    public void onPositionChange(float offset)
    {

    }

    @Override
    public void attachLayout(PullToRefreshLayout layout)
    {

    }
}
