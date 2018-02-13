package com.parse.ideanetwork;

import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ideanetwork.Messaging.MessageContainer;

import java.util.ArrayList;
import java.util.List;

public class ChatMesssageMap {

    String id;
    List<String> messages;
    List<String>  recipient;
    String convoName;
    List<String> authors;
    ArrayList<MessageContainer> msgContainers;
    List<ParseObject> profileCommentObj;

    public int unreadMsgCount;


    public ChatMesssageMap(String id,List<Object>  users){
        this.id = id;
        messages = new ArrayList<>();
        authors = new ArrayList<>();
        recipient = new ArrayList<>();
        profileCommentObj = new ArrayList<>();
        msgContainers = new ArrayList<>();
        msgContainers.add(new MessageContainer());
        convertToString(users);

    }

    public void setProfileCommentObj(ParseObject obj){
        profileCommentObj.add(obj);
    }

    public void addToMessages(String msg){
        messages.add(msg);
    }

    public void addToAuthors(String author){
        authors.add(author);
    }

    private void convertToString(List<Object> list){

        for (Object s:list){
            String t = (String) s;

            recipient.add(t);
            if (!t.equals(ParseUser.getCurrentUser().getUsername())){

                convoName = t;
              Log.i("recipient", convoName);
            }
        }
    }

    public void convertToMContainers(){
        Log.i("messageContainer", "messages: "+Integer.toString(messages.size()) + " " +
                "authors: "+Integer.toString(authors.size()));
        for (int i = 0;i<messages.size();i++){
            MessageContainer messageContainer = new MessageContainer(messages.get(i),authors.get(i));

            msgContainers.add(messageContainer);
        }

    }

    public void addToCount(boolean unread){
        if (unread) {
            unreadMsgCount++;
        }
    }

}
