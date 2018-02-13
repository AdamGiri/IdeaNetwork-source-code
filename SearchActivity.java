package com.parse.ideanetwork;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchOptionsAdapter arrayAdapter;
    ArrayList<SearchOption> options;
    GridView gridLayout;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");
        new DisplayAd(this);
        options = new ArrayList<>();
        options.add(new SearchOption("Politics"));
        options.add(new SearchOption("Software"));
        options.add(new SearchOption("Literature"));
        options.add(new SearchOption("Science & Tech"));
        options.add(new SearchOption("Social"));
        options.add(new SearchOption("Film/TV"));
        options.add(new SearchOption("Engineering"));
        options.add(new SearchOption("Lifestyle"));
        options.add(new SearchOption("Fiction"));
        options.add(new SearchOption("Art"));
        gridLayout = (GridView) findViewById(R.id.searchGridView);
        new FriendsRequests(this);
        activateAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_action, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PseudoUser");
        query.whereEqualTo("ParseUsername", ParseUser.getCurrentUser().getUsername());
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
                                        Intent intent = new Intent(SearchActivity.this,MainActivity.class);
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


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void activateAdapter() {
        arrayAdapter = new SearchOptionsAdapter(SearchActivity.
                this,R.layout.search_option,options);
        gridLayout.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    public void searchOptionOnClick(View view){
        SearchOption so = (SearchOption) view.getTag();
        Intent intent = new Intent(SearchActivity.this,SearchResultsActivity.class);
        intent.putExtra("category",so.label);
        startActivity(intent);
    }
}
