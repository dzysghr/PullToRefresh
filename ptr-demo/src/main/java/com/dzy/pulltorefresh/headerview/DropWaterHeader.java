package com.dzy.pulltorefresh.headerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.ptr.HeaderController;
import com.dzy.ptr.HeaderState;
import com.dzy.ptr.PullToRefreshLayout;

/**
 * 这个水滴刷新，效果不是很完美，代码极度混乱，待重写
 * Created by dzysg on 2016/4/17 0017.
 */
public class DropWaterHeader extends FrameLayout implements HeaderController
{

    int BigRadius = 30;
    float BigCircleMargin = 20;

    float curBigRadius;
    float curSmallRadius;
    float curBigY;
    float curSmallY;

    Paint p;
    HeaderState mHeaderState;
    ProgressBar mPb;
    TextView mTv;

    public DropWaterHeader(Context context)
    {
        super(context);
        setWillNotDraw(false);
        init(context);
    }


    public void init(Context context)
    {
        p = new Paint();
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);
        mPb = new ProgressBar(getContext());
        LayoutParams params = new LayoutParams(60, 60);
        params.bottomMargin = 10;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        mTv = new TextView(getContext());
        LayoutParams textparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textparams.bottomMargin = 10;
        textparams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mTv.setTextSize(12);

        addView(mPb, params);
        addView(mTv, textparams);


        mTv.setVisibility(View.INVISIBLE);
        mPb.setVisibility(View.INVISIBLE);
    }



    public int getMaxHeight()
    {
        return 200;
    }

    @Override
    public int getThresholdHeight()
    {
        return 180;
    }

    @Override
    public int getRefreshingHeight()
    {
        return (int) (2 * (BigRadius + BigCircleMargin));
    }


    @Override
    public void StateChange(HeaderState state)
    {
        mHeaderState = state;
        if (mHeaderState == HeaderState.refreshing)
        {
            mPb.setVisibility(View.VISIBLE);
        }
        Log.d("state", "statechange  " + mHeaderState.toString());

        if (mHeaderState == HeaderState.hide)
        {
            mPb.setVisibility(View.INVISIBLE);
            mTv.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void startRefresh()
    {

    }

    @Override
    public void onSucceedRefresh()
    {
        mTv.setVisibility(View.VISIBLE);
        mTv.setText("刷新成功");
        mPb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailRefresh()
    {
        mTv.setVisibility(View.VISIBLE);
        mTv.setText("刷新失败");
        mPb.setVisibility(View.INVISIBLE);
    }


    private boolean isAnimatingBackToRefresh = false;
    private static int BigCirlceDown = 0;
    private static int SmallCicleDown = 1;
    private static int backToRefresh = 2;
    int mState = 0;

    float mOffset;

    @Override
    public void onPositionChange(float offset)
    {
        Log.i("tag", "offset " + offset);

        mOffset = offset;
        if (mOffset <= 2 * (BigRadius + BigCircleMargin))
            mState = 0;
        else
            mState = 1;


        invalidate();
    }

    @Override
    public void attachLayout(PullToRefreshLayout layout)
    {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widht, getMaxHeight());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (mHeaderState == HeaderState.drag)
        {
            if (mState == 0)
                drawMoveBig(canvas);
            else if (mState == 1)
            {
                drawBig(canvas);
                drawDragSmall(canvas);
                drawLine(canvas);
            }
        }

    }


    private void drawLine(Canvas canvas)
    {

        Path path = new Path();
        path.moveTo(getMeasuredWidth() / 2 - curBigRadius, curBigY);
        path.lineTo(getMeasuredWidth() / 2 + curBigRadius, curBigY);
        path.quadTo(getMeasuredWidth() / 2 + curSmallRadius - 2, (curSmallY + curBigY) / 2, getMeasuredWidth() / 2 + curSmallRadius, curSmallY);
        path.lineTo(getMeasuredWidth() / 2 - curSmallRadius, curSmallY);
        path.quadTo(getMeasuredWidth() / 2 - curSmallRadius + 2, (curSmallY + curBigY) / 2, getMeasuredWidth() / 2 - curBigRadius, curBigY);

        canvas.drawPath(path, p);
    }

    private void drawMoveBig(Canvas canvas)
    {
        canvas.drawCircle(getMeasuredWidth() / 2, getMaxHeight() - BigCircleMargin - BigRadius, BigRadius, p);
    }

    private void drawDragSmall(Canvas canvas)
    {
        float offsetfrom = 2 * (BigRadius + BigCircleMargin);
        float offsetto = getThresholdHeight();

        curSmallRadius = BigRadius - 20 * (mOffset - offsetfrom) / (offsetto - offsetfrom);
        curSmallY = getMaxHeight() - BigCircleMargin - curSmallRadius;
        drawSmall(canvas);
    }

    private void drawBig(Canvas canvas)
    {


        float offsetfrom = 2 * (BigRadius + BigCircleMargin);
        float offsetto = getThresholdHeight();

        curBigRadius = BigRadius - 5 * (mOffset - offsetfrom) / (offsetto - offsetfrom);
        float cx = getMeasuredWidth() / 2;
        curBigY = getMaxHeight() - mOffset + BigCircleMargin + BigRadius;

        canvas.drawCircle(cx, curBigY, curBigRadius, p);

    }


    private void drawSmall(Canvas canvas)
    {
        canvas.drawCircle(getMeasuredWidth() / 2, curSmallY, curSmallRadius, p);
    }


}
