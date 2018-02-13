package com.parse.ideanetwork;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;



public class DrawLine extends View {
    Paint paint;
    Idea idea;
    Context mContext;
    DpConverter dpConverter;
    Idea parentIdea;
    ArrayList<Rect> collidingIdeas;
    ArrayList<CollisionMap> currentPoints;
    private static final float increment = 0.25f;
    private static final int lineOffsetAxis = 5;
    private static final int lineOffsetCorner = 10;
    public static final int rightRectOffset = 40;
    Path path;
    int ideaPixelHeight;
    int ideaPixelWidth;
    ArrayList<Boolean> collisions;
    ArrayList<Integer> distances;
    Map<Integer,DistanceMap> distanceMapList;
    IdeaVector lineStart;
    boolean isPathClear = true;
    float parentX;
    float parentY;
    IdeaVector parentVec;

    IdeaVector origin;
    IdeaVector goal;

    IdeaVector firstClearPoint;

    IdeaVector rightVector;
    IdeaVector leftVector;
    IdeaVector topVector;
    IdeaVector botVector;

    LinePos linePos;

    Paint rectPaint;
    double heightDisplay;
    double widthDisplay;
    DistanceMap distanceMap;
    IdeaVector lineDataParent;
    IdeaVector lineDataChild;




//TODO need  to amend the line drawn to the bottom right corner of each idea.
    public DrawLine(Context context, Idea idea, Idea parentIdea,@Nullable ArrayList<Rect>collidingIdeas) {
        super(context);
        Log.i("DrawLine","a");
        this.idea = idea;

        this.parentIdea = parentIdea;
        this.collidingIdeas = collidingIdeas;
        init(context);
        pathFind();
    }

    public DrawLine(Context context,Idea idea){
        super(context);
        Log.i("DrawLine","b");
        this.idea = idea;
        init(context);
        lineDraw();
    }

    public DrawLine(Context context){
        super(context);
        Log.i("DrawLine","c");
        this.mContext = context;
        dpConverter = new DpConverter(mContext);
        ideaPixelHeight = (int)dpConverter.convertDpToPixel(Idea.heightDp);
        ideaPixelWidth = (int)dpConverter.convertDpToPixel(Idea.widthDp);
    }

    public DrawLine(IdeaVector origin, IdeaVector goal, Context context){
        super(context);
        Log.i("DrawLine","d");
        Log.i("DrawLine","goal "+ "x "+Double.toString(goal.x)+"y "+Double.toString(goal.y));
        Log.i("DrawLine","origin "+ "x "+Double.toString(origin.x)+"y "+Double.toString(origin.y));
        init(context);
        drawAliasLine(origin,goal);

    }

    private void init(Context context) {
        Log.i("idea.colorValue","color: "+ idea.colorValue);
        this.mContext = context;
        dpConverter = new DpConverter(mContext);
        ideaPixelHeight = (int)dpConverter.convertDpToPixel(Idea.heightDp);
        ideaPixelWidth = (int)dpConverter.convertDpToPixel(Idea.widthDp);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.parseColor(idea.colorValue));
        paint.setStyle(Paint.Style.STROKE);
        Log.i("setStrokeWidth","MainActivity.screenWidth " + Integer.toString(MainActivity.screenWidth));
        paint.setStrokeWidth(6);
      /*  if (MainActivity.screenWidth<450){
            Log.i("setStrokeWidth","setStrokeWidth");
            paint.setStrokeWidth(2);
        } else {
            paint.setStrokeWidth(3);
        }*/
        paint.setAlpha(100);
        paint.setAntiAlias(true);

    }

    public void setDisplayMetrics(DisplayMetrics displayMetrics)
    {
      heightDisplay = dpConverter.convertPixelToDp(displayMetrics.heightPixels);
      widthDisplay = dpConverter.convertPixelToDp(displayMetrics.widthPixels);
    }

    private void drawAliasLine(IdeaVector origin, IdeaVector goal) {
        Log.i("DrawLine","f");
        this.origin = origin;
        this.goal = goal;
        Log.i("onDraw", "Originx: " + Double.toString(origin.x) + " Originy: " + Double.toString(origin.y));
        Log.i("onDraw", "Goalx: " + Double.toString(goal.x) + " Goaly: " + Double.toString(goal.y));
        parsePointPixel( this.origin);
        parsePointPixel( this.goal);
        path.moveTo((float)origin.x ,(float) origin.y);
        path.lineTo((float) goal.x, (float) goal.y);
    }

    public enum LinePos{
        TOP,
        LEFT,
        BOT,
        RIGHT,
    }

    private void lineDraw() {
        Log.i("DrawLine","g");
             lineDataParent = new IdeaVector(idea.lineDataDownload.parentX,idea.lineDataDownload.parentY);
           lineDataChild = new IdeaVector(idea.lineDataDownload.ideaX,idea.lineDataDownload.ideaY);
            parsePointPixel(lineDataParent);
            parsePointPixel(lineDataChild);
            tagLineStart(lineDataParent.x,lineDataParent.y);
        parentAdjustmentsDownloaded();
        childAdjustmentsDownloaded();

        Log.i("firstClearPoint", "x: " + Double.toString(dpConverter.convertPixelToDp(lineDataChild.x)) +" y: "
                + Double.toString(dpConverter.convertPixelToDp(lineDataChild.y)));

          //  path.lineTo((float) lineDataChild.x, (float) lineDataChild.y);

         isDirectPathClear(idea.generateDistanceMap(lineDataParent,lineDataChild),false);
    }



    private void pathFind() {
        Log.i("DrawLine","h");
        distanceMap = axisCheck();
        tagLineStart(distanceMap.parentX,distanceMap.parentY);
        Log.i("parentXDistance","x: " + Double.toString(distanceMap.parentX));
        parentAdjustments(distanceMap);
        firstClearPoint = isDirectPathClear(distanceMap,true);
        if (firstClearPoint != null) {
            parsePointPixel(firstClearPoint);
            childAdjustments(firstClearPoint);
            parsePointDp(distanceMap.parentVector);
            parsePointDp(distanceMap.ideaVector);
            idea.setGradient(distanceMap);
            idea.setLineMap(distanceMap,firstClearPoint);
        } else {
            DistanceMap distanceMapCorner = cornerCheck();
            idea.setGradient(distanceMapCorner);
            tagLineStart(distanceMapCorner.parentX,distanceMapCorner.parentY);
            parentAdjustments(distanceMapCorner);
            IdeaVector firstClearCornerPoint = isDirectPathClear(distanceMapCorner,true);
            Log.i("cornerAfter","x: " + Double.toString(dpConverter.convertPixelToDp(distanceMapCorner.parentX)) +
                    " y: " + Double.toString(dpConverter.convertPixelToDp(distanceMapCorner.parentY)));
            if (firstClearCornerPoint != null){
                Log.i("firstClearCornerPoint", "x: " + Double.toString(firstClearCornerPoint.x) +
                        " y: " + Double.toString(firstClearCornerPoint.y));
                parsePointPixel(firstClearCornerPoint);
                childAdjustments(firstClearCornerPoint);
                parsePointDp(distanceMapCorner.parentVector);
                parsePointDp(distanceMapCorner.ideaVector);
                idea.setLineMap(distanceMapCorner,firstClearCornerPoint);
            } else {
                Toast.makeText(mContext, "This branch is obstructed, please try a clear path", Toast.LENGTH_SHORT).show();
                isPathClear = false;
            }
        }
        }

    private void childAdjustments(IdeaVector firstClearCornerPoint) {
        float xOffset = 0;
        float yOffset = 0;

        if (firstClearCornerPoint.isTopRightVectorChild){
            Log.i("childadj", "isTopRightVectorChild");
           xOffset = -5;
           yOffset = 5;

        } else if (firstClearCornerPoint.isTopLeftVectorChild){
            Log.i("childadj", "isTopLeftVectorChild");
            xOffset = 5;
            yOffset = 5;
        } else if (firstClearCornerPoint.isBotRightVectorChild){
            Log.i("childadj", "isBotRightVectorChild");
            xOffset = -7;
            yOffset = -7;
        } else if (firstClearCornerPoint.isBotLeftVectorChild){
            Log.i("childadj", "isBotLeftVectorChild");
            xOffset = 6;
            yOffset = -5;
        } else if (firstClearCornerPoint.isTopVectorChild){
            Log.i("childadj", "isTopVectorChild");
             yOffset = -2;
            xOffset = -2;
        } else if (firstClearCornerPoint.isRightVectorChild){
            Log.i("childadj", "isRightVectorChild");
            yOffset = -5;
            xOffset = -4;
        }

        firstClearCornerPoint.x = (float) firstClearCornerPoint.x + (float)dpConverter.convertDpToPixel(xOffset);
        firstClearCornerPoint.y =  (float) firstClearCornerPoint.y + (float)dpConverter.convertDpToPixel(yOffset);
        path.lineTo((float) firstClearCornerPoint.x,(float) firstClearCornerPoint.y);

        Log.i("setLineMap", "firstClearCornerPoint.x " + Double.toString(firstClearCornerPoint.x) + "  firstClearCornerPoint.y " +
                Double.toString( firstClearCornerPoint.y));

    }

    private void childAdjustmentsDownloaded() {
        float xOffset = 0;
        float yOffset = 0;
        Log.i("childadj", "adjusting");
        if (idea.lineDataDownload.isTopRightVectorChild){
            Log.i("childadj", "isTopRightVectorChild");
            xOffset = -5;
            yOffset = 5;

        } else if (idea.lineDataDownload.isTopLeftVectorChild){
            Log.i("childadj", "isTopLeftVectorChild");
           // xOffset = 3;
           // yOffset = 3;
        } else if (idea.lineDataDownload.isBotRightVectorChild){
            Log.i("childadj", "isBotRightVectorChild");
           xOffset = 4;
            yOffset = 4;
        } else if (idea.lineDataDownload.isBotLeftVectorChild){
            Log.i("childadj", "isBotLeftVectorChild");
           xOffset = -3;
           yOffset = 4;
        } else if (idea.lineDataDownload.isTopVectorChild){
            Log.i("childadj", "isTopVectorChild");
            yOffset = -1;
        } else if (idea.lineDataDownload.isRightVectorChild){
            Log.i("childadj", "isRightVectorChild");
           // yOffset = -5;
        }

        lineDataChild.x = (float)  lineDataChild.x + (float)dpConverter.convertDpToPixel(xOffset);
        Log.i("childadj", "adding a y offset of " + Float.toString(yOffset));
        Log.i("childadj", "adding an x offset of " + Float.toString(xOffset));
        lineDataChild.y=  (float) lineDataChild.y + (float)dpConverter.convertDpToPixel(yOffset);
        path.lineTo((float)lineDataChild.x,(float)lineDataChild.y);


    }

    private void parentAdjustmentsDownloaded() {
        float xOffset = 0;
        float yOffset = 0;
        Log.i("parentAdjustments", "parentAdjustmentsDownloaded");
        if (idea.lineDataDownload.isBotLeftCornerVector){
            xOffset = 5;
            yOffset = -2;
            Log.i("parentAdjustments", "distanceMapCorner.isBotLeftCornerVector");
        } else if (idea.lineDataDownload.isBotRightCornerVector) {
           xOffset = -1;
            yOffset =-1;
            Log.i("parentAdjustments", "distanceMapCorner.isBotRightCornerVector");
        } else if (idea.lineDataDownload.isTopLeftVectorParent){
          //  xOffset = 10;
          //  yOffset =10;
            Log.i("parentAdjustments", "distanceMapCorner.isTopLeftVectorParent");
        } else if (idea.lineDataDownload.isTopRightVectorParent){
            Log.i("parentAdjustments", "distanceMapCorner.isTopRightVectorParent");
            xOffset = 3;
            yOffset =-2;
        } else if (idea.lineDataDownload.isBotVectorParent){
            Log.i("parentAdjustments", "distanceMapCorner.isBotVectorParent");
           // yOffset =-5;
        } else if (idea.lineDataDownload.isTopVectorParent){
            Log.i("parentAdjustments", "distanceMapCorner.isTopVectorParent");
            yOffset =4;
        }

        lineDataParent.x = (float)  lineDataParent.x  + (float)dpConverter.convertDpToPixel(xOffset);
        lineDataParent.y = (float)lineDataParent.y+(float)dpConverter.convertDpToPixel(yOffset);
        path.moveTo((float) lineDataParent.x,(float) lineDataParent.y);


    }

    private void parentAdjustments(DistanceMap distanceMapCorner) {
        float xOffset = 0;
        float yOffset = 0;
        if (distanceMapCorner.isBotLeftCornerVector){
         xOffset = 5;
         yOffset = -2;
        } else if (distanceMapCorner.isBotRightCornerVector) {
            xOffset = -17;
            yOffset =-1;
        } else if (distanceMapCorner.isTopLeftVectorParent){
            xOffset = 7;
            yOffset =7;

        } else if (distanceMapCorner.isTopRightVectorParent){
            yOffset =7;
            xOffset = -7;

        } else if (distanceMapCorner.isBotVectorParent){
           // yOffset =-5;
        } else if (distanceMapCorner.isTopVectorParent){
            Log.i("parentAdjustments", "distanceMapCorner.isTopVectorParent");
            yOffset =4;
        }
        distanceMapCorner.xOffset = xOffset;
        distanceMapCorner.yOffset = yOffset;
        distanceMapCorner.parentX = (float)distanceMapCorner.parentX  + (float)dpConverter.convertDpToPixel(xOffset);
        distanceMapCorner.parentY = (float)distanceMapCorner.parentY+(float)dpConverter.convertDpToPixel(yOffset);
        path.moveTo((float) distanceMapCorner.parentX,(float) distanceMapCorner.parentY);
    }

    private void tagLineStart(double parentX, double parentY) {
        lineStart = new IdeaVector(parentX,parentY);
        parsePointDp(lineStart);
    }


    private DistanceMap axisCheck() {
        Log.i("DrawLine","i");
        ArrayList<IdeaVector> vectors = new ArrayList<>();
        ArrayList<DistanceMap> mapList = new ArrayList<>();
        distanceMapList = new HashMap<>();
        rightVector = new IdeaVector(parentIdea.topRightVector.x
                , parentIdea.topRightVector.y + ideaPixelHeight/2,idea,ideaPixelHeight,ideaPixelWidth,mContext,true);
        Log.i("cornerLeft","x " + Double.toString(parentIdea.topLeftVector.x)+
                "y "+ Double.toString(parentIdea.topLeftVector.y));
        vectors.add(rightVector);
        leftVector =new IdeaVector(parentIdea.topLeftVector.x , parentIdea.topLeftVector.y
                + ideaPixelHeight/2,idea,ideaPixelHeight,ideaPixelWidth,mContext,true);
        vectors.add(leftVector);
        leftVector.isLeftVectorChild = true;
        botVector = new IdeaVector(parentIdea.bottomLeftVector.x+ideaPixelWidth/2
                , parentIdea.bottomLeftVector.y,idea,ideaPixelHeight,ideaPixelWidth,mContext,true);
        botVector.isBotVectorParent = true;
        vectors.add(botVector);
        topVector = new IdeaVector(parentIdea.topLeftVector.x + ideaPixelWidth/2,
                parentIdea.topLeftVector.y,idea,ideaPixelHeight,ideaPixelWidth,mContext,true);
        topVector.isTopVectorParent = true;
        vectors.add(topVector);
        for (int i=0; i<vectors.size();i++) {
            mapList.add(vectors.get(i).distanceCalculator(false));
            distanceMapList.put(mapList.get(i).getDistance(),mapList.get(i));
        }

        distances = new ArrayList<>(distanceMapList.keySet());
        Collections.sort(distances, new Comparator<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        });
        Log.i("distances", distances.toString());
        //returns smallest distance coordinates between ideas.
        return distanceMapList.get(distances.get(0));
    }





    private DistanceMap cornerCheck() {
        Log.i("DrawLine","j");
        ArrayList<IdeaVector> vectors = new ArrayList<>();
        ArrayList<DistanceMap> mapList = new ArrayList<>();
        distanceMapList = new HashMap<>();
        IdeaVector topRightVector = new IdeaVector(parentIdea.topRightVector.x, parentIdea.topRightVector.y
                ,idea,ideaPixelHeight,ideaPixelWidth,mContext,false);
        topRightVector.isTopRightVectorParent = true;
        vectors.add(topRightVector);
        IdeaVector topLeftVector =new IdeaVector(parentIdea.topLeftVector.x , parentIdea.topLeftVector.y
                ,idea,ideaPixelHeight,ideaPixelWidth,mContext,false);
        topLeftVector.isTopLeftVectorParent = true;
        vectors.add(topLeftVector);
        IdeaVector botLeftVector = new IdeaVector(parentIdea.bottomLeftVector.x
                , parentIdea.bottomLeftVector.y,idea,ideaPixelHeight,ideaPixelWidth,mContext,false);
        botLeftVector.isBotLeftCornerVector = true;
        vectors.add(botLeftVector);
        IdeaVector botRightVector = new IdeaVector(parentIdea.bottomRightVector.x + 20,
                parentIdea.bottomRightVector.y,idea,ideaPixelHeight,ideaPixelWidth,mContext,false);
        botRightVector.isBotRightCornerVector = true;
        vectors.add(botRightVector);
        for (int i=0; i<vectors.size();i++) {
            mapList.add(vectors.get(i).distanceCalculator(false));
            distanceMapList.put(mapList.get(i).getDistance(),mapList.get(i));
        }

        distances = new ArrayList<>(distanceMapList.keySet());
        Collections.sort(distances, new Comparator<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        });
        Log.i("distances", distances.toString());
        //returns smallest distance coordinates between ideas.
        return distanceMapList.get(distances.get(0));
    }

    //checks to see if simple direct path is clear for line
    private IdeaVector isDirectPathClear(DistanceMap distanceMap, Boolean collisionCheck) {
        Log.i("DrawLine","k");
        Log.i("maxDistance", Double.toString(distanceMap.getDistance()));
        double maxDistance = dpConverter.convertPixelToDp(distanceMap.getDistance());
        double currentDistance = 0;
        double x = 0;
        double y = 0;
        collisions = new ArrayList<>();
        currentPoints = new ArrayList<>();
        IdeaVector currentPoint = new IdeaVector(distanceMap.parentX , distanceMap.parentY);
        Log.i("pointCollision","starting point "+"x: " + Double.toString( dpConverter.
                convertPixelToDp(distanceMap.parentX))+ " y: "
                + Double.toString(dpConverter.convertPixelToDp(distanceMap.parentY)));
        parsePointDp(currentPoint);
        Log.i("currentPoint", "x: " + Double.toString(currentPoint.x) +
                " y: " + Double.toString(currentPoint.y));
        Log.i("ideavec", "x: " + Double.toString(distanceMap.ideaVector.x) +
                " y: " + Double.toString(distanceMap.ideaVector.y));
        int offset = 5;
        while (currentDistance <= maxDistance) {

            Log.i("currentDistance", Double.toString(currentDistance));

            if (distanceMap.isEndXLessThanParent()) {
                x -= distanceMap.normalisedUnitX * increment;
                Log.i("EndXLessThanParent", Double.toString(distanceMap.normalisedUnitX));
            } else {
                x += distanceMap.normalisedUnitX * increment;
            }
            if (distanceMap.isEndYLessThanParent()) {
                y -= distanceMap.normalisedUnitY * increment;
            } else {
                y += distanceMap.normalisedUnitY * increment;
                Log.i("EndYMoreThanParent", Double.toString(distanceMap.normalisedUnitY));
            }
            IdeaVector nextPoint = new IdeaVector(currentPoint.x + x, currentPoint.y + y);
            Log.i("nextpointPoint", "x: " + Double.toString(nextPoint.x) +
                    " y: " + Double.toString(nextPoint.y));
            if (collisionCheck) {
                if (checkCollision(nextPoint)) {
                    Log.i("outsideCheck", "test");
                    return null;
                }
            } else {
                currentPoints.add(new CollisionMap(nextPoint,false));
            }
            currentDistance += distanceMap.normalisedMag * increment;
            Log.i("normalisedMag", Double.toString(distanceMap.normalisedMag));
        }
            if (collisionCheck) {
                for (CollisionMap collision : currentPoints) {
                    collisions.add(collision.getCollision());
                }

                if (!collisions.contains(true)) {
                    parsePointDp(distanceMap.ideaVector);
                    return distanceMap.ideaVector;
                }
            }
         return null;
    }

    private Boolean checkCollision(IdeaVector nextPoint) {
        Log.i("DrawLine","l");
        CollisionMap collisionMap = new CollisionMap(nextPoint,true);
        currentPoints.add(collisionMap);
        Log.i("checkCollision","size of ideas found " + Integer.toString(collidingIdeas.size()));
        Log.i("checkCollision","next point x " + Double.toString(nextPoint.x)+"next point y " + Double.toString(nextPoint.y));
        for (int i = 0; i < collidingIdeas.size(); i++){
            Log.i("checkCollision","position of analysed collided rect: " + Integer.toString(i));
            Log.i("checkCollision","left " + Integer.toString( collidingIdeas.get(i).left)+
                    " right " + Integer.toString( collidingIdeas.get(i).right)
                    + " top " + Integer.toString( collidingIdeas.get(i).top) + " bot "
                    + Integer.toString( collidingIdeas.get(i).bottom));
            if (nextPoint.x < collidingIdeas.get(i).right  && nextPoint.x > collidingIdeas.get(i)
                    .left && nextPoint.y > collidingIdeas.get(i).top + 7 && nextPoint.y < collidingIdeas
                    .get(i).bottom - 7){

                Log.i("pointCollision","point checked "+"x: " + Double.toString(nextPoint.x)+ " y: " + Double.toString(nextPoint.y));
                Log.i("pointCollision","idea point bounds: " + "right " + Double.toString(collidingIdeas.get(i).right)+
                        " left " + Double.toString(collidingIdeas.get(i).left)+ " top " + Double.toString(collidingIdeas.get(i).top)+
                        " bottom " + Double.toString(collidingIdeas.get(i).bottom));
                Log.i("collidingIdeas","position of collided rect: " + Integer.toString(i));

                collisionMap.addCollision(true);
                return true;
            } else {
                collisionMap.addCollision(false);

            }
        }
        return false;
}




    private void parsePointDp(IdeaVector point) {
        point.x = dpConverter.convertPixelToDp(point.x);
        point.y = dpConverter.convertPixelToDp(point.y);
    }

    private void parsePointPixel(IdeaVector point) {
        point.x = dpConverter.convertDpToPixel(point.x);
        point.y = dpConverter.convertDpToPixel(point.y);
    }



    public DrawLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.
                LAYOUT_INFLATER_SERVICE);
    }

    public DrawLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        widthMeasureSpec = (int)dpConverter.convertDpToPixel(IdeaTreeActivity.IdeaTreeWidth);
        heightMeasureSpec = (int)dpConverter.convertDpToPixel(IdeaTreeActivity.IdeaTreeHeight);
        Log.i("onMeasure","widthspec " + Integer.toString(widthMeasureSpec)+
                "heightspec " + Integer.toString(heightMeasureSpec));
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(path,paint);


    }
}
