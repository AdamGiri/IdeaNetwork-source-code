package com.parse.ideanetwork;


import com.parse.ParseObject;

public class TrendingMap {

    public int voteCount;
    public ParseObject obj;
    public boolean alreadyUsed;

    public TrendingMap(int voteCount, ParseObject obj){
        this.voteCount = voteCount;
        this.obj = obj;
    }

}
