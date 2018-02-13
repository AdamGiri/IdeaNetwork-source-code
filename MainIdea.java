package com.parse.ideanetwork;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainIdea extends ConstraintLayout {
    String summary;
    String description;
    String author;
    Context mContext;
    String ideaTreeID;
    int totalVote = 0;
    long creation;
    ArrayList<String> tags;
    boolean isBookmarked;
    public ImageView bookmarkView;




    public MainIdea(Context context,String ideaTreeID) {
        super(context);
        this.ideaTreeID = ideaTreeID;
        this.mContext = context;
        author = ParseUser.getCurrentUser().getUsername();
        tags = new ArrayList<>();
        description = "Description";
    }

    public MainIdea(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainIdea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String title){
        this.summary = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setTags(List<Object> tags){
        for (Object tag:tags){
            String newTag = (String)tag;
            this.tags.add(newTag);
        }
    }

}
