package com.parse.ideanetwork;


import android.content.Context;
import android.support.v7.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FriendsRequests {

    Context context;
    LayoutInflater inflater;
    List<String> friendsReqList;
    ParseObject userObj;
    FriendReqAdapter friendsReqAdapter;
     AlertDialog dialog;
    String currentUsername;


    public FriendsRequests(Context context){
        this.context =  context;
        inflater = LayoutInflater.from(context);
        currentUsername = ParseUser.getCurrentUser().getUsername();
        checkForFriendRequests();
    }

    private void checkForFriendRequests() {
        ParseQuery<ParseObject> query = new ParseQuery<>("PseudoUser");
        query.whereEqualTo("ParseUsername", currentUsername);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()) {
                        userObj = objects.get(0);
                        friendsReqList = userObj.getList("FriendRequests");
                        if (friendsReqList!=null){
                            if (!friendsReqList.isEmpty()){
                                activatePopUp(friendsReqList);
                            }
                        }
                    }
                }
            }
        });
    }

    private void activatePopUp(List<String> friendsReqList) {
        friendsReqAdapter = new FriendReqAdapter(context,R.layout.friend_req_item,R.id.checkedTextView,friendsReqList
        ,this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.friend_request_popup, null);
        ListView listView = (ListView)view.findViewById(R.id.summary);
        listView.setAdapter(friendsReqAdapter);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    public void notifyParse(final boolean isEmpty, final String name, final boolean b){
        if (userObj!=null){
            userObj.put("FriendRequests",friendsReqList);
            userObj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){


                        if (isEmpty){
                            dialog.dismiss();
                        }
                        if (b) {
                            Toast.makeText(context, "Friend successfully added.", Toast.LENGTH_SHORT).show();
                            addToFriendsList(name);
                        } else {
                            ParseQuery<ParseObject> query = new ParseQuery<>("PseudoUser");
                            query.whereEqualTo("ParseUsername", currentUsername);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e==null){
                                        if (!objects.isEmpty()) {
                                            objects.get(0).put("FriendRequests",friendsReqList);
                                            objects.get(0).saveInBackground();
                                        }
                                     }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(context, "Friend unsuccessfully added; " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addToFriendsList( String name) {
        saveObj(userObj,name);
        addToOtherFriendsList(name);
    }

    private void addToOtherFriendsList(final String name) {
        ParseQuery<ParseObject> query = new ParseQuery<>("PseudoUser");
        query.whereEqualTo("ParseUsername", name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()) {
                        Log.i("addToFriendsList","addToOtherFriendsList" + currentUsername);
                       saveObj(objects.get(0),currentUsername);
                     }
                    }
                }

        });
    }

    private void saveObj(ParseObject obj,String name){
        if (obj.getList("friends")==null){
            List<String> friends = new ArrayList<>();
            friends.add(name);
            Log.i("addToFriendsList", "adding (new) " + name );
            obj.put("friends", friends);
        } else {
            Log.i("addToFriendsList", "adding (preexisting) " + name );
            obj.add("friends", name);
        }
        obj.saveInBackground();
    }
}
