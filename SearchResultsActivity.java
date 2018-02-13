package com.parse.ideanetwork;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.EditText;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

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


public class SearchResultsActivity extends AppCompatActivity {

    EditText searchView;
    Spinner spinner;
    GridView gridView;
    ArrayList<String> list;
    String tag;
    ArrayList<MainIdea> ideas;
    ArrayList<MainIdea> tmpIdeas;
    ArrayList<MainIdea> ideasStore;
    ArrayList<MainIdea> searchedTopIdeas;
    ArrayList<MainIdea> searchedAllIdeas;
    ArrayList<MainIdea> tmpSearchedIdeas;
    ArrayList<MainIdea> orderedSearchedIdeas;

    ArrayList<MainIdea> topIdeas;
    ArrayList<MainIdea> tmpTopIdeas;
    ArrayList<ArrayList<MainIdea>> allList;
    ArrayList<ArrayList<MainIdea>> allListTmp;
    ArrayList<MainIdea> recentIdeas;
    ArrayList<MainIdea> allTopIdeas;
    ArrayList<MainIdea> topSearchedIdeas;
    double voteThreshold = 0;
    List<String> bookmarkList;
    GridAdapter arrayAdapter;

    boolean listActivated = false;
    boolean firstTime = true;
    boolean searchActivated = false;
    boolean topVoteItemEnabled = false;
    boolean allItemEnabled = true;

    TextView noIdeasMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchView = (EditText)findViewById(R.id.searchView);
        spinner = (Spinner)findViewById(R.id.filterOptions);
        gridView = (GridView)findViewById(R.id.searchResultsGridView);
        noIdeasMsg = (TextView)findViewById(R.id.noIdeasMsg);
        list = new ArrayList<>();
        list.add("All (Newest - Oldest)");
        list.add("Top Voted");
        tag = getIntent().getStringExtra("category");
        if (tag.equals("Science & Tech")){
            tag = "ScienceTech";
        }
        if (tag.equals("Film/TV")){
            tag = "Film";
        }
        setTitle(tag);
        recentIdeas = new ArrayList<>();
        allTopIdeas = new ArrayList<MainIdea>();
        topIdeas = new ArrayList<>();
        allList = new ArrayList<ArrayList<MainIdea>>();
        allListTmp = new ArrayList<ArrayList<MainIdea>>();
        ideas = new ArrayList<>();
         new FriendsRequests(this);
        searchedTopIdeas = new ArrayList<>();
        searchedAllIdeas = new ArrayList<>();
        tmpSearchedIdeas =  new ArrayList<>();
        ideasStore = new ArrayList<>();
        tmpIdeas = new ArrayList<MainIdea>();
        tmpTopIdeas = new ArrayList<MainIdea>();
        queryParse();
        setArrayAdapter();
        Tips tipList = new Tips(this);
        tipList.setActivity("Search");
        addContentView(tipList,tipList.params);
    }

    public void searchBarOnClick(View view) {
        String searchMsg = searchView.getText().toString();
        if (!recentIdeas.isEmpty()) {
            if (!searchMsg.equals("")) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
                query.whereEqualTo(tag, true);
                query.whereContains("SummarySearch", searchMsg.toLowerCase());
                if (topVoteItemEnabled) {
                    query.orderByDescending("voteMainTotal");
                } else if (allItemEnabled) {
                    query.orderByDescending("creation");
                }
                query.findInBackground(new FindCallback<ParseObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (!objects.isEmpty()) {
                                searchActivated = true;
                                if (allItemEnabled) {
                                    if (searchedAllIdeas != null && !searchedAllIdeas.isEmpty()) {
                                        searchedAllIdeas.clear();
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                    if (!listActivated) {
                                        findAndClear(searchedAllIdeas);
                                    } else {
                                        findAndClearTmp(searchedAllIdeas);
                                    }

                                    for (ParseObject obj : objects) {
                                        searchedAllIdeas.add(makeIdea(obj));
                                    }
                                    activateGridAdapter(searchedAllIdeas);
                                } else {
                                    if (searchedTopIdeas != null && !searchedTopIdeas.isEmpty()) {
                                        searchedTopIdeas.clear();
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                    if (!listActivated) {
                                        findAndClear(searchedTopIdeas);
                                    } else {
                                        findAndClearTmp(searchedTopIdeas);
                                    }

                                    for (ParseObject obj : objects) {
                                        searchedTopIdeas.add(makeIdea(obj));
                                    }
                                    activateGridAdapter(searchedTopIdeas);
                                }
                            } else {
                                Toast.makeText(SearchResultsActivity.this, "No results found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } else {
            Toast.makeText(SearchResultsActivity.this, "No results found.", Toast.LENGTH_SHORT).show();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void resetOnClick(View view) {

        searchView.getText().clear();
        if (!recentIdeas.isEmpty()) {
            searchActivated = false;
            if (allItemEnabled) {
                if (ideas != null && !ideas.isEmpty()) {
                    activateGridAdapter(ideas);
                } else if (tmpIdeas != null && !tmpIdeas.isEmpty()) {
                    activateGridAdapter(tmpIdeas);
                }
            } else {
                Log.i("testing", "topIdeas " + topIdeas.toString());
                Log.i("testing", "tmpTopIdeas " + tmpTopIdeas.toString());
                if (topIdeas != null && !topIdeas.isEmpty()) {
                    Log.i("testing", "topIdeas != null && !topIdeas.isEmpty()");
                    activateGridAdapter(topIdeas);
                } else if (tmpTopIdeas != null && !tmpTopIdeas.isEmpty()) {
                    Log.i("testing", "tmpTopIdeas != null && !tmpTopIdeas.isEmpty()");
                    activateGridAdapter(tmpTopIdeas);
                } else {
                    Log.i("recentIdeas", Integer.toString(recentIdeas.size()));
                    findTopIdeas(recentIdeas, allTopIdeas, topIdeas);

                }
            }
        }
    }

    private void queryParse() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo(tag,true);
        query.whereEqualTo("Identifier", "Main");
        query.orderByDescending("voteMainTotal");
        query.findInBackground(new FindCallback<ParseObject>() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    if (!objects.isEmpty()){
                        addToAllList();
                        for (ParseObject obj:objects) {
                            recentIdeas.add( makeIdea(obj));
                        }
                        findNewIdeas();

                    } else {
                        Log.i("testing", "empty");
                    }
                } else {
                    Log.i("testing", e.getMessage());
                }
            }
        });
    }

    private MainIdea makeIdea(ParseObject obj) {
        MainIdea idea = new MainIdea(SearchResultsActivity.this, obj.
                getString("IdeaTree"));
        if (obj.getString("Summary") != null) {
            idea.setTitle(obj.getString("Summary"));
        }
        if (obj.getString("Description") != null) {
            idea.setDescription(obj.getString("Description"));
        }
        if (obj.getString("Author") != null) {
            idea.author = obj.getString("Author");
        }
        if (obj.getNumber("voteMainTotal") != null) {
            idea.totalVote = obj.getNumber("voteMainTotal").intValue();
            Log.i("voteMainTotal", Integer.toString(idea.totalVote));
        }
        if (obj.getNumber("creation") != null) {
            idea.creation = obj.getNumber("creation").longValue();
        }
        List<String> bookmarkID = ParseUser.getCurrentUser().getList("BookmarkID");
        if (bookmarkID!=null) {
            if (bookmarkID.contains(idea.ideaTreeID)) {
                idea.isBookmarked = true;
            }
        }
        return  idea;
    }

    public void onClickCreateBookmark(View view){
        ImageView bookmarkWidget = (ImageView)view;

        MainIdea mainIdea = (MainIdea) view.getTag();
        bookmarkList = ParseUser.getCurrentUser().getList("BookmarkID");
        if (!mainIdea.isBookmarked) {
            bookmarkWidget.setBackgroundResource(R.drawable.ic_unbookmark);
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
                        Toast.makeText(SearchResultsActivity.this, "MindMap bookmarked", Toast.LENGTH_SHORT).show();
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

    public void onClickIdeaTree(View view){

        Intent intent = new Intent(SearchResultsActivity.this, IdeaTreeActivity.class);
        String clickedIdea = (String)view.getTag();
        intent.putExtra("IdeaTreeID", clickedIdea);
        Log.i("onClickIdeaTree",clickedIdea);
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


    private void addToAllList() {
        if (allList != null) {
            allList.add(topIdeas);
            allList.add(ideas);
           // allList.add(searchedIdeas);
        }
        if (allListTmp != null){
            allListTmp.add(tmpTopIdeas);
            allListTmp.add(tmpIdeas);
            allListTmp.add(tmpSearchedIdeas);
            allListTmp.add(topIdeas);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void findTopIdeas(ArrayList<MainIdea> givingList,ArrayList<MainIdea> receivingList,
                              ArrayList<MainIdea> finalList) {
        voteThreshold = 0.7*givingList.get(0).totalVote;
        for (MainIdea idea:givingList){
                receivingList.add(idea);
        }

        firstTime = false;
             if (receivingList != null) {
               if (!receivingList.isEmpty()) {

                ArrayList<Integer> topIdeaVoteTotals = new ArrayList<>();
                for (MainIdea idea : receivingList) {
                    topIdeaVoteTotals.add(idea.totalVote);
                }

                Collections.sort(topIdeaVoteTotals, new Comparator<Integer>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return Integer.compare(o1,o2);
                    }
                });

                Collections.sort(topIdeaVoteTotals, Collections.<Integer>reverseOrder());

                Log.i("testing", "creationTimes " + topIdeaVoteTotals.toString());


                for (int j = 0; j < receivingList.size(); j++) {
                    for (int i = 0; j < topIdeaVoteTotals.size(); i++) {
                        if (receivingList.get(i).totalVote == topIdeaVoteTotals.get(j)) {
                            if (!finalList.contains(receivingList.get(i))) {
                                finalList.add(receivingList.get(i));
                                break;
                            }
                        }
                    }
                }
            }
        }
        listActivated = true;
        activateGridAdapter(finalList);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void activateGridAdapter(ArrayList<MainIdea> list) {
        Log.i("activateGridAdapter", list.toString());
        arrayAdapter = new GridAdapter(SearchResultsActivity.
                this,R.layout.adapterlayout,list,true);
        arrayAdapter.search = true;
        gridView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        if (list.isEmpty()){
            noIdeasMsg.setVisibility(View.VISIBLE);
        } else {
            noIdeasMsg.setVisibility(View.INVISIBLE);
        }

    }

    public void onClickGotoProfile(View view){
        String author = (String) view.getTag();
        Log.i("author",author);
        Intent intent = new Intent(this,
                ProfileReadOnlyActivity.class);
        intent.putExtra("name",author);
        this.startActivity(intent);

    }

    private void setArrayAdapter() {
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,
                android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              if (position==1 && topIdeas != null) {
                  topVoteItemEnabled = true;
                  allItemEnabled = false;
                  Log.i("testing", "red");
                  if (searchActivated) {
                          if (searchedTopIdeas != null && !searchedTopIdeas.isEmpty()) {
                              topSearchedIdeas = new ArrayList<MainIdea>();
                              orderedSearchedIdeas = new ArrayList<MainIdea>();
                              activateGridAdapter(searchedTopIdeas);
                              //findTopIdeas(searchedTopIdeas, topSearchedIdeas, orderedSearchedIdeas);
                          }
                  } else {

                  if (listActivated) {
                      Log.i("testing", "position == 1 & listActivated");
                      Log.i("testing", "green");
                      if (firstTime) {
                          Log.i("testin", "blu");
                          findTopIdeas(recentIdeas,allTopIdeas,topIdeas);
                          Log.i("testing", "topIdeas size in firstTime" + Integer.toString(topIdeas.size()));
                      }
                      Log.i("topIdeas", "org");
                      if (!topIdeas.isEmpty()) {
                          findAndClear(topIdeas);
                          Log.i("testing", "topIdeas size after" + Integer.toString(topIdeas.size()));
                          activateGridAdapter(topIdeas);
                      } else if (!tmpTopIdeas.isEmpty()) {
                          findAndClearTmp(tmpTopIdeas);
                          Log.i("topIdeas", "tmpTopIdeas size after" + Integer.toString(tmpTopIdeas.size()));
                          activateGridAdapter(tmpTopIdeas);
                      } else {
                          allTopIdeas = new ArrayList<MainIdea>();
                          topIdeas = new ArrayList<MainIdea>();
                          findTopIdeas(recentIdeas,allTopIdeas,topIdeas);
                      }
                  } else {
                      Log.i("testing", "position == 1 & !listActivated");
                      Log.i("testing", "yello");
                      findAndClearTmp(tmpTopIdeas);
                      Log.i("testing", "tmpTopIdeas size after" + Integer.toString(tmpTopIdeas.size()));
                      activateGridAdapter(tmpTopIdeas);
                  }
              }
                }

                if (position == 0 && ideas != null ) {
                    allItemEnabled = true;
                    topVoteItemEnabled = false;
                    if (searchActivated) {
                        Log.i("testing", "searchActivated");
                        if (searchedAllIdeas != null && !searchedAllIdeas.isEmpty()) {
                            activateGridAdapter(searchedAllIdeas);
                        }
                    } else {
                        if (!listActivated) {
                            Log.i("testing", "position == 0 & !listActivated");
                            findAndClear(ideas);
                            activateGridAdapter(ideas);
                        } else {
                            Log.i("testing", "position == 0 & listActivated");
                            if (!tmpIdeas.isEmpty()) {
                                findAndClearTmp(tmpIdeas);
                                activateGridAdapter(tmpIdeas);
                            } else if (!ideas.isEmpty()){
                                findAndClearTmp(ideas);
                                activateGridAdapter(ideas);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void findAndClear(ArrayList<MainIdea> list) {
        Log.i("testing", "findAndClear called" );
        for (ArrayList<MainIdea> item: allList){
            if (item != list){
                if (item == ideas && !ideas.isEmpty()){
                    tmpIdeas.addAll(item);
                    listActivated = true;

                } else if (item == topIdeas && !topIdeas.isEmpty()){
                    tmpTopIdeas.addAll(item);
                    listActivated = true;
                }
                item.clear();
            }
            arrayAdapter.notifyDataSetChanged();
        }
        Log.i("testing", "topIdeas " + Integer.toString(topIdeas.size()) );
        Log.i("testing", "tmpTopIdeas " + Integer.toString(tmpTopIdeas.size()));
        Log.i("testing", "ideas " + Integer.toString(ideas.size()));
        Log.i("testing", "tmpIdeas " + Integer.toString(tmpIdeas.size()));

    }

    private void findAndClearTmp(ArrayList<MainIdea> list) {
        Log.i("testing", "findAndClearTmp called" );
        for (ArrayList<MainIdea> item: allListTmp){
            if (item != list){
                 if (item == tmpIdeas && !tmpIdeas.isEmpty()){
                    ideas.addAll(tmpIdeas);
                     listActivated = false;
                } else if (item == tmpTopIdeas && !tmpTopIdeas.isEmpty()){
                    topIdeas.addAll(tmpTopIdeas);
                     listActivated = false;
                } else if (item == topIdeas && !topIdeas.isEmpty()){
                     tmpTopIdeas.addAll(item);
                     listActivated = false;
                 }
                item.clear();
            }

            arrayAdapter.notifyDataSetChanged();
        }
        Log.i("testing", "topIdeas " + Integer.toString(topIdeas.size()) );
        Log.i("testing", "tmpTopIdeas " + Integer.toString(tmpTopIdeas.size()));
        Log.i("testing", "ideas " + Integer.toString(ideas.size()));
        Log.i("testing", "tmpIdeas " + Integer.toString(tmpIdeas.size()));
}

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void findNewIdeas() {

        if (ideas != null) {
            if (ideas.isEmpty()) {

                ArrayList<Long> creationTimes = new ArrayList<>();
                for (MainIdea idea : recentIdeas) {
                    creationTimes.add(idea.creation);
                }

                Collections.sort(creationTimes, new Comparator<Long>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public int compare(Long o1, Long o2) {
                        long no = Long.compare(o1, o2);
                        Log.i("compareNo", "no " + Long.toString(no));
                        return Long.compare(o1, o2);
                    }
                });

                Collections.sort(creationTimes, Collections.<Long>reverseOrder());

                Log.i("creationTimesOrdered", "creationTimes " + creationTimes.toString());

                for (int j = 0; j < recentIdeas.size(); j++) {
                    for (int i = 0; j < recentIdeas.size(); i++) {
                        if (recentIdeas.get(i).creation == creationTimes.get(j)) {
                            ideas.add(recentIdeas.get(i));
                            break;
                        }
                    }
                }
                listActivated = true;
                activateGridAdapter(ideas);
            }

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
                                        Intent intent = new Intent(SearchResultsActivity.this,MainActivity.class);
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

}
