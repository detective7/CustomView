package com.ys.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ys.customview.R;
import com.ys.customview.exception.NoDetermineSizeException;
import com.ys.customview.util.DpSpUtil;

import java.util.Calendar;

/**
 * Describe:
 * Created by ys on 2016/10/9 9:48.
 */

public class WatchBoard extends View {

    private Context context;

    private float mRadius; //外圆半径
    private float mPadding;
    private float mTextSize;
    private float mHourPointWidth;
    private float mMinutePointWidth;
    private float mSecondPointWidth;
    private float mPointRadius;// 指针圆角
    private float mPointEndLength;

    private int mColorLong;
    private int mColorShort;
    private int mHourPointColor;
    private int mMinutePointColor;
    private int mSecondPointColor;

    private Paint mPaint;

    public WatchBoard(Context context) {
        super(context);
        this.context=context;
    }

    public WatchBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        obtainStyledAtters(attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 1000;//最大值，正方形的边长不超过1000

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            try{
                throw new NoDetermineSizeException("宽度和高度至少有一个确定的值，不能同时为warp_content");
            }catch (NoDetermineSizeException e){
                e.printStackTrace();
            }
        }else {//至少有一个为确定值，获取最小值，做正方形的边长
            if(widthMode==MeasureSpec.EXACTLY){
                width = Math.min(widthSize,width);
            }
            if(heightSize==MeasureSpec.EXACTLY){
                width = Math.min(heightSize,width);
            }
        }
        setMeasuredDimension(width,width);
    }

    //获取表盘圆的半径值与尾部长度值应该在测量完成之后,所以在onSizeChange里面获取
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w,h)-mPadding)/2;
        mPointEndLength = mRadius/6; //尾部指针默认为半径的六分之一
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);//画布居中，以圆心为坐标（0,0）

        //绘制外边框的圆
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,mRadius,mPaint);
        //绘制刻度盘
        paintScale(canvas);
        //绘制指针
        paintPointer(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,20,mPaint);
        canvas.restore();
        //刷新
        postInvalidateDelayed(1000);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                postInvalidateDelayed(1000);
//            }
//        }).start();
    }

    //绘制刻度盘
    private void paintScale(Canvas canvas){
        canvas.save();
        mPaint.setStrokeWidth(DpSpUtil.dp2px(context,1));
        int lineWidth=0;
        for(int i = 0;i<60;i++){
            if(i%5==0){
                mPaint.setStrokeWidth(DpSpUtil.dp2px(context, 1.5f));
                mPaint.setColor(mColorLong);
                lineWidth = 40;
                String text = ((i / 5) == 0 ? 12 : (i / 5)) + "";
                Rect textBounds = new Rect();
                //根据字符串取最小textView的宽高,放到textBounds里
                mPaint.getTextBounds(text,0,text.length(),textBounds);
                //先保存用来画刻度线条的canvas
                canvas.save();
                //将画布沿着现在画布的y轴负方向移动靠近圆心，到离线条5dp
                // (textBounds.bottom-textBounds.top) 的位置(大概是为了防止字体比给的5dp还大，被挡住)
                canvas.translate(0,-mRadius+DpSpUtil.dp2px(context, 5)+lineWidth+(textBounds.bottom-textBounds.top));
                //然后反方向旋转回去，此刻canvas的坐标原点，作为textView的中心位置
                canvas.rotate(-6 * i);
                mPaint.setStyle(Paint.Style.FILL);
                //测试框
//                mPaint.setColor(Color.BLUE);
//                canvas.drawRect(0,0,50,50,mPaint);
                //开始绘制数字
                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(mTextSize);
                canvas.drawText(text,-(textBounds.right-textBounds.left)/2,(textBounds.bottom-textBounds.top)/2,mPaint);
                //回复旋转的画布
                canvas.restore();
            }else { //非整点
                lineWidth = 30;
                mPaint.setColor(mColorShort);
                mPaint.setStrokeWidth(DpSpUtil.dp2px(context, 1));
            }
            //与圆形边距为10dp
            canvas.drawLine(0, -mRadius+ DpSpUtil.dp2px(context, 10),0,-mRadius+ DpSpUtil.dp2px(context, 10)+lineWidth,mPaint);
            canvas.rotate(6);
        }
        canvas.restore();
    }

    //绘制指针
    private void paintPointer(Canvas canvas){
        //获取当前时间
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        Float hourAngle=0f;
        Float minAngle=0f;
        if(min!=0) {
            hourAngle = hour * 30 + 0.5f*min;
        }else{
            hourAngle =  hour * 30.0f;
        }
        if(sec!=0){
            minAngle = min*6+0.1f*sec;
        }else {
            minAngle = min*6.0f;
        }
        int secAngle = sec*6;
        //Log.d("abc",hourAngle+"   "+minAngle+"   "+secAngle);
        //绘制时针
        canvas.save();
        canvas.rotate(hourAngle);
        RectF rectFHour = new RectF(-mHourPointWidth / 2, -mRadius * 3 / 5, mHourPointWidth / 2, mPointEndLength);
        mPaint.setColor(mHourPointColor); //设置指针颜色
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mHourPointWidth); //设置边界宽度
        canvas.drawRoundRect(rectFHour, mPointRadius, mPointRadius, mPaint); //绘制时针
        canvas.restore();

        canvas.save();
        canvas.rotate(minAngle);
        RectF rectFMinute = new RectF(-mMinutePointWidth / 2, -mRadius * 3.5f / 5, mMinutePointWidth / 2, mPointEndLength);
        mPaint.setColor(mMinutePointColor);
        mPaint.setStrokeWidth(mMinutePointWidth);
        canvas.drawRoundRect(rectFMinute, mPointRadius, mPointRadius, mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(secAngle);
        RectF rectFSecond = new RectF(-mSecondPointWidth / 2, -mRadius + 15, mSecondPointWidth / 2, mPointEndLength);
        mPaint.setColor(mSecondPointColor);
        mPaint.setStrokeWidth(mSecondPointWidth);
        canvas.drawRoundRect(rectFSecond, mPointRadius, mPointRadius, mPaint);
        canvas.restore();

//        mPaint.setColor(Color.BLACK);
//        canvas.drawRect(-20,-20,20,20,mPaint);
    }

    private void init() {
        mPaint = new Paint();
        //去锯齿
        mPaint.setAntiAlias(true);
        //防抖动
        mPaint.setDither(true);
    }

    //获取属性值
    private void obtainStyledAtters(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.WatchBoard);
            mPadding = array.getDimension(R.styleable.WatchBoard_wb_padding, DpSpUtil.dp2px(context, 10));
            mTextSize = array.getDimension(R.styleable.WatchBoard_wb_text_size, DpSpUtil.sp2px(context, 16));
            mHourPointWidth = array.getDimension(R.styleable.WatchBoard_wb_hour_pointer_width, DpSpUtil.dp2px(context, 5));
            mMinutePointWidth = array.getDimension(R.styleable.WatchBoard_wb_minute_pointer_width, DpSpUtil.dp2px(context, 3));
            mSecondPointWidth = array.getDimension(R.styleable.WatchBoard_wb_second_pointer_width, DpSpUtil.dp2px(context, 2));
            mPointRadius = array.getDimension(R.styleable.WatchBoard_wb_pointer_corner_radius, DpSpUtil.dp2px(context, 10));
            mPointEndLength = array.getDimension(R.styleable.WatchBoard_wb_pointer_end_length, DpSpUtil.dp2px(context, 10));

            mColorLong = array.getColor(R.styleable.WatchBoard_wb_scale_long_color, Color.argb(255, 0, 0, 0));
            mColorShort = array.getColor(R.styleable.WatchBoard_wb_scale_short_color, Color.argb(125, 0, 0, 0));
            mHourPointColor = array.getColor(R.styleable.WatchBoard_wb_hour_pointer_color, Color.BLUE);
            mMinutePointColor = array.getColor(R.styleable.WatchBoard_wb_minute_pointer_color, Color.BLACK);
            mSecondPointColor = array.getColor(R.styleable.WatchBoard_wb_second_pointer_color, Color.RED);
        }catch(Exception e){
            //一旦出现错误全部使用默认值
            mPadding =DpSpUtil.dp2px(context,10);
            mTextSize = DpSpUtil.sp2px(context,16);
            mHourPointWidth = DpSpUtil.dp2px(context,5);
            mMinutePointWidth = DpSpUtil.dp2px(context,3);
            mSecondPointWidth =DpSpUtil.dp2px(context,2);
            mPointRadius = DpSpUtil.dp2px(context,10);
            mPointEndLength = DpSpUtil.dp2px(context,10);

            mColorLong = Color.argb(225, 0, 0, 0);
            mColorShort = Color.argb(125, 0, 0, 0);
            mHourPointColor = Color.BLUE;
            mMinutePointColor = Color.BLACK;
            mSecondPointColor = Color.RED;
        }finally {
            if(array!=null){
                array.recycle();
            }
        }
    }

}
