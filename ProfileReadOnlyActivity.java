package com.parse.ideanetwork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import android.support.annotation.RequiresApi;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ideanetwork.Messaging.MessageContainer;
import com.parse.ideanetwork.Messaging.MessagingAdapter;
import com.parse.ideanetwork.Messaging.PopUp;


import java.util.ArrayList;
import java.util.List;

public class ProfileReadOnlyActivity extends AppCompatActivity {

    RelativeLayout rl;
    LayoutInflater inflater;
    View contactDetails;
    View bio;
    ListView listView;
    boolean activated;
    ArrayList<String> list;

    TextView emailEdit;
    TextView skypeEdit;
    TextView teleEdit;
    TextView textView;
    TextView facebookEdit;
    TextView linkedInEdit;


    boolean contactBool;
    boolean skillBool;
    boolean listSaved;
    TextView tv;
    SkillsAdapter test;
    TextView name;
    TextView job;
    TextView age;
    TextView bioText;
    ImageView picture;

    List<String> skillsList;
    ListView friendsList;
    View friends;
    List<String> friendsArray;
    List<String> friendStatus;
    String userName;
    ParseObject userObj;

    FriendsListAdapter friendsAdapter;
    PopUp popUp;

    MessagingAdapter adapter;
    ArrayList<MessageContainer> msgContainers;
    ArrayList<String> users;

    String msg;
    String chatId;

    TextView bioTitle;
    TextView contactTitle;
    TextView friendsTitle;
    TextView skillsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_read_only);
        new DisplayAd(this);
        list = new ArrayList<>();
        setTitle("Profile");
        rl = (RelativeLayout)findViewById(R.id.container);
        inflater = LayoutInflater.from(this);
        listView = new ListView(this);

        contactDetails = inflater.inflate(R.layout.contact_details_ro,rl,false);
        bio = inflater.inflate(R.layout.bio_ro,rl,false);
        friends = inflater.inflate(R.layout.friends_list,rl,false);
        friendsList = (ListView)friends.findViewById(R.id.friendsList);
        bioText = (TextView)bio.findViewById(R.id.bioTextRO);
        bioText.setMovementMethod(new ScrollingMovementMethod());
        picture = (ImageView) findViewById(R.id.profilePicRO);
        emailEdit = (TextView)contactDetails.findViewById(R.id.emailRO);
        facebookEdit = (TextView)contactDetails.findViewById(R.id.facebookRO);
        linkedInEdit = (TextView)contactDetails.findViewById(R.id.linkedInRO);
        bioTitle = (TextView)findViewById(R.id.bio);
        contactTitle = (TextView)findViewById(R.id.contactTitle);
        friendsTitle = (TextView)findViewById(R.id.friends);
        skillsTitle = (TextView)findViewById(R.id.skillsInterests);
        skypeEdit = (TextView)contactDetails.findViewById(R.id.skypeRO);

        teleEdit = (TextView)contactDetails.findViewById(R.id.telephoneRO);

        name = (TextView)findViewById(R.id.nameRO);
        job = (TextView)findViewById(R.id.jobRO);
        age = (TextView)findViewById(R.id.ageRO);
        listSaved = true;
        Intent intent = getIntent();
        userName = intent.getStringExtra("name");
        rl.addView(contactDetails);
        activated=true;
        obtainProfileInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PseudoUser");
        query.whereEqualTo("ParseUsername",userName);
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
                                        Intent intent = new Intent(ProfileReadOnlyActivity.this,MainActivity.class);
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

    private void obtainProfileInfo() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PseudoUser");
        query.whereEqualTo("ParseUsername",userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects != null){
                        if (!objects.isEmpty()){
                            userObj = objects.get(0);
                            name.setText(userObj.getString("name"));
                            age.setText(userObj.getString("age"));
                            job.setText(userObj.getString("job"));
                            setProfilePic();
                            getData();
                        }
                    }
                }
            }
        });

    }

    private void resetUnderbars() {
        bioTitle.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
        contactTitle.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
        skillsTitle.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
        friendsTitle.setBackgroundColor(Color.parseColor(ProfileActivity.tealLight));
    }

    private void downloadParseData() {

        skillsList = userObj.getList("skillsList");
        Log.i("skillsList", skillsList.toString());
        if (skillsList == null) {
            if (!skillsList.isEmpty()) {
                Log.i("skillsList", "isEmpty");
                skillsList = new ArrayList<>();
                userObj.put("skillsList", skillsList);
                userObj.saveInBackground();
            }

        }
        list.addAll(skillsList);
    }

    public void addFriend(View view) {
        if (userObj != null) {
            if (userObj.getString("ParseUsername") != null) {
                if (!isAlreadyFriend()) {
                    if (userObj.getList("FriendRequests") == null) {
                        List<String> friendsList = new ArrayList<>();
                        friendsList.add(ParseUser.getCurrentUser().getUsername());
                        userObj.put("FriendRequests", friendsList);
                    } else {
                        userObj.add("FriendRequests", ParseUser.getCurrentUser().getUsername());
                    }
                    userObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(ProfileReadOnlyActivity.this, "Friend request has been sent successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileReadOnlyActivity.this, "Friend request has been sent unsuccessfully " +
                                        e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "You are already friends with this person.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Sorry, friend request was unsuccessful.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAlreadyFriend() {
        List<String> friends = userObj.getList("friends");
       if (friends!=null) {
           if (!friends.isEmpty()) {
               if (friends.contains(ParseUser.getCurrentUser().getUsername())) {
                   return true;
               }
               if (userObj.getString("ParseUsername").equals(ParseUser.getCurrentUser().getUsername())) {
                   Toast.makeText(this, "You can't add yourself.", Toast.LENGTH_SHORT).show();
                   return true;
               }
           }
       }
       return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void contactDetailsOnClick(View view){
        resetUnderbars();
        contactTitle.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
        getData();
        removeViews();
        contactBool = true;
        skillBool = false;
        rl.addView(contactDetails);
    }

    private void getData() {
        if (userObj!=null) {
            String email = (String) userObj.get("email");
            String skype = (String) userObj.get("skypeEdit");
            String phone = (String) userObj.get("telephone");
            String facebook = (String) userObj.get("Facebook");
            String linkedIn = (String) userObj.get("LinkedIn");

            if (email != null) {
                emailEdit.setText(email);
            }
            if (skype != null) {
                skypeEdit.setText(skype);
            }
            if (phone != null) {
                teleEdit.setText(phone);
            }
            if (facebook !=null){
                facebookEdit.setText(facebook);
            }
            if (linkedIn!=null){
                linkedInEdit.setText(linkedIn);
            }
        }
    }

    private void setProfilePic() {
        final ParseFile profilePic = userObj.getParseFile("profilePic");
        Log.i("setProfilePic", "0");
        if (profilePic != null) {
            Log.i("setProfilePic", "1");
            profilePic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Log.i("setProfilePic", "2");
                        if (data != null) {
                            Log.i("setProfilePic", "3");
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            picture.setImageBitmap(bmp);

                        }

                    }
                }
            });
        }
    }

    public void chatOnClick(View view){

        Intent intent = new Intent(this, ProfileChatActivity.class);
        intent.putExtra("name",ParseUser.getCurrentUser().getUsername());

        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void skillsInterestsOnClick(View view){
        resetUnderbars();
        skillsTitle.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
        removeViews();
        rl.addView(listView);
        if (list.isEmpty()){
            downloadParseData();
        }

        Log.i("skillsList", list.toString());
        test = new SkillsAdapter(this, android.R.layout.simple_list_item_1, list,true);
        test.readOnly = true;
        listView.setAdapter(test);

        Log.i("skillsInterestsOnClick", "downloadParseData");
        test.notifyDataSetChanged();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Test");
        query.whereEqualTo("objectId", "MBmQgGhHBK");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                activated = true;
            }
        });

    }








    private void activatePopUp(TextView view) {
        int pos = (int) view.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View layout =  inflater.inflate(R.layout.skills_popup,null);
        TextView et = (TextView)layout.findViewById(R.id.skillsTextView);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        et.setText(list.get(pos));
        builder.show();
    }


    public void tapOnClick(View view){
        TextView e = (TextView) view;
        activatePopUp(e);
    }



    public void bioOnClick(View view){
        resetUnderbars();
        bioTitle.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
        removeViews();
        bioText.setHorizontallyScrolling(false);
        if (userObj!=null) {
            bioText.setText(userObj.getString("bio"));
        }
        rl.addView(bio);
    }

    private void removeViews(){
        if (activated) {
            if (contactDetails != null) {
                if (contactDetails.getParent() != null) {
                    Log.i("skillsInterestsOnClick", "contactDetails.getParent() != null");
                    rl.removeView(contactDetails);
                }

            }
            if (listView != null && listView.getParent() != null) {
                Log.i("skillsInterestsOnClick", "listView != null && listView.getParent() != null");
                rl.removeView(listView);
            }

            if (bio != null && bio.getParent() != null){
                rl.removeView(bio);
            }

            if (friends != null && friends.getParent() != null){
                rl.removeView(friends);
            }

        } else {
            activated = true;
        }
    }


    public void friendsOnClick(View view){
        resetUnderbars();
        friendsTitle.setBackgroundColor(Color.parseColor(ProfileActivity.activatedColor));
        removeViews();
        rl.addView(friends);
        friendStatus = new ArrayList<>();
        getFriendStatus();
    }


    private void getFriendStatus() {
        if (userObj!=null){
            friendsArray = userObj.getList("friends");
        } else {
            Toast.makeText(this, "Unable to successfully retrieve friends list, please try again.", Toast.LENGTH_SHORT).show();
        }

        if (friendsArray!=null) {
            ParseQuery<ParseObject> query = new ParseQuery<>("PseudoUser");
            query.whereContainedIn("ParseUsername", friendsArray);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    Log.i("getFriendStatus", "a");
                    if (e == null) {
                        Log.i("getFriendStatus", "b");
                        if (objects != null) {
                            Log.i("getFriendStatus", "c");
                            Log.i("getFriendStatus", "array: " + friendsArray.toString());
                            if (!objects.isEmpty()) {
                                Log.i("getFriendStatus", "d");
                                if (objects.get(0).getString("status") != null) {
                                    Log.i("getFriendStatus", "e");
                                    for (ParseObject obj : objects) {
                                        friendStatus.add(obj.getString("status"));
                                    }
                                    friendsAdapter = new FriendsListAdapter(ProfileReadOnlyActivity.this, android.R.layout.simple_list_item_1,
                                            friendsArray, friendStatus);
                                    Log.i("getFriendStatus", "friends " + friendsArray.toString());
                                    Log.i("getFriendStatus", "status " + friendStatus.toString());
                                    friendsList.setAdapter(friendsAdapter);
                                    friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(ProfileReadOnlyActivity.this,
                                                    ProfileReadOnlyActivity.class);
                                            intent.putExtra("name", friendsArray.get(position));
                                            startActivity(intent);
                                        }
                                    });
                                    friendsAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void onClickChatRO(View view){
          activateChatBox();
    }

    private void activateChatBox() {
        msgContainers = new ArrayList<>();
        msgContainers.add(new MessageContainer());
        popUp = new PopUp(this);
        setMessagingAdapter();
    }

    private void setMessagingAdapter(){
        adapter = new MessagingAdapter(this
                ,msgContainers);
        adapter.popUp = popUp;
        popUp.listView.setAdapter(adapter);
        adapter.setInvis  = true;
        adapter.notifyDataSetChanged();
        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Test");
        q.whereEqualTo("objectId","MBmQgGhHBK");
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                popUp.activatePopUp();
            }
        });
    }

    public void onClickSend(View view){
        EditText et = (EditText)view.getTag();
        msg = et.getText().toString();
        addMessageToParse(msg);
    }

    private void addMessageToParse(String msg) {
        MessageContainer mContainer = new MessageContainer(msg, ParseUser.getCurrentUser()
                .getUsername());
        msgContainers.add(mContainer);
        adapter.notifyDataSetChanged();
        addToParse();
    }

    private void addToParse() {
        users = new ArrayList<>();
        users.add(ParseUser.getCurrentUser().getUsername());
        users.add(userName);
        chatId = new IDGenerator().generateID();
        ParseObject parseMessage = new ParseObject("ProfileComments");
        parseMessage.put("message", msg);
        parseMessage.put("chatId", chatId);
        parseMessage.put("users", users);
        parseMessage.put("author", ParseUser.getCurrentUser().getUsername());
        parseMessage.put("unread",true);
        parseMessage.put("creation",new TimeSetter().presentTime);
        parseMessage.put("update",new TimeSetter().presentTime);
        parseMessage.saveInBackground();
        addToChatIDs();
    }

    private void addToChatIDs() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PseudoUser");
        query.whereContainedIn("name",users);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects !=null ) {
                        if (!objects.isEmpty()) {
                            for (ParseObject obj:objects){
                                if (obj.getList("chatIds") != null){
                                    ArrayList<String> chatIds = new ArrayList<String>();
                                    chatIds.add(chatId);
                                    obj.put("chatIds", chatIds);
                                } else {
                                    obj.add("chatIds", chatId);
                                }
                                obj.saveInBackground();
                            }
                        }}}

            }
        });
    }
}
