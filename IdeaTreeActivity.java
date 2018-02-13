package com.parse.ideanetwork;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ideanetwork.Collation.Alias;
import com.parse.ideanetwork.Messaging.MessageContainer;
import com.parse.ideanetwork.Messaging.MessagingAdapter;
import com.parse.ideanetwork.Messaging.PopUp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import  android.os.Handler;

public class IdeaTreeActivity extends AppCompatActivity implements View.OnLongClickListener {

    RelativeLayout rl;
    public static final int IdeaTreeWidth = 10000;
    public static final int IdeaTreeHeight = 10000;

    public static final int minSpace = Idea.widthDp/10;
    public static final int rightVectorOffset = 40;
    //TODO change interval to every second
    public static final long intervalQuery = 1000;
    public static int startingLocation = 5000;
    private int startingLocationPx;
    private int startingLocationPy;
    DpConverter dpConverter;
    DisplayMetrics displayMetrics;
    Intent intent;
    String ideaTreeID;
    int replaceIndex;
    String mainID;
    String replyTotal;
    Rect rect;
    MessageContainer mc;
    List<String> availableColors;
    ParseObject mainObject;


    boolean weirdCase;
    Map<String,Idea> ideaHashMap;
    Map<String,Alias> aliasMap;
    public ArrayList<Idea> ideas;
    Idea idea;
    ArrayList<Rect> collidingIdeas;
    public  ArrayList<Boolean> safeToAdd;
    ArrayList<Boolean> hasLineCollided;
    ArrayList<String> ideaTags;
    ArrayList<String>ideaIDs;
    ArrayList<DrawLine> lines;
    ArrayList<DrawLine> collidingLines;
    ArrayList<MessageContainer> messageContainers;
    Map<String,MessageContainer> replyContainers;
    ArrayList<MessageContainer> mainContainers;
    List<String> replyIDs;

    MessageContainer replyContainer;
    DrawLine connectingLine;
    RelativeLayout.LayoutParams params;
    Idea parentIdea;
    public int width = 0;
    public int height = 0;
    Idea mainIdea;
    Idea localIdea;
    Rect mainIdeaRect;
    List<ParseObject>wholeIdeas;
    List<Idea>existingIdeas;
    LayoutInflater inflater;

    Handler handler;
    Thread thread;
    String descriptionText;

    MessageContainer messageContainer;


    PopUp popUp;
    MessagingAdapter adapter;
    String commentId;
    String reply;
    SpannableString initialTag;
    boolean replyEnabled;
    boolean userInChat;
    boolean voteLock = false;
    ParseObject votedCommentObj;

    HScroll hsv;
    ArrayList<String> commentIds;
    List<String> voteContributors;

    boolean extractIdea;

    public enum IdeaAge{
    OLD,
    NEW
}



    //  @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DisplayAd(this);

        /*BIDIRECTIONAL SCROLLVIEW*/
        ScrollView sv = new ScrollView(this);
        hsv = new HScroll(this);
        hsv.sv = sv;
        /*END OF BIDIRECTIONAL SCROLLVIEW*/
        rl = new RelativeLayout(this);

        dpConverter = new DpConverter(this);
        sv.addView(rl, new RelativeLayout.LayoutParams((int) dpConverter.convertDpToPixel(IdeaTreeWidth),
                (int) dpConverter.convertDpToPixel(IdeaTreeHeight)));
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        hsv.addView(sv, params);
        setContentView(hsv);
        setTitle("");
        inflater = this.getLayoutInflater();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ideaHashMap = new HashMap<>();
        ideas = new ArrayList<>();
        ideaIDs = new ArrayList<>();

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        rl.setClipChildren(false);
        rl.setClipToPadding(false);
        rl.setBackgroundColor(Color.WHITE);


        ProgressBar progressBar = new ProgressBar(this);
        rl.addView(progressBar);


        startingLocationPx= (int)dpConverter.convertDpToPixel(startingLocation);
        startingLocationPy = (int)dpConverter.convertDpToPixel(startingLocation);
        Log.i("startingLocation", Integer.toString(startingLocation));
        height = (int) dpConverter.convertDpToPixel(Idea.heightDp);
        width = (int) dpConverter.convertDpToPixel(Idea.widthDp);
        lines = new ArrayList<>();



        intent = getIntent();
        ideaTreeID = intent.getStringExtra("IdeaTreeID");


        //query Idea class to extract idea data for display
        wholeIdeas = new ArrayList<>();
        getMainIdea();

        queryIdeas(ideaTreeID,null);
        queryAlias();
        FriendsRequests friendsRequests = new FriendsRequests(this);
        rl.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch(action){
                    case DragEvent.ACTION_DROP:
                        generateParams(event);
                        break;
                }
                return true;

            }
        });
        Tips tipList = new Tips(this);
        tipList.setActivity("Map");
        addContentView(tipList,tipList.params);
    }

    private void getMainIdea() {
        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Idea");
        q.whereEqualTo("Identifier","Main");
        q.whereEqualTo("IdeaTree",ideaTreeID);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null) {
                    if (objects != null) {
                        mainObject = objects.get(0);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PseudoUser");
        query.whereEqualTo("ParseUsername",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    if (objects != null) {
                        if (!objects.isEmpty()) {
                            objects.get(0).put("status","offline");
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null){
                                        ParseUser.logOut();
                                        Intent intent = new Intent(IdeaTreeActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        return super.onOptionsItemSelected(item);
    }

    private void queryAlias() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Alias");
        Log.i("queryAlias",ideaTreeID);
        query.whereEqualTo("IdeaTreeId",ideaTreeID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if(!objects.isEmpty()){
                        extractAliasData(objects);
                    } else {
                        Log.i("queryAlias","objects empty");
                    }
                } else {
                    Log.i("queryAlias","error " +e.getMessage());
                }
            }
        });
    }

    private void extractAliasData(List<ParseObject> objects) {
        aliasMap = new HashMap<>();
        for (ParseObject object:objects){
            Alias alias = new Alias(object.getNumber("x").doubleValue(),object.getNumber("y").doubleValue(),
                    object.getString("randomId"), object.getString("ideaTreeId"),this);
            alias.extractLineData(object.getString("LineMap"));
            aliasMap.put(alias.randomId,alias);
            alias.setVectors();
            DrawLine drawLine = new DrawLine(alias.parentVector,alias.aliasVector,this);
            lines.add(drawLine);
            rl.addView(drawLine);
            Log.i("alias", "x "+Double.toString(alias.x)+"y "+Double.toString(alias.y) +
                    "randomId "+ alias.randomId);
        }

    }

    private void activateRunnable() {
        handler = new Handler();
        thread = new Thread(){
            @Override
            public void run() {
                Log.i("thread", "inside");
                TimeSetter timeSetter = new TimeSetter();
                queryIdeas(ideaTreeID,timeSetter);
               /* if (userInChat) {
                    Log.i("activateRunnable", Long.toString(new TimeSetter().presentTime));
                    queryMessages(new TimeSetter());
                }*/
                handler.postDelayed(this,intervalQuery);
            }
        };
        thread.start();


    }

    private void queryIdeas(String ideaTreeID,@Nullable final TimeSetter timeSetter) {
        extractIdea = false;
        existingIdeas = new ArrayList<>();
        ParseQuery<ParseObject> query = new ParseQuery<>("Idea");
        query.whereEqualTo("IdeaTree",ideaTreeID);
        Log.i("idtest", ideaTreeID);
        if (timeSetter != null){
            Log.i("findTime","presentTime: " + Long.toString(timeSetter.presentTime));
            Log.i("findTime","beforeTime: " + Long.toString(timeSetter.beforeTime));
            query.whereLessThan("update",timeSetter.presentTime);
            query.whereGreaterThan("update",timeSetter.beforeTime);

        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.i("test", "works here");
                if (e == null) {
                    if (!objects.isEmpty()) {
                        Log.i("test", "queryIdeasTrue");
                                    //found all info related to stored ideas on parse

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        Log.i("xTest",Integer.toString(objects.size()));
                                        for (ParseObject object : objects) {
                                                if (timeSetter != null) {
                                                    if (!ideaIDs.isEmpty()){
                                                        if (ideaIDs.contains(object.getString("randomId"))){
                                                            Log.i("findTime", "extractIdea = true ");
                                                            extractIdea = true;
                                                        }
                                                    }

                                                    if (object.getNumber("creation") != null && object.getString("Author") != null){
                                                        Log.i("findTime", "obj msg: " + object.getString("Summary"));
                                                        if ((object.getNumber("creation").longValue()) >= timeSetter.beforeTime
                                                                && !object.getString("Author").
                                                                equals(ParseUser.getCurrentUser().getUsername())) {

                                                            Log.i("dateTestafter", Long.toString(object.getNumber("creation").longValue()));
                                                            if (!extractIdea) {
                                                                Log.i("findTime", "adding to whole ideas ");
                                                                wholeIdeas.add(object);
                                                            }
                                                        } else {
                                                            Log.i("wholeIdeas", "red");
                                                            updateExisting(object);
                                                            Log.i("idea has changed", object.getString("randomId"));
                                                        }
                                                } } else {
                                                    wholeIdeas.add(object);
                                                }

                                        }

                                        Log.i("findTime", "im here");
                            if (!wholeIdeas.isEmpty()) {
                                Log.i("findTime", "extracting for: " + wholeIdeas.get(wholeIdeas.size()-1).getString("Summary"));
                                   extractIdeaData(wholeIdeas);
                            } else {
                                Log.i("findTime", "whole ideas  empty");
                            }


                            if (timeSetter == null){
                                Log.i("thread", "running");
                                activateRunnable();
                            }


                        } else {
                                        Log.i("wholeIdeas", "version error");
                        }
                    } else {
                        Log.i("wholeIdeas", "objects is empty");
                        }
                }else{
                         Log.i("wholeIdeas", e.getMessage());
                            }
                        }

        });

    }

    private void updateExisting(ParseObject object) {
        //TODO add comment discrepency check
        localIdea = ideaHashMap.get(object.getString("randomId"));
        if (localIdea != null) {
            int newVote = object.getNumber("voteTotal").intValue();
            if (newVote != localIdea.voteTotal) {
                localIdea.setVote(newVote);
            }
        }
    }




    private void queryMessages(TimeSetter timeSetter) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
        query.whereEqualTo("randomId", commentId);
        if (timeSetter != null){
            Log.i("newMsg","ts is not null");
            query.whereLessThan("update",timeSetter.presentTime);
            Log.i("newPresentTime", Long.toString(timeSetter.presentTime));
            Log.i("newMsg", "commentIds " + commentIds.toString());
            query.whereNotContainedIn("messageId",commentIds);
            query.whereGreaterThan("update",timeSetter.beforeTime);
            Log.i("newbeforeTime", Long.toString(timeSetter.beforeTime));
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                    if (e==null){
                        if (!objects.isEmpty()){
                            for (ParseObject object : objects){
                                Log.i("newMsg", object.getString("message"));
                                    setMessageContainer(object);
                            }
                        } else {
                            Log.i("newMsg", "objects empty");
                        }
                    } else {
                        Log.i("newMsger", e.getMessage());
                    }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void generateParams(DragEvent event) {
        Log.i("availableColors", Integer.toString(availableColors.size()));
        if (parentIdea.getIdentifier().equals("Main")) {
            if (availableColors.size() > 0) {
                makeIdea(event);
            } else {
                Toast.makeText(this, "Max number of ideas for main idea has been reached.", Toast.LENGTH_SHORT).show();
            }
        } else {
            makeIdea(event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void makeIdea(DragEvent event) {
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        hasLineCollided = new ArrayList<>();
        collidingLines = new ArrayList<>();
        idea = new Idea(this, null, intent.getStringExtra("IdeaTreeID"), "Child");
        idea.setRandomId(new IDGenerator().generateID());
        idea.setOnLongClickListener(this);
        params.leftMargin = (int) dpConverter.convertPixelToDp((int) event.getX() - width / 2);
        params.topMargin = (int) dpConverter.convertPixelToDp((int) event.getY() - height / 2);
        parentIdea.parseToDp();
        rect = parentIdea.topLeftVector.createRect(false, Idea.widthDp, Idea.heightDp);
        parentIdea.parseToPixels();
        if (parentIdea.id.equals("Main")) {
            idea.secondaryIdea = true;
        }
        idea.setWidth(Idea.widthDp);
        idea.setHeight(Idea.heightDp);
        idea.setTopMargin(params.topMargin);
        idea.setLeftMargin(params.leftMargin);
        idea.setVote(0);
        idea.setInheritedIds(parentIdea);
        idea.setAuthor(ParseUser.getCurrentUser().getUsername());
        idea.setVectorCoordinates(idea, IdeaAge.NEW);
        if (parentIdea.randomId.equals(mainIdea.randomId)) {
            idea.setLineageId(new IDGenerator().generateID());
            idea.setLevel(1);
        } else {
            idea.setLineageId(parentIdea.lineageId);
            idea.setLevel(parentIdea.level + 1);
        }
        Log.i("rect", "left: " + Integer.toString(rect.left) + "right: " + Integer.toString(rect.right)
                + "bot: " + Integer.toString(rect.bottom) + "top: " + Integer.toString(rect.top));
        if ((idea.topLeftVector.x + Idea.widthDp / 2 < rect.right) &&
                (idea.topLeftVector.y + Idea.heightDp / 2 > rect.top) &&
                (rect.bottom > idea.topLeftVector.y + Idea.heightDp / 2) &&
                (rect.left < idea.topLeftVector.x + Idea.widthDp / 2)) {
            if (!lines.isEmpty()) {
                Log.i("lines", "lines is not empty");
                for (DrawLine line : lines) {
                    Log.i("linestartcoords", "linestartX " + Double.toString(line.lineStart.x) +
                            "linestartY " + Double.toString(line.lineStart.y));
                    if (line.lineStart.x < rect.right && line.lineStart.x > rect
                            .left && line.lineStart.y > rect.top && line.lineStart.y < rect.bottom) {
                        collidingLines.add(line);
                    }

                }
                if (!collidingLines.isEmpty()) {
                    for (DrawLine collidingLine : collidingLines) {
                        for (int i = 0; i < collidingLine.currentPoints.size(); i++) {
                            IdeaVector points = collidingLine.currentPoints.get(i).currentPoint;
                            if (points.x < idea.topRightVector.x && points.x > idea.topLeftVector.x
                                    && points.y > idea.topRightVector.y && points.y < idea.bottomRightVector.y) {
                                Toast.makeText(this, "Please find somewhere less cluttered.", Toast.LENGTH_SHORT).show();
                                hasLineCollided.add(true);
                                break;
                            } else {
                                Log.i("lineCollision", "not collided");
                                hasLineCollided.add(false);
                            }
                        }
                    }
                    if (!hasLineCollided.contains(true)) {
                        parseQuery(rect);
                    }
                } else {
                    parseQuery(rect);
                }
            } else {
                Log.i("lines", "lines is empty");
                parseQuery(rect);
            }
        } else {
            Toast.makeText(this, "Please find a spot closer", Toast.LENGTH_SHORT).show();
        }
    }

    private String selectColor() {
        if (parentIdea.id.equals("Main")){
            //choose random color from available
            Random rand = new Random();
            int max = availableColors.size();
            Log.i("randomNum", "max " + Integer.toString(max));

                int randomNum = rand.nextInt((max));
            Log.i("randomNum", "randomNum " + Integer.toString(randomNum));
                String availableColor = availableColors.get(randomNum);
                removeColor(availableColor);
                Log.i("randomNum", availableColor);
                return availableColor;

        } else {
            //choose parentIdea color
            Log.i("randomNum", "returning " + parentIdea.color);
            return parentIdea.color;
        }
    }

    private void removeColor(String availableColor) {
        Log.i("randomNum","availableColors befre " + availableColors.toString());
         availableColors.remove(availableColors.indexOf(availableColor));
         mainObject.put("Colors",availableColors);
        Log.i("randomNum","availableColors after " + availableColors.toString());
    }

    private void parseQuery(Rect rect) {
        //querying parse to find space for idea
        //TODO here add any more implementation to extract any extra data added to the idea at a later stage

        ParseQuery<ParseObject> query = new ParseQuery<>("Idea");
        query.whereEqualTo("IdeaTree", ideaTreeID);
        query.whereGreaterThan("y", rect.top);
        query.whereLessThan("y", rect.bottom);
        query.whereLessThan("x", rect.right);
        query.whereGreaterThan("x", rect.left);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    safeToAdd = new ArrayList<>();
                    if (objects.isEmpty()) {
                        Log.i("objectsparsequery","objects is empty");
                        addIdeaToParse();
                    } else {
                        collidingIdeas = new ArrayList<>();
                        ideaTags = new ArrayList<String>();
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject collidingIdea = objects.get(i);
                            if (!collidingIdea.getString("Identifier").equals("Main")) {
                                int collidingIdeaX = (int) collidingIdea.getNumber("x");
                                int collidingIdeaY = (int) collidingIdea.getNumber("y");
                                Log.i("collidingIdeas","position: " + Integer.toString(i)+"colliding rect id: " + collidingIdea.getString("randomId"));
                                IdeaVector collidingIdeaVector = new IdeaVector(collidingIdeaX, collidingIdeaY, IdeaTreeActivity.this);
                                collidingIdeas.add(collidingIdeaVector.createRect(true, Idea.widthDp+3, Idea.heightDp+3));
                            }
                        }

                        calculateOverlap();
                        if (!safeToAdd.contains(false)) {
                            popUp();
                        } else {
                            Toast.makeText(getApplicationContext(), "This Idea is too close to another, please find more space.", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
                }
        });
    }



    private void popUp() {
        idea.parseToPixels();
        idea.setColorTheme(selectColor());
        connectingLine = new DrawLine(this,idea,parentIdea,collidingIdeas);
        idea.parseToDp();
        if (connectingLine.isPathClear) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = inflater.inflate(R.layout.idea_pop_up, null);
            final EditText description = (EditText) view.findViewById(R.id.description);
            final EditText summary = (EditText) view.findViewById(R.id.summary);
            TextView textCount = (TextView) view.findViewById(R.id.textCount);
            final CustomTextWatcher  ctw = new CustomTextWatcher(textCount,summary);
            summary.addTextChangedListener(ctw);
            Button createButton = (Button) view.findViewById(R.id.create);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.show();
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ctw.charsLeft != ctw.maxChars) {
                        if (!ctw.maxCharsReached) {
                            dialog.dismiss();
                            idea.summary.setText(summary.getText().toString());
                            descriptionText = description.getText().toString();
                            idea.descriptionText = descriptionText;
                            setTapForMore(idea);
                            idea.setIdeaTags();
                            idea.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String s = (String) v.getTag();
                                    if (s != null && !s.equals("")) {
                                        Log.i("description", s);
                                        PopUp descriptionPop = new PopUp(IdeaTreeActivity.this);
                                        descriptionPop.activateDescriptionPopUp();
                                        descriptionPop.description.setText(s);

                                    } else {
                                        Toast.makeText(IdeaTreeActivity.this, "This idea has no further description.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            addIdeaToParse();
                        } else {
                            Toast.makeText(IdeaTreeActivity.this, "Max chars have been reached, please shorten your summary.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(IdeaTreeActivity.this, "Please provide more detail to your summary.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void setTapForMore(Idea idea) {
        if (!idea.descriptionText.equals("")){
            TextView tapForMore = (TextView) idea.findViewById(R.id.tapForMore);
            Log.i("tapForMore","visibility set");
            tapForMore.setVisibility(View.VISIBLE);
        }
    }


    public void addIdeaToLayout(){

                params.leftMargin = (int)dpConverter.convertDpToPixel(params.leftMargin);
                params.topMargin =  (int)dpConverter.convertDpToPixel(params.topMargin);
                rl.addView(idea, params);
                rl.addView(connectingLine);
                ideas.add(idea);
                ideaHashMap.put(idea.randomId,idea);
                collidingIdeas.clear();
                safeToAdd.clear();
        }


    private void calculateOverlap() {
        for (int i = 0; i < collidingIdeas.size(); i++) {
            if ((idea.topRightVector.x + rightVectorOffset < collidingIdeas.get(i).left) ||
                    (idea.bottomLeftVector.y < collidingIdeas.get(i).top - minSpace) ||
                    (collidingIdeas.get(i).right + minSpace < idea.bottomLeftVector.x) ||
                    (collidingIdeas.get(i).bottom + minSpace < idea.topLeftVector.y )){
                safeToAdd.add(true);
            } else{
                safeToAdd.add(false);
                Log.i("safeToAddBefore", "collidingleft "+ Integer.toString(collidingIdeas.get(i).left - minSpace)
                        + "collidingIdeaRight "+ Double.toString(idea.topRightVector.x));
            }

        }
        if (((idea.topRightVector.x-rightVectorOffset < mainIdeaRect.left-minSpace) ||
                (idea.bottomLeftVector.y < mainIdeaRect.top-minSpace) ||
                (mainIdeaRect.right +minSpace-10 + DrawLine.rightRectOffset < idea.bottomLeftVector.x) ||
                (mainIdeaRect.bottom+minSpace < idea.topLeftVector.y ))){
            safeToAdd.add(true);
        } else {
            safeToAdd.add(false);
        }
        Log.i("safeToAddAfter", safeToAdd.toString());
        ideaTags.clear();
    }


    //adds Idea data to parse
    public void addIdeaToParse() {
        if ((idea.topLeftVector.x >= 0)) {
            if (idea.topLeftVector.y >= 0) {

                if (connectingLine.isPathClear) {
                lines.add(connectingLine);
                final ParseObject parseObject = new ParseObject("Idea");
                 //TODO update   comments
                parseObject.put("x", idea.topLeftVector.x);
                parseObject.put("y", idea.topLeftVector.y);
                parseObject.put("IdeaTree", idea.getIdeaTreeID());
                parseObject.put("Identifier", idea.getIdentifier());
                parseObject.put("randomId", idea.randomId);
                parseObject.put("parentId",parentIdea.randomId);
                parseObject.put("lineageId", idea.lineageId);
                parseObject.put("creation",TimeSetter.getTime());
                parseObject.put("Description", idea.descriptionText);
                parseObject.put("level",idea.level);
                parseObject.put("run", idea.run);
                parseObject.put("rise", idea.rise);
                parseObject.put("update",TimeSetter.getTime());
                parseObject.put("voteTotal",idea.voteTotal);
                parseObject.put("Author",idea.ideaAuthor);
                parseObject.put("secondaryIdea",idea.secondaryIdea);
                parseObject.put("inheritedIds", idea.inheritedIds);

                parseObject.put("Color",idea.color);

                parseObject.put("Summary",idea.summary.getText().toString());
                Gson gson = new Gson();
                    Log.i("addingLineDataParse", "in addIdeaToParse "+"parentX " + Double.toString(idea.lineData.parentX)
                            + " parentY " +
                            Double.toString(idea.lineData.parentY));
                    String lineMapJSON = gson.toJson(idea.lineData);

                parseObject.put("LineMap",lineMapJSON);
                Log.i("test", parentIdea.randomId);
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("added", "successfully added to parse");
                            idea.parseToPixels();
                            Log.i("cornerTest", Double.toString(idea.topLeftVector.x) + "y: " +
                                    Double.toString(idea.topLeftVector.y));
                                    addIdeaToLayout();

                        } else {
                            Log.i("not added", e.getMessage());
                        }
                    }
                });
            }} else {
                Toast.makeText(this, "Please try somewhere with more space", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please try somewhere with more space", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public  void extractIdeaData(List<ParseObject> objects) {
        String ideaTreeID = "";
        String identifier = "";
        int x;
        int y;
        String randomId = "";
        String parentId = "";
        String summary = "";
        String description = "";
        String author = "";
        String lineageId = "";
        int level;
        Log.i("wholeExtract", Integer.toString(wholeIdeas.size()));
        for (int i = 0; i < objects.size(); i++) {
            ideaTreeID = objects.get(i).getString("IdeaTree");
            identifier = objects.get(i).getString("Identifier");
            x = (int)dpConverter.convertDpToPixel((int)objects.get(i).getNumber("x"));
            y = (int)dpConverter.convertDpToPixel((int)objects.get(i).getNumber("y"));
            Log.i("longfirst",objects.get(i).getCreatedAt().toString());
            randomId = objects.get(i).getString("randomId");
            ideaIDs.add(randomId);
            parentId = objects.get(i).getString("parentId");
            summary = objects.get(i).getString("Summary");
            description = objects.get(i).getString("Description");
            lineageId =  objects.get(i).getString("lineageId");
            level = objects.get(i).getNumber("level").intValue();
            Idea idea = new Idea(this, null, ideaTreeID, identifier);
            idea.setColorTheme(objects.get(i).getString("Color"));
            if (!idea.id.equals("Main")) {
                idea.rise = objects.get(i).getNumber("rise").doubleValue();
                idea.run = objects.get(i).getNumber("run").doubleValue();
            } else {
                availableColors = objects.get(i).getList("Colors");

            }

            idea.setWidth(width);
            idea.setHeight(height);
            idea.setLeftMargin(x);
            idea.setTopMargin(y);
            idea.setRandomId(randomId);
            idea.setParentId(parentId);
            idea.setTitle(summary);
            idea.setOnLongClickListener(this);
            idea.setDescription(description);
            idea.setIdeaTags();
            setTapForMore(idea);
            idea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = (String)v.getTag();
                    if (s!=null) {
                        Log.i("description", s);
                        PopUp descriptionPop = new PopUp(IdeaTreeActivity.this);
                        descriptionPop.activateDescriptionPopUp();
                        descriptionPop.description.setText(s);
                    } else {
                        Toast.makeText(IdeaTreeActivity.this, "This idea has no further description.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            idea.secondaryIdea = objects.get(i).getBoolean("secondaryIdea");
            idea.setLevel(level);
            idea.setLineageId(lineageId);
            idea.setVectorCoordinates(idea, IdeaAge.OLD);
            if (objects.get(i).getNumber("voteTotal") != null) {
                idea.setVote(objects.get(i).getNumber("voteTotal").intValue());
                if (objects.get(i).getList("voteConUp")!=null) {
                    if (objects.get(i).getList("voteConUp").contains(ParseUser.getCurrentUser().getUsername())) {
                        Log.i("upVoteset","true");
                        idea.setActivatedUpVotes();
                        idea.upActive = true;
                    }
                }
                if (objects.get(i).getList("voteConDwn")!=null) {
                    if (objects.get(i).getList("voteConDwn").contains(ParseUser.getCurrentUser().getUsername())) {
                        idea.setActivatedDwnVotes();
                        idea.dwnActive = true;
                    }
                }

                } else {
                idea.setVote(0);
            }
            author = objects.get(i).getString("Author");
            if (objects.get(i).getString("Author") != null){
                idea.author.setText(author);
            }
            idea.setJSONLineMap(objects.get(i).getString("LineMap"));
            ideas.add(idea);
            ideaHashMap.put(randomId, idea);
        }



        for (int i = 0; i < ideas.size(); i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            if (ideas.get(i).parentId != null){
                //TODO need to reset the parentIDs after calculating inheritance.
                Log.i("setInheritedIds","child " + ideas.get(i).randomId);
                ideas.get(i).setInheritedIds(ideaHashMap.get(ideas.get(i).parentId));
            }
            if (ideas.get(i).getIdentifier().equals("Main")) {
                mainIdea = ideas.get(i);
                params.leftMargin =  (int)mainIdea.topLeftVector.x;
                params.topMargin =  (int)mainIdea.topLeftVector.y;
                rl.addView( mainIdea, params);

                focusScreen();

                mainIdea.setTopMargin(startingLocationPy);
                mainIdea.setLeftMargin(startingLocationPx);
                mainIdea.setVectorCoordinates(ideas.get(i),IdeaAge.OLD);
                mainIdea.parseToDp();
                mainIdeaRect = mainIdea.topLeftVector.createRect(true,Idea.widthDp,Idea.heightDp);
                mainIdea.parseToPixels();
                Log.i("ideatreepath",Double.toString( mainIdea.topRightVector.x) + "y " + Double.toString(mainIdea.topRightVector.y-Idea.heightDp/2));


            } else {
                if (ideaHashMap.get(ideas.get(i).parentId) != null) {
                    params.leftMargin = (int)ideas.get(i).topLeftVector.x;
                    params.topMargin = (int)ideas.get(i).topLeftVector.y;
                    DrawLine drawLine = new DrawLine(this, ideas.get(i));
                    Log.i("ideaID", "hello");
                    Log.i("ideaID",ideas.get(i).randomId);
                    lines.add(drawLine);
                    rl.addView(ideas.get(i), params);
                    rl.addView(drawLine);
                    Log.i("ideaID","success");

                }
            }
        }
        ideas.clear();
        wholeIdeas.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void focusScreen() {
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
                LinearLayout focusNode = (LinearLayout) inflater.inflate(R.layout.focus_node,null);
                RelativeLayout.LayoutParams focusParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                focusParams.leftMargin =startingLocationPx+(int)(displayWidth*0.75);
                focusParams.topMargin = startingLocationPy+(int)(displayHeight*0.5);
                rl.addView(focusNode,focusParams);
                focusNode.setFocusable(true);
                focusNode.setFocusableInTouchMode(true);
                focusNode.requestFocus();
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onLongClick(final View v) {
        parentIdea = (Idea)v;
        Log.i("longclick",parentIdea.randomId);
        ClipData.Item item;
            item = new ClipData.Item((CharSequence)v.getTag());
            ClipData dragData = new ClipData((CharSequence)v.getTag(), new String[]{ClipDescription.
                    MIMETYPE_TEXT_PLAIN},item);
            CustomDragShadow myShadow = new CustomDragShadow(this,v);
            v.startDrag(dragData,myShadow,null,0);


        Log.i("longclick","left  " + Integer.toString(v.getLeft()) + " top   " + Integer.toString(v.getTop()));

        return false;
    }

    public void upVoteOnClick(View view){
        if (!voteLock) {
            voteLock = true;
            Log.i("onClickUpVoteMsg", "click");
            Idea idea  = (Idea) view.getTag();
            boolean b = false;

            if (!idea.dwnActive) {
                if (!idea.upActive){
                    idea.setActivatedUpVotes();
                } else {
                    b= true;
                    idea.setNormUpVotes();
                }
                idea.disableButton("voteConUp");
                voteClick(1,view,b);
            } else {
                voteLock = false;
            }
        }
    }

    public void downVoteOnClick(View view){
        if (!voteLock) {
            voteLock = true;
            Idea idea  = (Idea) view.getTag();
            boolean b = false;
            if (!idea.upActive) {
                if (!idea.dwnActive){
                    idea.setActivatedDwnVotes();
                } else {
                    idea.setNormDwnVotes();
                    b= true;
                    weirdCase = true;
                }
                Log.i("onClickUpVoteMsg", "!upactive");
                idea.disableButton("voteConDwn");
                voteClick(-1, view,b);
            } else {
                voteLock = false;
            }
        }
    }

    private void voteClick(final int n, final View view, final boolean b){
        final Idea idea = (Idea)view.getTag();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("randomId", idea.randomId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()){
                        String voteColumn;
                        final String oppositeKey;
                        final List<String> oppositeColumn;
                        if (n > 0) {
                            voteColumn = "voteConUp";
                            oppositeColumn = objects.get(0).getList("voteConDwn");
                            oppositeKey = "voteConDwn";
                        } else {
                            voteColumn = "voteConDwn";
                            oppositeColumn = objects.get(0).getList("voteConUp");
                            oppositeKey = "voteConUp";
                        }

                        if (objects.get(0).getList(voteColumn) != null) {
                            List<String> voteContributors = objects.get(0).getList(voteColumn);
                            if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
                                Log.i("voteLock", "green");
                                Log.i("commentVoteClick", "vot con does not contain user");

                                objects.get(0).put("voteTotal", idea.voteTotal + n);
                                voteContributors.add(ParseUser.getCurrentUser().getUsername());
                                objects.get(0).put(voteColumn, voteContributors);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i("voteLock","changing votelock to false");
                                            voteLock = false;
                                            idea.setVote(idea.voteTotal + n);
                                            mainVote(n,b);
                                            if (oppositeColumn != null) {
                                                if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                                    oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                            getUsername()));
                                                    objects.get(0).put(oppositeKey, oppositeColumn);
                                                    objects.get(0).saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.i("commentVoteClick", "saved");
                                                            } else {
                                                                Log.i("commentVoteClick", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            //     messageContainer.vote.setTypeface(null, Typeface.BOLD);
                                            //  messageContainer.vote.setTextSize(16f);
                                            Log.i("vote", e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.i("voteLock", "blue");
                                Log.i("commentVoteClick", "vot con does  contain user");
                                voteContributors.remove(voteContributors.indexOf(ParseUser.getCurrentUser().
                                        getUsername()));
                                int s = 0;
                                if (n > 0) {
                                    s = -1;
                                } else {
                                    s = 1;
                                }

                                objects.get(0).put("voteTotal", idea.voteTotal + s);
                                objects.get(0).put(voteColumn, voteContributors);
                                final int finalS = s;
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            idea.setVote(idea.voteTotal + finalS);
                                            mainVote(n,b);
                                            Log.i("voteLock","changing votelock to false");
                                            voteLock = false;
                                            idea.resetButtons();
                                            //   Log.i("commentVoteClick","saved");

                                        } else {
                                            Log.i("commentVoteClick", e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("voteLock", "red");
                            objects.get(0).put(voteColumn, new ArrayList<>());
                            objects.get(0).add(voteColumn, ParseUser.
                                    getCurrentUser().getUsername());
                            Log.i("idea.voteTotal",Integer.toString(idea.voteTotal));
                            idea.setVote(idea.voteTotal + n);

                            objects.get(0).put("voteTotal", idea.voteTotal);
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i("voteLock","changing votelock to false");
                                        voteLock = false;
                                        mainVote(n,b);
                                        if (oppositeColumn != null) {
                                            Log.i("oppositeColumn", "opposite not null");
                                            if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                                oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                        getUsername()));
                                                objects.get(0).put(oppositeKey, oppositeColumn);
                                                objects.get(0).saveInBackground();
                                            }
                                        }
                                    } else {
                                        //   idea.totalVote.setTypeface(null, Typeface.BOLD);
                                        Log.i("vote", e.getMessage());
                                        //  idea.totalVote.setTextSize(16f);
                                    }
                                }
                            });
                        }

                    } else {
                        Log.i("vote", "objects empty");
                    }

                } else {
                    Log.i("vote", e.getMessage());
                }


            }
        });
    }


    private void mainVote(final int n, final boolean b) {
        ParseObject vote = new ParseObject("Votes");
        vote.put("IdeaTreeId",ideaTreeID);
        vote.put("creation", TimeSetter.getTime());
        vote.put("vote",n);
        vote.saveInBackground();
        ParseQuery<ParseObject> query=  new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("Identifier", "Main");
        query.whereEqualTo("IdeaTree", ideaTreeID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()){
                        List<Number> voteTimes = objects.get(0).getList("VoteTimes");
                        if (voteTimes==null){
                            voteTimes = new ArrayList<>();
                            objects.get(0).put("VoteTimes",voteTimes);
                        }
                        if (n>0 && !b) {
                            if (objects.get(0).getNumber("voteMainTotal").intValue() >= 0) {
                                objects.get(0).add("VoteTimes", TimeSetter.getTime());
                            }
                        }
                        Log.i("voteMainTotal",Integer.toString(objects.get(0).getNumber("voteMainTotal")
                                .intValue()));
                        if (b){
                            objects.get(0).put("voteMainTotal",objects.get(0).getNumber("voteMainTotal")
                                    .intValue() - n);
                            if (weirdCase){
                                if (objects.get(0).getNumber("voteMainTotal").intValue() >= 0) {
                                    objects.get(0).add("VoteTimes", TimeSetter.getTime());
                                }
                                weirdCase = false;
                            } else {
                                if (!voteTimes.isEmpty()) {
                                    voteTimes.remove(voteTimes.size() - 1);
                                    objects.get(0).put("VoteTimes", voteTimes);
                                }
                            }
                        } else {
                            objects.get(0).put("voteMainTotal", objects.get(0).getNumber("voteMainTotal")
                                    .intValue() + n);
                        }
                        objects.get(0).saveInBackground();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void commentsOnClick(final View view) {
        if (popUp == null) {
            Log.i("commentsOnClick", "test");
            userInChat = true;
            commentIds = new ArrayList<>();
            final Idea idea = (Idea) view.getTag();
            commentId = idea.randomId;
            voteLock = false;
            popUp = new PopUp(IdeaTreeActivity.this);
            messageContainers = new ArrayList<MessageContainer>();
            replyContainers = new HashMap<>();
            mainContainers = new ArrayList<>();
            messageContainers.add(new MessageContainer());
            setMessagingAdapter();
            Log.i("locationIdea", Double.toString(idea.topRightVector.x));
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
            query.whereEqualTo("randomId", commentId);
            query.orderByDescending("update");
            query.findInBackground(new FindCallback<ParseObject>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void done(final List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (!objects.isEmpty()) {
                            for (final ParseObject object : objects) {
                                Log.i("messageAuthor", object.getString("message"));
                                setMessageContainer(object);

                            }
                            generateReplies();
                        }
                        popUp.activatePopUp();
                       // adapter.setListView(popUp.listView);
                        setDismissListener();
                    } else {
                        Log.i("commentsclick", e.getMessage());
                    }
                }
            });
        }
    }

    private void generateReplies() {
        if (!mainContainers.isEmpty()){
            for (int j=0;j<mainContainers.size();j++) {
                Log.i("messageContainers", "adding msg to messageContainers: "+mainContainers.get(j).message);
                messageContainers.add(mainContainers.get(j));
                adapter.notifyDataSetChanged();

                queryVotes(mainContainers.get(j));
                Log.i("messageId", mainContainers.get(j).messageId);
                if (mainContainers.get(j).replyIDs!= null) {
                    for (int i = 0; i < mainContainers.get(j).replyIDs.size(); i++) {
                        if (!replyContainers.isEmpty()) {
                            Log.i("generateReplies","adding replycontainer: " + replyContainers.get(mainContainers.get(j).replyIDs.get(i)).getMessage());
                            messageContainers.add(replyContainers.get(mainContainers.get(j).replyIDs.get(i)));
                            adapter.notifyDataSetChanged();
                            queryVotes(replyContainers.get(mainContainers.get(j).replyIDs.get(i)));

                        }
                    }
                }
            }
            Log.i("generateReplies","replycntainer size " + Integer.toString(replyContainers.size()));

        }
    }

    public void queryVotes(final MessageContainer mContainer){
        Log.i("mContainer", mContainer.messageId);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
        query.whereEqualTo("messageId", mContainer.messageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()){
                        List<String> voteUp =  objects.get(0).getList("voteConUp");
                        List<String> voteDwn=  objects.get(0).getList("voteConDwn");
                        if (voteUp!=null){
                            if (voteUp.contains(ParseUser.getCurrentUser().getUsername())){
                                Log.i("setButtons", "changing color for upvote button for " + mContainer.messageId);
                                mContainer.userUpVoted = true;
                                if ( mContainer.upVote!=null) {
                                    mContainer.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                                }
                                mContainer.disableButton("voteConUp");

                            }}
                        if (voteDwn!=null){
                            if (voteDwn.contains(ParseUser.getCurrentUser().getUsername())){
                                Log.i("setButtons", "changing color for dwnvote button for " + mContainer.messageId);
                                mContainer.userDwnVoted = true;
                                if (mContainer.dwnVote!=null){
                                    mContainer.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                                }

                                mContainer.disableButton("voteConDwn");
                            }
                        }

                    }

                }
            }
        });
    }

    private void setMessageContainer(ParseObject object) {
        String msgId = object.getString("messageId");
        commentIds.add(msgId);
        MessageContainer mContainer = new MessageContainer(
                object.getString("message"), object.getString("Author"));
        if (msgId != null){
            mContainer.messageId = msgId;
        }

        Number votes = object.getNumber("votes");

        if (votes != null) {
            mContainer.setVote(votes.intValue());
        }

        if (object.getString("replyTag") != null) {
            String replyTag = "@"+object.getString("replyTag");
            mContainer.setInitialTag(new SpannableString(replyTag));
            mContainer.stringBuilder();
            mContainer.setIdentifier(object.getString("Identifier"));
        } else {
            mContainer.setIdentifier(object.getString("Identifier"));
        }

        if (object.getNumber("replaceIndex")!=null){
            mContainer.setReplaceIndex(object.getNumber("replaceIndex").intValue());
        }

        if (object.getList("replyIDs")!=null){
            List<String> replyIDs = object.getList("replyIDs");
            if (!replyIDs.isEmpty()){
                mContainer.setReplyIDs(replyIDs);
                Log.i("replyIDs", mContainer.replyIDs.toString());
            }
        }

        if (object.getString("Identifier") !=null){
            mContainer.setIdentifier(object.getString("Identifier"));
            if (mContainer.identifier.equals("main")){
                mainContainers.add(mContainer);
            } else {
                String mainId = object.getString("mainId");
                if (mainId!= null){
                    mContainer.mainId = mainId;
                }
                replyContainers.put(mContainer.messageId,mContainer);
            }
        }




    }

    private void setDismissListener() {
        popUp.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialog != null){
                    userInChat = false;
                    popUp = null;
                }
            }
        });
    }

    private void setMessagingAdapter(){
        adapter = new MessagingAdapter(IdeaTreeActivity.this
                ,messageContainers);
        popUp.listView.setAdapter(adapter);
        adapter.popUp = popUp;
        adapter.notifyDataSetChanged();
    }

    public void onClickSendIndent(View view){

        replyContainer.isStillReplyBaby = false;
        TextView tv = (TextView) view.getTag();
        replyTotal = tv.getText().toString();
        reply = obtainText(replyTotal,initialTag.length());
        replyEnabled = true;
        addMessageToParse();

    }

    public void onClickReply(final View view){





        if (replyContainer != null) {
            if (replyContainer.message.equals("")&& messageContainers.contains(replyContainer)) {
                messageContainers.remove(messageContainers.indexOf(replyContainer));
                adapter.notifyDataSetChanged();
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageContainer mc = (MessageContainer) view.getTag();
                initialTag = new SpannableString("@"+mc.username);
                int index = mc.position+1;
                if (mc.identifier.equals("main")){
                    mainID = mc.messageId;
                    mc.mainId = mainID;
                    Log.i("mainId", mc.mainId);
                } else {
                    mainID = mc.mainId;
                }
                replyEnabled = true;
                 replyContainer = new MessageContainer();
                replyContainer.isStillReplyBaby = true;
                replyContainer.mainId = mc.mainId;
                replyContainer.setReplaceIndex(index);
                Log.i("onClickReply", "adding reply container");
                messageContainers.add(index,replyContainer);
                adapter.setIndex(index);
                adapter.setReplyTag(stringBuilder(initialTag));
                adapter.notifyDataSetChanged();
                if (index==messageContainers.size()-1){
                    popUp.listView.setSelection(messageContainers.size()-1);
                }

                Log.i("onClickReply", stringBuilder(initialTag).toString());
            }
        }, 100);

    }

    public void onClickSend(View view){
        if (replyContainer != null) {
            if (replyContainer.message.equals("")&& messageContainers.contains(replyContainer)) {
                messageContainers.remove(messageContainers.indexOf(replyContainer));
                adapter.notifyDataSetChanged();
            }
        }
        reply = adapter.sendMsg.getText().toString();
        replyEnabled = false;
        adapter.sendMsg.getText().clear();
        popUp.initialMsg = "";
        Log.i("candy", reply);
        addMessageToParse();

    }



    private String obtainText(String s, int start) {
       return s.substring(start,s.length());
    }


    private void addMessageToParse() {
        final String msgId = new IDGenerator().generateID();
        MessageContainer mContainer;

        if (replyEnabled){
            if (!replyTotal.equals(initialTag.toString())){
                mContainer = new MessageContainer(reply, ParseUser.getCurrentUser()
                        .getUsername());
                mContainer.setId(msgId);
                mContainer.setInitialTag(initialTag);
                mContainer.stringBuilder();
                mContainer.setIdentifier("reply");
                mContainer.mainId = mainID;
                replaceIndex = messageContainers.indexOf(replyContainer);
                mContainer.setReplaceIndex(replaceIndex);
                adapter.setReplyEnabled(true);
                Log.i("replaceIndex", Integer.toString(replaceIndex));
                messageContainers.remove(replaceIndex);
                Log.i("size", "after " + Integer.toString(messageContainers.size()));
                messageContainers.add(replaceIndex, mContainer);
                addToParse(mContainer,msgId);
            } else {
                adapter.notifyDataSetChanged();
                replyEnabled = false;
                messageContainers.remove(replyContainer.replaceIndex);
            }

        } else {
            Log.i("reply", "reply disenabled");
            if (!reply.equals("")) {
                mContainer = new MessageContainer(reply, ParseUser.getCurrentUser()
                        .getUsername());
                mContainer.setIdentifier("main");
                mContainer.setId(msgId);
                messageContainers.add(1,mContainer);
                Log.i("mContainer", mContainer.getMessage());
                addToParse(mContainer,msgId);
            }
        }

        Log.i("addMessageToParse",Integer.toString(messageContainers.size()));


    }

    private void addToParse(final MessageContainer mContainer, final String msgId) {
        adapter.notifyDataSetChanged();
        ParseObject parseObject = new ParseObject("IdeaComments");
        parseObject.put("randomId", commentId);
        parseObject.put("messageId", msgId);
        parseObject.put("update", new TimeSetter().presentTime);
        if (mContainer.identifier.equals("main")){
            mContainer.mainId = mContainer.messageId;
        }
        parseObject.put("mainId", mContainer.mainId);
        if (replyEnabled) {
            //TODO once youve sorted out the profile inbox, need to set a system whereby
            //TODO repliers that are saved in "replyTag" on parse need to be queried and sent
            //TODO an alert that such and such has replied.
            parseObject.put("replyTag", obtainText(initialTag.toString(),1));
            Log.i("put", "e");
            parseObject.put("message", reply);
            parseObject.put("mainId", mContainer.mainId);
            parseObject.put("Identifier","reply");
            parseObject.put("replaceIndex",mContainer.replaceIndex);
            replyEnabled = false;
            parseObject.put("Author", ParseUser.getCurrentUser().getUsername());
            parseObject.put("Index",messageContainers.indexOf(mContainer));
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        adjustIndex(mContainer);
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
                        query.whereEqualTo("messageId",mContainer.mainId);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e==null){
                                    if (!objects.isEmpty()){
                                        replyIDs = objects.get(0).getList("replyIDs");
                                        if (replyIDs== null){
                                            replyIDs = new ArrayList<String>();
                                            objects.get(0).put("replyIDs",replyIDs);
                                        }
                                        replyIDs.add(msgId);
                                        objects.get(0).put("replyIDs", replyIDs);
                                        objects.get(0).saveInBackground();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } else {
            adjustIndex(mContainer);
            parseObject.put("Identifier","main");
            parseObject.put("message", reply);
            parseObject.put("Author", ParseUser.getCurrentUser().getUsername());
            parseObject.saveInBackground();
        }


    }





    private void adjustIndex(MessageContainer mContainer) {
        int startingIndex = messageContainers.indexOf(mContainer) +1;
        if (startingIndex<messageContainers.size()) {
            for (int i = startingIndex; i < messageContainers.size(); i++) {
                if (messageContainers.get(i).identifier.equals("reply")) {
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
                    query.whereEqualTo("messageId", messageContainers.get(i).messageId);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                if (!objects.isEmpty()) {
                                    if (objects.get(0).getNumber("Index") != null) {
                                        Log.i("adjustIndex", objects.get(0).getString("messageId"));
                                        int objectIndex = objects.get(0).getNumber("Index").intValue();
                                        objects.get(0).put("Index", objectIndex + 1);
                                        objects.get(0).saveInBackground();
                                    }

                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private SpannableStringBuilder stringBuilder(SpannableString text){
        SpannableStringBuilder sp = new SpannableStringBuilder();
        text.setSpan(new ForegroundColorSpan(Color.parseColor("#335ce5")),0, text.length(),0);
        sp.append(text);
        return sp;
    }


    public void onClickUpVoteMsg(View view) {
        Log.i("candy", Boolean.toString(voteLock));
        if (!voteLock) {
            voteLock = true;
            messageContainer = (MessageContainer) view.getTag();
            Log.i("candy",  "dwnActive: "+Boolean.toString(messageContainer.dwnActive));
            String voteColumn = "voteConUp";
            getCommentData(voteColumn);
        }
    }

    public void onClickDownVoteMsg(View view){
        Log.i("candy", Boolean.toString(voteLock));
        if (!voteLock) {
            voteLock = true;
            messageContainer = (MessageContainer) view.getTag();
            String voteColumn = "voteConDwn";
            getCommentData(voteColumn);
            Log.i("candy", Boolean.toString(messageContainer.upActive));

        }}

    private void getCommentData(final String voteColumn){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
        Log.i("candy", "msgId: "+messageContainer.messageId);
        query.whereEqualTo("messageId", messageContainer.messageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    Log.i("candy", "e==null)");
                    if (!objects.isEmpty()) {
                        Log.i("candy", "!objects.isEmpty()");
                        votedCommentObj = objects.get(0);
                        voteContributors = votedCommentObj.getList(voteColumn);
                        if (voteContributors == null) {
                            voteContributors = new ArrayList<String>();
                            votedCommentObj.put(voteColumn,voteContributors);
                            votedCommentObj.saveInBackground();
                            Log.i("candy", "votedCommentObj.getList(voteColumn) == null");
                        }
                        Log.i("candy", "voteColumn " + voteColumn);
                                if (voteColumn.equals("voteConUp")) {
                                    List<String> oppositeList = votedCommentObj.getList("voteConDwn");
                                    if (oppositeList == null) {
                                        Log.i("candy", "voteColumn.equals(\"voteConUp\") and oppositeList == null");
                                       upActive();
                                    } else {
                                        Log.i("candy", "grey");
                                        if (!oppositeList.contains(ParseUser.getCurrentUser().getUsername())) {
                                            Log.i("candy", "voteColumn.equals(\"voteConUp\"");
                                            upActive();
                                        }else {
                                            voteLock = false;
                                        }
                                    }
                                } else {
                                    List<String> oppositeList = votedCommentObj.getList("voteConUp");
                                    if (oppositeList == null) {
                                        Log.i("candy", "!voteColumn.equals(\"voteConUp\"");
                                        dwnActive();
                                    } else {
                                        Log.i("candy", "purple");
                                        if (!oppositeList.contains(ParseUser.getCurrentUser().getUsername())) {
                                            dwnActive();
                                        } else {
                                            voteLock = false;
                                        }
                                    }
                                }
                    }
            }
        }
    });
    }

    private void upActive(){
        if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
            Log.i("candy", "  !voteContributors.contains(ParseUser.getCurrentUser().getUsername())");
            messageContainer.upActive = false;
            messageContainer.userUpVoted = true;

        } else {
            Log.i("candy", "  voteContributors.contains(ParseUser.getCurrentUser().getUsername());");
            messageContainer.upActive = true;
            messageContainer.userUpVoted = false;
        }
        if (!messageContainer.upActive) {
            Log.i("candy", "!upactive");
            messageContainer.disableButton("voteConDwn");
            commentVoteClick(1);
        } else {
            Log.i("candy", "messageContainer.upActive");
            commentVoteClick(1);
        }
    }

    private void dwnActive(){
        if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
            Log.i("candy", "!voteContributors.contains(ParseUser.getCurrentUser().getUsername()");
            messageContainer.dwnActive = false;
            messageContainer.userDwnVoted = true;
        } else {
            Log.i("candy", "voteContributors.contains(ParseUser.getCurrentUser().getUsername()");

            messageContainer.dwnActive = true;
            messageContainer.userDwnVoted = false;
        }
        if (!messageContainer.dwnActive) {
            Log.i("candy", "!messageContainer.dwnActive");
            messageContainer.disableButton("voteConUp");
            commentVoteClick(-1);
        } else {
            Log.i("candy", "messageContainer.dwnActive");
            commentVoteClick(-1);
        }
    }

    private void commentVoteClick(final int n){
                    if (votedCommentObj != null) {
                        Log.i("candy", "1");
                        String voteColumn;
                        final String oppositeKey;
                        final List<String> oppositeColumn;
                        if (n > 0) {
                            Log.i("candy", "2");
                            voteColumn = "voteConUp";
                            messageContainer.resetButtonColors();
                            messageContainer.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                            oppositeColumn = votedCommentObj.getList("voteConDwn");
                            oppositeKey = "voteConDwn";
                        } else {
                            voteColumn = "voteConDwn";
                            messageContainer.resetButtonColors();
                            messageContainer.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                            oppositeColumn = votedCommentObj.getList("voteConUp");
                            oppositeKey = "voteConUp";
                        }



                        if (votedCommentObj != null) {
                            Log.i("candy", "3");
                            if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
                                Log.i("candy", "green");
                                Log.i("candy", "vot con does not contain user");
                                votedCommentObj.put("votes", messageContainer.vote + n);
                                voteContributors.add(ParseUser.getCurrentUser().getUsername());
                                votedCommentObj.put(voteColumn, voteContributors);
                                votedCommentObj.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i("candy","changing votelock to false");
                                            voteLock = false;
                                            messageContainer.setVote(messageContainer.vote + n);
                                            if (oppositeColumn != null) {
                                                if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                                    oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                            getUsername()));
                                                    votedCommentObj.put(oppositeKey, oppositeColumn);
                                                    votedCommentObj.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.i("commentVoteClick", "saved");
                                                            } else {
                                                                Log.i("commentVoteClick", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            //     messageContainer.vote.setTypeface(null, Typeface.BOLD);
                                            //  messageContainer.vote.setTextSize(16f);
                                            Log.i("vote", e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.i("candy", "blue");
                                Log.i("candy", "vot con does  contain user");
                                voteContributors.remove(voteContributors.indexOf(ParseUser.getCurrentUser().
                                        getUsername()));
                                int s = 0;
                                if (n > 0) {
                                    s = -1;
                                    messageContainer.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol);
                                } else {
                                    s = 1;
                                    messageContainer.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol);
                                }

                                Log.i("votecount", "contains " + Integer.toString(messageContainer.vote + s));
                                votedCommentObj.put("votes", messageContainer.vote + s);
                                votedCommentObj.put(voteColumn, voteContributors);
                                final int finalS = s;
                                votedCommentObj.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            messageContainer.setVote(messageContainer.vote + finalS);
                                            Log.i("candy","changing votelock to false");
                                            Log.i("candy","finalS "+Integer.toString(finalS));
                                            voteLock = false;
                                            messageContainer.resetButtons();
                                            //   Log.i("commentVoteClick","saved");

                                        } else {
                                            Log.i("commentVoteClick", e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("candy", "red");
                            votedCommentObj.put(voteColumn, new ArrayList<>());
                            votedCommentObj.add(voteColumn, ParseUser.
                                    getCurrentUser().getUsername());
                            messageContainer.setVote(messageContainer.vote + n);
                            votedCommentObj.put("votes", messageContainer.vote);
                            votedCommentObj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i("candy","changing votelock to false");
                                        voteLock = false;
                                        if (oppositeColumn != null) {
                                            Log.i("oppositeColumn", "opposite not null");
                                            if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                                oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                        getUsername()));
                                                votedCommentObj.put(oppositeKey, oppositeColumn);
                                                votedCommentObj.saveInBackground();
                                            }
                                        }
                                    } else {
                                        //   idea.totalVote.setTypeface(null, Typeface.BOLD);
                                        Log.i("vote", e.getMessage());
                                        //  idea.totalVote.setTextSize(16f);
                                    }
                                }
                            });
                        }

                    } else {
                        Log.i("vote", "objects empty");
                    }





    }






}
