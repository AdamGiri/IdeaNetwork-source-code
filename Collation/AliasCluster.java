package com.parse.ideanetwork.Collation;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseObject;
import com.parse.ideanetwork.DistanceMap;
import com.parse.ideanetwork.IdeaVector;
import com.parse.ideanetwork.LineData;

import java.util.ArrayList;

public class AliasCluster {
    public static final int objectLimit = 2;
    public static final int aliasDistance = 10;
    ArrayList<ParseObject> objects;
    ParseObject originObject;

    String lineageId;
    String randomId;
    String ideaTreeId;

    IdeaVector aliasPos;
    IdeaVector originPos;

    Context context;

    public AliasCluster(ParseObject originObject, Context context) {
        this.context = context;
        lineageId = originObject.getString("lineageId");
        this.originObject = originObject;
        objects = new ArrayList<>();
        randomId = originObject.getString("randomId");
        ideaTreeId = originObject.getString("IdeaTree");
    }

    public boolean isFull() {
        return objects.size() == objectLimit;
    }

    public void generateNextLevel() {
        DistanceMap distanceMap = new DistanceMap();
        originPos = new IdeaVector(originObject.getNumber("x").doubleValue(),
                originObject.getNumber("y").doubleValue());
        aliasPos = distanceMap.aliasPoint(originObject.getNumber("rise").doubleValue(),
                originObject.getNumber("run").doubleValue(), originPos);
        Alias alias = new Alias(aliasPos.x, aliasPos.y, randomId, ideaTreeId, context);
        Log.i("aliasCoords", "runvalue" + Double.toString(originObject.getNumber("run").doubleValue()));
        Log.i("aliasCoords", "x " + Double.toString(aliasPos.x) + " y " +
                Double.toString(aliasPos.y));
        alias.setParentIdea(originObject);
        Log.i("parentPos", "x " + Double.toString(alias.parentVector.x) + " y " +
                Double.toString(alias.parentVector.y));
        LineData lineData = new LineData(alias.parentVector.x, alias.parentVector.y, aliasPos.x, aliasPos.y);
        alias.setLineData(lineData);
        uploadToParse(alias);

    }

    private void uploadToParse(Alias alias) {
        ParseObject aliasObj = new ParseObject("Alias");
        Gson gson = new Gson();
        String lineMapJSON = gson.toJson(alias.lineData);
        aliasObj.put("LineMap", lineMapJSON);
        aliasObj.put("x", alias.x);
        aliasObj.put("y", alias.y);
        Log.i("aliasPos", "randomid " + alias.randomId + "ideatreeid " + alias.ideaTreeId);
        aliasObj.put("randomId", alias.randomId);
        aliasObj.put("IdeaTreeId", alias.ideaTreeId);
        aliasObj.saveInBackground();
    }

}
