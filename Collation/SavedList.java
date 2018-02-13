package com.parse.ideanetwork.Collation;


import java.util.ArrayList;

public class SavedList {
    ArrayList<String> savedIds;
    public String mainId;
    public String mainTitle;
    public String author;

    public SavedList(ArrayList<String> savedIds, String mainId){
        this.savedIds=savedIds;
        this.mainId=mainId;
    }

    public void setMainTitle(String mainTitle){
        this.mainTitle = mainTitle;
    }

    public void setAuthor(String author){
        this.author = author;
    }
}
