package com.parse.ideanetwork;


import android.util.Log;

import com.parse.ideanetwork.Collation.AliasCluster;

// used to tie vectors with distance values between ideas
public class DistanceMap {
    int distance;
    IdeaVector ideaVector;
    IdeaVector parentVector;
    IdeaVector normalisedVector;

    public double parentX;
    public double parentY;
    double run;
    double rise;
    double normalisedUnitX;
    double normalisedUnitY;
    double normalisedRawUnitX;
    double normalisedRawUnitY;
    double gradient;
    public double rawRun;
    public double rawRise;
    public DrawLine.LinePos linePos;

    double  normalisedMag;
    DpConverter dpConverter;

    boolean isBotRightCornerVector;
    boolean isTopLeftVectorParent;
    boolean isBotRightVectorChild;
    boolean isBotLeftVectorChild;
    boolean isTopRightVectorParent;
    boolean isBotVectorChild;
    boolean isBotVectorParent;
    boolean isBotLeftCornerVector;
    boolean isTopVectorParent;
    Float xOffset;
    Float yOffset;

    public DistanceMap(){}

    public DistanceMap(IdeaVector ideaVector){
        this.ideaVector = ideaVector;
    }

    public int getDistance(){
        return distance;
    }

    public IdeaVector getIdeaVector(){
        return ideaVector;
    }

    public IdeaVector getParentVector(){
        return parentVector;
    }

    public void setParentVector(double parentX, double parentY){

            this.parentX = parentX;
            this.parentY = parentY;

        parentVector = new IdeaVector(parentX, parentY);
    }

    public Boolean isEndYLessThanParent(){
        return (parentY > ideaVector.y);
    }

    public Boolean isEndXLessThanParent(){
        return (parentX > ideaVector.x);
    }

    public void distanceGen(){
        run = Math.abs(parentX - ideaVector.x);
        rise = Math.abs(parentY - ideaVector.y);
        distance = (int) Math.sqrt(Math.pow(run, 2) + Math.pow(rise, 2));
        rawRun = ideaVector.x- parentX;
        rawRise = ideaVector.y-parentY;
    }

    public IdeaVector aliasPoint(double rise,double run, IdeaVector originPos){
        double x = originPos.x + (run * AliasCluster.aliasDistance);
        double y = originPos.y + (rise * AliasCluster.aliasDistance);
        Log.i("aliasPoint","aliasdistance "+ Double.toString( AliasCluster.aliasDistance));
        Log.i("aliasPoint","run "+ Double.toString(run));
        Log.i("aliasPoint","rise "+ Double.toString(rise));
        return  new IdeaVector(x,y);
    }

    public void vectorNormalise(){
        normalisedUnitX = (run/Math.sqrt(distance));
        normalisedUnitY = (rise/Math.sqrt(distance));
        normalisedRawUnitX = (rawRun/Math.sqrt(distance));
        normalisedRawUnitY = (rawRise/Math.sqrt(distance));
        normalisedVector = new IdeaVector(normalisedUnitX,normalisedUnitY);
        normalisedMag =   Math.sqrt(Math.pow(normalisedUnitX, 2) + Math.pow(normalisedUnitY, 2));
    }

    public void setLinePos(DrawLine.LinePos linePos){
        this.linePos = linePos;
    }
}
