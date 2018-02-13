package com.parse.ideanetwork.Messaging;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;




public class CustomListView extends ListView implements AbsListView.OnScrollListener{

    int firstVisibleItem;
    public int position;
    public boolean replyActivated;

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(this);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.i("editTextSizeChanged", "onScrollStateChanged");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.i("editTextSizeChanged", "onScroll to " + Integer.toString(firstVisibleItem));
        if (firstVisibleItem == 0 && replyActivated){
            this.smoothScrollToPosition(position);
        }
        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void editTextSizeChanged(Boolean b, Integer pos){
    /*    Log.i("editTextSizeChanged", Integer.toString(pos));
        if (b){
            Log.i("editTextSizeChanged", "called");
            this.smoothScrollToPosition(pos);
        }*/
    }


}
