package com.parse.ideanetwork.Messaging;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;


public class CustomEditText extends android.support.v7.widget.AppCompatEditText{

    CustomListView listView;
    public int pos;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setListView(CustomListView customListView){
        this.listView = customListView;
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("editTextSizeChanged", "edit text size changed");
        if (listView != null){
            Log.i("editTextSizeChanged", "sending to listview");
            listView.editTextSizeChanged(true, pos);
        }
    }



}
