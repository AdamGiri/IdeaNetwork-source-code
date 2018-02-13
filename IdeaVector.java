package com.parse.ideanetwork;

//sets the vector coordinates of each point of the idea

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class IdeaVector  {
    public double x;
    public double y;
    int stopX;
    int stopY;
    int ideaPixelHeight;
    int ideaPixelWidth;

    static final int xOffset = 10;
    static final int yOffset = 30;

    static final int xOffsetTopLeft = 80;
    static final int yOffsetTopLeft = 80;


    Idea idea;
    private static final int searchRectScalar = 4;
    private static final int topOffset = 16;
    Context mContext;
    Boolean isCollidingIdea;
    Idea endVector;
    ArrayList<IdeaVector> endVectorsArray;
    Map<Integer,IdeaVector> endVectorMap;
    ArrayList<DistanceMap> distanceMaps;

    double parentX;
    double parentY;
    IdeaVector aliasVector;
    IdeaVector botRightVector;

    DrawLine.LinePos linePos;
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
    boolean isLeftVectorChild;

    IdeaVector botLeftVector;
    DpConverter dpConverter;

    public IdeaVector(double x, double y,Context mContext){
        this.x = x;
        this.y = y;
        this.mContext = mContext;
        isCollidingIdea = false;
    }

    public IdeaVector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public IdeaVector(double x, double y, Idea endVector, int ideaPixelHeight, int ideaPixelWidth, Context mContext, Boolean isAxisCheck){
        this.endVector = endVector;
        this.mContext = mContext;
        this.x = x;
        this.y = y;
        this.ideaPixelHeight = ideaPixelHeight;
        this.ideaPixelWidth = ideaPixelWidth;
        dpConverter = new DpConverter(mContext);
        if (isAxisCheck) {
            addToAxisArray();
        } else {
            addToCornerArray();
        }
    }

    public IdeaVector(double x,double y,IdeaVector aliasVector){
        Log.i("DrawLine","1");
        this.x = x;
        this.y = y;
        this.aliasVector=aliasVector;
    }



    private void addToCornerArray() {
        Log.i("DrawLine","2");
        endVectorsArray = new ArrayList<>();
        botLeftVector = new IdeaVector(endVector.bottomLeftVector.x ,
                endVector.bottomLeftVector.y,mContext);
        botLeftVector.isBotLeftVectorChild = true;
        endVectorsArray.add(botLeftVector);
        botRightVector = new IdeaVector(endVector.bottomRightVector.x,
                endVector.bottomRightVector.y,mContext);
        botRightVector.isBotRightVectorChild = true;
        endVectorsArray.add(botRightVector);
        IdeaVector topLeftVector = new IdeaVector(endVector.topLeftVector.x,
                endVector.topLeftVector.y,mContext);
        topLeftVector.isTopLeftVectorChild = true;
        endVectorsArray.add(topLeftVector);
        IdeaVector topRightVector = new IdeaVector(endVector.topRightVector.x,
                endVector.topRightVector.y,mContext);
        topRightVector.isTopRightVectorChild = true;
        endVectorsArray.add(topRightVector);
    }

    private void addToAxisArray() {
        Log.i("DrawLine","3");
       endVectorsArray = new ArrayList<>();
       IdeaVector botVec = new IdeaVector(endVector.bottomLeftVector.x + ideaPixelWidth/2,
                endVector.bottomLeftVector.y,mContext);
       botVec.setLineTag(DrawLine.LinePos.TOP);
        botVec.isBotVectorChild = true;
       IdeaVector leftVec = new IdeaVector(endVector.bottomLeftVector.x,
                endVector.bottomLeftVector.y - ideaPixelHeight/2,mContext);
       leftVec.setLineTag(DrawLine.LinePos.RIGHT);
       IdeaVector topVec =  new IdeaVector(endVector.topLeftVector.x + ideaPixelWidth/2,
                endVector.topLeftVector.y +topOffset ,mContext);
        topVec.isTopVectorChild = true;
        topVec.setLineTag(DrawLine.LinePos.BOT);
       IdeaVector rightVec = new IdeaVector(endVector.topRightVector.x,
                endVector.topRightVector.y + topOffset+ ideaPixelHeight/2,mContext);
        rightVec.isRightVectorChild = true;
        rightVec.setLineTag(DrawLine.LinePos.LEFT);
        endVectorsArray.add(botVec);
        endVectorsArray.add(leftVec);
        endVectorsArray.add(rightVec);
        endVectorsArray.add(topVec);
    }

    private void setLineTag(DrawLine.LinePos linePos){
        this.linePos = linePos;
    }

        public void translateIdea(){
        x += 20;
        y += 20;
    }

    public Rect createRect(Boolean isCollidingIdea,int width, int height){
        Rect searchRect = new Rect();
        if (isCollidingIdea) {
            searchRect.set((int)x,(int) y,(int) x+width-10,(int) y+height);
            return searchRect;
        } else {
            int mX = (int)x-searchRectScalar*width;
            int mY = (int)y-searchRectScalar*height;
            searchRect.set(mX, mY,mX+2*searchRectScalar*width, mY+2*searchRectScalar*height);
            return searchRect;
        }
    }


    public DistanceMap distanceCalculator(boolean isAlias){
        Log.i("DrawLine","4");
        if (!isAlias) {
            distanceMaps = new ArrayList<>();
            for (int i = 0; i < endVectorsArray.size(); i++) {

                DistanceMap distanceMap = new DistanceMap(endVectorsArray.get(i));



                if (isTopRightVectorParent){
                    Log.i("distanceCalculator", "isTopRightVectorParent");
                    distanceMap.isTopRightVectorParent = true;

                }


                if (isTopLeftVectorParent){
                    Log.i("distanceCalculator", "isTopLeftVectorParent");
                    distanceMap.isTopLeftVectorParent = true;

                }


                if (isBotRightCornerVector){
                    Log.i("distanceCalculator", "isBotRightCornerVector");
                    distanceMap.isBotRightCornerVector = true;

                }

                if (isBotVectorParent){
                    Log.i("distanceCalculator", "isBotVectorParent");
                    distanceMap.isBotVectorParent = true;

                }

                if (isTopVectorParent){
                    Log.i("distanceCalculator", "isTopVectorParent");
                    distanceMap.isTopVectorParent = true;

                }



                if (isBotLeftCornerVector){
                    Log.i("distanceCalculator", "isBotLeftCornerVector");
                    distanceMap.isBotLeftCornerVector = true;
                    endVectorsArray.get(i).isBotLeftCornerVector = true;
                }


                    distanceMap.setParentVector(x,y);


                distanceMap.distanceGen();
                distanceMap.vectorNormalise();
                distanceMap.setLinePos(endVectorsArray.get(i).linePos);
                distanceMaps.add(distanceMap);

            }

            int magA = Math.min(distanceMaps.get(0).getDistance(),distanceMaps.get(1).getDistance());
            int magB = Math.min(magA,distanceMaps.get(2).getDistance());
            int smallestMag = Math.min(magB,distanceMaps.get(3).getDistance());

            //endVectorsArray.clear();
            for (DistanceMap distanceMap1 : distanceMaps){
                if (distanceMap1.getDistance() == smallestMag){
                    return distanceMap1;
                }
            }
        } else {
            Log.i("distcalc", "is alias true");
            DistanceMap distanceMap = new DistanceMap(aliasVector);

            distanceMap.setParentVector(x, y);
            distanceMap.distanceGen();
            distanceMap.vectorNormalise();

            return distanceMap;
        }
       return null;
    }

}
