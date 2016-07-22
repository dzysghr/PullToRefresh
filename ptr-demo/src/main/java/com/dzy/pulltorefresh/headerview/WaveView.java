package com.dzy.pulltorefresh.headerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * Created by dzysg on 2016/7/21 0021.
 */
public class WaveView extends View {
    private float mDuration = 1600;
    private float mInitialRadius = 10;
    private float mMaxRadius = 200;

    private boolean mIsRunning = false;

    private int mSmallSpeed = 400;
    private Paint mStickPaint;
    private Paint mRetPaint;
    private Paint mTestPaint;
    private float mStickHigh = 100;
    private float mStickWidth = 10;
    private float mShakeAngle = 40;
    private float mRetOffset = 20;


//    private Circle mFirstCircle;
//    private Circle mSecondCircle;


    private Retangle mFistRet;
    private Retangle mSecondRet;

    double w = Math.PI / mSmallSpeed;//mSmallSpeed
    float mRightX;
    float mRightY;
    float mLeftX;
    float mLeftY;


    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mStickPaint = new Paint();
        mStickPaint.setColor(Color.WHITE);


        mRetPaint = new Paint();
        mRetPaint.setColor(Color.WHITE);
        mRetPaint.setStyle(Paint.Style.STROKE);
        mRetPaint.setStrokeWidth(5);


        mTestPaint = new Paint();
        mTestPaint.setColor(Color.BLACK);
        mTestPaint.setStyle(Paint.Style.FILL);

//        if (!isInEditMode())
//            start();

    }


    private Runnable mUpdate = new Runnable() {
        @Override
        public void run() {
            if (!mIsRunning)
                return;
            long l = System.currentTimeMillis();

            if (l - mFistRet.mCreateTime > mDuration)
                mFistRet.reset(l);
            if (l - mSecondRet.mCreateTime > mDuration)
                mSecondRet.reset(l);
            invalidate();
            postDelayed(mUpdate, 10);
        }
    };

    public void start() {
        mIsRunning = true;
        //mFirstCircle = new Circle(System.currentTimeMillis());
        //mSecondCircle = new Circle(System.currentTimeMillis() + mSmallSpeed);

        mFistRet = new Retangle();
        mSecondRet = new Retangle(System.currentTimeMillis() + mSmallSpeed);
        mUpdate.run();
    }

    public void Stop() {
        mIsRunning = false;
    }


    private void initPoint() {

        mRightX = getMeasuredWidth() / 2 + mStickWidth / 2 + mRetOffset;
        mRightY = getMeasuredHeight() / 2;
        mLeftX = getMeasuredWidth() / 2 - mStickWidth / 2 - mRetOffset;
        mLeftY = mRightY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initPoint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

      //  drawCirCle(canvas, mFirstCircle);
       // drawCirCle(canvas, mSecondCircle);
        canvas.drawColor(0xFFE91E63);

        drawRoundRect(canvas, mFistRet);
        drawRoundRect(canvas, mSecondRet);
        drawStick(canvas);

    }


    private void drawRoundRect(Canvas canvas,Retangle retangle)
    {
        if (retangle.getDiff()<mDuration)
        {
            int left = (getMeasuredWidth() -  retangle.getCurrentWidth())/2;
            int top = (getMeasuredHeight() -  retangle.getCurrentHigh())/2;
            int right = left + retangle.getCurrentWidth();
            int bottom = top + retangle.getCurrentHigh();
            RectF rectf = new RectF(left,top,right,bottom);
            mRetPaint.setAlpha(retangle.getAlpha());
            canvas.drawRoundRect(rectf,50, 50, mRetPaint);
        }
    }

    private void drawStick(Canvas canvas) {

        boolean willRotate = true;
        float diff = mFistRet.getDiff();
        if (diff > mSmallSpeed) {
            diff = mSecondRet.getDiff();
            if (diff > mSmallSpeed) {
                willRotate = false;
            }
        }

        float left = getMeasuredHeight() / 2 + mRetOffset - mRightX;
        float top = (getMeasuredHeight() - mStickHigh) / 2 - mRightY;
        float right = left + mStickWidth;
        float bottom = top + mStickHigh;

        //画右矩形
        canvas.save();
        canvas.translate(mRightX, mRightY + 50);
        if (willRotate) {
            float rotate = (float) (mShakeAngle * Math.sin(w * diff));
            canvas.rotate(rotate);
        }

        canvas.drawRect(left, top, right, bottom, mStickPaint);
        canvas.restore();

        //画左矩形
        canvas.save();
        canvas.translate(mLeftX, mLeftY + 50);
        if (willRotate) {
            float rotate = (float) -(mShakeAngle * Math.sin(w * diff));
            canvas.rotate(rotate);
        }

        left = getMeasuredHeight() / 2 - mRetOffset - mStickWidth - mLeftX;
        right = left + mStickWidth;
        canvas.drawRect(left, top, right, bottom, mStickPaint);
        canvas.restore();


    }


    private void drawCirCle(Canvas canvas, Circle c) {

        if (System.currentTimeMillis() - c.mCreateTime < mDuration) {
            mStickPaint.setAlpha(c.getAlpha());
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, c.getCurrentRadius(), mStickPaint);
        }

    }


    private class Retangle {
        private long mCreateTime;
        private float MaxHigh = 150;
        private float MaxWidth = 300;

        public Retangle(long createTime) {
            mCreateTime = createTime;
        }

        public Retangle() {

        }


        public int getCurrentHigh() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ( Math.abs(percent)* MaxHigh);
        }

        public int getCurrentWidth() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return  (int) ( Math.abs(percent)* MaxWidth);
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - percent) * 255);
        }

        public void reset(long ms) {
            this.mCreateTime = System.currentTimeMillis();
        }

        public long getDiff() {
            return System.currentTimeMillis() - mCreateTime;
        }

    }

    private class Circle {
        private long mCreateTime;

        public Circle(long createTime) {
            mCreateTime = createTime;
        }

        public Circle() {
            this.mCreateTime = System.currentTimeMillis();
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - percent) * 255);
        }

        public float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + percent * (mMaxRadius - mInitialRadius);
        }

        public void reset() {
            this.mCreateTime = System.currentTimeMillis();
        }

        public void reset(long ms) {
            this.mCreateTime = System.currentTimeMillis();
        }

        public long getDiff() {
            return System.currentTimeMillis() - mCreateTime;
        }

    }
}

