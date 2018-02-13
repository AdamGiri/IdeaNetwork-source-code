package com.parse.ideanetwork;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends AppCompatActivity{

    RelativeLayout rl;
    LayoutInflater inflater;
    View contactDetails;
    View bio;
    ListView listView;
    boolean activated;
    ArrayList<String> list;

    EditText emailEdit;
    EditText skypeEdit;
    EditText teleEdit;
    ImageView picture;
    EditText editText;



    boolean listSaved;
    TextView tv;
    SkillsAdapter test;
    EditText name;
    EditText job;
    EditText age;
    EditText bioText;
    EditText facebook;
    EditText linkedIn;
    EditText email;
    EditText skype;
    EditText phone;


    TextView bioTitle;
    TextView contactTitle;
    TextView friendsTitle;
    TextView skillsTitle;
    List<String> skillsList;
    ListView friendsList;
    View friends;
    List<String> friendsArray;
    List<String> friendStatus;
    TextView picInfo;
    TextView nameSave;
    TextView ageSave;
    TextView jobSave;
    TextView emailSave;
    TextView skypeSave;
    TextView phoneSave;
    ImageView fbSave;
    ImageView linkedSave;
    boolean savesActivated = true;
    boolean contactActivated = true;
    boolean bioActivated;
    boolean skillsActivated;


    TextView bioSave;

    FriendsListAdapter friendsAdapter;
    ParseObject parseUser;
    ImageView floatingActionButton;
    public static String tealLight = "#26a69a";
    public static String activatedColor =  "#4db6ac";

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        list = new ArrayList<>();
        setTitle("Profile");
        rl = (RelativeLayout)findViewById(R.id.container);
        inflater = LayoutInflater.from(this);
        listView = (ListView) findViewById(R.id.skillsListView);
        bioTitle = (TextView)findViewById(R.id.bio);
        contactTitle = (TextView)findViewById(R.id.contactTitle);
        friendsTitle = (TextView)findViewById(R.id.friends);
        skillsTitle = (TextView)findViewById(R.id.skillsInterests);
        contactDetails = inflater.inflate(R.layout.contact_details,rl,false);

        bio = inflater.inflate(R.layout.bio,rl,false);

        friends = inflater.inflate(R.layout.friends_list,rl,false);
        FriendsRequests friendsRequests = new FriendsRequests(this);
        friendsList = (ListView)friends.findViewById(R.id.friendsList);
        bioText = (EditText)bio.findViewById(R.id.bioText);

        emailEdit = (EditText)contactDetails.findViewById(R.id.email);
        picInfo = (TextView) findViewById(R.id.textView5);
        skypeEdit = (EditText)contactDetails.findViewById(R.id.skype);
        picture = (ImageView) findViewById(R.id.profilePic);
        teleEdit = (EditText)contactDetails.findViewById(R.id.telephone);

        nameSave = (TextView) findViewById(R.id.nameSave);
        ageSave = (TextView) findViewById(R.id.ageSave);
        jobSave = (TextView) findViewById(R.id.jobSave);
        emailSave = (TextView) contactDetails.findViewById(R.id.emailSave);
        skypeSave = (TextView) contactDetails.findViewById(R.id.skypeSave);
        phoneSave = (TextView) contactDetails.findViewById(R.id.phoneSave);
        fbSave = (ImageView) contactDetails.findViewById(R.id.facebookSave);
        linkedSave = (ImageView) contactDetails.findViewById(R.id.linkedInSave);
        bioSave = (TextView)bio.findViewById(R.id.bioSave);
        name = (EditText)findViewById(R.id.name);
        job = (EditText)findViewById(R.id.job);
        age = (EditText)findViewById(R.id.age);
        facebook = (EditText)contactDetails.findViewById(R.id.facebook);
        linkedIn = (EditText)contactDetails.findViewById(R.id.linkedIn);
        listSaved = true;
        activated = true;
        rl.addView(contactDetails);
        setTextWatcher(bioText);
        setTextWatcher(name);
        setTextWatcher(job);
        setTextWatcher(age);
        setTextWatcher(facebook);
        setTextWatcher(linkedIn);
        setTextWatcher(skypeEdit);
        setTextWatcher(teleEdit);
        setTextWatcher(emailEdit);
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
                                        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
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
        query.whereEqualTo("ParseUsername", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    if (objects != null) {
                        if (!objects.isEmpty()) {
                            parseUser = objects.get(0);
                            name.setText(parseUser.getString("name"));
                            age.setText(parseUser.getString("age"));
                            job.setText(parseUser.getString("job"));
                            facebook.setText(parseUser.getString("Facebook"));
                            linkedIn.setText(parseUser.getString("LinkedIn"));
                            emailEdit.setText(parseUser.getString("email"));
                            skypeEdit.setText(parseUser.getString("skypeEdit"));
                            teleEdit.setText(parseUser.getString("telephone"));
                            setProfilePic();
                        } else {
                            parseUser = new ParseObject("PseudoUser");
                            parseUser.put("ParseUsername",ParseUser.getCurrentUser().getUsername());
                            parseUser.saveInBackground();
                        }
                    } else {
                        parseUser = new ParseObject("PseudoUser");
                        parseUser.put("ParseUsername",ParseUser.getCurrentUser().getUsername());
                        parseUser.saveInBackground();
                    }

                        }
                        savesActivated = false;
            }
        });


    }


    private void setTextWatcher(EditText et){
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!savesActivated) {
                    showSaves(true);
                }
            }
        });
    }


    private void showSaves(boolean b) {

            Log.i("showSaveOnClickEmail", "!savesActivated)");
            if (b) {
                Log.i("showSaveOnClickEmail", "b)");
                nameSave.setVisibility(View.VISIBLE);
                ageSave.setVisibility(View.VISIBLE);
                jobSave.setVisibility(View.VISIBLE);
                if (contactDetails != null) {
                    if (contactActivated) {
                        Log.i("showSaveOnClickEmail", "contactDetails != null");
                        emailSave.setVisibility(View.VISIBLE);
                        skypeSave.setVisibility(View.VISIBLE);
                        phoneSave.setVisibility(View.VISIBLE);
                        fbSave.setVisibility(View.VISIBLE);
                        linkedSave.setVisibility(View.VISIBLE);
                    }
                }


                if (bio != null){
                    if (bioActivated) {
                        Log.i("showSaveOnClickEmail", "bio != null");
                        bioSave.setVisibility(View.VISIBLE);
                    }
                }
                savesActivated = true;
            } else {
                nameSave.setVisibility(View.INVISIBLE);
                ageSave.setVisibility(View.INVISIBLE);
                jobSave.setVisibility(View.INVISIBLE);
                if (contactDetails != null) {
                    emailSave.setVisibility(View.INVISIBLE);
                    skypeSave.setVisibility(View.INVISIBLE);
                    phoneSave.setVisibility(View.INVISIBLE);
                    fbSave.setVisibility(View.INVISIBLE);
                    linkedSave.setVisibility(View.INVISIBLE);
                }
                if (bio != null){
                    bioSave.setVisibility(View.INVISIBLE);
                }
            }

    }

    private void downloadParseData() {

        skillsList = parseUser.getList("skillsList");
        if (skillsList == null) {
            Log.i("downloadParseData", "red");
            skillsList = new ArrayList<>();
            copyToPseudoUser("skillsList",null,skillsList);
        }
        list.addAll(skillsList);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void contactDetailsOnClick(View view){
        savesActivated = true;
        contactActivated = true;
        skillsActivated = false;
        bioActivated = false;
        hideSaves();
        rl.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        if (floatingActionButton!=null){
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
        resetUnderbars();
        contactTitle.setBackgroundColor(Color.parseColor(activatedColor));
        getData();
        removeViews();

        rl.addView(contactDetails);
        savesActivated = false;
    }

    private void getData() {
        String email = (String)parseUser.get("email");
        String skype =(String) parseUser.get("skypeEdit");
        String phone = (String)parseUser.get("telephone");
        if (email != null) {
            emailEdit.setText(email);
        }
        if (skype != null){
            skypeEdit.setText(skype);
        }
       if (phone!=null){
           teleEdit.setText(phone);
       }


    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void skillsInterestsOnClick(final View view){
        savesActivated = true;
        contactActivated = false;
        bioActivated = false;
        skillsActivated = true;
        hideSaves();
        listView.setVisibility(View.VISIBLE);
        rl.setVisibility(View.INVISIBLE);
        floatingActionButton = (ImageView)findViewById(R.id.addSkillButton);
        floatingActionButton.setVisibility(View.VISIBLE);
        resetUnderbars();
        skillsTitle.setBackgroundColor(Color.parseColor(activatedColor));
     removeViews();

        if (list.isEmpty()){
            downloadParseData();
        }


    test = new SkillsAdapter(this, android.R.layout.simple_list_item_1, list,false);
    listView.setAdapter(test);
    Log.i("skillsInterestsOnClick", "downloadParseData");
    test.notifyDataSetChanged();
    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Test");
    query.whereEqualTo("objectId", "MBmQgGhHBK");
    query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> objects, ParseException e) {
            activated = true;
            for (int i=0;i<test.ets.size();i++) {
                final int finalI = i;
                test.ets.get(i).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                      test.saves.get(finalI).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
    });
        savesActivated = false;
    }




    private void addToParse() {
        List<String> tmpList = new ArrayList<>();
        tmpList.addAll(list);
        tmpList.remove(0);
        Log.i("tmpList", "tmpList "+tmpList.toString());
        parseUser.put("skillsList", tmpList);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e== null){
                    Log.i("addToParse", "test");
                }
            }
        });
    }


    public void addSkillOnClick(View view){
        Log.i("getView", "magenta");
            list.add("");
            test.notifyDataSetChanged();
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Test");
            query.whereEqualTo("objectId", "MBmQgGhHBK");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    editText = test.viewHolder.et;
                    if (editText != null) {
                            Log.i("length", Integer.toString(editText.getText().toString().length()));
                            list.add(editText.getText().toString());
                            list.remove(list.indexOf(""));
                            addToParse();
                            test.notifyDataSetChanged();
                        }
                    }
            });
        }


    public void editOnClick(View view) {
            EditText e = (EditText) view.getTag();
            int ePosition = (int) e.getTag();
            String skill = "";
            skill = e.getText().toString();
            Log.i("editOnClick", Integer.toString(e.getText().toString().length()));
            list.remove(ePosition);
            if (!skill.equals("")){
            list.add(ePosition, e.getText().toString());
            parseUser.put("skillsList",list);
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        Toast.makeText(ProfileActivity.this, "Entry saved.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Save unsuccessful "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
                Toast.makeText(this, "Please add more detail.", Toast.LENGTH_SHORT).show();
            }
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

    public void nameSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("name",name.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Name saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ageSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("age",age.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Age saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fbSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("Facebook",facebook.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Facebook saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void linkedInSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("LinkedIn",linkedIn.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "LinkedIn saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void jobSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("job",job.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Job saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

  public void emailSaveOnClick(View view){
      view.setVisibility(View.INVISIBLE);
      parseUser.put("email",emailEdit.getText().toString());
      parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Email saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void skypeSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("skypeEdit",skypeEdit.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Skype saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void phoneSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        parseUser.put("telephone",teleEdit.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Telephone number saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void hideSaves(){
        ageSave.setVisibility(View.INVISIBLE);
        jobSave.setVisibility(View.INVISIBLE);
        nameSave.setVisibility(View.INVISIBLE);
        bioSave.setVisibility(View.INVISIBLE);
        emailSave.setVisibility(View.INVISIBLE);
        skypeSave.setVisibility(View.INVISIBLE);
        phoneSave.setVisibility(View.INVISIBLE);
        fbSave.setVisibility(View.INVISIBLE);
        linkedSave.setVisibility(View.INVISIBLE);
    }

    public void bioOnClick(View view){
        hideSaves();
        savesActivated = true;
        contactActivated = false;
        bioActivated = true;
        skillsActivated = false;
        rl.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        if (floatingActionButton!=null){
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
        resetUnderbars();
        bioTitle.setBackgroundColor(Color.parseColor(activatedColor));
        removeViews();
        if (parseUser.getString("bio")!=null){
            bioText.setHorizontallyScrolling(false);
            bioText.setText(parseUser.getString("bio"));

        }
        rl.addView(bio);
        savesActivated = false;
    }

    private void resetUnderbars() {
        bioTitle.setBackgroundColor(Color.parseColor(tealLight));
        contactTitle.setBackgroundColor(Color.parseColor(tealLight));
        skillsTitle.setBackgroundColor(Color.parseColor(tealLight));
        friendsTitle.setBackgroundColor(Color.parseColor(tealLight));
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

    public void bioSaveOnClick(View view){
        view.setVisibility(View.INVISIBLE);
        Log.i("bio9","clicked");
        parseUser.put("bio",bioText.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(ProfileActivity.this, "Bio saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   public void friendsOnClick(View view){
        hideSaves();
       listView.setVisibility(View.INVISIBLE);
       rl.setVisibility(View.VISIBLE);
       if (floatingActionButton!=null){
           floatingActionButton.setVisibility(View.INVISIBLE);
       }
       resetUnderbars();
       friendsTitle.setBackgroundColor(Color.parseColor(activatedColor));
        removeViews();
        rl.addView(friends);
        friendStatus = new ArrayList<>();
        getFriendStatus();
    }


    private void getFriendStatus() {
        friendsArray = parseUser.getList("friends");
        if (friendsArray != null) {
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
                            if (!objects.isEmpty()) {
                                Log.i("getFriendStatus", "d");
                                if (objects.get(0).getString("status") != null) {
                                    Log.i("getFriendStatus", "e");
                                    for (ParseObject obj : objects) {
                                        friendStatus.add(obj.getString("status"));
                                    }
                                    friendsAdapter = new FriendsListAdapter(ProfileActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            friendsArray, friendStatus);
                                    Log.i("getFriendStatus", "friends " + friendsArray.toString());
                                    Log.i("getFriendStatus", "status " + friendStatus.toString());
                                    friendsList.setAdapter(friendsAdapter);
                                    friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(ProfileActivity.this,
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

    public  void copyToPseudoUser(final String key, @Nullable final String item, @Nullable final List list){
        if (item !=null) {
            parseUser.put(key, item);
        } else {

            parseUser.put(key, list);
        }
        parseUser.saveInBackground();
    }

    public void chatOnClick(View view){
        if (floatingActionButton!=null){
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
            Intent intent = new Intent(this, ProfileChatActivity.class);
            intent.putExtra("name",ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addPicOnClick(View view){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("addPicOnClick", "red");
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);

        } else {
            Log.i("addPicOnClick", "green");
            getPhoto();

        }
    }

    public void getPhoto(){
        Log.i("addPicOnClick", "blue");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            Log.i("addPicOnClick", "orange");
        if (requestCode==1){
            Log.i("addPicOnClick", "lilac");
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i("addPicOnClick", "grey");
                getPhoto();
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("addPicOnClick", "1");
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            Log.i("addPicOnClick", "2");
            Uri selectedImage = data.getData();

            try {
                Log.i("addPicOnClick", "3");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                picture.setImageBitmap(bitmap);
                picInfo.setText("");
                if (bitmap !=null) {
                    savePicToParse(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void savePicToParse(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        ParseFile pf = new ParseFile("profilePic",image);
        parseUser.put("profilePic",pf);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Log.i("savePicToParse", "saved");
                }
            }
        });
    }

    private void setProfilePic() {
        final ParseFile profilePic = parseUser.getParseFile("profilePic");
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
                            picInfo.setText("");
                        }

                    }
                }
            });
        }else {
            picInfo.setHint("      Tap to \n add a picture");
        }
    }

    public void addSkillEntry(View view){
        Log.i("addSkillEntry", "sdasd");

        list.add(0,"");
        test.notifyDataSetChanged();
     /*   try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (test.saves!=null) {
            for (int i = 0; i < test.saves.size(); i++) {
                test.saves.get(i).setVisibility(View.INVISIBLE);
            }
        }*/
    }
}
