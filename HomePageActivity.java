package com.parse.ideanetwork;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;





public class HomePageActivity extends AppCompatActivity {


    public static final float voteRateDuration =36000000*24;

    ArrayList<ParseObject> voteList;

    ArrayList<MainIdea> list;
    ArrayList<MainIdea> genericList;
    ArrayList<MainIdea> politicsList;
    ArrayList<MainIdea> artList;
    ArrayList<MainIdea> softwareList;
    ArrayList<MainIdea> literatureList;
    ArrayList<MainIdea> filmList;
    ArrayList<MainIdea> fictionList;
    ArrayList<MainIdea> engineeringList;
    ArrayList<MainIdea> lifestyleList;
    ArrayList<MainIdea> socialList;
    ArrayList<MainIdea> scienceTechList;
    TextView noIdeasMsg;
    TextView display;
    List<String> bookmarkList;
    ArrayList<TrendingMap> timeMaps;
    LayoutInflater inflater;

    boolean isExample;
    ParseObject exampleObj;


    ArrayList<String> spinnerList;
    GridView gridView;
    GridAdapter topIdeaAdapter;
    ListView dropDownListView;

    boolean tagListEnabled;
    boolean tagSelected;
    ArrayList<String> tags;
    CheckBox cb;


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

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void checkOnClick(View view){
        if (cb != null){
            cb.toggle();
        }
        cb = (CheckBox)view;
        String tag = cb.getText().toString();
        listSelector(tag);
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void listSelector(String tag) {
        switch (tag) {
            case "Politics":
                setTrendingList(politicsList,tag);
                display.setText(tag);
                break;
            case "Art":
                setTrendingList(artList,tag);
                display.setText(tag);
                break;
            case "Literature":
                setTrendingList(literatureList,tag);
                display.setText(tag);
                break;
            case "Fiction":
                setTrendingList(fictionList,tag);
                display.setText(tag);
                break;
            case "Film":
                setTrendingList(filmList,tag);
                display.setText(tag);
                break;
            case "Lifestyle":
                setTrendingList(lifestyleList,tag);
                display.setText(tag);
                break;
            case "Engineering":
                setTrendingList(engineeringList,tag);
                display.setText(tag);
                break;
            case "Science/Tech":
                setTrendingList(scienceTechList,tag);
                display.setText(tag);
                break;
            case "Social":
                setTrendingList(socialList,tag);
                display.setText(tag);
                break;
            case "Software":
                setTrendingList(softwareList,tag);
                display.setText(tag);
                break;
            case "All":
                setTrendingList(list,tag);
                display.setText(tag);
                break;
                }
        }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void setTrendingList(ArrayList<MainIdea> list,String tag){
        if (list != null  && !list.isEmpty()){
            Log.i("activateGridAdapter", "politicsList != null  && !politicsList.isEmpty()");
            activateGridAdapter(list);
        } else {
            Log.i("activateGridAdapter", "red");
            list = new ArrayList<>();
            compileIdeas(tag,list);
        }
        if (!genericList.isEmpty()){
            genericList.clear();
        }
        genericList.addAll(list);
        if (genericList.isEmpty()){
            Log.i("trendissue", "setting visible in setrendlist");
            noIdeasMsg.setVisibility(View.VISIBLE);
        } else {
            noIdeasMsg.setVisibility(View.INVISIBLE);
        }
        activateGridAdapter(list);
    }

    private void compileIdeas(String tag, ArrayList<MainIdea> ideaList) {
        for (MainIdea item:list){
            if (item.tags.contains(tag)){
                ideaList.add(item);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void activateGridAdapter(ArrayList<MainIdea> list) {
        Log.i("activateGridAdapter", list.toString());
        topIdeaAdapter = new GridAdapter(this, R.layout.list_view,list,true);
        gridView.setAdapter(topIdeaAdapter);
        topIdeaAdapter.notifyDataSetChanged();
        // noIdeasMsg.setAlpha(0);
    }


    private void populateTags() {
        spinnerList = new ArrayList<>();
        spinnerList.add("All");
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

    public void libraryOnClick(View view){
        dropDownListView.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(HomePageActivity.this, IdeaTreeLibraryActivity.class);
        startActivity(intent);
    }

    public void profileOnClick(View view){
        dropDownListView.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(HomePageActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    public void searchOnClick(View view){
        dropDownListView.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(HomePageActivity.this,SearchActivity.class);
        startActivity(intent);
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
                        Toast.makeText(HomePageActivity.this, "MindMap bookmarked", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.i("onClickCreateBookmark", "removing bookmark");
            bookmarkWidget.setBackgroundResource(R.drawable.ic_bookmark);
            Toast.makeText(this, "MindMap removed from bookmarks.", Toast.LENGTH_SHORT).show();
            mainIdea.isBookmarked = false;
            bookmarkList.remove(mainIdea.ideaTreeID);
            ParseUser.getCurrentUser().put("BookmarkID",bookmarkList);
            ParseUser.getCurrentUser().saveInBackground();
        }
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
                                        Intent intent = new Intent(HomePageActivity.this,MainActivity.class);
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setTitle("Home");



        list = new ArrayList<>();
        gridView=(GridView)findViewById(R.id.gridView);

        activateGridAdapter(list);
        voteList = new ArrayList<>();
        dropDownListView = (ListView)findViewById(R.id.dropDownList);
        spinnerList = new ArrayList<>();
        genericList = new ArrayList<>();
        tags = new ArrayList<>();
        display = (TextView)findViewById(R.id.display);
        display.setText("All");
        tagSelected = false;
        noIdeasMsg = (TextView)findViewById(R.id.noIdeasMsg);
        bookmarkList = ParseUser.getCurrentUser().getList("BookmarkID");
        inflater = LayoutInflater.from(this);
        populateTags();
         timeMaps = new ArrayList<>();
        findTopTrending();
        new FriendsRequests(this);
        Tips tipList = new Tips(this);

        tipList.setActivity("HomePage");
        addContentView(tipList,tipList.params);

    }



    public void findTopTrending(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("Identifier", "Main");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()) {
                        for (ParseObject obj:objects){
                            isExample = obj.getBoolean("example");

                            if (isExample){
                                createIdea((obj));
                                noIdeasMsg.setVisibility(View.INVISIBLE);
                            } else {
                                getRecentVotes(obj);
                            }

                        }
                        if (!timeMaps.isEmpty()) {
                            organiseVotes();
                          //  setClickListener();
                        }

                      } else {
                        noIdeasMsg.setVisibility(View.VISIBLE);
                        Log.i("trendissue", "setting visible in findtoptrending");
                    }
                    }
            }
        });
    }

    private void organiseVotes() {


            ArrayList<Integer> times = new ArrayList<>();
            ArrayList<Integer> topTimes = new ArrayList<>();
            ArrayList<TrendingMap> topMaps = new ArrayList<>();
            ArrayList<ParseObject> orderedObjs = new ArrayList<>();
            for (TrendingMap map : timeMaps) {

                times.add(map.voteCount);


            }
            Log.i("organiseVotes", "before " + times.toString());

            Collections.sort(times, new Comparator<Integer>() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public int compare(Integer o1, Integer o2) {
                    return Integer.compare(o1, o2);
                }
            });

            Collections.sort(times, Collections.<Integer>reverseOrder());
            Log.i("organiseVotes", "after " + times.toString());

            double voteMin = times.get(0) * 0.7;
            for (TrendingMap map : timeMaps) {
                if (map.voteCount >= voteMin) {
                    topMaps.add(map);
                    topTimes.add(map.voteCount);
                }
            }

            Log.i("organiseVotes", "topMaps " + Integer.toString(topMaps.size()));
            Log.i("organiseVotes", "topTimes " + Integer.toString(topTimes.size()));

            Collections.sort(topTimes, new Comparator<Integer>() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public int compare(Integer o1, Integer o2) {
                    return Integer.compare(o1, o2);
                }
            });

            Collections.sort(topTimes, Collections.<Integer>reverseOrder());

            Log.i("organiseVotes", "topTimes " + topTimes.toString());
            for (int j = 0; j < topTimes.size(); j++) {
                Log.i("orderedObjs", "looping to next topTime");
                for (int i = 0; i < topMaps.size(); i++) {
                    Log.i("organiseVotes", "1 ");
                    if (topMaps.get(i).voteCount == topTimes.get(j)) {
                        Log.i("orderedObjs", "inner loop " + topMaps.get(i).obj.getString("Summary"));
                        if (!topMaps.get(i).alreadyUsed) {
                            orderedObjs.add(topMaps.get(i).obj);
                            topMaps.get(i).alreadyUsed = true;
                            break;
                        }

                    }
                }

            }
            Log.i("organiseVotes", "orderedObjs " + Integer.toString(orderedObjs.size()));
            if (!orderedObjs.isEmpty()) {
                noIdeasMsg.setVisibility(View.INVISIBLE);
                for (ParseObject obj : orderedObjs) {
                    if (obj != null) {
                        Log.i("organiseVotes", "creating obj");
                        createIdea(obj);
                    }
                }
            } else {
                noIdeasMsg.setVisibility(View.VISIBLE);
            }



        }


    private void getRecentVotes(ParseObject obj) {
        List<Long> voteTimes = obj.getList("VoteTimes");
        int recentVotes = 0;

        if (voteTimes != null) {
            if (!voteTimes.isEmpty()) {
                for (Long time : voteTimes) {
                    if (time >= TimeSetter.getTime() - voteRateDuration ||
                            obj.getBoolean("example")) {
                        if (obj.getNumber("voteMainTotal").intValue() > 0){
                            recentVotes++;
                        }

                    }
                }
               TrendingMap trendingMap = new TrendingMap(recentVotes,obj);
               timeMaps.add(trendingMap);

            }
        }

    }


    private void createIdea(ParseObject object) {
                    MainIdea idea = new MainIdea(HomePageActivity.this, object.getString("IdeaTree"));
                    if (object.getString("Description") != null) {
                        idea.setDescription(object.getString("Description"));
                    }
                    if (object.getString("Summary") != null) {
                        idea.setTitle(object.getString("Summary"));
                    }
                    if (object.getString("Author") != null) {
                        idea.author = object.getString("Author");
                    }
                    if (object.getNumber("voteMainTotal") != null) {
                         idea.totalVote = object.getNumber("voteMainTotal").intValue();
                    }
                    if (object.getNumber("voteMainTotal") != null) {
                        idea.totalVote = object.getNumber("voteMainTotal").intValue();
                     }
                    if (object.getList("tags") != null){
                        idea.setTags(object.getList("tags"));
                    }

                    List<String> bookmarkIDs =ParseUser.getCurrentUser().getList("BookmarkID");
        if (bookmarkIDs != null) {
            if (ParseUser.getCurrentUser().getList("BookmarkID").contains(idea.ideaTreeID)) {
                idea.isBookmarked = true;
            }
        }
        list.add(idea);
                 topIdeaAdapter.notifyDataSetChanged();
                }



    public void onClickCollate(View view){

        Intent intent = new Intent(this,CollateActivity.class);
        MainIdea idea = (MainIdea) view.getTag();
        intent.putExtra("id",idea.ideaTreeID);
        intent.putExtra("title",idea.summary);
        intent.putExtra("author",idea.author);
        startActivity(intent);
    }

    public void onClickGotoProfile(View view){
        String author = (String) view.getTag();
        Log.i("author",author);
            Intent intent = new Intent(this,
                    ProfileReadOnlyActivity.class);
            intent.putExtra("name",author);
        this.startActivity(intent);

    }


    public void onClickIdeaTree(View view){
        Intent intent = new Intent(HomePageActivity.this, IdeaTreeActivity.class);
        String clickedIdea = (String)view.getTag();
        intent.putExtra("IdeaTreeID", clickedIdea);
        startActivity(intent);
    }




}



