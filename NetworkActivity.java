package com.parse.ideanetwork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class NetworkActivity extends AppCompatActivity {

//TODO delete this as in IdeaTreeActivity, this was just a test for adding lines
    RelativeLayout rl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        Intent intent = getIntent();
        Button button = (Button)findViewById(R.id.button);
        rl = (RelativeLayout)findViewById(R.id.rl);

        ParseQuery<ParseObject> query = new ParseQuery<>("Idea");
        TimeSetter timeSetter = new TimeSetter();
        Log.i("beforetime",Long.toString(timeSetter.beforeTime));
        query.whereEqualTo("IdeaTree","00a6842420");
        //query.whereLessThan("update",timeSetter.presentTime);
        Log.i("dateTestafter","hello");
                //query.whereGreaterThan("update",timeSetter.beforeTime);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (!objects.isEmpty()){
                    for (ParseObject object : objects) {
                        Log.i("dateTestafter", object.getNumber("creation").toString());
                    }
                } else {
                    Log.i("dateTestafter","objects is empty");
                }
            }
        });
    }


}
