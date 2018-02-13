package com.parse.ideanetwork;

import java.util.ArrayList;

public class CollisionMap {

    IdeaVector currentPoint;
    ArrayList<Boolean> collisionList;

    public CollisionMap(IdeaVector currentPoint, Boolean collectCollision){
        this.currentPoint = currentPoint;
        if (collectCollision) {
            collisionList = new ArrayList<>();
        }
    }

    public void addCollision(Boolean b){
        collisionList.add(b);
    }

    public Boolean getCollision(){
        if (collisionList.contains(true)){
            return true;
        } else {
            return false;
        }
    }
}
