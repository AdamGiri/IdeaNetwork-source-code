package com.parse.ideanetwork.Messaging;


import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ideanetwork.R;

import java.util.ArrayList;
import java.util.List;


///maps message widgets together
public class MessageContainer {



    public String message = "";
    public String username = "";
    public SpannableStringBuilder messageBuilder;
    public SpannableString initialTag;
    public int position;
    public String messageId = "";
    public int vote;
    public TextView voteView;
    public Button upVote;
    public Button dwnVote;

    public boolean dwnActive = false;
    public boolean upActive = false;
    public int replaceIndex = -1;
    public boolean isStillReplyBaby = false;

    public boolean userUpVoted;
    public boolean userDwnVoted;

    public ArrayList<MessageContainer> replies;

    public List<String> replyIDs;

    public String identifier = "";
    public String mainId = "";

    public MessageContainer(){}

    public MessageContainer(String message, String username){
            this.message = " "+message;
            this.username = username;
            replies = new ArrayList<>();
        }

        public String getMessage() {
            return message;
        }

        public void setReplyIDs(List<String> replyIDs){
            this.replyIDs = replyIDs;
        }



        public void stringBuilder(){
        SpannableString authorTag = new SpannableString(message);
        messageBuilder = new SpannableStringBuilder();
        if (initialTag!= null){
            initialTag.setSpan(new ForegroundColorSpan(Color.parseColor("#335ce5")),0, initialTag.length(),0);
            messageBuilder.append(initialTag);
        }
        messageBuilder.append(authorTag);
    }

    public void setIdentifier(String id){
        identifier = id;
    }

    public void setReplaceIndex(int replaceIndex){
        this.replaceIndex = replaceIndex;
    }

    public void setInitialTag(SpannableString initialTag){
        this.initialTag = initialTag;
    }

    public void setId(String id){
        messageId = id;
    }

    public void setVote(int vote){
        this.vote=vote;
        Log.i("voteLock","vote "+Integer.toString(vote));
        if (voteView!=null) {
            Log.i("voteLock","vote view not null");
            voteView.setText(Integer.toString(vote));
            Log.i("voteLock","vote view get text: "+voteView.getText().toString());
        }
    }


    public void setVoteView(TextView tv){
        voteView = tv;
    }

    public void setButtons(Button upVote,Button dwnVote){
        this.upVote = upVote;
        this.dwnVote = dwnVote;
        if (upVote !=null){
            Log.i("setButtons", "upVote not null for "+messageId);
        } else {
            Log.i("setButtons", "upVote  null for "+messageId);
        }
        if (dwnVote !=null){
            Log.i("setButtons", "dwnVote not null for "+messageId);
        } else {
            Log.i("setButtons", "dwnVote  null for "+messageId);
        }
    }

    public void resetButtonColors() {
        Log.i("resetButtonColors","called");
        if (upVote != null && dwnVote != null) {
            upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol);
            dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol);
        } else {
            Log.i("resetButtonColors","null");
        }
    }

    public void disableButton(String key){
        if (key.equals("voteConUp")){
            upActive = true;
            dwnActive = false;
        } else {
            dwnActive = true;
            upActive = false;
        }
    }

    public void resetButtons(){
        upActive = false;
        dwnActive = false;
    }



    }


