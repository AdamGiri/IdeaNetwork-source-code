package com.parse.ideanetwork;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.GridView;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ideanetwork.Collation.CollatedListsActivity;
import com.parse.ideanetwork.Collation.Collation;


import java.util.ArrayList;
import java.util.List;

public class IdeaTreeLibraryActivity extends AppCompatActivity {

    GridView gridLayout;
    String ideaTreeID;
    List<String> ideaTrees;
    List<String> ideaTreeIDs;
    ArrayList<MainIdea> allIdeasStore;
    ArrayList<MainIdea> allIdeasStoreTmp;
    ArrayList<MainIdea> bookmarkIdeasStore;
    ArrayList<MainIdea> bookmarkIdeasStoreTmp;
    ArrayList<String> bookmarkIdeasStoreIDs;
    ArrayList<String> bookmarkIdeasStoreTmpIDs;
    ArrayList<MainIdea> personalIdeasStore;
    ArrayList<MainIdea> personalIdeasStoreTmp;
    ArrayList<String> personalIdeasStoreIDs;
    ArrayList<String> personalIdeasStoreTmpIDs;
    ArrayList<String> lastClicks;
    List<String> availableColors;
    List<String> bookmarkIDs;
    TextView createButton;
    EditText summary;
    EditText description;
    GridAdapter arrayAdapter;
    AlertDialog dialog;
    MainIdea mainIdea;
    Collation collation;
    String allIdeas = "IdeaTreeID";
    String bookmarkIdeas = "BookmarkID";
    String personalIdeas = "PersonalID";
    String collatedLists = "CollatedLists";

    TextView allIdeasButton;
    TextView bookmarkButton;
    TextView personalButton;
    TextView collatedListsButton;
    TextView noIdeasMsg;

    boolean isAllIdeas;
    boolean isYourIdeas;
    boolean isCollatedLists;
    boolean isBookmarkIdeas;
    CustomTextWatcher  ctw;
    boolean ideasAvailable;
    boolean popupActive;
    List<String> bookmarkList;
    ArrayList<String> spinnerList;
    Boolean tagListEnabled;

    ListView dropDownListView;

    ArrayList<String> tags;
    Boolean tagSelected;

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
                                        Intent intent = new Intent(IdeaTreeLibraryActivity.this,MainActivity.class);
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


    public void createTree(View view) {

    if (ctw != null) {
        if (ctw.charsLeft != ctw.maxChars) {
            if (!ctw.maxCharsReached) {
                if (tagSelected) {
                    noIdeasMsg.setVisibility(View.INVISIBLE);
                    tagSelected = false;
                    if (view.getTag() == null) {
                        ideaTreeID = new IDGenerator().generateID();
                        view.setTag(ideaTreeID);
                        if (ParseUser.getCurrentUser().getList("IdeaTreeID") == null) {
                            ParseUser.getCurrentUser().put("IdeaTreeID", new ArrayList<>());
                        }
                        Log.i("createTree", ideaTreeID);
                        ParseUser.getCurrentUser().add("IdeaTreeID", ideaTreeID);
                        if (isBookmarkIdeas) {
                            Log.i("createTree", "isBookmarkIdeas");
                            if (ParseUser.getCurrentUser().getList("BookmarkID") == null) {
                                ParseUser.getCurrentUser().put("BookmarkID", new ArrayList<>());
                            }
                            ParseUser.getCurrentUser().add("BookmarkID", ideaTreeID);
                        }
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ideaTreeIDs.add(ideaTreeID);
                                    ideaTrees.add(ideaTreeID);
                                    mainIdea = new MainIdea(IdeaTreeLibraryActivity.this, ideaTreeID);
                                    mainIdea.setTitle(summary.getText().toString());
                                    mainIdea.setDescription(description.getText().toString());
                                    if (!allIdeasStore.isEmpty()) {
                                        Log.i("allIdeasStore", "allIdeasStore not empty");
                                        allIdeasStore.add(mainIdea);
                                    } else if (!allIdeasStoreTmp.isEmpty()) {
                                        Log.i("allIdeasStoretmp", "allIdeasStore tmp not empty");
                                        allIdeasStoreTmp.add(mainIdea);
                                    } else if (allIdeasStoreTmp.isEmpty() && allIdeasStore.isEmpty()) {
                                        allIdeasStore.add(mainIdea);
                                    }
                                    if (isBookmarkIdeas) {
                                        if (!bookmarkIdeasStore.isEmpty()) {
                                            Log.i("createTree", "adding to bookmarkIdeasStore");
                                            bookmarkIdeasStore.add(mainIdea);
                                            bookmarkIdeasStoreIDs.add(mainIdea.ideaTreeID);
                                        } else if (!bookmarkIdeasStoreTmp.isEmpty()) {
                                            Log.i("createTree", "adding to bookmarkIdeasStoreTmp");
                                            bookmarkIdeasStoreTmp.add(mainIdea);
                                            bookmarkIdeasStoreTmpIDs.add(mainIdea.ideaTreeID);
                                        } else if (bookmarkIdeasStoreTmp.isEmpty() && bookmarkIdeasStore.isEmpty()) {
                                            bookmarkIdeasStore.add(mainIdea);
                                            bookmarkIdeasStoreIDs.add(mainIdea.ideaTreeID);
                                        }
                                    }
                                    if (isYourIdeas) {
                                        personalIdeasStoreIDs.add(mainIdea.ideaTreeID);
                                        if (!personalIdeasStore.isEmpty()) {
                                            Log.i("createTree", "adding to bookmarkIdeasStore");
                                            personalIdeasStore.add(mainIdea);
                                        } else if (!personalIdeasStoreTmp.isEmpty()) {
                                            Log.i("createTree", "adding to bookmarkIdeasStoreTmp");
                                            personalIdeasStoreTmp.add(mainIdea);
                                        } else if (personalIdeasStore.isEmpty() && personalIdeasStoreTmp.isEmpty()) {
                                            personalIdeasStore.add(mainIdea);
                                        }
                                    }
                                    addIdeaTreeToParse();
                                }
                            }
                        });
                    }
                    Log.i("popupActive", "dialog.is Showing()");
                } else {
                    Toast.makeText(this, "Please set tags for your idea.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(IdeaTreeLibraryActivity.this, "Max characters have been reached, please shorten your summary.", Toast.LENGTH_SHORT).show();

            }
        } else {

                Toast.makeText(IdeaTreeLibraryActivity.this, "Please provide more detail to your summary.", Toast.LENGTH_SHORT).show();

        }
    }
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void addIdeaTree(View view){
        activatePopUp();
    }

    private void addIdeaTreeToParse() {
        ParseObject parseIdeaTree = new ParseObject("IdeaTree");
        parseIdeaTree.saveInBackground(new SaveCallback() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void done(ParseException e) {
                if (e==null){
                    //adding the main idea to the tree
                    Idea idea = new Idea(IdeaTreeLibraryActivity.this,null,ideaTreeID, "Main");
                    idea.setColorTheme("purple");
                    idea.setTopMargin(5000);
                    idea.setLeftMargin(IdeaTreeActivity.startingLocation);
                    idea.setRandomId(new IDGenerator().generateID());
                    idea.setVectorCoordinates(idea, IdeaTreeActivity.IdeaAge.OLD);
                    idea.setVote(0);
                    idea.setLevel(0);
                    idea.setTitle(mainIdea.summary);
                    if (mainIdea.description != null) {
                        idea.setDescription(mainIdea.description);
                    }
                    idea.setAuthor(ParseUser.getCurrentUser().getUsername());
                    addMainIdeaToParse(idea);
                } else {
                    Log.i("addIdeaerror",e.getMessage());
                }
            }
        });
    }

    //TODO add any extra idea data to parse here
    private void addMainIdeaToParse(final Idea idea) {
        ParseObject parseObjectMainIdea = new ParseObject("Idea");
        parseObjectMainIdea.put("x", idea.topLeftVector.x);
        parseObjectMainIdea.put("y", idea.topLeftVector.y);
        parseObjectMainIdea.put("IdeaTree",idea.getIdeaTreeID());
        parseObjectMainIdea.put("Summary",summary.getText().toString());
        parseObjectMainIdea.put("SummarySearch",summary.getText().toString().toLowerCase());
        parseObjectMainIdea.put("Description", idea.descriptionText);
        parseObjectMainIdea.put("Identifier",idea.getIdentifier());
        parseObjectMainIdea.put("randomId",idea.randomId);
        parseObjectMainIdea.put("creation",TimeSetter.getTime());
        parseObjectMainIdea.put("voteTotal",idea.voteTotal);
        parseObjectMainIdea.put("voteMainTotal",0);
        parseObjectMainIdea.put("Colors",availableColors);
        parseObjectMainIdea.put("Color", Idea.mainIdeaColor);
        for (String tag: tags) {
            parseObjectMainIdea.put(tag, true);
            parseObjectMainIdea.add("tags", tag);
        }
        parseObjectMainIdea.put("level",idea.level);
        parseObjectMainIdea.put("Author",idea.ideaAuthor);
        parseObjectMainIdea.saveInBackground(new SaveCallback() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (tags != null && !tags.isEmpty()){
                        tags.clear();
                    }
                    Log.i("added", "Main idea successfully added to parse");
                    if (isAllIdeas){
                        if (!allIdeasStore.isEmpty()) {
                            activateAdapter(allIdeasStore);
                        } else {
                            activateAdapter(allIdeasStoreTmp);
                        }
                    }

                    if (isYourIdeas){
                        if (!personalIdeasStore.isEmpty()) {
                            activateAdapter(personalIdeasStore);
                        } else {
                            activateAdapter(personalIdeasStoreTmp);
                        }
                    }

                    if (isBookmarkIdeas){
                        if (!bookmarkIdeasStore.isEmpty()) {
                            activateAdapter(bookmarkIdeasStore);
                        } else {
                            activateAdapter(bookmarkIdeasStoreTmp);
                        }
                    }

                    Intent intent = new Intent(IdeaTreeLibraryActivity.this, IdeaTreeActivity.class);
                    intent.putExtra("IdeaTreeID",ideaTreeID);
                    Toast.makeText(IdeaTreeLibraryActivity.this, "MindMap created", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                            } else {
                                Toast.makeText(IdeaTreeLibraryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            }

    //creates a popup to define main idea
    private void activatePopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.main_idea_pop_up,null);
        dropDownListView = (ListView)view.findViewById(R.id.dropDownList);
        summary = (EditText)view.findViewById(R.id.summary);
        TextView textCount = (TextView) view.findViewById(R.id.textCount);
        ctw = new CustomTextWatcher(textCount,summary);
        summary.addTextChangedListener(ctw);
        description = (EditText)view.findViewById(R.id.description);
        createButton = (TextView)view.findViewById(R.id.create);
        builder.setView(view);
        dialog = builder.create();
        builder.show();
        popupActive = true;
    }


    public void onClickDelete(View view) {
        MainIdea deleteIdea = (MainIdea)view.getTag();
        activateDeleteDialog(deleteIdea);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void activateAdapter(ArrayList<MainIdea> ideas) {
        arrayAdapter = new GridAdapter(IdeaTreeLibraryActivity.
                this,R.layout.adapterlayout,ideas,false);
        gridLayout.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
       // noIdeasMsg.setAlpha(0);
    }

    public void onClickIdeaTree(View view){
        Intent intent = new Intent(IdeaTreeLibraryActivity.this, IdeaTreeActivity.class);
        String clickedIdea = (String)view.getTag();
        intent.putExtra("IdeaTreeID", clickedIdea);
        startActivity(intent);
    }

    public void onClickCollate(View view){

        Intent intent = new Intent(this,CollateActivity.class);
        MainIdea idea = (MainIdea) view.getTag();
        intent.putExtra("id",idea.ideaTreeID);
        intent.putExtra("title",idea.summary);
        intent.putExtra("author",idea.author);
        startActivity(intent);
    }





    private void activateDeleteDialog(final MainIdea mainIdea) {
        Log.i("activateDeleteDialog","resposne");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog,null);
        Button yes = (Button)view.findViewById(R.id.yes);
        Button no = (Button)view.findViewById(R.id.no);
        builder.setView(view);
        final AlertDialog show = builder.show();
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                show.dismiss();
                removeFromParse(mainIdea);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void removeFromParse(final MainIdea mainIdea) {
//TODO perhaps only delete the idea from users library and not from parse
        Log.i("removeFromParse", "removal idea id "+mainIdea.ideaTreeID);
        List<String> userIdList = new ArrayList<>();
        String ideaList = "";
        if (isAllIdeas || isYourIdeas){
            ideaList = "IdeaTreeID";
            if (!allIdeasStore.isEmpty()) {
                allIdeasStore.remove(allIdeasStore.indexOf(mainIdea));
                for (MainIdea mainIdea1:allIdeasStore){
                     userIdList.add(mainIdea1.ideaTreeID);
                }
            } else if (!allIdeasStoreTmp.isEmpty()){
                allIdeasStoreTmp.remove(allIdeasStoreTmp.indexOf(mainIdea));
                for (MainIdea mainIdea1:allIdeasStoreTmp){
                    userIdList.add(mainIdea1.ideaTreeID);
                }
            } if (allIdeasStore.isEmpty() && allIdeasStoreTmp.isEmpty()){
                noIdeasMsg.setVisibility(View.VISIBLE);
            }
            if (!personalIdeasStore.isEmpty()) {
                Log.i("removeFromParse","!personalIdeasStore.isEmpty()");
                personalIdeasStore.remove(personalIdeasStore.indexOf(mainIdea));
                for (MainIdea mainIdea1:personalIdeasStore){
                    userIdList.add(mainIdea1.ideaTreeID);
                }
            } else if (!personalIdeasStoreTmp.isEmpty()){
                Log.i("removeFromParse","personalIdeasStore.isEmpty()");
                personalIdeasStoreTmp.remove(personalIdeasStoreTmp.indexOf(mainIdea));
                for (MainIdea mainIdea1:personalIdeasStoreTmp){
                    userIdList.add(mainIdea1.ideaTreeID);
                }
            }
        }
            if (isYourIdeas){
                Log.i("setAlpha","isYourIdeas");
                if (personalIdeasStoreTmp.isEmpty() && personalIdeasStore.isEmpty()){
                    noIdeasMsg.setVisibility(View.VISIBLE);
                    } else {
                    Log.i("setAlpha","red");
                }
                    } else {
                Log.i("setAlpha","personalIdeasStoreTmp " + Integer.toString(personalIdeasStoreTmp.size()));
                Log.i("setAlpha","personalIdeasStore "+Integer.toString(personalIdeasStore.size()));
            }

        if (isBookmarkIdeas){
            ideaList = "BookmarkID";
            if (!bookmarkIdeasStore.isEmpty()) {
                Log.i("setAlpha","bookmarkIdeasStore not empty");
                Log.i("bookmarkIdeasStore",Integer.toString(bookmarkIdeasStore.size()));
                bookmarkIdeasStore.remove(bookmarkIdeasStore.indexOf(mainIdea));
                for (MainIdea mainIdea1:bookmarkIdeasStore){
                    userIdList.add(mainIdea1.ideaTreeID);
                }
            } else if (!bookmarkIdeasStoreTmp.isEmpty()){
                Log.i("bookmarkIdeasStoreTmp",Integer.toString(bookmarkIdeasStoreTmp.size()));
                Log.i("setAlpha","bookmarkIdeasStoreTmp not empty");
                bookmarkIdeasStoreTmp.remove(bookmarkIdeasStoreTmp.indexOf(mainIdea));
                for (MainIdea mainIdea1:bookmarkIdeasStoreTmp){
                    userIdList.add(mainIdea1.ideaTreeID);
                }
                Log.i("bookmarkIdeasStoreTmp",Integer.toString(bookmarkIdeasStoreTmp.size()));
                Log.i("bookmarkIdeasStore",Integer.toString(bookmarkIdeasStore.size()));

            }
            if (bookmarkIdeasStore.isEmpty() && bookmarkIdeasStoreTmp.isEmpty()){
                Log.i("setAlpha","true");
                noIdeasMsg.setVisibility(View.VISIBLE);
            }
        }
        arrayAdapter.notifyDataSetChanged();
        Log.i("idealist",ideaList);
        ParseUser.getCurrentUser().put(ideaList,userIdList);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e== null){
                    removeIdea(mainIdea);
                } else {
                    Toast.makeText(IdeaTreeLibraryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void removeIdea(MainIdea mainIdea) {
        List<String> ideaTreeList = ParseUser.getCurrentUser().getList("IdeaTreeID");
        List<String> bookmarkID = ParseUser.getCurrentUser().getList("BookmarkID");
        if (bookmarkID == null) {
            bookmarkID = new ArrayList<>();
            ParseUser.getCurrentUser().put("BookmarkID", bookmarkID);
        }

        if (ideaTreeList == null) {
            ideaTreeList = new ArrayList<>();
            ParseUser.getCurrentUser().put("IdeaTreeID", ideaTreeList);
        }

        /*    if (!ideaTreeList.contains(mainIdea.ideaTreeID) && !bookmarkID.contains(mainIdea.ideaTreeID)) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
                query.whereEqualTo("IdeaTree", mainIdea.ideaTreeID);
                Log.i("removeFromParse", mainIdea.ideaTreeID);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                for (ParseObject object : objects) {
                                    object.deleteInBackground();
                                }
                            } else {
                                Toast.makeText(IdeaTreeLibraryActivity.this, "no objects", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }*/
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_tree_library);
        setTitle("Library");

        gridLayout = (GridView)findViewById(R.id.gridView);
        ideaTrees = new ArrayList<>();
        ideaTreeIDs = new ArrayList<>();
        bookmarkIDs = new ArrayList<>();
        allIdeasStoreTmp = new ArrayList<>();
        bookmarkIdeasStoreTmp = new ArrayList<>();
        bookmarkIdeasStoreIDs = new ArrayList<>();
        bookmarkIdeasStoreTmpIDs = new ArrayList<>();
        personalIdeasStore = new ArrayList<>();
        personalIdeasStoreTmp = new ArrayList<>();
        personalIdeasStoreIDs = new ArrayList<>();
        personalIdeasStoreTmpIDs = new ArrayList<>();
        allIdeasButton = (TextView)findViewById(R.id.allIdeas);
        bookmarkButton = (TextView)findViewById(R.id.bookmarkIdeas);
        personalButton = (TextView)findViewById(R.id.yourIdeas);
        availableColors = new ArrayList<>();
         new FriendsRequests(this);
        availableColors.add("purple");
        availableColors.add("indigo");
       availableColors.add("blue");
         availableColors.add("light blue");
       availableColors.add("cyan");
         availableColors.add("teal");
         availableColors.add("green");
        availableColors.add("light green");
        collatedListsButton = (TextView)findViewById(R.id.topLists);
        isAllIdeas = true;
        popupActive = false;
        noIdeasMsg = (TextView)findViewById(R.id.noIdeasMsg);
        allIdeasStore = new ArrayList<>();
        bookmarkIdeasStore = new ArrayList<>();
        lastClicks = new ArrayList<>();
        lastClicks.add("all");
        tagListEnabled = false;
        tags = new ArrayList<>();
        bookmarkList = ParseUser.getCurrentUser().getList("BookmarkID");
        tagSelected = false;
        allIdeasButton.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
        populateTags();
        downloadIdeas(allIdeas);
        Tips tipList = new Tips(this);
        tipList.setActivity("Library");
        addContentView(tipList,tipList.params);

       //clearIDs();
    }

    private void populateTags() {
        spinnerList = new ArrayList<>();
        spinnerList.add("Politics");
        spinnerList.add("Software");
        spinnerList.add("Literature");
        spinnerList.add("Science/Tech");
        spinnerList.add("Social");
        spinnerList.add("Film");
        spinnerList.add("Engineering");
        spinnerList.add("Lifestyle");
        spinnerList.add("Fiction");
        spinnerList.add("Art");
    }



    public void dropDownOnClick(View view) {

        if (tagListEnabled) {
            dropDownListView.setVisibility(View.INVISIBLE);
            tagListEnabled = false;
        } else {

            final CheckBoxAdapter spinnerAdapter = new CheckBoxAdapter(this,
                    R.layout.check_list, spinnerList);
            dropDownListView.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();
            dropDownListView.setVisibility(View.VISIBLE);

            tagListEnabled = true;
        }
    }

    public void checkOnClick(View view){
        CheckBox cb = (CheckBox)view;
        String tag = cb.getText().toString();
        if (tag.equals("Science/Tech")){
            tag = "ScienceTech";
        }
        tags.add(tag);
        tagSelected = true;
        Log.i("tags", tags.toString());
    }



    private void downloadIdeas(String ideaType) {
        grabIdeaTreeIDs(ideaType);

    }


    private void addIdeas(List<String> ideaTreeIDs) {
        for (int i = 0; i < ideaTreeIDs.size(); i++) {
            ideaTrees.add(ideaTreeIDs.get(i));
            Log.i("addIdeas", ideaTreeIDs.toString());
            findMainIdea(ideaTreeIDs.get(i));
        }

    }

    public void grabIdeaTreeIDs(String ideaType) {
        if (ParseUser.getCurrentUser() != null) {
            if(ParseUser.getCurrentUser().getList(ideaType) != null){
                   if( !ParseUser.getCurrentUser().getList(ideaType).isEmpty()){
                       Log.i("grabIdeaTreeIDs","0");
                Log.i("ideaTreeIDs", "before " +ideaTreeIDs.toString());
                List<String> ideaTreeIDsx = ParseUser.getCurrentUser().getList(ideaType);

                Log.i("ideaTreeIDs","size " +Integer.toString(ideaTreeIDs.size()));
                Log.i("ideaTreeIDs","after " + ideaTreeIDs.toString());
                noIdeasMsg.setVisibility(View.INVISIBLE);
                addIdeas(ideaTreeIDsx);
            } else {
                       Log.i("grabIdeaTreeIDs","1");
                noIdeasMsg.setVisibility(View.VISIBLE);
            }
            } else {
                Log.i("grabIdeaTreeIDs","2");
                noIdeasMsg.setVisibility(View.VISIBLE);
            }
        }
    }

    private void findMainIdea(final String ideaTreeID) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("IdeaTree",ideaTreeID);
        query.whereEqualTo("Identifier", "Main");
        query.findInBackground(new FindCallback<ParseObject>() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects.size() > 0){
                       // noIdeasMsg.setAlpha(0);
                        ideasAvailable = true;
                        MainIdea idea = new MainIdea(IdeaTreeLibraryActivity.this, objects.get(0).getString("IdeaTree"));
                        if (objects.get(0).getString("Summary") != null) {
                            idea.setTitle(objects.get(0).getString("Summary"));
                        }
                        if (objects.get(0).getString("Description") != null) {
                            idea.setDescription(objects.get(0).getString("Description"));
                        }
                        if (objects.get(0).getString("Author") != null) {
                            idea.author = objects.get(0).getString("Author");
                        }

                        if (objects.get(0).getNumber("voteMainTotal") != null) {
                            idea.totalVote = objects.get(0).getNumber("voteMainTotal").intValue();
                        }

                        if (ParseUser.getCurrentUser().getList("BookmarkID")==null){
                            ParseUser.getCurrentUser().put("BookmarkID",new ArrayList<>());
                            ParseUser.getCurrentUser().saveInBackground();
                        } else if (ParseUser.getCurrentUser().getList("BookmarkID").contains(ideaTreeID)){
                            idea.isBookmarked = true;
                        }


                        if (isAllIdeas){
                            Log.i("findMainIdea","isallideas is true");
                            if (!allIdeasStore.isEmpty()) {
                                allIdeasStore.add(idea);
                                Log.i("findMainIdea","adding ideas to allIdeasStoree");
                                activateAdapter(allIdeasStore);
                            } else if (!allIdeasStoreTmp.isEmpty()){
                                allIdeasStoreTmp.add(idea);
                                Log.i("findMainIdea","adding ideas to allIdeasStoreetmp");
                                activateAdapter(allIdeasStoreTmp);
                            }  else if (allIdeasStoreTmp.isEmpty() && allIdeasStore.isEmpty()){
                                Log.i("findMainIdea","adding ideas to allIdeasStoree");
                                allIdeasStore.add(idea);
                                activateAdapter(allIdeasStore);
                            }
                        } else if (isBookmarkIdeas){
                            Log.i("bookmarkIdeasStoreIDs","ideatreeid "+idea.ideaTreeID);
                            Log.i("bookmarkIdeasStoreIDs",bookmarkIdeasStoreIDs.toString());
                            Log.i("bookmarkIdeasStoreIDsTm",bookmarkIdeasStoreTmpIDs.toString());
                            if (!bookmarkIdeasStore.isEmpty()) {
                                if (!bookmarkIdeasStoreIDs.contains(idea.ideaTreeID)) {
                                    bookmarkIdeasStore.add(idea);
                                    bookmarkIdeasStoreIDs.add(idea.ideaTreeID);
                                    Log.i("findMainIdea", "adding ideas to bookmarkIdeasStore");
                                }
                                activateAdapter(bookmarkIdeasStore);
                            }else if (!bookmarkIdeasStoreTmp.isEmpty()){

                                if (!bookmarkIdeasStoreTmpIDs.contains(idea.ideaTreeID)) {
                                    bookmarkIdeasStoreTmp.add(idea);
                                    bookmarkIdeasStoreTmpIDs.add(idea.ideaTreeID);
                                    Log.i("findMainIdea", "adding ideas to bookmarkIdeasStoretmp");
                                }
                                activateAdapter(bookmarkIdeasStoreTmp);
                            }  else if (bookmarkIdeasStoreTmp.isEmpty() && bookmarkIdeasStore.isEmpty()){
                                bookmarkIdeasStore.add(idea);
                                bookmarkIdeasStoreIDs.add(idea.ideaTreeID);
                                Log.i("findMainIdea","adding ideas to bookmarkIdeasStore");
                                Log.i("bookmarkIdeasStoresize",Integer.toString(bookmarkIdeasStore.size()));
                                activateAdapter(bookmarkIdeasStore);
                            }
                            Log.i("count","bookmarkideasstore " + Integer.toString(bookmarkIdeasStore.size()));
                            Log.i("count","bookmarkideasstoretmp " + Integer.toString(bookmarkIdeasStoreTmp.size()));
                        } else if (isYourIdeas){
                            Log.i("findMainIdea","isyouridea is true");
                            if (!personalIdeasStore.isEmpty()) {
                                personalIdeasStore.add(idea);
                                activateAdapter(personalIdeasStore);
                            }else if (!personalIdeasStoreTmp.isEmpty()){
                                personalIdeasStoreTmp.add(idea);
                                activateAdapter(personalIdeasStoreTmp);
                            }  else if (personalIdeasStoreTmp.isEmpty() && personalIdeasStore.isEmpty()){
                                personalIdeasStore.add(idea);
                                activateAdapter(personalIdeasStore);
                            }
                        }


                    } else {
                        Log.i("noIdeasMsg", "setting alpha");
                      //  noIdeasMsg.setAlpha(1);
                    }
                } else {
                    Log.i("findMainIdea", e.getMessage());
                }
            }
        });
    }


    //utility method to clear ids next to user
    private void clearIDs() {
        List clear = ParseUser.getCurrentUser().getList("IdeaTreeID");
        clear.clear();
        ParseUser.getCurrentUser().put("IdeaTreeID",clear);
        try {
            ParseUser.getCurrentUser().save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onClickBookMarkIdeas(View view){
        Log.i("onClickBookMarkIdeas",lastClicks.get(lastClicks.size() - 1));
        if (!lastClicks.get(lastClicks.size() - 1).equals("bookmark")) {
            lastClicks.add("bookmark");
            isAllIdeas = false;
            isYourIdeas = false;
            isBookmarkIdeas = true;
          resetButtons("bookmark");
            cleanAllIdeas();
            cleanYourIdeas();
            downloadIdeas(bookmarkIdeas);

        }
        }

    private void cleanYourIdeas() {
        if (!personalIdeasStore.isEmpty()){
            personalIdeasStoreTmp.addAll(personalIdeasStore);
            Log.i("findMainIdea","clearing all items in allideastore and adding to allideasstoretmp");
            personalIdeasStore.clear();
            if (arrayAdapter !=null) {
                arrayAdapter.notifyDataSetChanged();
            }
        } else {
            Log.i("findMainIdea","clearing all items in allideasstoretmp and adding to allideastore");
            personalIdeasStore.addAll(personalIdeasStoreTmp);
            personalIdeasStoreTmp.clear();
            if (arrayAdapter !=null) {
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onClickYourIdeas(View view) {
        if (!lastClicks.get(lastClicks.size() - 1).equals("your")) {
            lastClicks.add("your");
            isBookmarkIdeas = false;
            isAllIdeas = false;
            isYourIdeas = true;
            Log.i("onClickYourIdeas", "clicked");

            Log.i("onClickYourIdeas", Integer.toString(ideaTreeIDs.size()));
            resetButtons("your");
            cleanAllIdeas();
            cleanBookmarks();
            if (!personalIdeasStore.isEmpty()) {
                personalIdeasStoreTmp.addAll(personalIdeasStore);
                noIdeasMsg.setVisibility(View.INVISIBLE);
                Log.i("onClickYourIdeas", "personalIdeasStoreTmp size " + Integer.toString(personalIdeasStoreTmp.size()));
                Log.i("onClickYourIdeas", "personalIdeasStore not empty");
                personalIdeasStore.clear();
                arrayAdapter.notifyDataSetChanged();
                findPersonalIdeas(personalIdeasStoreTmp);
            } else if (!personalIdeasStoreTmp.isEmpty()) {
                Log.i("onClickYourIdeas", "personalIdeasStore size " + Integer.toString(personalIdeasStore.size()));
                Log.i("onClickYourIdeas", "personalIdeasStoreTmp not empty");
                personalIdeasStore.addAll(personalIdeasStoreTmp);
                personalIdeasStoreTmp.clear();
                arrayAdapter.notifyDataSetChanged();
                noIdeasMsg.setVisibility(View.INVISIBLE);
                findPersonalIdeas(personalIdeasStore);
            } else if (personalIdeasStore.isEmpty() && personalIdeasStoreTmp.isEmpty()) {
                Log.i("onClickYourIdeas", "both empty");
                findPersonalIdeas(personalIdeasStore);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void cleanBookmarks() {
        if (!bookmarkIdeasStore.isEmpty()){
            bookmarkIdeasStoreTmp.addAll(bookmarkIdeasStore);
            bookmarkIdeasStoreTmpIDs.addAll(bookmarkIdeasStoreIDs);
            Log.i("findMainIdea","bookmarkideasstore is not empty clearing it and adding to tmp");
            bookmarkIdeasStoreIDs.clear();
            bookmarkIdeasStore.clear();
            if (arrayAdapter !=null) {
                arrayAdapter.notifyDataSetChanged();
            }
        } else if (!bookmarkIdeasStoreTmp.isEmpty()) {
            Log.i("findMainIdea","bookmarkideasstoretmp is not empty  clearing it and adding to bookmarkideasstore");
            bookmarkIdeasStore.addAll(bookmarkIdeasStoreTmp);
            bookmarkIdeasStoreIDs.addAll(bookmarkIdeasStoreTmpIDs);
            bookmarkIdeasStoreTmpIDs.clear();
            bookmarkIdeasStoreTmp.clear();
            if (arrayAdapter !=null) {
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private void cleanAllIdeas(){
        if (!allIdeasStore.isEmpty()){
            allIdeasStoreTmp.addAll(allIdeasStore);
            Log.i("findMainIdea","clearing all items in allideastore and adding to allideasstoretmp");
            allIdeasStore.clear();
            if (arrayAdapter !=null) {
                arrayAdapter.notifyDataSetChanged();
            }

        } else {
            Log.i("findMainIdea","clearing all items in allideasstoretmp and adding to allideastore");
            allIdeasStore.addAll(allIdeasStoreTmp);
            allIdeasStoreTmp.clear();
            if (arrayAdapter !=null) {
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void findPersonalIdeas(ArrayList<MainIdea> personalIdeasStoreX) {
        Log.i("findPersonalIdeas","personalIdeasStoreIDs "+Integer.toString(personalIdeasStoreIDs.size()));
            if (!allIdeasStore.isEmpty()) {
                Log.i("findPersonalIdeas", "allIdeasStore size  " + Integer.toString(allIdeasStore.size()));
                for (MainIdea idea : allIdeasStore) {
                    if (idea.author.equals(ParseUser.getCurrentUser().getUsername())) {
                        if (!personalIdeasStoreIDs.contains(idea.ideaTreeID)) {
                            Log.i("findPersonalIdeas", "adding id to personalIdeasStoreIDs");
                            personalIdeasStoreX.add(idea);
                            personalIdeasStoreIDs.add(idea.ideaTreeID);
                        }
                    }
                }
            } else {
                Log.i("findPersonalIdeas", "allIdeasStore.isEmpty");
                Log.i("findPersonalIdeas", "allIdeasStoreTmp size  " + Integer.toString(allIdeasStoreTmp.size()));
                for (MainIdea idea : allIdeasStoreTmp) {
                    if (idea.author.equals(ParseUser.getCurrentUser().getUsername())) {
                        if (!personalIdeasStoreIDs.contains(idea.ideaTreeID)) {
                            Log.i("findPersonalIdeas", "adding id to personalIdeasStoreTmpIDs");
                            personalIdeasStoreX.add(idea);
                            personalIdeasStoreIDs.add(idea.ideaTreeID);
                        }
                    }
                }
            }
        Log.i("findPersonalIdeas", "personalIdeasStoreX size " + Integer.toString(personalIdeasStoreX.size()));
        Log.i("findPersonalIdeas","personalIdeasStoreIDs "+personalIdeasStoreIDs.toString());
        activateAdapter(personalIdeasStoreX);
        if (personalIdeasStoreX.isEmpty()){
            Log.i("personalIdeasStoreX", "personalIdeasStoreX empty setting alpa");
            noIdeasMsg.setAlpha(1);
        } else {
            noIdeasMsg.setVisibility(View.INVISIBLE);
        }
            }



    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onClickAllIdeas(View view) {
        if (!lastClicks.get(lastClicks.size() - 1).equals("all")) {
            lastClicks.add("all");
            isBookmarkIdeas = false;
            isAllIdeas = true;
            isYourIdeas = false;
            Log.i("onClickAllIdeas", "clicked");
            resetButtons("all");
            Log.i("onClickAllIdeas", Integer.toString(ideaTreeIDs.size()));
            cleanBookmarks();
            if (!allIdeasStore.isEmpty()) {
                Log.i("findMainIdea", "allIdeasStore is not empty adding to it");
                activateAdapter(allIdeasStore);
                noIdeasMsg.setVisibility(View.INVISIBLE);
            } else if (!allIdeasStoreTmp.isEmpty()) {
                Log.i("findMainIdea", "allIdeasStoretmp is not empty adding to it");
                noIdeasMsg.setVisibility(View.INVISIBLE);
                activateAdapter(allIdeasStoreTmp);
            } else if (allIdeasStoreTmp.isEmpty() && allIdeasStore.isEmpty()) {
                noIdeasMsg.setAlpha(1);
            }
        }
    }

    public void onClickCreateBookmark(View view){
        ImageView bookmarkWidget = (ImageView)view;

        MainIdea mainIdea = (MainIdea) view.getTag();
        if (!mainIdea.isBookmarked) {
            bookmarkWidget.setBackgroundResource(R.drawable.ic_unbookmark);
            mainIdea.isBookmarked = true;
            if (bookmarkList != null) {
                bookmarkList.add(mainIdea.ideaTreeID);
            } else {
                bookmarkList = new ArrayList<>();
            }
            Log.i("onClickCreateBookmark", bookmarkList.toString());
            ParseUser.getCurrentUser().put("BookmarkID", bookmarkList);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(IdeaTreeLibraryActivity.this, "MindMap bookmarked", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.i("onClickCreateBookmark", "removing bookmark");
            bookmarkWidget.setBackgroundResource(R.drawable.ic_bookmark);
            Toast.makeText(this, "MindMap removed from bookmarks.", Toast.LENGTH_SHORT).show();
            mainIdea.isBookmarked = false;
            bookmarkIdeasStore.remove(mainIdea);
            bookmarkIdeasStoreTmp.remove(mainIdea);
            bookmarkIdeasStoreTmpIDs.remove(mainIdea.ideaTreeID);
            bookmarkIdeasStoreIDs.remove(mainIdea.ideaTreeID);
            arrayAdapter.notifyDataSetChanged();
            bookmarkList.remove(mainIdea.ideaTreeID);
            ParseUser.getCurrentUser().put("BookmarkID",bookmarkList);
            ParseUser.getCurrentUser().saveInBackground();
        }

        }

     @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
     public void onClickCollatedLists(View view){
         if (!lastClicks.get(lastClicks.size()-1).equals("Extracted Lists")){
             lastClicks.add("Extracted Lists");
         }
         Log.i("extracted lists", lastClicks.toString());
         resetButtons("Extracted Lists");
        Intent intent = new Intent(this, CollatedListsActivity.class);
        startActivity(intent);
     }

     private void resetButtons(String key){
         switch (key){
             case "Extracted Lists":
                 collatedListsButton.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
                 allIdeasButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 personalButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 bookmarkButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 break;
             case "all":
                 collatedListsButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 bookmarkButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 personalButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 allIdeasButton.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
                 break;
             case "your":
                 collatedListsButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 bookmarkButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 allIdeasButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 personalButton.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
                 break;
             case "bookmark":
                 collatedListsButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 allIdeasButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 personalButton.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
                 bookmarkButton.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
         }
     }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (lastClicks!=null){
            if (lastClicks.size() > 0) {
                if (lastClicks.get(lastClicks.size() - 1) != null) {
                    findLastSelectedItem(1);
                }
            }
        }
    }

    void findLastSelectedItem(int i){
        switch (lastClicks.get(lastClicks.size() - i)) {
            case "Extracted Lists":
                if (lastClicks.size() > 1) {
                    findLastSelectedItem(2);
                    break;
                }
            case "all":
                resetButtons("all");
                break;
            case "your":
                resetButtons("your");
                break;
            case "bookmark":
                resetButtons("bookmark");
                break;
        }
    }
}
