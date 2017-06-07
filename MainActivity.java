/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseAnalytics;


public class MainActivity extends AppCompatActivity  {


    GestureDetector gestureDetector;



  //  @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Idea idea = new Idea(this,null);
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayout);
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.RelativeLayout);
        linearLayout.addView(idea);
        relativeLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                Log.i("test","red");
                switch(action){
                    case DragEvent.ACTION_DROP:

                        Idea idea = new Idea(getApplicationContext(),null);

                        relativeLayout.addView(idea,generateParams(event,idea));
                }
                return true;

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private RelativeLayout.LayoutParams generateParams(DragEvent event,  Idea idea) {
        RelativeLayout.LayoutParams params = new RelativeLayout.
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //TODO becareful here if you change the dimensions of the Idea the mouse positioning will be off
        //TODO therefore, review these calculations upon dimension change
        params.leftMargin = (int)event.getX()-75;
        params.topMargin =  (int)event.getY()-80;
        return params;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
      gestureDetector.onTouchEvent(event);



        return true;
    }
    }
