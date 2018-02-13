package com.parse.ideanetwork;



import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;

import android.util.AttributeSet;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;

import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;


import java.util.ArrayList;


//TODO needs to extend constraint layout
public class Idea extends ConstraintLayout{



    View view;

    private static final String IDEA_TAG = "idea";
    public static final int heightDp = 175;
    public static final int widthDp = 185;
    public static final int heightDpWidgets = 84;
    public static final int widthDpWidgets = 10;
    public static final int rightOffset = 14;
    public int width;
    public int height;
    Context mContext;

    int topMargin;
    int leftMargin;

    public IdeaVector topLeftVector;
    public IdeaVector topRightVector;
    public IdeaVector bottomLeftVector;
    public IdeaVector bottomRightVector;
    public String id;
    String ideaTreeID = "";

    String parentId = "";
    String randomId = "";
    TextView author;
    TextView summary;
    String summaryText;
    String descriptionText;

    ImageButton upVote;
    ImageButton downVote;
    ImageButton comments;
    TextView totalVote;
    DpConverter dpConverter;
    public LineData lineDataDownload;
    LineData lineData;

    int voteTotal;
    String ideaAuthor;
    String lineageId;
    double rise;
    double run;

    int level;
    boolean secondaryIdea;
    ArrayList<String> inheritedIds;

    public boolean dwnActive = false;
    public boolean upActive = false;
    public boolean isBotRightCorner;
    ImageButton profileBadge;
    LinearLayout node1;
    LinearLayout node2;
    LinearLayout node3;
    LinearLayout node4;
    LinearLayout node5;
    LinearLayout node6;
    LinearLayout node7;
    LinearLayout node8;

    String color;
    LayoutInflater inflater;

    int thumbsUpId;
    int thumbsDwnId;

    int thumbsUpNormId;
    int thumbsDwnNormId;

    static String purpleColor = "#f06292";
    static String pinkColor = "#e1bee7";
    static String deepPurpleColor = "#9575cd";
    static String indigoColor = "#7986cb";
    static String blueColor = "#64b5f6";
    static String lightBlueColor = "#4fc3f7";
    static String cyanColor = "#4dd0e1";
    static String tealColor = "#4db6ac";
    static String greenColor = "#81c784";
    static String lightGreenColor = "#aed581";
    public static String mainIdeaColor = "pink";

    public String colorValue;





    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public Idea(Context context, @Nullable AttributeSet attrs,@Nullable String ideaTreeID,   String  id
               ) {
        super(context, attrs);

        this.ideaTreeID = ideaTreeID;
        this.id = id;
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.
                LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.idea,this,true);

       summary = (TextView)view.findViewById(R.id.summary);
       author = (TextView)view.findViewById(R.id.author);
       node1 = (LinearLayout)view.findViewById(R.id.node1);
        node2 = (LinearLayout)view.findViewById(R.id.node2);
        node3 = (LinearLayout)view.findViewById(R.id.node3);
        node4 = (LinearLayout)view.findViewById(R.id.node4);
        node5 = (LinearLayout)view.findViewById(R.id.node5);
        node6 = (LinearLayout)view.findViewById(R.id.node6);
        node7 = (LinearLayout)view.findViewById(R.id.node7);
        node8 = (LinearLayout)view.findViewById(R.id.node8);

       profileBadge = (ImageButton)view.findViewById(R.id.profileBadge);
       profileBadge.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               setProfileClick();
           }
       });
       upVote = (ImageButton)view.findViewById(R.id.upVote);
       upVote.setTag(view);
       totalVote = (TextView)view.findViewById(R.id.totalVote);
       downVote = (ImageButton)view.findViewById(R.id.downVote);
       downVote.setTag(view);
       comments = (ImageButton)view.findViewById(R.id.comments);
       comments.setTag(view);
       dpConverter = new DpConverter(mContext);
       setIdeaTags();
       getIdeaDimensions();

    }

    public void setColorTheme(String color) {
        this.color = color;
        String colorCode = "fffff";
        switch (color){
            case ("purple"):
                Log.i("setColorTheme", "purple");
                colorCode = purpleColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_purple);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_purple);
                comments.setBackgroundResource(R.drawable.ic_chat_purple);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_purple);
                setNodeBackgrounds(R.drawable.circle_purple);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_purple;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_purple;
                thumbsUpNormId = R.drawable.ic_thumbs_up_purple;
                thumbsDwnNormId = R.drawable.ic_thumb_down_purple;
                break;
            case ("pink"):
                Log.i("setColorTheme", "pink");
                colorCode = deepPurpleColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_pink);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_pink);
                comments.setBackgroundResource(R.drawable.ic_chat_pink);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_pink);
                setNodeBackgrounds(R.drawable.circle);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_pink;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_pink;
                thumbsUpNormId = R.drawable.ic_thumbs_up_pink;
                thumbsDwnNormId = R.drawable.ic_thumb_down_pink;
                break;
            case ("indigo"):
                Log.i("setColorTheme", "indigo");
                colorCode = indigoColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_indigo);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_indigo);
                comments.setBackgroundResource(R.drawable.ic_chat_indigo);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_indigo);
                setNodeBackgrounds(R.drawable.circle_indigo);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_indigo;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_indigo;
                thumbsUpNormId = R.drawable.ic_thumbs_up_indigo;
                thumbsDwnNormId = R.drawable.ic_thumb_down_indigo;
                break;
            case ("blue"):
                Log.i("setColorTheme", "blue");
                colorCode = blueColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_blue);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_blue);
                comments.setBackgroundResource(R.drawable.ic_chat_blue);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_blue);
                setNodeBackgrounds(R.drawable.circle_blue);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_blue;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_blue;
                thumbsUpNormId = R.drawable.ic_thumbs_up_blue;
                thumbsDwnNormId = R.drawable.ic_thumb_down_blue;
                break;
            case ("light blue"):
                Log.i("setColorTheme", "lightBlueColor");
                colorCode = lightBlueColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_lb);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_lb);
                comments.setBackgroundResource(R.drawable.ic_chat_lb);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_lb);
                setNodeBackgrounds(R.drawable.circle_lb);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_lb;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_lb;
                thumbsUpNormId = R.drawable.ic_thumbs_up_lb;
                thumbsDwnNormId = R.drawable.ic_thumb_down_lb;
                break;
            case ("cyan"):
                Log.i("setColorTheme", "cyan");
                colorCode = cyanColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_cyan);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_cyan);
                comments.setBackgroundResource(R.drawable.ic_chat_cyan);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_cyan);
                setNodeBackgrounds(R.drawable.circle_cyan);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_cyan;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_cyan;
                thumbsUpNormId = R.drawable.ic_thumbs_up_cyan;
                thumbsDwnNormId = R.drawable.ic_thumb_down_cyan;
                break;
            case ("teal"):
                Log.i("setColorTheme", "teal");
                colorCode = tealColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_teal);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_teal);
                comments.setBackgroundResource(R.drawable.ic_chat_teal);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_teal);
                setNodeBackgrounds(R.drawable.circle_teal);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_teal;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_teal;
                thumbsUpNormId = R.drawable.ic_thumbs_up_teal;
                thumbsDwnNormId = R.drawable.ic_thumb_down_teal;
                break;
            case ("green"):
                Log.i("setColorTheme", "green");
                colorCode = greenColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_green);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_green);
                comments.setBackgroundResource(R.drawable.ic_chat_green);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_green);
                setNodeBackgrounds(R.drawable.circle_green);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_green;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_green;
                thumbsUpNormId = R.drawable.ic_thumbs_up_green;
                thumbsDwnNormId = R.drawable.ic_thumb_down_green;
                break;
            case ("light green"):
                Log.i("setColorTheme", "green");
                colorCode = lightGreenColor;
                upVote.setBackgroundResource(R.drawable.ic_thumbs_up_lg);
                downVote.setBackgroundResource(R.drawable.ic_thumb_down_lg);
                comments.setBackgroundResource(R.drawable.ic_chat_lg);
                profileBadge.setBackgroundResource(R.drawable.ic_user_profile_lg);
                setNodeBackgrounds(R.drawable.circle_lg);
                thumbsDwnId = R.drawable.ic_thumb_down_activated_lg;
                thumbsUpId = R.drawable.ic_thumbs_up_activated_lg;
                thumbsUpNormId = R.drawable.ic_thumbs_up_lg;
                thumbsDwnNormId = R.drawable.ic_thumb_down_lg;
                break;
        }
        colorValue = colorCode;
        Log.i("setColorTheme", "colorCode " + colorCode);
        summary.setBackgroundColor(Color.parseColor(colorCode));
        author.setBackgroundColor(Color.parseColor(colorCode));
        totalVote.setBackgroundColor(Color.parseColor(colorCode));
    }

    private void setNodeBackgrounds(int circle_purple) {
        node1.setBackgroundResource(circle_purple);
        node2.setBackgroundResource(circle_purple);
        node3.setBackgroundResource(circle_purple);
        node4.setBackgroundResource(circle_purple);
        node5.setBackgroundResource(circle_purple);
        node6.setBackgroundResource(circle_purple);
        node7.setBackgroundResource(circle_purple);
        node8.setBackgroundResource(circle_purple);

    }

    public void setActivatedUpVotes(){
        upVote.setBackgroundResource(thumbsUpId);


    }

    public void setActivatedDwnVotes(){
        downVote.setBackgroundResource(thumbsDwnId);
    }

    public  void setNormUpVotes(){
        upVote.setBackgroundResource(thumbsUpNormId);
    }

    public void setNormDwnVotes(){
        downVote.setBackgroundResource(thumbsDwnNormId);
    }

    private void setProfileClick() {
        Intent intent = new Intent(mContext,
                ProfileReadOnlyActivity.class);
        intent.putExtra("name",author.getText().toString());
        mContext.startActivity(intent);
    }

    public Idea(Context context){
        super(context);
        dpConverter = new DpConverter(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public Idea(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setJSONLineMap(String lineMap){
        Log.i("setJSONLineMap", "setting");
        Gson gson = new Gson();
        lineDataDownload = gson.fromJson(lineMap,LineData.class);
    }

    public void setLineMap(DistanceMap distanceMap,IdeaVector ideaVector){
        lineData = new LineData(dpConverter.convertPixelToDp( distanceMap.parentX),
                dpConverter.convertPixelToDp( distanceMap.parentY)
                ,distanceMap.ideaVector.x,distanceMap.ideaVector.y);
        lineData.setLinePos(distanceMap.linePos);

        if (ideaVector.isTopRightVectorChild) {
            lineData.isTopRightVectorChild = true;
        } else if (ideaVector.isTopLeftVectorChild){
            lineData.isTopLeftVectorChild = true;
        } else if (ideaVector.isBotRightVectorChild){
            lineData.isBotRightVectorChild = true;
        }else if (ideaVector.isBotLeftVectorChild){
            lineData.isBotLeftVectorChild = true;
        }else if (ideaVector.isRightVectorChild){
            lineData.isRightVectorChild = true;
        }else if (ideaVector.isTopVectorChild){
            lineData.isTopVectorChild = true;
        }

        if (distanceMap.isBotLeftCornerVector){
            Log.i("setLineMap", "isBotLeftCornerVector");
            lineData.isBotLeftCornerVector = true;
        } else if (distanceMap.isBotRightCornerVector){
            Log.i("setLineMap", "isBotRightCornerVector");
            lineData.isBotRightCornerVector = true;
        } else if (distanceMap.isTopLeftVectorParent){
            Log.i("setLineMap", "isTopLeftVectorParent");
            lineData.isTopLeftVectorParent = true;
        } else if (distanceMap.isTopRightVectorParent){
            Log.i("setLineMap", "isTopRightVectorParent");
            lineData.isTopRightVectorParent = true;
        } else if (distanceMap.isBotVectorParent){
            Log.i("setLineMap", "isBotVectorParent");
            lineData.isBotVectorParent = true;
        } else if (distanceMap.isTopVectorParent){
            Log.i("setLineMap", "isTopVectorParent");
            lineData.isTopVectorParent = true;
        }
        Log.i("setLineMap", "in Idea " +"x: " + Double.toString(   distanceMap.ideaVector.x) +
                " y: " + Double.toString(   distanceMap.ideaVector.y));
    }

    public void setWidth(int width){
        this.width = width;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public void setLevel(int l){
        level = l;
    }

    public void setAuthor(String user){
        ideaAuthor = user;
        author.setText(ideaAuthor);
    }

    public void setLineageId(String lineageId){
        this.lineageId = lineageId;
    }

    public void setInheritedIds(Idea parentIdea) {
        inheritedIds = new ArrayList<>();
        if (parentIdea != null) {
            if (!parentIdea.id.equals("Main")) {
                Log.i("setInheritedIds", "parent " + parentIdea.randomId);
                if (parentIdea.inheritedIds != null) {
                    inheritedIds.addAll(parentIdea.inheritedIds);
                }
            } else {
                inheritedIds.add(parentIdea.randomId);
            }
            inheritedIds.add(randomId);
        }
    }



    //TODO may not need this, perhaps review and delete
    public void setIdeaTags() {
        if (descriptionText != null) {
            Log.i("setIdeaTags", descriptionText);
            view.setTag(descriptionText);
        }
    }

    //gets ID for IdeaTree this idea belongs to
    public String getIdeaTreeID(){
        return ideaTreeID;
    }



    public void getIdeaDimensions(){
        view.post(new Runnable() {
            @Override
            public void run() {
                Log.i("dimensions", Integer.toString(view.getWidth())+ "h: " +
                        Integer.toString(view.getHeight()));
            }
        });
    }

    public void setGradient(DistanceMap distanceMap){
        rise = distanceMap.normalisedRawUnitY;
        run = distanceMap.normalisedRawUnitX;
    }

    //setters for idea positioning

    public void setTopMargin(int topMargin){
        this.topMargin = topMargin;
    }

    public void setLeftMargin(int leftMargin){
        this.leftMargin = leftMargin;
    }



   public Rect setVectorCoordinates(Idea idea, IdeaTreeActivity.IdeaAge age){
        initVectors();
        if (idea.getIdentifier().equals("Child") && age != IdeaTreeActivity.IdeaAge.OLD) {
            return topLeftVector.createRect(false,width,height);
        } else {
            return null;
        }

   }

   public void setVectorCoordinatesAlias(){
       initVectors();
   }

   private void initVectors(){
       topLeftVector = new IdeaVector(leftMargin,topMargin,mContext);
       topRightVector = new IdeaVector(leftMargin + width
               ,topMargin,mContext);
       bottomLeftVector = new IdeaVector(leftMargin,topMargin+height,mContext);
       Log.i("botIdea", Integer.toString(height));
       bottomRightVector = new IdeaVector(leftMargin+width ,topMargin+height,mContext);
   }


    public void setVote(Integer vote){
       voteTotal = vote;
       totalVote.setText(Integer.toString(voteTotal));

}

   //gets idea identifier as child or parent
    public String getIdentifier(){
        return id;
    }

    //sets the ideas parent id
    public void setParentId(String parentId){
        this.parentId = parentId;
    }

    public void setRandomId(String randomId){
        this.randomId = randomId;
    }


    public void setTitle(String title){
         summaryText = title;
         summary.setText(title);
    }


    public void parseToPixels(){
        setLeftMargin((int) dpConverter.convertDpToPixel(topLeftVector.x));
        setTopMargin((int) dpConverter.convertDpToPixel(topLeftVector.y));
        setWidth((int)dpConverter.convertDpToPixel(width));
        setHeight((int)dpConverter.convertDpToPixel(height));
        setVectorCoordinates(this, IdeaTreeActivity.IdeaAge.OLD);
    }

    public void parseToDp(){
        setLeftMargin((int) dpConverter.convertPixelToDp(topLeftVector.x));
        setTopMargin((int) dpConverter.convertPixelToDp(topLeftVector.y));
        setWidth((int)dpConverter.convertPixelToDp(width));
        setHeight((int)dpConverter.convertPixelToDp(height));
        setVectorCoordinates(this, IdeaTreeActivity.IdeaAge.OLD);
    }

    public DistanceMap generateDistanceMap(IdeaVector parent, IdeaVector child){
        DistanceMap distanceMap = new DistanceMap(child);
        distanceMap.setParentVector(parent.x,parent.y);
        distanceMap.distanceGen();
        distanceMap.vectorNormalise();
        return distanceMap;
    }

    public void disableButton(String key){
        if (key.equals("voteConUp")){
            upActive = true;
            dwnActive = false;
        } else {
            dwnActive = true;
            upActive = false;
        }
    }

    public void resetButtons(){
        upActive = false;
        dwnActive = false;
    }

    public void setDescription(String description){
        descriptionText = description;
    }




}

