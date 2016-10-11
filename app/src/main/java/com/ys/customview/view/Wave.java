package com.ys.customview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Describe:
 * Created by ys on 2016/10/11 16:48.
 */

public class Wave extends View {
    private Paint paint;
    private Path path;
    private int mItemWaveLength=400;
    private int dx=0;
    private ValueAnimator animator1=null,animator2=null;
    private int height,width;

    public Wave(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.GREEN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        int originY=300;
        int halfWaveLen = mItemWaveLength/2;

        path.moveTo(-mItemWaveLength+dx,originY);
        for(int i = -mItemWaveLength;i<=getWidth()+mItemWaveLength;i+=mItemWaveLength){
            path.rQuadTo(halfWaveLen/2,-50,halfWaveLen,0);
            path.rQuadTo(halfWaveLen/2,50,halfWaveLen,0);
        }
        path.lineTo(getWidth(),getHeight());
        path.lineTo(0,getHeight());
        path.close();

        canvas.drawPath(path,paint);
    }

    public void startAnim(){
        animator1 = ValueAnimator.ofInt(0,mItemWaveLength);
        animator1.setDuration(1000);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx=(int)animation.getAnimatedValue();
                postInvalidate();
            }
        });

        animator2 = ValueAnimator.ofInt(0,mItemWaveLength);
        animator2.setDuration(1000);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx=(int)animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator1.start();
        animator2.start();
    }

}
