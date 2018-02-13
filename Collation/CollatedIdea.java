package com.parse.ideanetwork.Collation;


import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class CollatedIdea {
    public String title;
    String description;
    String author;
    public String id;
    public Integer votes;
    public TextView voteView;
    public Button upVote;
    public Button dwnVote;


    public boolean dwnActive = false;
    public boolean upActive = false;


    public boolean canUpVote = true;
    public boolean canDownVote = true;


    public int initialVote;
    public String descriptionText;

    public TextView tapForMore;
    public TextView summary;

    public CollatedIdea(String title, String description, String author){
        this.title=title;
        this.description=description;
        this.author=author;

    }

    public  CollatedIdea(){}

    public void setVotes(Integer votes){
        this.votes=votes;

    }

    public void setVoteView(){
        if (voteView!=null) {
            Log.i("voteLock","votes " + Integer.toString(votes));
            voteView.setText(Integer.toString(votes));
            canUpVote = true;
            canDownVote = true;
        } else {
            Log.i("voteLock","null");
        }
    }

    public void setId(String id){
        this.id = id;
    }

    public void setVoteView(TextView tv){
        voteView = tv;

    }

    public void setDescription(String description){
        descriptionText = description;
    }

    public void setButtons(Button upVote,Button dwnVote){
        this.upVote = upVote;
        this.dwnVote = dwnVote;
        if (this.upVote!=null){
            Log.i("setButtons","upvote not null");
        }
        if (this.dwnVote!=null){
            Log.i("setButtons","dwnvote not null");
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
