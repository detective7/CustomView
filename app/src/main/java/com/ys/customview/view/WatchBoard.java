package com.ys.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ys.customview.R;
import com.ys.customview.exception.NoDetermineSizeException;
import com.ys.customview.util.DpSpUtil;

/**
 * Describe:
 * Created by ys on 2016/10/9 9:48.
 */

public class WatchBoard extends View {

    private Context context;

    private float mPadding;
    private float mTextSize;
    private float mHourPointWidth;
    private float mMinutePointWidth;
    private float mSecondPointWidth;
    private float mPointRadius;
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
        mPointRadius = (Math.min(w,h)-mPadding)/2;
        mPointEndLength = mPointRadius/6; //尾部指针默认为半径的六分之一
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);//画布居中，以圆心为坐标（0,0）

        //绘制外边框的圆
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,mPointRadius,mPaint);

        canvas.restore();
    }

    //绘制刻度盘
    private void paintScale(Canvas canvas){
        mPaint.setStrokeWidth(DpSpUtil.dp2px(context,1));
        int lineWidth=0;
        for(int i = 0;i<60;i++){
            if(i%5==0){
                mPaint.setStrokeWidth(DpSpUtil.dp2px(context, 1.5f));
                mPaint.setColor(mColorLong);
                lineWidth = 40;
            }else { //非整点
                lineWidth = 30;
                mPaint.setColor(mColorShort);
                mPaint.setStrokeWidth(DpSpUtil.dp2px(context, 1));
            }
            //与圆形边距为10dp
            canvas.drawLine(0, -mPointRadius+ DpSpUtil.dp2px(context, 10),0,-mPointRadius+ DpSpUtil.dp2px(context, 10)+lineWidth,mPaint);
            canvas.rotate(6);
            canvas.save()
        }
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
            mPointRadius = (int) array.getDimension(R.styleable.WatchBoard_wb_pointer_corner_radius, DpSpUtil.dp2px(context, 10));
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
            mPointRadius = (int) DpSpUtil.dp2px(context,10);
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
