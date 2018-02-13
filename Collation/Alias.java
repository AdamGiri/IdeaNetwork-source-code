package com.parse.ideanetwork.Collation;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseObject;
import com.parse.ideanetwork.DpConverter;
import com.parse.ideanetwork.Idea;
import com.parse.ideanetwork.IdeaVector;
import com.parse.ideanetwork.LineData;

public class Alias {
    public  double x;
    public double y;
    public String randomId;
    public String ideaTreeId;
    public LineData lineDataDownload;
    public LineData lineData;
    public static final int radius = 10;

    public Idea parentIdea;
    Context context;

    public IdeaVector parentVector;
    public IdeaVector aliasVector;

    public Alias(double x, double y, String randomId, String ideaTreeId, Context mContext){
        this.x = x;
        this.y = y;
        this.randomId = randomId;
        this.ideaTreeId = ideaTreeId;
        context = mContext;
    }

    public void extractLineData(String lineData){
        Gson gson = new Gson();
        lineDataDownload = gson.fromJson(lineData,LineData.class);
    }

    public void setLineData(LineData lineData){
        this.lineData = lineData;
    }

    public void setVectors(){
        Log.i("setVectors","sda");
        parentVector = new IdeaVector(lineDataDownload.parentX-100000,lineDataDownload.parentY);
        aliasVector = new IdeaVector(lineDataDownload.ideaX,lineDataDownload.ideaY);
    }

    public void setParentIdea(ParseObject parentObj){
        parentIdea = new Idea(context);
        parentIdea.setTopMargin(parentObj.getNumber("y").intValue());
        parentIdea.setLeftMargin(parentObj.getNumber("x").intValue());
        parentIdea.setVectorCoordinatesAlias();
        Log.i("setParentIdea", "x: " + Double.toString(parentIdea.topLeftVector.x) +
                " y: " + Double.toString(parentIdea.topLeftVector.y));
        parentIdea.setJSONLineMap(parentObj.getString("LineMap"));
        findParentCoords();
    }

    public void findParentCoords(){
        DpConverter dpConverter = new DpConverter(context);
        int ideaPixelHeight = (int)dpConverter.convertDpToPixel(Idea.heightDp);
        int ideaPixelWidth = (int)dpConverter.convertDpToPixel(Idea.widthDp);
        switch (parentIdea.lineDataDownload.linePos){
            case TOP:
                parentVector = new IdeaVector(parentIdea.topLeftVector.x + ideaPixelWidth/2,
                        parentIdea.topLeftVector.y);
            case BOT:
                parentVector = new IdeaVector(parentIdea.bottomLeftVector.x+ideaPixelWidth/2
                        ,parentIdea.bottomLeftVector.y);
            case LEFT:
                parentVector = new IdeaVector(parentIdea.topLeftVector.x , parentIdea.topLeftVector.y
                        + ideaPixelHeight/2);
            case RIGHT:
                parentVector = new IdeaVector(parentIdea.topRightVector.x
                        , parentIdea.topRightVector.y + ideaPixelHeight/2);
        }
    }
}
