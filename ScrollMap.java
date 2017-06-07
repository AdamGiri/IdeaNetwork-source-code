package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Px;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;


public class ScrollMap extends View implements GestureDetector.OnGestureListener{
    // Methods handling 2d Scrolling
    public ScrollMap(Context context) {
        super(context);



    }



    @Override
    public void scrollBy(@Px int x, @Px int y) {
        super.scrollBy(x, y);
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrollBy((int)distanceX,(int)distanceY);
        postInvalidate();
        Log.i("scroll", "X " + Float.toString(distanceX) + " Y " + Float.toString(distanceY));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("scroll","true");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
