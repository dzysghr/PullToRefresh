package com.dzy.pulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.dzy.ptr.BaseHeaderView;

/**
 *
 * Created by dzysg on 2016/4/17 0017.
 */
public class CircleHeaderView extends BaseHeaderView
{

    int BigRadius = 30;
    Paint p;
    Paint grean;

    public CircleHeaderView(Context context)
    {
        super(context);
    }




    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        p = new Paint();
        grean = new Paint();


        p.setColor(Color.GRAY);
        grean.setColor(Color.GREEN);

        //setBackgroundResource(R.drawable.beizhu);
    }


    @Override
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
        return 180;
    }


    @Override
    public void StateChange(HeaderState state)
    {

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



    int circleDown = 1;

    int state = 1;
    float mOffset;
    @Override
    public void onOffset(float offset)
    {
        Log.i("tag", "offset " + offset);
        mOffset = offset;
        if (offset<80)
            state = 1;
        else
            state=2;

        requestLayout();


    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Log.i("tag", "CircleHeaderView ondraw");
        grean.setColor(Color.GREEN);
        canvas.drawRect(0, 0, getMeasuredWidth(),getMaxHeight()- getThresholdHeight(), grean);

        grean.setColor(Color.CYAN);
        canvas.drawRect(0,getMaxHeight()- getThresholdHeight(),getMeasuredWidth(),getMeasuredHeight(),grean);

        drawBig(canvas);
        drawSamll(canvas);
    }

    private void drawBig(Canvas canvas)
    {
        if (state==1)
            canvas.drawCircle(getMeasuredWidth() / 2, 200 - BigRadius-10, BigRadius, p);
        else if (state==2)
        {
            float r = (80-mOffset)/120*10;
            canvas.drawCircle(getMeasuredWidth() / 2,200-mOffset+10+BigRadius, BigRadius+r, p);
        }
    }


    private void drawSamll(Canvas canvas)
    {
        if (state==2)
        {
            float r = (80-mOffset)/120*18;
            canvas.drawCircle(getMeasuredWidth() / 2,200-10-BigRadius, BigRadius+r, p);
        }
    }

}
