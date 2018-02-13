package com.parse.ideanetwork.Collation;

import com.parse.ParseObject;

public class SimilarityMap {
    public float similarity;
    public ParseObject obj;

    public SimilarityMap(float similarity, ParseObject obj){
        this.similarity = similarity;
        this.obj = obj;
    }

    public ParseObject getObj(){
            return obj;
    }
}
