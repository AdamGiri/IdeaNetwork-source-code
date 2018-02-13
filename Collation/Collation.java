package com.parse.ideanetwork.Collation;


import android.content.Context;
import android.util.Log;

import com.parse.ParseObject;

import java.util.ArrayList;

public class Collation{
    public static final float collateLimit = 0.2f;
    public int maxVote;
    public float minVote;
    public ArrayList<ParseObject> topIdeaObjects;
    public ArrayList<CollatedIdea> topIdeas;
    public ArrayList<String> topIdeasIds;
    /*ArrayList<Integer> levels;
    Map<IdeaVector,Boolean> positionMap;
    //Map<InitialPosition,IdeaVector> initialPositionMap;
    Map<String,AliasCluster> clusterMap;
    List<String> inheritedChildIds;


    public ArrayList<IdeaVector> initialPositions;
    ArrayList<ObjectLevelMap> orderedMaps;
    ArrayList<ObjectLevelMap> objectLevelMaps;
    ArrayList<AliasCluster> aliasClusters;
    ArrayList<ParseObject> lineageObjects;
    ArrayList<ParseObject> trashObjects;*/

    public String ideaTreeId;

    Context context;

    public Collation(int maxVote, String ideaTreeId, Context context){
        this.maxVote = maxVote;
        this.context = context;
        topIdeaObjects = new ArrayList<>();
       /* positionMap = new HashMap<>();
        clusterMap = new HashMap<>();
        initialPositions = new ArrayList<>();
        aliasClusters = new ArrayList<>();
        trashObjects = new ArrayList<>();*/
        this.ideaTreeId = ideaTreeId;
        topIdeas = new ArrayList<>();
    }

    public Collation(){
        topIdeaObjects = new ArrayList<>();
    }

    public float calculateLimit(){
        minVote = maxVote*(1-collateLimit);
        return  minVote;
    }

    public void parseToIdeas(){

        if (topIdeas==null){
            topIdeas = new ArrayList<>();
        }
        for (ParseObject topIdeaObj:topIdeaObjects){
            CollatedIdea collatedIdea = new CollatedIdea(topIdeaObj.getString("Title"),
                    topIdeaObj.getString("Description"),topIdeaObj.getString("Author"));
            collatedIdea.setVotes(topIdeaObj.getNumber("voteTotal").intValue());
            collatedIdea.setId(topIdeaObj.getString("randomId"));
            topIdeas.add(collatedIdea);
           Log.i("parseToIdeas","titiel: " + collatedIdea.title);
            Log.i("parseToIdeas","desc: " + collatedIdea.description);
            Log.i("parseToIdeas","auth: " + collatedIdea.author);
            Log.i("parseToIdeas","v: " + Integer.toString(collatedIdea.votes));
        }
    }

    public void extractIds(){
        topIdeasIds = new ArrayList<>();
        for (CollatedIdea collatedIdea:topIdeas){
            topIdeasIds.add(collatedIdea.id);
        }
    }

    /*
       public enum InitialPosition{
           TOPLEFT,
           TOPMID,
           TOPRIGHT,
           MIDLEFT,
           MIDRIGHT,
           BOTLEFT,
           BOTMID,
           BOTRIGHT,
       }

       public void initiate(){
           removeLowIdeas();
           Log.i("trashTest","afterRemove" +Integer.toString(trashObjects.size()));

           Log.i("trashTest","afterOrder" +Integer.toString(trashObjects.size()));

       }

     private void removeLowIdeas() {
           Log.i("topIdeaObjects",Integer.toString(topIdeaObjects.size()));
           ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
           query.whereEqualTo("IdeaTree",ideaTreeId);
           query.whereLessThan("voteTotal", minVote);
           query.whereNotEqualTo("secondaryIdea",true);
           query.whereNotEqualTo("Identifier","Main");
           query.findInBackground(new FindCallback<ParseObject>() {
               @Override
               public void done(List<ParseObject> objects, ParseException e) {
                   if (e==null){
                       if(!objects.isEmpty()){
                           Log.i("removeLowIdeas",Integer.toString(objects.size()));
                           for (ParseObject obj:objects){
                               trashObjects.add(obj);
                           }
                           orderLevels();
                           assignPosition();
                           for (ParseObject obj:objects){
                               obj.deleteInBackground();
                           }
                       }
                   }
               }
           });
       }


       private void orderLevels() {
           levels = new ArrayList<>();
           objectLevelMaps = new ArrayList<>();
           orderedMaps = new ArrayList<>();
           Log.i("topIdeaObjects",Integer.toString(topIdeaObjects.size()));
           for (int i=0;i<topIdeaObjects.size();i++) {
               ObjectLevelMap objectLevelMap = new ObjectLevelMap((int)topIdeaObjects.get(i).getNumber("level")
                       ,topIdeaObjects.get(i));
               objectLevelMaps.add(objectLevelMap);
               levels.add((int)topIdeaObjects.get(i).getNumber("level"));
           }
           Collections.sort(levels, new Comparator<Integer>() {
               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
               @Override
               public int compare(Integer o1, Integer o2) {
                   return Integer.compare(o1, o2);
               }
           });
           Log.i("orderLevelsbefore",Integer.toString(objectLevelMaps.size()));
           for (int i=0;i<levels.size();i++) {
               for (int j=0;j<levels.size();j++){
                   if (levels.get(i)==objectLevelMaps.get(j).level){
                       if (!orderedMaps.contains(objectLevelMaps.get(j))) {
                           orderedMaps.add(objectLevelMaps.get(j));
                       }
                   }
               }
           }
           Log.i("orderLevelsoutside",Integer.toString(orderedMaps.size()));
       }

       private void assignPosition() {
           Log.i("trashTest","beginnignAssignPos" +Integer.toString(trashObjects.size()));
           for (int i = 0; i < orderedMaps.size(); i++) {
               ParseObject object = orderedMaps.get(i).object;
               if (!object.getBoolean("secondaryIdea")) {
                   Log.i("trashTest","assignPosFirstIf" +Integer.toString(trashObjects.size()));
                   Log.i("assignPosition", "object is not secondary but has a secondary lineage present, forming" +
                           "alias cluster");
                   if (clusterMap.get(object.getString("lineageId")) == null) {
                       Log.i("trashTest","assignPosSecondIf" +Integer.toString(trashObjects.size()));
                       ParseObject lineageObject = getLineage(object.getString("lineageId"), i);
                       removeInheritedIds(object);
                       object.put("parentId",lineageObject.getString("randomId"));
                       object.saveInBackground();
                       Log.i("lineageID","x" + lineageObject.getString("x") + "y" +
                               object.getString("y"));
                      AliasCluster newCluster = new AliasCluster(lineageObject,context);
                      newCluster.generateNextLevel();
                      //clusterMap.put(newCluster.lineageId,newCluster);
                      // Log.i("assignPosition", "new cluster formed: " + newCluster.lineageId);
                   } else {
                       Log.i("assignPosition", "A cluster is already formed for this lineage, adding to " +
                               "existing cluster ");
                   }
                   }
           }
       }

       private void removeInheritedIds(ParseObject object) {
           List<String> inheritedIds = object.getList("inheritedIds");
           List<String> removableIds = new ArrayList<>();
           Log.i("removeInheritedIds","pink");
           Log.i("removeInheritedIds", "trash size " +Integer.toString(trashObjects.size()));
           for (ParseObject trash:trashObjects){
               Log.i("removeInheritedIds","orange");
               if (object.getString("lineageId").equals(trash.getString("lineageId"))){
                   Log.i("removeInheritedIds","green");
                   for (String id:inheritedIds){
                       if (id.equals(trash.getString("randomId"))){
                           Log.i("removeInheritedIds","blue");
                           removableIds.add(id);
                       }
                   }
               }
           }
           inheritedIds.removeAll(removableIds);
           object.put("inheritedIds",inheritedIds);
       }

       private ParseObject getLineage(String lineageId,int j) {
           lineageObjects = new ArrayList<>();
           for (int i=0;i<j;i++){
               ParseObject object = orderedMaps.get(i).object;
               Log.i("getLineage",orderedMaps.get(i).object.getString("lineageId"));
               if (lineageId.equals(object.getString("lineageId"))){
                   lineageObjects.add(object);
               }
           }
           Log.i("getLineage", lineageObjects.get(lineageObjects.size()-1).getString("randomId"));
          return inheritanceCalc(orderedMaps.get(j));
       }

       private ParseObject inheritanceCalc(ObjectLevelMap selectedObj) {
           List<String> inheritedChildIds = selectedObj.object.getList("inheritedIds");
           ArrayList<SimilarityMap> similarityMaps = new ArrayList<>();
           ArrayList<SimilarityMap> orderedSimilarityMaps = new ArrayList<>();
           ArrayList<Float> similarities = new ArrayList<>();

           for (ParseObject obj:lineageObjects){
              List<String> inheritedParentIds = obj.getList("inheritedIds");
              int similarity = 0;
              for (int i=0;i<inheritedParentIds.size();i++){
                  if (inheritedParentIds.get(i).equals(inheritedChildIds.get(i))){
                     similarity++;
                  }
              }
               SimilarityMap similarityMap = new SimilarityMap((float)similarity/inheritedChildIds.size() * 100,
                       obj);
               similarityMaps.add(similarityMap);
               similarities.add(similarityMap.similarity);
           }
           Log.i("inheritanceCalcSize","linobj size " + Integer.toString(similarityMaps.size()));

               Collections.sort(similarities, new Comparator<Float>() {
                   @Override
                   public int compare(Float o1, Float o2) {
                       return Float.compare(o1, o2);
                   }
               });

           for (int i=0;i<similarities.size();i++) {
               for (int j=0;j<similarities.size();j++){
                   if (similarities.get(i)==similarityMaps.get(j).similarity){
                       if (!orderedSimilarityMaps.contains(similarityMaps.get(j))) {
                           orderedSimilarityMaps.add(similarityMaps.get(j));
                       }
                   }
               }
           }

          int i=similarities.size()-1;
          while (i >= 0){
              Log.i("inheritanceCalcSize","pink");
              Log.i("inheritanceCalcSize",Integer.toString(similarities.size()));
              if (orderedSimilarityMaps.get(i).getObj().getNumber("creation").longValue()<
                      selectedObj.object.getNumber("creation").longValue()){
                  Log.i("inheritanceCalc",orderedSimilarityMaps.get(i).getObj().getString("randomId"));
                  return similarityMaps.get(i).obj;
              }
              i--;
          }
          return null;

       }
   */

}
