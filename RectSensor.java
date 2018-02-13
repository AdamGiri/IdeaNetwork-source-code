package com.parse.ideanetwork;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public  class RectSensor extends View  {

    private Rect rectangle;
    private Paint paint;
    public OverlapListener overlapListener;
    int top;
    int left;


    public RectSensor(Context context, int top, int left) {
        super(context);

        this.top = top;
        this.left = left;
      //  rectangle = new Rect(left,top,width,height);
        paint = new Paint();
        paint.setColor(Color.GREEN);

    }

    public RectSensor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RectSensor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RectSensor(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOverlapListener(OverlapListener overlapListener){
        this.overlapListener = overlapListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void translateRect(){
        top = top - 20;
        left = left + 20;
      // rectangle.set(left, top,width,height);
    }



    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(rectangle,paint);
    }




}
