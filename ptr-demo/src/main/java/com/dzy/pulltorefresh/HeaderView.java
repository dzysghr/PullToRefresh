package com.dzy.pulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.ptr.BaseHeaderView;

/**
 *
 * Created by dzysg on 2016/4/16 0016.
 */
public class HeaderView extends BaseHeaderView
{

    TextView mTV;
    ProgressBar mPb;
    ImageView mImageView;


    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private HeaderState mLastState = HeaderState.drag;
    public HeaderView(Context context)
    {
        super(context);
    }



    @Override
    public void init(Context context)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.headerlayout,this);
        mTV = (TextView) v.findViewById(R.id.tv);
        mPb = (ProgressBar) v.findViewById(R.id.pb);
        mImageView = (ImageView) v.findViewById(R.id.iv_arrow);
        mFlipAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(300);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(300);
        mReverseFlipAnimation.setFillAfter(true);
    }


    @Override
    public int getMaxHeight()
    {
        return (int) getContext().getResources().getDisplayMetrics().density * 120;
    }

    @Override
    public int getThresholdHeight()
    {
        return (int) getContext().getResources().getDisplayMetrics().density * 100;
    }

    @Override
    public int getRefreshingHeight()
    {
        return (int) getContext().getResources().getDisplayMetrics().density * 60;
    }

    @Override
    public void StateChange(HeaderState state)
    {
        if (state==HeaderState.over)
        {
            mImageView.startAnimation(mFlipAnimation);
            mTV.setText("松开刷新");
        }
        else if (state == HeaderState.release||state==HeaderState.refreshing)
        {
            mImageView.clearAnimation();
            mImageView.setVisibility(GONE);
            mPb.setVisibility(VISIBLE);
            mTV.setVisibility(GONE);

        }
        else if (state==HeaderState.finish)
        {
            mPb.setVisibility(GONE);
            mTV.setVisibility(VISIBLE);
            mTV.setText("刷新成功");
        }
        else if(state==HeaderState.fail)
        {
            mPb.setVisibility(GONE);
            mTV.setVisibility(VISIBLE);
            mTV.setText("刷新失败");
        }
        else if (state==HeaderState.hide)
        {
            mImageView.setVisibility(VISIBLE);
            mFlipAnimation.reset();
        }

        else//drag
        {
            if (mLastState==HeaderState.over)
            {
                mImageView.clearAnimation();
                mImageView.startAnimation(mReverseFlipAnimation);
            }
            mTV.setText("下拉刷新");
            mPb.setVisibility(GONE);

        }
        mLastState = state;
        Log.d("tag", state.toString());
        //requestLayout();
    }

    @Override
    public void startRefresh()
    {

    }

    @Override
    public void onFinishRefresh()
    {
        notifyUpToTop();
    }

    @Override
    public void onFailRefresh()
    {
        notifyUpToTop();
    }

    @Override
    public void onPositionChange(float offset)
    {
        //requestLayout();
        //Log.d("tag", "offset " + offset);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Log.i("tag", "ondraw");
        canvas.drawColor(0xFFFFa112);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        Log.i("tag", "onLayout");
    }

}
