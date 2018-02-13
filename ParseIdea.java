package com.parse.ideanetwork;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

//TODO redundant class, potentially remove
public class ParseIdea extends ParseQuery implements FindCallback<ParseObject> {
    ParseQuery<ParseObject> query;
    Rect searchRect;
    String ideaTreeID = "";
    ArrayList<Rect> collidingIdeas = new ArrayList<>();
    public static ArrayList<Boolean> safeToAdd;
    Idea idea;
    static Context mContext;


    public ParseIdea(Class subclass) {
        super(subclass);
    }

    public ParseIdea(@Nullable Rect searchRect, Idea idea) {
        super("Idea");
        this.idea = idea;
        if (idea.getIdentifier().equals("Child")) {
            Log.i("test","ParseIdea");
            this.searchRect = searchRect;
            safeToAdd = new ArrayList<>();
            query("Idea");
        }

    }

    public ParseIdea(ParseQuery<ParseObject> query,String id,Context context) {
        super("Idea");


    }



    // assigns searchRect coordinates to a search criteria to find all ideas within the bounding coordinates.
    private void query(String theClassName) {
        query = new ParseQuery<>(theClassName);
        query.whereEqualTo("IdeaTree",idea.getIdeaTreeID());
        query.whereGreaterThan("y", searchRect.top);
        query.whereLessThan("y", searchRect.bottom);
        query.whereLessThan("x", searchRect.right);
        query.whereGreaterThan("x", searchRect.left);
        query.findInBackground(this);
    }

    // returns list of ideas found from parse within the search rect boundaries, then creates rects
    //around each child idea to calculate free space, else if idea is Main it will just add the main
    //idea to parse.
    @Override
    public void done(List<ParseObject> objects, ParseException e) {
        Log.i("test", "done");
        if (e == null){
        if (!objects.isEmpty()){
        for (int i = 0; i < objects.size(); i++) {
            ParseObject collidingIdea =  objects.get(i);
            int collidingIdeaX = (int)collidingIdea.getNumber("x");
            Log.i("collidingX", collidingIdea.getNumber("x").toString());
            int collidingIdeaY = (int)collidingIdea.getNumber("y");
            Log.i("collidingY", collidingIdea.getNumber("y").toString());
            IdeaVector collidingIdeaVector = new IdeaVector(collidingIdeaX, collidingIdeaY,mContext);
           // collidingIdeas.add(collidingIdeaVector.createRect(true,idea));
        }
            calculateOverlap(idea);
            addIdeaToParse();
        } else {
            addIdeaToParse();
        }
        } else {
           Log.i("test", e.getMessage());
        }
    }


    public void calculateOverlap(Idea idea) {

        for (int i = 0; i < collidingIdeas.size(); i++) {
            if ((idea.topRightVector.x < collidingIdeas.get(i).left) ||
                    (idea.bottomLeftVector.y < collidingIdeas.get(i).top) ||
                    (collidingIdeas.get(i).right < idea.bottomLeftVector.x) ||
                    (collidingIdeas.get(i).bottom < idea.topLeftVector.y)) {
                Log.i("testCollidingIdea"," Xright " +Integer.toString(collidingIdeas.get(i).right) + " Xleft " +
                        Integer.toString(collidingIdeas.get(i).left) + " Ytop " +Integer.toString(collidingIdeas.get(i).top)
                        + " Ybot " + Integer.toString(collidingIdeas.get(i).bottom));
                Log.i("testIdea"," Xright " +Double.toString(idea.topRightVector.x) + " Xleft " +
                        Double.toString(idea.bottomLeftVector.x) + " Ytop " +Double.toString(idea.topLeftVector.y)
                + " Ybot " + Double.toString(idea.bottomRightVector.y));
                Log.i("test",Integer.toString(collidingIdeas.size()));
                safeToAdd.add(true);
            } else {
                Log.i("test","calculateOverlapfalse");
                Log.i("test","X " +Integer.toString(collidingIdeas.get(i).left) + "Y " +
                        Integer.toString(collidingIdeas.get(i).top));
                safeToAdd.add(false);
            }
        }
        if (ParseIdea.safeToAdd.isEmpty()) {
            Log.i("safeToAddparseidea", ParseIdea.safeToAdd.toString());
        } else {
            Log.i("safeToAddparseidea", ParseIdea.safeToAdd.toString());
        }
        collidingIdeas.clear();
    }

    //adds Idea data to parse
    public void addIdeaToParse() {
        Log.i("test","addIdeaToParse");
        if (!ParseIdea.safeToAdd.contains(false)||ParseIdea.safeToAdd.isEmpty()) {
            ParseObject parseObject = new ParseObject("Idea");
            parseObject.put("x", idea.topLeftVector.x);
            parseObject.put("y", idea.topLeftVector.y);
            parseObject.put("IdeaTree",idea.getIdeaTreeID());
            parseObject.put("Identifier",idea.getIdentifier());
            Log.i("test","addIdeaToParsetrue");
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("added", "successfully added to parse");
                    } else {
                        Log.i("added", e.getMessage());
                    }
                }
            });
        }}

    public static ArrayList<Boolean> getSafeToAdd(){
        return safeToAdd;
    }
    }



