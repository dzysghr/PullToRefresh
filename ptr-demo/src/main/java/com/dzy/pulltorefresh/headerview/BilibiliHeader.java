package com.dzy.pulltorefresh.headerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dzy.ptr.HeaderController;
import com.dzy.ptr.HeaderState;
import com.dzy.ptr.PullToRefreshLayout;

/**
 * bilibili效果
 * Created by dzysg on 2016/7/22 0022.
 */
public class BilibiliHeader extends View implements HeaderController {


    private float mDuration = 1600; //波浪生命
    private boolean mIsRunning = false;

    private int mSmallSpeed = 400; //触角摆动周期
    private Paint mStickPaint;
    private Paint mRetPaint;
    private Paint mWhitePaint;
    private Paint mBottomTextPaint;
    private Paint mTopTextPaint;
    private float mStickHigh = 100; //触角高
    private float mStickWidth = 10; //触角宽
    private float mShakeAngle = 40; //触角摆动角度
    private float mRetOffset = 20; // 触角离中心点距离
    private Retangle mFistRet;
    private Retangle mSecondRet;

    private int mRefreshHeight;
    private int mThresholdHeight;


    double w = Math.PI / mSmallSpeed;//触角摆一次周期为mSmallSpeed（400），触角角度 =最大角度*sin(wt)，使当t = 200时，角度达到最大值
    float mRightX;
    float mRightY;
    float mLeftX;
    float mLeftY;


    public BilibiliHeader(Context context)
    {
        this(context, null);
    }

    public BilibiliHeader(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BilibiliHeader(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BilibiliHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        mStickPaint = new Paint();
        mStickPaint.setColor(Color.WHITE);
        mStickPaint.setAntiAlias(true);


        //波浪笔
        mRetPaint = new Paint();
        mRetPaint.setColor(Color.WHITE);
        mRetPaint.setStyle(Paint.Style.STROKE);
        mRetPaint.setStrokeWidth(4);

        //下方白色块笔
        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setStyle(Paint.Style.FILL);

        //下方字体笔
        mBottomTextPaint = new Paint();
        mBottomTextPaint.setColor(0xFF6E6E6E);
        mBottomTextPaint.setStyle(Paint.Style.FILL);
        mBottomTextPaint.setTextSize(26);
        mBottomTextPaint.setAntiAlias(true);
        mBottomTextPaint.setTextAlign(Paint.Align.CENTER);

        //上方字体笔
        mTopTextPaint = new Paint();
        mTopTextPaint.setColor(0xAAFFFFFF);
        mTopTextPaint.setStyle(Paint.Style.FILL);
        mTopTextPaint.setTextSize(26);
        mTopTextPaint.setAntiAlias(true);
        mTopTextPaint.setTextAlign(Paint.Align.CENTER);


    }

    private Runnable mUpdate = new Runnable() {
        @Override
        public void run()
        {
            if (!mIsRunning)
                return;
            long l = System.currentTimeMillis();

            //如果波浪生命到期，重置
            if (l - mFistRet.mCreateTime > mDuration)
                mFistRet.reset(l);
            if (l - mSecondRet.mCreateTime > mDuration)
                mSecondRet.reset(l);
            invalidate();
            postDelayed(mUpdate, 10);
        }
    };


    //开始触角动画
    public void start()
    {
        mIsRunning = true;
        mFistRet = new Retangle();
        mSecondRet = new Retangle(mFistRet.mCreateTime + mSmallSpeed);//第二个波浪比第一个晚400
        mUpdate.run();
    }

    //停止触角动画
    public void Stop()
    {
        mIsRunning = false;
    }


    private void initPoint()
    {
        // mRightX，mRightY 指向右触角的几何中心，用于触角旋转
        mRightX = getMeasuredWidth() / 2 + mStickWidth / 2 + mRetOffset;
        mRightY = getMeasuredHeight() / 2;

        //同上
        mLeftX = getMeasuredWidth() / 2 - mStickWidth / 2 - mRetOffset;
        mLeftY = mRightY;
    }

    private int dp2px(int dpValue)
    {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), dp2px(180));
        mRefreshHeight = dp2px(60);
        mThresholdHeight = dp2px(120);
        initPoint();
        if (mWhileRect == null)
            mWhileRect = new RectF(0, getMeasuredHeight() - mRefreshHeight, getMeasuredWidth(), getMeasuredHeight() + 20);
    }


    //下方白色矩形
    RectF mWhileRect;

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Log.e("tag", "ondraw");
        //画背景
        canvas.drawColor(0xFFFF4F81);

        //画下面白色
        canvas.drawRoundRect(mWhileRect, 15, 15, mWhitePaint);


        //如果超过刷新,画波浪动画
        if (mStates == HeaderState.over)
        {
            drawRoundRect(canvas, mFistRet);
            drawRoundRect(canvas, mSecondRet);
            drawShakeStick(canvas, true);
        }


        //如果正在下拉
        if (mStates == HeaderState.drag)
        {
            //画上方字体
            if (mOffset > mRefreshHeight + 40)//40 表示白色上方露出40的红色时才画
            {
                float pecent = (1 - (mThresholdHeight - mOffset) / (mThresholdHeight - mRefreshHeight));
                mTopTextPaint.setAlpha((int) (180 * pecent));
                canvas.drawText(" 再用力点！", getMeasuredWidth() / 2, getMeasuredHeight() - mThresholdHeight + 50, mTopTextPaint);
            }

            //画两个触角
            drawShakeStick(canvas, false);
        }

        //如果正在刷新,画下方字体
        if (mStates == HeaderState.release || mStates == HeaderState.refreshing)
        {
            canvas.drawText("正在更新", getMeasuredWidth() / 2, getMeasuredHeight() - mRefreshHeight + 50, mBottomTextPaint);
        }
        //如果刷新完成，画下方字体
        if (mStates == HeaderState.finish)
        {
            canvas.drawText("更新完成", getMeasuredWidth() / 2, getMeasuredHeight() - mRefreshHeight + 50, mBottomTextPaint);
        }

        //如果超过触发刷新线，画上方字体
        if (mStates == HeaderState.over)
        {
            mTopTextPaint.setAlpha(180);
            canvas.drawText("松手加载", getMeasuredWidth() / 2, getMeasuredHeight() - mOffset + 50, mTopTextPaint);
        }
    }


    //画波浪
    private void drawRoundRect(Canvas canvas, Retangle retangle)
    {
        if (retangle.getDiff() < mDuration)
        {
            int left = (getMeasuredWidth() - retangle.getCurrentWidth()) / 2;
            int top = (getMeasuredHeight() - retangle.getCurrentHigh()) / 2;
            int right = left + retangle.getCurrentWidth();
            int bottom = top + retangle.getCurrentHigh();
            RectF rectf = new RectF(left, top, right, bottom);
            mRetPaint.setAlpha(retangle.getAlpha());
            canvas.drawRoundRect(rectf, 50, 50, mRetPaint);
        }
    }

    //画下方摆动的触角
    private void drawShakeStick(Canvas canvas, boolean isShake)
    {

        float rotate = 0;

        //计算触角的角度
        //如果是摆动状态的触角，则根据波浪的生命来计算
        if (isShake)
        {
            float diff = mFistRet.getDiff();
            if (diff > mSmallSpeed)//触角摆动一次周期为波浪生命前mSmallSpeed ms,如果时间超过了mSmallSpeed,则观察下一个波浪
            {
                diff = mSecondRet.getDiff();
                if (diff < mSmallSpeed)
                {
                    rotate = (float) (mShakeAngle * Math.sin(w * diff));
                }
            } else
            {
                rotate = (float) (mShakeAngle * Math.sin(w * diff));
            }
        } else //根据下拉状态计算触角
        {
            //一开始为60度，离触发线越近，触角越垂直
            rotate = 60;
            if (mOffset > mRefreshHeight + 20)
            {
                rotate = 60 * (mThresholdHeight - mOffset) / (mThresholdHeight - mRefreshHeight - 20);
            }
        }

        float left = getMeasuredWidth() / 2 + mRetOffset - mRightX;
        float top = (getMeasuredHeight() - mStickHigh) / 2 - mRightY;
        float right = left + mStickWidth;
        float bottom = top + mStickHigh;

        //画右长条
        canvas.save();
        canvas.translate(mRightX, mRightY + 50);
        canvas.rotate(rotate);
        canvas.drawRect(left, top, right, bottom, mStickPaint);
        canvas.restore();

        //画左长条
        canvas.save();
        canvas.translate(mLeftX, mLeftY + 50);
        canvas.rotate(-rotate);
        left = getMeasuredWidth() / 2 - mRetOffset - mStickWidth - mLeftX;
        right = left + mStickWidth;
        canvas.drawRect(left, top, right, bottom, mStickPaint);
        canvas.restore();
    }


    @Override
    public int getThresholdHeight()
    {
        return mThresholdHeight;
    }

    @Override
    public int getRefreshingHeight()
    {
        return mRefreshHeight;
    }


    private HeaderState mStates;
    private float mOffset;

    @Override
    public void StateChange(HeaderState state)
    {
        mStates = state;
        if (state == HeaderState.over)
            start();
        else
            Stop();
    }

    @Override
    public void startRefresh() {}

    @Override
    public void onSucceedRefresh() {}

    @Override
    public void onFailRefresh() {}

    @Override
    public void onPositionChange(float offset)
    {
        mOffset = offset;
        if (!mIsRunning)
            invalidate();
    }

    @Override
    public void attachLayout(PullToRefreshLayout layout)
    {

    }



    private class Retangle {
        private long mCreateTime;
        private float MaxHigh = 150;
        private float MaxWidth = 300;

        public Retangle(long createTime)
        {
            mCreateTime = createTime;
        }

        public Retangle()
        {
            mCreateTime = System.currentTimeMillis();
        }

        public int getCurrentHigh()
        {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) (Math.abs(percent) * MaxHigh);
        }

        public int getCurrentWidth()
        {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) (Math.abs(percent) * MaxWidth);
        }

        public int getAlpha()
        {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - percent) * 255);
        }

        public void reset(long ms)
        {
            this.mCreateTime = System.currentTimeMillis();
        }

        public long getDiff()
        {
            return System.currentTimeMillis() - mCreateTime;
        }

    }


}
