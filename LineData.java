package com.parse.ideanetwork;


public class LineData {
    public double parentX;
    public double parentY;
    public double ideaX;
    public double ideaY;
    public DrawLine.LinePos linePos;
    boolean isBotRightCornerVector;
    boolean isTopLeftVectorParent;
    boolean isBotRightVectorChild;
    boolean isBotLeftVectorChild;
    boolean isTopRightVectorParent;
    boolean isBotVectorChild;
    boolean isBotVectorParent;
    boolean isBotLeftCornerVector;
    boolean isTopRightVectorChild;
    boolean isTopLeftVectorChild;
    boolean isTopVectorChild;
    boolean isRightVectorChild;
    boolean isTopVectorParent;
   // DistanceMap distanceMap;




    public LineData(double parentX, double parentY, double ideaX, double ideaY){

        this.parentX = parentX;
        this.parentY = parentY;
        this.ideaX = ideaX;
        this.ideaY = ideaY;
    }

    public void setLinePos(DrawLine.LinePos linePos){
        this.linePos = linePos;
    }


}
