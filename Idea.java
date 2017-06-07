package com.parse.starter;


import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.zip.Inflater;

public class Idea extends LinearLayout implements View.OnLongClickListener{



    View view;
    ClipData.Item item;
    ViewHolder viewHolder;
    private static final String IDEA_TAG = "idea";


    //inner class ViewHolder to define idea tag
    private class ViewHolder{
        TextView title;
        TextView description;
        TextView author;
    }

    //Note, remember at these @requireApi notations, surround with if statements to account for
    //versions lower than required.
   @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public Idea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        viewHolder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.
                LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.idea_layout,this,true);
        view.setOnLongClickListener(this);
        setIdeaTags();
        setDragNewIdea();


    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public Idea(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Idea(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setIdeaTags() {
        viewHolder.title = (TextView)view.findViewById(R.id.title);
        viewHolder.description = (TextView)view.findViewById(R.id.Description);
        viewHolder.author = (TextView)view.findViewById(R.id.Author);
        //view.setTag(viewHolder);
        view.setTag(IDEA_TAG);

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void setDragNewIdea() {

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onLongClick(View v) {
        Log.i("long","response");
        ClipData.Item item;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            item = new ClipData.Item((CharSequence)view.getTag());
            ClipData dragData = new ClipData((CharSequence)v.getTag(), new String[]{ClipDescription.
                    MIMETYPE_TEXT_PLAIN},item);
            DragShadowBuilder myShadow = new DragShadowBuilder(view);
            view.startDragAndDrop(dragData,myShadow,null,0);
        }

        return false;
    }





}
