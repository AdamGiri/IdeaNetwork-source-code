package com.parse.ideanetwork;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ideanetwork.Messaging.MessageContainer;
import com.parse.ideanetwork.Messaging.MessagingAdapter;
import com.parse.ideanetwork.Messaging.PopUp;


import java.util.ArrayList;
import java.util.List;

public class ProfileChatActivity extends AppCompatActivity {

    List<String> chatIds;
    ArrayList<Boolean> safeToAdd;
    ArrayList<ChatMesssageMap> messageMaps;
    ArrayList<ChatMesssageMap> newMessageMaps;
    public ChatAdapter chatAdapter;
    ChatMesssageMap selectedConvo;
    String msg;

    PopUp popUp;
    MessagingAdapter adapter;

    Handler handler;
    Thread thread;

    boolean firstQuery = true;
    TimeSetter timeSetter;
    ArrayList<String> adapterList;

    boolean userInChat;
    ArrayList<Integer> unreadMsgs;
    ListView  listView;
    TimeSetter ts;
    TextView noMsgs;

    TextView unreadMsgTV;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_chat);
        messageMaps = new ArrayList<>();
        newMessageMaps = new ArrayList<>();
        ts = new TimeSetter();
        safeToAdd = new ArrayList<>();
        new FriendsRequests(this);
        noMsgs = (TextView)findViewById(R.id.noChatMsg);
        setTitle("Conversations");
        getParseData();

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
                                        Intent intent = new Intent(ProfileChatActivity.this,MainActivity.class);
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

    private void getParseData() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PseudoUser");
        query.whereEqualTo("name",getIntent().getStringExtra("name"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects !=null ) {
                        if (!objects.isEmpty()) {
                                chatIds = objects.get(0).getList("chatIds");
                            if (chatIds!=null) {
                                noMsgs.setVisibility(View.INVISIBLE);
                                getMessages();
                            } else {
                                noMsgs.setVisibility(View.VISIBLE);
                            }
                        }
                        }
                    }
            }
        });
    }

    private void getMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ProfileComments");
        query.whereContainedIn("chatId",chatIds);
        query.orderByDescending("update");
        if (!firstQuery){
            Log.i("firstQuery", "present "+Long.toString(timeSetter.presentTime));
            Log.i("firstQuery", "before "+Long.toString(timeSetter.beforeTime));
            query.whereLessThan("update",timeSetter.presentTime);
            query.whereGreaterThan("update",timeSetter.beforeTime);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects !=null ) {
                        if (!objects.isEmpty()) {
                            for (ParseObject obj:objects) {
                                Log.i("sizeObjects", Integer.toString(objects.size()));
                                if (!messageMaps.isEmpty()) {
                                    Log.i("green", "2");
                                    for (int i =0;i<messageMaps.size();i++) {
                                        Log.i("red", messageMaps.get(i).id);
                                         if (!obj.getString("chatId").equals(messageMaps.get(i).id)) {

                                             safeToAdd.add(true);
                                         } else {
                                             Log.i("getMessages", "4");
                                             messageMaps.get(i).addToMessages(obj.getString("message"));
                                             messageMaps.get(i).addToAuthors(obj.getString("author"));
                                             messageMaps.get(i).setProfileCommentObj(obj);
                                             if (!obj.getString("author").equals(ParseUser.getCurrentUser().getUsername()))
                                             {
                                                 messageMaps.get(i).addToCount(obj.getBoolean("unread"));
                                             }
                                             safeToAdd.add(false);
                                         }
                                         if (i==messageMaps.size()-1){
                                             Log.i("getMessages", "5");
                                             if (!safeToAdd.contains(false)) {
                                                 Log.i("getMessages", "6");
                                                 ChatMesssageMap chatMessageMap = new
                                                         ChatMesssageMap(obj.getString("chatId"),obj
                                                         .getList("users"));
                                                 chatMessageMap.addToMessages(obj.getString("message"));
                                                 chatMessageMap.addToAuthors(obj.getString("author"));

                                                 chatMessageMap.setProfileCommentObj(obj);
                                                 if (!obj.getString("author").equals(ParseUser.getCurrentUser().getUsername())) {
                                                     chatMessageMap.addToCount(obj.getBoolean("unread"));
                                                 }
                                                 messageMaps.add(chatMessageMap);
                                                 if (!firstQuery){
                                                     newMessageMaps.add(chatMessageMap);
                                                 }
                                                 break;
                                             }
                                         }
                                    }

                                } else {
                                    Log.i("green", "x");
                                    ChatMesssageMap chatMessageMap = new ChatMesssageMap(obj.
                                            getString("chatId"),obj.getList("users"));
                                    chatMessageMap.addToMessages(obj.getString("message"));
                                    chatMessageMap.addToAuthors(obj.getString("author"));
                                    chatMessageMap.setProfileCommentObj(obj);
                                    if (!obj.getString("author").equals(ParseUser.getCurrentUser().getUsername())) {
                                        chatMessageMap.addToCount(obj.getBoolean("unread"));
                                    }
                                    messageMaps.add(chatMessageMap);
                                }
                                safeToAdd.clear();
                            }
                            mContainerConversion();
                            setAdapter();
                            if (firstQuery){
                                activateRunnable();
                            }
                        }
                    }
                }
            }
        });
    }

    private void mContainerConversion() {
        for (ChatMesssageMap cmp: messageMaps){

            cmp.convertToMContainers();
        }
    }

    private void setAdapter() {


        if (firstQuery){
            adapterList = new ArrayList<>();
            unreadMsgs = new ArrayList<>();
        for (ChatMesssageMap chatMap:messageMaps){
            if (chatMap.convoName !=null) {
                adapterList.add(chatMap.convoName);
                unreadMsgs.add(chatMap.unreadMsgCount);
            }
        }
            listView = (ListView)findViewById(R.id.chatListView);
        } else {
            for (ChatMesssageMap chatMap:newMessageMaps){
                if (chatMap.convoName !=null) {
                    adapterList.add(chatMap.convoName);
                    unreadMsgs.add(chatMap.unreadMsgCount);
                }
            }
        }

        Log.i("adapterList", adapterList.toString());
        chatAdapter = new ChatAdapter(this,R.layout.tv,adapterList);
        chatAdapter.setUnreadMsgCount(unreadMsgs);
        chatAdapter.setChatMessageMaps(messageMaps);
        listView.setAdapter(chatAdapter);
        if (firstQuery) {
            chatAdapter.notifyDataSetChanged();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedConvo = messageMaps.get(position);
                unreadMsgTV = (TextView) view.findViewById(R.id.textView4);
                Log.i("unreadMsgCount",Integer.toString(selectedConvo.unreadMsgCount));
                setMsgsToRead();
                activateChatBox(position);
            }
        });
        if (!newMessageMaps.isEmpty()){
            newMessageMaps.clear();
        }

    }

    private void setMsgsToRead() {
        for (ParseObject obj:selectedConvo.profileCommentObj){
            obj.put("unread",false);
            obj.saveInBackground();
        }
    }

    private void activateChatBox(int position) {
        popUp = new PopUp(this);
        userInChat = true;
        setMessagingAdapter(position);
    }

    private void setMessagingAdapter(int position){
        Log.i("zxc",Integer.toString(selectedConvo.msgContainers.size()));
        adapter = new MessagingAdapter(this
                ,selectedConvo.msgContainers);
        popUp.listView.setAdapter(adapter);
        adapter.popUp = popUp;
        adapter.setInvis  = true;
        adapter.notifyDataSetChanged();
        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Test");
        q.whereEqualTo("objectId","MBmQgGhHBK");
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                popUp.activatePopUp();
                setDismissListener();
            }
        });
    }

    public void onClickSend(View view){
        msg = adapter.sendMsg.getText().toString();
        adapter.sendMsg.getText().clear();
        popUp.initialMsg = "";
        addMessageToParse(msg);
    }

    private void addMessageToParse(String msg) {
       MessageContainer mContainer = new MessageContainer(msg, ParseUser.getCurrentUser()
                .getUsername());
        selectedConvo.msgContainers.add(1,mContainer);
        adapter.notifyDataSetChanged();
        addToParse();
    }

    private void addToParse() {
        ParseObject parseMessage = new ParseObject("ProfileComments");
        parseMessage.put("message", msg);
        parseMessage.put("chatId", selectedConvo.id);
        parseMessage.put("users", selectedConvo.recipient);
        parseMessage.put("author", ParseUser.getCurrentUser().getUsername());
        parseMessage.put("unread", true);
        parseMessage.put("creation", ts.presentTime);
        parseMessage.put("update", ts.presentTime);
        parseMessage.saveInBackground();
    }

    private void activateRunnable() {
        firstQuery = false;
        handler = new Handler();
        thread = new Thread(){
            @Override
            public void run() {
                timeSetter = new TimeSetter();
                getMessages();
                handler.postDelayed(this,IdeaTreeActivity.intervalQuery);
                Log.i("activateRunnable", "running");
            }
        };
        thread.start();
    }

    private void setDismissListener() {
        popUp.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialog != null){
                    userInChat = false;
                    firstQuery = false;
                    unreadMsgTV.setText("0 new message(s)");
                }
            }
        });
    }


}
