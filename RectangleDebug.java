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
import android.util.Log;
import android.view.View;

//TODO redundant class, potentially remove
public class RectangleDebug extends View {
    private Paint paint;
    private Context mContext;
    Rect rect;


    public RectangleDebug(Context context,Rect rect) {
        super(context);
        mContext = context;
        this.rect = rect;
        paint = new Paint();
        Log.i("test", Integer.toString(rect.top));
        setMeasuredDimension(rect.width(),rect.height());


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public RectangleDebug(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangleDebug(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RectangleDebug(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("test", "l " +Integer.toString(rect.left)+ "t " + Integer.toString(rect.top) + "r " +
                Integer.toString(rect.right) + "b " + Integer.toString(rect.bottom));
        paint.setColor(Color.GREEN);
        canvas.drawRect((float)rect.left,(float)rect.top,(float)rect.right,(float)rect.bottom,paint);

    }


}
