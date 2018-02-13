package com.parse.ideanetwork.Collation;

import android.content.Intent;
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

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ideanetwork.CollateActivity;
import com.parse.ideanetwork.CustomTextWatcher;
import com.parse.ideanetwork.FriendsRequests;
import com.parse.ideanetwork.MainActivity;
import com.parse.ideanetwork.R;

import java.util.ArrayList;
import java.util.List;

public class CollatedListsActivity extends AppCompatActivity {

    ArrayList<SavedList> savedLists;
    GridView collatedGrid;
    CollationListAdapterMain adapter;
    Button createButton;
    AlertDialog dialog;
    SavedList editSavedList;
    EditText title;
    List<String> collatedLists;
    Gson gson;
    SavedList savedList1;
    int index;
    TextView noIdeaMsg;
    AlertDialog.Builder builder;
     CustomTextWatcher ctw;


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
                                        Intent intent = new Intent(CollatedListsActivity.this,MainActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collated_lists);
        collatedGrid = (GridView)findViewById(R.id.collatedListGrid);
        noIdeaMsg = (TextView)findViewById(R.id.noIdeasMsg);
        setTitle("Extracted top idea lists");
         new FriendsRequests(this);
        gson = new Gson();
        queryLists();
    }

    private void queryLists() {
        savedLists = new ArrayList<>();
        collatedLists = ParseUser.getCurrentUser().getList("CollatedLists");
        if (collatedLists != null) {
            if (!collatedLists.isEmpty()){
                for (String collatedList : collatedLists) {
                    SavedList savedList = gson.fromJson(collatedList, SavedList.class);
                    savedLists.add(savedList);
                }
            adapter = new CollationListAdapterMain(this, R.layout.collated_list_main, savedLists);
            collatedGrid.setAdapter(adapter);
            collatedGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("onItemClick", Integer.toString(position));
                    gotoCollateList(position);
                }
            });
            Log.i("savedLists", savedLists.get(0).savedIds.toString());
            Log.i("savedListstitle", savedLists.get(0).author);
        } else {
                noIdeaMsg.setVisibility(View.VISIBLE);
            }
    } else {
            noIdeaMsg.setVisibility(View.VISIBLE);
        }
    }

    public void onClickEdit(View view){
        editSavedList = (SavedList)view.getTag();
        activatePopUp(editSavedList);
    }

    private void activatePopUp( SavedList savedList) {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.collate_edit_popup,null);
        title = (EditText)view.findViewById(R.id.editTitle);
        TextView textCount = (TextView) view.findViewById(R.id.textCount);
        title.setText(savedList.mainTitle);
         ctw = new CustomTextWatcher(textCount,title);
        title.addTextChangedListener(ctw);
        createButton = (Button)view.findViewById(R.id.create);
        builder.setView(view);
        dialog = builder.create();
        builder.show();
    }

    public void onClickChangeTitle(View view) {
        if (ctw.charsLeft != ctw.maxChars) {
            if (!ctw.maxCharsReached) {
                String json = gson.toJson(editSavedList);
                index = collatedLists.indexOf(json);
                collatedLists.remove(json);
                savedList1 = gson.fromJson(json, SavedList.class);
                savedList1.setMainTitle(title.getText().toString());
                Log.i("savedLists", "title " + editSavedList.mainTitle);
                savedLists.add(savedLists.indexOf(editSavedList), savedList1);
                savedLists.remove(editSavedList);
                builder = null;
                adapter.notifyDataSetChanged();
                parseChange();
            } else {
                Toast.makeText(CollatedListsActivity.this, "Max chars have been reached, please shorten your summary.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CollatedListsActivity.this, "Please provide more detail to your summary.", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseChange() {
        String savedIDJSON = gson.toJson(savedList1);
        collatedLists.add(index,savedIDJSON);
        ParseUser.getCurrentUser().put("CollatedLists",collatedLists);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(CollatedListsActivity.this, "Title amended", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("parseChange", e.getMessage());
                }
            }
        });
    }

    public void gotoCollateList(int pos){
        Intent intent =  new Intent(this, CollateActivity.class);
        intent.putStringArrayListExtra("savedIds",savedLists.get(pos).savedIds);
        intent.putExtra("title",savedLists.get(pos).mainTitle);
        Log.i("gotoCollateList", savedLists.get(pos).savedIds.toString());
        startActivity(intent);
    }

    public void onClickDeleteCollated(final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View popUp = inflater.inflate(R.layout.delete_dialog_list,null);
        Button yes = (Button)popUp.findViewById(R.id.yes);
        final Button no = (Button)popUp.findViewById(R.id.no);
        builder.setView(popUp);
        final AlertDialog show = builder.show();
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                show.dismiss();
                SavedList deleteSavedList = (SavedList)view.getTag();
                Log.i("onClickDeleteCollated", deleteSavedList.mainTitle);
                savedLists.remove(deleteSavedList);
                adapter.notifyDataSetChanged();
                String jsonItem = gson.toJson(deleteSavedList);
                collatedLists.remove(jsonItem);

                ParseUser.getCurrentUser().put("CollatedLists",collatedLists);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Toast.makeText(CollatedListsActivity.this, "List deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("parseChange", e.getMessage());
                        }
                    }
                });
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

    }

}
