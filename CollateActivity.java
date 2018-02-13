package com.parse.ideanetwork;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ideanetwork.Collation.CollatedIdea;
import com.parse.ideanetwork.Collation.Collation;
import com.parse.ideanetwork.Collation.CollationAdapter;
import com.parse.ideanetwork.Collation.SavedList;
import com.parse.ideanetwork.Messaging.MessageContainer;
import com.parse.ideanetwork.Messaging.MessagingAdapter;
import com.parse.ideanetwork.Messaging.PopUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollateActivity extends AppCompatActivity {

    Collation collation;
    TextView listTitle;
    List<String> voteContributors;
    ParseObject votedCommentObj;
    CollationAdapter arrayAdapter;
    ListView listView;
    String ideaTreeId;
    String mainTitle;
    String mainAuthor;
    Intent intent;
    boolean userInChat;
    String commentId;
    PopUp popUp;
    ArrayList<MessageContainer> messageContainers;
    MessagingAdapter adapter;
    SpannableString initialTag;
    boolean replyEnabled;
    String replyTotal;
    String reply;
    int index = -1;
    MessageContainer messageContainer;
    CollatedIdea collatedIdea;
    ArrayList<CollatedIdea> topIdeas;
    MessageContainer replyContainer;
    TextView tv;
    String msg;
    String mainID;
    int replaceIndex = -1;
    List<String> replyIDs;
    boolean replyComplete;
    ArrayList<MessageContainer> mainContainers;
    Map<String,MessageContainer> replyContainers;
    boolean voteLock;
    boolean weirdCase;

    TextView noListsMsg;

    int s;
    ArrayList<String> commentIds;

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
                                        Intent intent = new Intent(CollateActivity.this,MainActivity.class);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collate);
        setTitle("Top voted ideas");
        new DisplayAd(this);
        intent = getIntent();
        listView = (ListView)findViewById(R.id.listViewCollate);
        topIdeas = new ArrayList<>();
        noListsMsg = (TextView)findViewById(R.id.noListMsg);
        listTitle = (TextView)findViewById(R.id.listTitle);
        listTitle.setText("Top extracted ideas from: "+"'"+intent.getStringExtra("title")+"'");
        ideaTreeId = intent.getStringExtra("id");
        new FriendsRequests(this);
        mainTitle = intent.getStringExtra("title");
        mainAuthor = intent.getStringExtra("author");
        if (intent.getStringArrayListExtra("savedIds")==null) {
            queryTopIdeas(ideaTreeId);
        } else {
            Log.i("CollateActivity", "list sent through");
            queryCollatedList();
        }
    }

    private void queryCollatedList() {
        final ArrayList<String> topIdeaIDs = intent.getStringArrayListExtra("savedIds");
        Log.i("queryCollatedList", topIdeaIDs.toString());
        collation = new Collation();
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
                query.whereContainedIn("randomId", topIdeaIDs);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (!objects.isEmpty()) {
                                Log.i("topIdeaIDs", "size " + Integer.toString(collation.topIdeaObjects.size()));
                                collation.topIdeaObjects.addAll(objects);
                                collation.parseToIdeas();
                                activateAdapter();
                                makeIdea();

                            } else {
                                noListsMsg.setVisibility(View.VISIBLE);
                                Log.i("queryCollatedList", "objects empty");
                            }
                        } else {
                            noListsMsg.setVisibility(View.VISIBLE);
                            Log.i("queryCollatedList", e.getMessage());
                        }
                    }
                });
            }


    private void makeIdea(){
        for (final ParseObject topIdeaObj : collation.topIdeaObjects) {
            final CollatedIdea collatedIdea = new CollatedIdea(topIdeaObj.getString("Summary"),
                    topIdeaObj.getString("Description"), topIdeaObj.getString("Author"));
            collatedIdea.initialVote = topIdeaObj.getNumber("voteTotal").intValue();
            collatedIdea.setVotes(collatedIdea.initialVote);
            collatedIdea.setId(topIdeaObj.getString("randomId"));
            collatedIdea.setDescription(topIdeaObj.getString("Description"));

            topIdeas.add(collatedIdea);
            arrayAdapter.notifyDataSetChanged();

            //THIS  QUERY IS JUST A HACK TO DELAY THE THREAD AS FOR SOME REASON GETVIEW IS BEING CALLED
            //AFTER collatedIdea.upVote.setBackgroundColor THEREFORE NPE IS OCCURING.
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Test");
            query.whereEqualTo("objectId", "MBmQgGhHBK");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    List<String> voteUp = topIdeaObj.getList("voteConUp");
                    List<String> voteDwn = topIdeaObj.getList("voteConDwn");
                    if (voteUp != null) {
                        if (voteUp.contains(ParseUser.getCurrentUser().getUsername())) {
                            Log.i("getView", "after notifyDataSetChanged");
                            if (collatedIdea.upVote!=null) {
                                collatedIdea.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                                collatedIdea.upActive = true;
                            }
                        }
                    }
                    if (voteDwn != null) {
                        if (voteDwn.contains(ParseUser.getCurrentUser().getUsername())) {

                            if (collatedIdea.dwnVote!=null) {
                                collatedIdea.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                                collatedIdea.dwnActive = true;
                            }
                        }
                    }
                }
            });
        }
    }


    private void queryTopIdeas(final String id){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("IdeaTree", id);
        query.orderByDescending("voteTotal");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    if (!objects.isEmpty()) {
                        Log.i("queryTopIdeas", objects.get(0).getNumber("voteTotal").toString());
                        activateAdapter();
                        collation = new Collation(objects.get(0).getNumber("voteTotal").intValue(), id,
                                CollateActivity.this);
                        Log.i("calculateLimit", Float.toString(collation.calculateLimit()));
                        float voteLimit = collation.calculateLimit();
                        for (int i = 0; i < objects.size(); i++) {
                            int voteTotal = objects.get(i).getNumber("voteTotal").intValue();
                            if ( voteTotal >= voteLimit
                                    && voteTotal != 0) {
                                collation.topIdeaObjects.add(objects.get(i));
                            }
                        }
                        collation.parseToIdeas();
                    } else {
                        noListsMsg.setVisibility(View.VISIBLE);
                    }
                    makeIdea();

                }else{
                    noListsMsg.setVisibility(View.VISIBLE);}
            }
        });
    }


    private void activateAdapter(){
        Log.i("activateAdapter", "red");
        arrayAdapter = new CollationAdapter(this,R.layout.collated_idea,topIdeas);
        Log.i("activateAdapter", "green");
        listView.setAdapter(arrayAdapter);
    }

    public void saveList(View view){
        if (!collation.topIdeaObjects.isEmpty()) {
            collation.extractIds();
            Log.i("extractIds", collation.topIdeasIds.toString());
            SavedList savedList = new SavedList(collation.topIdeasIds, ideaTreeId);
            savedList.setMainTitle(mainTitle);
            savedList.setAuthor(mainAuthor);
            Gson gson = new Gson();
            String savedIDJSON = gson.toJson(savedList);

            if (ParseUser.getCurrentUser().getList("CollatedLists") == null) {
                ParseUser.getCurrentUser().put("CollatedLists", new ArrayList<>());
            }
            ParseUser.getCurrentUser().add("CollatedLists", savedIDJSON);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(CollateActivity.this, "List successfully added to your lists " +
                                "within your library", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "There are no ideas to add to a list.", Toast.LENGTH_SHORT).show();
        }
        }



    public void onClickupVoteCollated(View view){
        if (!voteLock) {
            voteLock = true;
            Log.i("votetest", "onClickupVoteCollated");
            collatedIdea = (CollatedIdea)view.getTag();
            boolean b = false;

            if (!collatedIdea.dwnActive) {
                Log.i("votetest", "onClickupVoteCollated "+" !collatedIdea.dwnActive");
                if (!collatedIdea.upActive){
                    Log.i("votetest", "onClickupVoteCollated "+" !collatedIdea.upActive");
                    view.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                } else {
                    b= true;

                    Log.i("votetest", "onClickupVoteCollated "+" collatedIdea.upActive");
                    view.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol);
                }
                collatedIdea.disableButton("voteConUp");
                voteClick(1,view,b);
            } else {
                Log.i("votetest", "onClickupVoteCollated "+"    voteLock = false;");
                voteLock = false;
            }
        }
    }

    public void onClickdownVoteCollated(View view){
        if (!voteLock) {
            voteLock = true;
            collatedIdea = (CollatedIdea)view.getTag();
            boolean b = false;
            Log.i("votetest", "onClickdownVoteCollated");
            if (!collatedIdea.upActive) {
                Log.i("votetest", "onClickdownVoteCollated" + " !collatedIdea.upActive");
                if (!collatedIdea.dwnActive){
                    Log.i("votetest", "onClickdownVoteCollated" + " !collatedIdea.dwnActive");
                   view.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                } else {
                    b= true;
                    weirdCase = true;
                    Log.i("votetest", "onClickdownVoteCollated" + " collatedIdea.dwnActive");
                  view.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol);
                }
                Log.i("votetest", "!upactive");
                collatedIdea.disableButton("voteConDwn");
                voteClick(-1, view,b);
            } else {
                Log.i("votetest", "onClickdownVoteCollated" + " voteLock = false;");
                voteLock = false;
            }
        }
    }


    private void voteClick(final int n, final View view, final boolean b){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("randomId", collatedIdea.id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()){
                        String voteColumn;
                        final String oppositeKey;
                        final List<String> oppositeColumn;
                        if (n > 0) {
                            voteColumn = "voteConUp";
                            oppositeColumn = objects.get(0).getList("voteConDwn");
                            oppositeKey = "voteConDwn";
                        } else {
                            voteColumn = "voteConDwn";
                            oppositeColumn = objects.get(0).getList("voteConUp");
                            oppositeKey = "voteConUp";
                        }

                        if (objects.get(0).getList(voteColumn) != null) {
                            List<String> voteContributors = objects.get(0).getList(voteColumn);
                            if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
                                Log.i("voteLock", "green");
                                Log.i("commentVoteClick", "vot con does not contain user");

                             objects.get(0).put("voteTotal", collatedIdea.votes + n);
                                voteContributors.add(ParseUser.getCurrentUser().getUsername());
                                objects.get(0).put(voteColumn, voteContributors);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i("voteLock","changing votelock to false");
                                            voteLock = false;
                                            collatedIdea.setVotes(collatedIdea.votes + n);
                                            collatedIdea.setVoteView();
                                            arrayAdapter.notifyDataSetChanged();
                                            mainVote(n,b);
                                            if (oppositeColumn != null) {
                                                if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                                    oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                            getUsername()));
                                                    objects.get(0).put(oppositeKey, oppositeColumn);
                                                    objects.get(0).saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.i("commentVoteClick", "saved");
                                                            } else {
                                                                Log.i("commentVoteClick", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            //     messageContainer.vote.setTypeface(null, Typeface.BOLD);
                                            //  messageContainer.vote.setTextSize(16f);
                                            Log.i("vote", e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.i("voteLock", "blue");
                                Log.i("commentVoteClick", "vot con does  contain user");
                                voteContributors.remove(voteContributors.indexOf(ParseUser.getCurrentUser().
                                        getUsername()));
                                int s = 0;
                                if (n > 0) {
                                    s = -1;
                                } else {
                                    s = 1;
                                }

                           objects.get(0).put("voteTotal", collatedIdea.votes + s);
                                objects.get(0).put(voteColumn, voteContributors);
                                final int finalS = s;
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            collatedIdea.setVotes(collatedIdea.votes +  finalS);
                                            collatedIdea.setVoteView();
                                            arrayAdapter.notifyDataSetChanged();
                                            mainVote(finalS,b);
                                            Log.i("voteLock",topIdeas.get(0).voteView.getText().toString());
                                            voteLock = false;
                                            collatedIdea.resetButtons();

                                        } else {
                                            Log.i("voteLock", e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("voteLock", "red");
                            objects.get(0).put(voteColumn, new ArrayList<>());
                            objects.get(0).add(voteColumn, ParseUser.
                                    getCurrentUser().getUsername());

                            collatedIdea.setVotes(collatedIdea.votes + n);
                            collatedIdea.setVoteView();
                            arrayAdapter.notifyDataSetChanged();
                            objects.get(0).put("voteTotal", collatedIdea.votes);
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i("voteLock","changing votelock to false");
                                        voteLock = false;
                                        mainVote(n,b);
                                        if (oppositeColumn != null) {
                                            Log.i("oppositeColumn", "opposite not null");
                                            if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                                oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                        getUsername()));
                                                objects.get(0).put(oppositeKey, oppositeColumn);
                                                objects.get(0).saveInBackground();
                                            }
                                        }
                                    } else {
                                        //   idea.totalVote.setTypeface(null, Typeface.BOLD);
                                        Log.i("vote", e.getMessage());
                                        //  idea.totalVote.setTextSize(16f);
                                    }
                                }
                            });
                        }

                    } else {
                        Log.i("vote", "objects empty");
                    }

                } else {
                    Log.i("vote", e.getMessage());
                }


            }
        });
    }


    private void setTapForMore(CollatedIdea idea) {
        if (!idea.descriptionText.equals("")){
            TextView tapForMore = (TextView) idea.tapForMore;
            if (tapForMore != null) {
                tapForMore.setVisibility(View.VISIBLE);
            }
        }
    }

    private void mainVote(final int n, final boolean b) {
        //TODO add voteTimes to parse when n is + like in ideaactivity mainvote.
        ParseObject vote = new ParseObject("Votes");
        vote.put("IdeaTreeId",ideaTreeId);
        vote.put("creation", TimeSetter.getTime());
        vote.saveInBackground();
        ParseQuery<ParseObject> query=  new ParseQuery<ParseObject>("Idea");
        query.whereEqualTo("Identifier", "Main");
        query.whereEqualTo("IdeaTree", ideaTreeId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (!objects.isEmpty()) {
                        Log.i("mainVote", "n: " + Integer.toString(n));

                        List<Number> voteTimes = objects.get(0).getList("VoteTimes");
                        if (voteTimes == null) {
                            voteTimes = new ArrayList<>();
                            objects.get(0).put("VoteTimes", voteTimes);
                        }
                        if (n > 0 && !b) {
                            if (objects.get(0).getNumber("voteMainTotal").intValue() >= 0) {
                                objects.get(0).add("VoteTimes", TimeSetter.getTime());
                            }
                        }

                        if (b) {
                            if (weirdCase) {
                                if (objects.get(0).getNumber("voteMainTotal").intValue() >= 0) {
                                    objects.get(0).add("VoteTimes", TimeSetter.getTime());
                                }
                                weirdCase = false;
                            } else {
                                if (!voteTimes.isEmpty()) {
                                    voteTimes.remove(voteTimes.size() - 1);
                                    objects.get(0).put("VoteTimes", voteTimes);
                                }
                            }
                        }
                        objects.get(0).put("voteMainTotal", objects.get(0).getNumber("voteMainTotal")
                                .intValue() + n);

                        objects.get(0).saveInBackground();
                    }
                }
            }
        });
        }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void commentsOnClick(final View view) {
        if (popUp == null) {
            Log.i("commentsOnClick", "test");
            userInChat = true;
            commentIds = new ArrayList<>();
            final CollatedIdea idea = (CollatedIdea) view.getTag();
            commentId = idea.id;
            voteLock = false;
            popUp = new PopUp(CollateActivity.this);
            messageContainers = new ArrayList<MessageContainer>();
            replyContainers = new HashMap<>();
            mainContainers = new ArrayList<>();
            messageContainers.add(new MessageContainer());
            setMessagingAdapter();
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
            query.whereEqualTo("randomId", commentId);
            query.orderByDescending("update");
            query.findInBackground(new FindCallback<ParseObject>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void done(final List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (!objects.isEmpty()) {
                            for (final ParseObject object : objects) {
                                Log.i("messageAuthor", object.getString("message"));
                                setMessageContainer(object);

                            }
                            generateReplies();
                        }
                        popUp.activatePopUp();
                        setDismissListener();
                    } else {
                        Log.i("commentsclick", e.getMessage());
                    }
                }
            });
        }
    }

    private void generateReplies() {
        if (!mainContainers.isEmpty()){
            for (int j=0;j<mainContainers.size();j++) {
                Log.i("messageContainers", "adding msg to messageContainers: "+mainContainers.get(j).message);
                messageContainers.add(mainContainers.get(j));
                adapter.notifyDataSetChanged();
                queryVotes(mainContainers.get(j));

                Log.i("messageId", mainContainers.get(j).messageId);
                if (mainContainers.get(j).replyIDs!= null) {
                    for (int i = 0; i < mainContainers.get(j).replyIDs.size(); i++) {
                        if (!replyContainers.isEmpty()) {
                            Log.i("generateReplies","adding replycontainer: " + replyContainers.get(mainContainers.get(j).replyIDs.get(i)).getMessage());
                            messageContainers.add(replyContainers.get(mainContainers.get(j).replyIDs.get(i)));
                            adapter.notifyDataSetChanged();
                            queryVotes(replyContainers.get(mainContainers.get(j).replyIDs.get(i)));

                        }
                    }
                }
            }
            Log.i("generateReplies","replycntainer size " + Integer.toString(replyContainers.size()));

        }
    }

    public void queryVotes(final MessageContainer mContainer){
        Log.i("mContainer", mContainer.messageId);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
        query.whereEqualTo("messageId", mContainer.messageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()){
                        List<String> voteUp =  objects.get(0).getList("voteConUp");
                        List<String> voteDwn=  objects.get(0).getList("voteConDwn");
                        if (voteUp!=null){
                            if (voteUp.contains(ParseUser.getCurrentUser().getUsername())){
                                mContainer.userUpVoted = true;
                                Log.i("setButtons", "changing color for upvote button for " + mContainer.messageId);
                                if ( mContainer.upVote!=null) {
                                    mContainer.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                                }
                                mContainer.disableButton("voteConUp");

                            }}
                        if (voteDwn!=null){
                            if (voteDwn.contains(ParseUser.getCurrentUser().getUsername())){
                                Log.i("setButtons", "changing color for dwnvote button for " + mContainer.messageId);
                                mContainer.userDwnVoted = true;
                                if (mContainer.dwnVote!=null) {
                                    mContainer.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                                }
                                mContainer.disableButton("voteConDwn");
                            }
                        }

                    }

                }
            }
        });
    }

    private void setMessageContainer(ParseObject object) {
        String msgId = object.getString("messageId");
        commentIds.add(msgId);
        MessageContainer mContainer = new MessageContainer(
                object.getString("message"), object.getString("Author"));
        if (msgId != null){
            mContainer.messageId = msgId;
        }

        Number votes = object.getNumber("votes");

        if (votes != null) {
            mContainer.setVote(votes.intValue());
        }

        if (object.getString("replyTag") != null) {
            String replyTag = "@"+object.getString("replyTag");
            mContainer.setInitialTag(new SpannableString(replyTag));
            mContainer.stringBuilder();
            mContainer.setIdentifier(object.getString("Identifier"));
        } else {
            mContainer.setIdentifier(object.getString("Identifier"));
        }

        if (object.getNumber("replaceIndex")!=null){
            mContainer.setReplaceIndex(object.getNumber("replaceIndex").intValue());
        }

        if (object.getList("replyIDs")!=null){
            List<String> replyIDs = object.getList("replyIDs");
            if (!replyIDs.isEmpty()){
                mContainer.setReplyIDs(replyIDs);
                Log.i("replyIDs", mContainer.replyIDs.toString());
            }
        }

        if (object.getString("Identifier") !=null){
            mContainer.setIdentifier(object.getString("Identifier"));
            if (mContainer.identifier.equals("main")){
                mainContainers.add(mContainer);
            } else {
                String mainId = object.getString("mainId");
                if (mainId!= null){
                    mContainer.mainId = mainId;
                }
                replyContainers.put(mContainer.messageId,mContainer);
            }
        }

        if (object.getString("Author")!= null){

        }



    }

    private void setDismissListener() {
        popUp.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialog != null){
                    userInChat = false;
                    popUp = null;
                }
            }
        });
    }

    private void setMessagingAdapter(){
        adapter = new MessagingAdapter(CollateActivity.this
                ,messageContainers);
        popUp.listView.setAdapter(adapter);
        adapter.popUp = popUp;
        adapter.notifyDataSetChanged();
    }

    public void onClickSendIndent(View view){
        replyContainer.isStillReplyBaby = false;
        TextView tv = (TextView) view.getTag();
        replyTotal = tv.getText().toString();
        reply = obtainText(replyTotal,initialTag.length());
        replyEnabled = true;
        addMessageToParse();

    }

    public void onClickReply(final View view){

        if (replyContainer != null) {
            if (replyContainer.message.equals("")&& messageContainers.contains(replyContainer)) {
                messageContainers.remove(messageContainers.indexOf(replyContainer));
                adapter.notifyDataSetChanged();
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageContainer mc = (MessageContainer) view.getTag();
                initialTag = new SpannableString("@"+mc.username);
                int index = mc.position+1;
                if (mc.identifier.equals("main")){
                    mainID = mc.messageId;
                    mc.mainId = mainID;
                    Log.i("mainId", mc.mainId);
                } else {
                    mainID = mc.mainId;
                }
                replyEnabled = true;
                replyContainer = new MessageContainer();
                replyContainer.isStillReplyBaby = true;
                replyContainer.mainId = mc.mainId;
                replyContainer.setReplaceIndex(index);
                messageContainers.add(index,replyContainer);
                adapter.setIndex(index);
                adapter.setReplyTag(stringBuilder(initialTag));
                adapter.notifyDataSetChanged();
                if (index==messageContainers.size()-1){
                    popUp.listView.setSelection(messageContainers.size()-1);
                }

                Log.i("onClickReply", stringBuilder(initialTag).toString());
            }
        }, 100);

    }

    public void onClickSend(View view){
        if (replyContainer != null) {
            if (replyContainer.message.equals("")&& messageContainers.contains(replyContainer)) {
                messageContainers.remove(messageContainers.indexOf(replyContainer));
                adapter.notifyDataSetChanged();
            }
        }
        if (adapter.sendMsg != null) {
            reply = adapter.sendMsg.getText().toString();
        }
        if (replyEnabled) {
            reply = obtainText(reply,initialTag.length());
        }
        Log.i("candy", reply);

        adapter.sendMsg.getText().clear();
        popUp.initialMsg = "";
        addMessageToParse();

    }

    private String obtainText(String s, int start) {
        return s.substring(start,s.length());
    }


    private void addMessageToParse() {
        final String msgId = new IDGenerator().generateID();
        MessageContainer mContainer;

        if (replyEnabled){
            if (!replyTotal.equals(initialTag.toString())){
                mContainer = new MessageContainer(reply, ParseUser.getCurrentUser()
                        .getUsername());
                mContainer.setId(msgId);
                mContainer.setInitialTag(initialTag);
                mContainer.stringBuilder();
                mContainer.setIdentifier("reply");
                mContainer.mainId = mainID;
                replaceIndex = messageContainers.indexOf(replyContainer);
                mContainer.setReplaceIndex(replaceIndex);
                adapter.setReplyEnabled(true);
                Log.i("replaceIndex", Integer.toString(replaceIndex));
                messageContainers.remove(replaceIndex);
                Log.i("size", "after " + Integer.toString(messageContainers.size()));
                messageContainers.add(replaceIndex, mContainer);
                addToParse(mContainer,msgId);
            } else {
                adapter.notifyDataSetChanged();
                replyEnabled = false;
                messageContainers.remove(replyContainer.replaceIndex);
            }

        } else {
            Log.i("reply", "reply disenabled");
            if (!reply.equals("")) {
                mContainer = new MessageContainer(reply, ParseUser.getCurrentUser()
                        .getUsername());
                mContainer.setIdentifier("main");
                mContainer.setId(msgId);
                messageContainers.add(1,mContainer);
                Log.i("mContainer", mContainer.getMessage());
                addToParse(mContainer,msgId);
            }
        }

        Log.i("addMessageToParse",Integer.toString(messageContainers.size()));


    }

    private void addToParse(final MessageContainer mContainer, final String msgId) {
        adapter.notifyDataSetChanged();
        ParseObject parseObject = new ParseObject("IdeaComments");
        parseObject.put("randomId", commentId);
        parseObject.put("messageId", msgId);
        parseObject.put("update", new TimeSetter().presentTime);
        if (mContainer.identifier.equals("main")){
            mContainer.mainId = mContainer.messageId;
        }
        parseObject.put("mainId", mContainer.mainId);
        if (replyEnabled) {
            //TODO once youve sorted out the profile inbox, need to set a system whereby
            //TODO repliers that are saved in "replyTag" on parse need to be queried and sent
            //TODO an alert that such and such has replied.
            parseObject.put("replyTag", obtainText(initialTag.toString(),1));
            Log.i("put", "e");
            parseObject.put("message", reply);
            parseObject.put("mainId", mContainer.mainId);
            parseObject.put("Identifier","reply");
            parseObject.put("replaceIndex",mContainer.replaceIndex);
            replyEnabled = false;
            parseObject.put("Author", ParseUser.getCurrentUser().getUsername());
            parseObject.put("Index",messageContainers.indexOf(mContainer));
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        adjustIndex(mContainer);
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
                        query.whereEqualTo("messageId",mContainer.mainId);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e==null){
                                    if (!objects.isEmpty()){
                                        replyIDs = objects.get(0).getList("replyIDs");
                                        if (replyIDs== null){
                                            replyIDs = new ArrayList<String>();
                                            objects.get(0).put("replyIDs",replyIDs);
                                        }
                                        replyIDs.add(msgId);
                                        objects.get(0).put("replyIDs", replyIDs);
                                        objects.get(0).saveInBackground();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } else {
            adjustIndex(mContainer);
            parseObject.put("Identifier","main");
            parseObject.put("message", reply);
            parseObject.put("Author", ParseUser.getCurrentUser().getUsername());
            parseObject.saveInBackground();
        }


    }





    private void adjustIndex(MessageContainer mContainer) {
        int startingIndex = messageContainers.indexOf(mContainer) +1;
        if (startingIndex<messageContainers.size()) {
            for (int i = startingIndex; i < messageContainers.size(); i++) {
                if (messageContainers.get(i).identifier.equals("reply")) {
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
                    query.whereEqualTo("messageId", messageContainers.get(i).messageId);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                if (!objects.isEmpty()) {
                                    if (objects.get(0).getNumber("Index") != null) {
                                        Log.i("adjustIndex", objects.get(0).getString("messageId"));
                                        int objectIndex = objects.get(0).getNumber("Index").intValue();
                                        objects.get(0).put("Index", objectIndex + 1);
                                        objects.get(0).saveInBackground();
                                    }

                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private SpannableStringBuilder stringBuilder(SpannableString text){
        SpannableStringBuilder sp = new SpannableStringBuilder();
        text.setSpan(new ForegroundColorSpan(Color.parseColor("#335ce5")),0, text.length(),0);
        sp.append(text);
        return sp;
    }


    public void onClickUpVoteMsg(View view) {
        Log.i("candy", Boolean.toString(voteLock));
        if (!voteLock) {
            voteLock = true;
            messageContainer = (MessageContainer) view.getTag();
            Log.i("candy",  "dwnActive: "+Boolean.toString(messageContainer.dwnActive));
            String voteColumn = "voteConUp";
            getCommentData(voteColumn);
        }
    }

    public void onClickDownVoteMsg(View view){
        Log.i("candy", Boolean.toString(voteLock));
        if (!voteLock) {
            voteLock = true;
            messageContainer = (MessageContainer) view.getTag();
            String voteColumn = "voteConDwn";
            getCommentData(voteColumn);
            Log.i("candy", Boolean.toString(messageContainer.upActive));

        }}

    private void getCommentData(final String voteColumn){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("IdeaComments");
        Log.i("candy", "msgId: "+messageContainer.messageId);
        query.whereEqualTo("messageId", messageContainer.messageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    Log.i("candy", "e==null)");
                    if (!objects.isEmpty()) {
                        Log.i("candy", "!objects.isEmpty()");
                        votedCommentObj = objects.get(0);
                        voteContributors = votedCommentObj.getList(voteColumn);
                        if (voteContributors == null) {
                            voteContributors = new ArrayList<String>();
                            votedCommentObj.put(voteColumn,voteContributors);
                            votedCommentObj.saveInBackground();
                            Log.i("candy", "votedCommentObj.getList(voteColumn) == null");
                        }
                        Log.i("candy", "voteColumn " + voteColumn);
                        if (voteColumn.equals("voteConUp")) {
                            List<String> oppositeList = votedCommentObj.getList("voteConDwn");
                            if (oppositeList == null) {
                                Log.i("candy", "voteColumn.equals(\"voteConUp\") and oppositeList == null");
                                upActive();
                            } else {
                                Log.i("candy", "grey");
                                if (!oppositeList.contains(ParseUser.getCurrentUser().getUsername())) {
                                    Log.i("candy", "voteColumn.equals(\"voteConUp\"");
                                    upActive();
                                }else {
                                    voteLock = false;
                                }
                            }
                        } else {
                            List<String> oppositeList = votedCommentObj.getList("voteConUp");
                            if (oppositeList == null) {
                                Log.i("candy", "!voteColumn.equals(\"voteConUp\"");
                                dwnActive();
                            } else {
                                Log.i("candy", "purple");
                                if (!oppositeList.contains(ParseUser.getCurrentUser().getUsername())) {
                                    dwnActive();
                                } else {
                                    voteLock = false;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void upActive(){
        if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
            Log.i("candy", "  !voteContributors.contains(ParseUser.getCurrentUser().getUsername())");
            messageContainer.upActive = false;
            messageContainer.userUpVoted = true;
        } else {
            Log.i("candy", "  voteContributors.contains(ParseUser.getCurrentUser().getUsername());");
            messageContainer.upActive = true;
            messageContainer.userUpVoted = false;
        }
        if (!messageContainer.upActive) {
            Log.i("candy", "!upactive");
            messageContainer.disableButton("voteConDwn");
            commentVoteClick(1);
        } else {
            Log.i("candy", "messageContainer.upActive");
            commentVoteClick(1);
        }
    }

    private void dwnActive(){
        if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
            Log.i("candy", "!voteContributors.contains(ParseUser.getCurrentUser().getUsername()");
            messageContainer.dwnActive = false;
            messageContainer.userDwnVoted = true;
        } else {
            Log.i("candy", "voteContributors.contains(ParseUser.getCurrentUser().getUsername()");

            messageContainer.dwnActive = true;
            messageContainer.userDwnVoted = false;
        }
        if (!messageContainer.dwnActive) {
            Log.i("candy", "!messageContainer.dwnActive");
            messageContainer.disableButton("voteConUp");
            commentVoteClick(-1);
        } else {
            Log.i("candy", "messageContainer.dwnActive");
            commentVoteClick(-1);
        }
    }

    private void commentVoteClick(final int n){
        if (votedCommentObj != null) {
            Log.i("candy", "1");
            String voteColumn;
            final String oppositeKey;
            final List<String> oppositeColumn;
            if (n > 0) {
                Log.i("candy", "2");
                voteColumn = "voteConUp";
                messageContainer.resetButtonColors();
                messageContainer.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                oppositeColumn = votedCommentObj.getList("voteConDwn");
                oppositeKey = "voteConDwn";
            } else {
                voteColumn = "voteConDwn";
                messageContainer.resetButtonColors();
                messageContainer.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                oppositeColumn = votedCommentObj.getList("voteConUp");
                oppositeKey = "voteConUp";
            }



            if (votedCommentObj != null) {
                Log.i("candy", "3");
                if (!voteContributors.contains(ParseUser.getCurrentUser().getUsername())) {
                    Log.i("candy", "green");
                    Log.i("candy", "vot con does not contain user");
                    votedCommentObj.put("votes", messageContainer.vote + n);
                    voteContributors.add(ParseUser.getCurrentUser().getUsername());
                    votedCommentObj.put(voteColumn, voteContributors);
                    votedCommentObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("candy","changing votelock to false");
                                voteLock = false;
                                messageContainer.setVote(messageContainer.vote + n);
                                if (oppositeColumn != null) {
                                    if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                        oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                                getUsername()));
                                        votedCommentObj.put(oppositeKey, oppositeColumn);
                                        votedCommentObj.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.i("commentVoteClick", "saved");
                                                } else {
                                                    Log.i("commentVoteClick", e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                }
                            } else {
                                //     messageContainer.vote.setTypeface(null, Typeface.BOLD);
                                //  messageContainer.vote.setTextSize(16f);
                                Log.i("vote", e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.i("candy", "blue");
                    Log.i("candy", "vot con does  contain user");
                    voteContributors.remove(voteContributors.indexOf(ParseUser.getCurrentUser().
                            getUsername()));
                    int s = 0;
                    if (n > 0) {
                        s = -1;
                        messageContainer.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol);
                    } else {
                        s = 1;
                        messageContainer.dwnVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol);
                    }

                    Log.i("votecount", "contains " + Integer.toString(messageContainer.vote + s));
                    votedCommentObj.put("votes", messageContainer.vote + s);
                    votedCommentObj.put(voteColumn, voteContributors);
                    final int finalS = s;
                    votedCommentObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                messageContainer.setVote(messageContainer.vote + finalS);
                                Log.i("candy","changing votelock to false");
                                Log.i("candy","finalS "+Integer.toString(finalS));
                                voteLock = false;
                                messageContainer.resetButtons();
                                //   Log.i("commentVoteClick","saved");

                            } else {
                                Log.i("commentVoteClick", e.getMessage());
                            }
                        }
                    });
                }
            } else {
                Log.i("candy", "red");
                votedCommentObj.put(voteColumn, new ArrayList<>());
                votedCommentObj.add(voteColumn, ParseUser.
                        getCurrentUser().getUsername());
                messageContainer.setVote(messageContainer.vote + n);
                votedCommentObj.put("votes", messageContainer.vote);
                votedCommentObj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("candy","changing votelock to false");
                            voteLock = false;
                            if (oppositeColumn != null) {
                                Log.i("oppositeColumn", "opposite not null");
                                if (oppositeColumn.contains(ParseUser.getCurrentUser().getUsername())) {
                                    oppositeColumn.remove(oppositeColumn.indexOf(ParseUser.getCurrentUser().
                                            getUsername()));
                                    votedCommentObj.put(oppositeKey, oppositeColumn);
                                    votedCommentObj.saveInBackground();
                                }
                            }
                        } else {
                            //   idea.totalVote.setTypeface(null, Typeface.BOLD);
                            Log.i("vote", e.getMessage());
                            //  idea.totalVote.setTextSize(16f);
                        }
                    }
                });
            }

        } else {
            Log.i("vote", "objects empty");
        }





    }



    }


