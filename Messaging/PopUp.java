package com.parse.ideanetwork.Messaging;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ideanetwork.R;

public class PopUp extends ConstraintLayout {
    Context context;
    public ListView listView;
    public View popUpView;
    public View descriptionView;
    public static final int widthDp = 212;
    public static final int heightDp = 344;
    LayoutInflater layoutInflater;
    public AlertDialog dialog;
    public TextView description;
    public EditText reply;
    public EditText initialEt;
    public String replyMsg = "";
    public String  initialMsg = "";


    public PopUp(Context context) {
        super(context);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        popUpView = layoutInflater.inflate(R.layout.messaging, null);
        popUpView.setFocusable(true);
        listView = (ListView) popUpView.findViewById(R.id.commentsSection);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (reply != null){
                    replyMsg = reply.getText().toString();
                    Log.i("onScroll", "rep msg " + replyMsg);
                }
                if (initialEt != null ){
                    initialMsg = initialEt.getText().toString();
                }
        }
        });
        disableParentScrolling();
    }

    private void disableParentScrolling() {
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public void activatePopUp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popUpView);
        dialog = builder.create();
        dialog.show();

    }

    public void activateDescriptionPopUp(){
        descriptionView = layoutInflater.inflate(R.layout.idea_description,null);
        description = (TextView)descriptionView.findViewById(R.id.description);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(descriptionView);
        dialog = builder.create();
        dialog.show();
    }

    public void setDisplayMetrics(DisplayMetrics displayMetrics)
    {
        Log.i("dispmet", "setting height " + Integer.toString(displayMetrics.heightPixels));
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.
                WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = displayMetrics.heightPixels;
        listView.setLayoutParams(params);
    }
}
