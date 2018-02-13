package com.parse.ideanetwork;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;


@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class CustomDragShadow extends View.DragShadowBuilder {
    Idea idea;
    Context mContext;
    DpConverter dpConverter;
    Drawable d;


    public CustomDragShadow(Context mContext,View view){
        super(view);
        this.mContext = mContext;
        dpConverter = new DpConverter(mContext);


    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        int width, height;


        width = (int)dpConverter.convertDpToPixel(Idea.widthDp);
        height = (int)dpConverter.convertDpToPixel(Idea.heightDp);



        d = mContext.getResources().getDrawable(R.drawable.ic_drag_shadow,null);
        d.setBounds(0,0,width,height);

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
         outShadowSize.set(width, height);

        // Sets the touch point's position to be in the middle of the drag shadow
           outShadowTouchPoint.set(width/2 , height/2 );
    }


    @Override
    public void onDrawShadow(Canvas canvas) {
        d.draw(canvas);

    }
}
