package com.parse.ideanetwork.Messaging;

import android.content.Context;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ideanetwork.ProfileReadOnlyActivity;
import com.parse.ideanetwork.R;

import java.util.ArrayList;


public class MessagingAdapter extends ArrayAdapter<MessageContainer> {
    ArrayList<MessageContainer> dataSet;
    Context mContext;
    String ideaTreeId = "";
    public EditText reply;
    ViewCache viewHolder;
    LayoutInflater inflater;
    MessageContainer messageContainer;
    MessageContainer replyContainer;
    int index = -1;
    String replyTag;
    TextView send;
    boolean replyEnabled;
    public boolean setInvis;
    public EditText sendMsg;
    public CustomListView customListView;
    String replyText;
    public PopUp popUp;
    String initialEt = "";




    public MessagingAdapter(@NonNull Context context,
                         ArrayList<MessageContainer> data) {
        super(context, R.layout.msg_container_redo,data);
        this.dataSet = data;
        Log.i("authorsize",Integer.toString(dataSet.size()));
        mContext = context;

    }




    public class ViewCache{
        public TextView author;
        TextView message;
        TextView replyButton;
        Button upVote;
        Button downVote;
        TextView votes;


    }

    public void setIndex(int i){
        index = i;
    }

    public void setReplyTag(SpannableStringBuilder tag){
        replyTag = tag.toString();
    }




    public void setReplyEnabled(boolean b){
        replyEnabled = b;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        messageContainer =  getItem(position);

        inflater = LayoutInflater.from(mContext);

        Log.i("isStillReplyBaby", Boolean.toString(messageContainer.isStillReplyBaby) + "  pos " +
         Integer.toString(position));

        if (popUp != null){
       if (popUp.listView != null){
           if (!popUp.replyMsg.equals("")){
               replyText = popUp.replyMsg;
               Log.i("kitkat", "replyText " + replyText);
               Log.i("kitkat", "index " + index);
           }
       }}


        if (messageContainer!= null) {
            Log.i("candy","position: " + Integer.toString(position));
            Log.i("candy","msg: " + messageContainer.getMessage());
            Log.i("candy","vote: " + messageContainer.vote);
            Log.i("candy","identifier: " + messageContainer.identifier);
            messageContainer.position = position;
            if (messageContainer.identifier.equals("reply")) {
                Log.i("candy", "reply");
                convertView = inflater.inflate(R.layout.sent_reply_layout, parent, false);
                setItemTag(convertView);

            } else if (messageContainer.identifier.equals("main")) {
                Log.i("candy", "main");
                convertView = inflater.inflate(R.layout.msg_container_redo, parent, false);
                setItemTag(convertView);
            }
            if (messageContainer.isStillReplyBaby){
                Log.i("candy", "d");
                convertView = inflater.inflate(R.layout.reply_layout, parent, false);
                reply = (EditText) convertView.findViewById(R.id.replyIndent);
               // reply.pos = position;
               // reply.setListView(customListView);
                send = (TextView) convertView.findViewById(R.id.sendIndent);
                send.setTag(reply);
                Log.i("kitkat", "setting reply to: " + replyText);
                reply.setText(replyText);
                replyText = "";
                reply.setTag(messageContainer);
            }



            if (position == 0 || position == index) {
                viewHolder = new ViewCache();
                if (position == index) {
                    convertView = inflater.inflate(R.layout.reply_layout, parent, false);

                    reply = (EditText) convertView.findViewById(R.id.replyIndent);

                    reply.setText(replyTag);
                    disableParentScrolling(reply);

                        popUp.reply = reply;

                    send = (TextView) convertView.findViewById(R.id.sendIndent);
                    send.setTag(reply);



                    reply.setTag(messageContainer);
                    index = -1;
                } else {
                    Log.i("candy", "position == 0 ");
                    convertView = inflater.inflate(R.layout.message_container_reply, parent, false);
                    sendMsg = (EditText) convertView.findViewById(R.id.reply);


                    if (!popUp.initialMsg.equals("")){
                        sendMsg.setText(popUp.initialMsg);
                        popUp.initialMsg = "";
                    }
                    send = (TextView) convertView.findViewById(R.id.send);
                    send.setTag(sendMsg);
                    popUp.initialEt = sendMsg;

                }

            } else {

                if (!messageContainer.isStillReplyBaby) {
                    if (convertView == null) {
                        Log.i("candy", "convertView == null");
                        convertView = inflater.inflate(com.parse.ideanetwork.R.layout.msg_container_redo, parent, false);
                        setItemTag(convertView);
                    } else {
                        if (convertView.findViewById(R.id.username) == null) {
                            Log.i("candy", "convertView != null");
                            convertView = inflater.inflate(com.parse.ideanetwork.R.layout.msg_container_redo, parent, false);
                            setItemTag(convertView);

                        } else {
                            Log.i("candy", "convertView.getTag()");
                            viewHolder = (ViewCache) convertView.getTag();
                        }

                    }
                    Log.i("candy", "magenta");
                    viewHolder.author.setText(messageContainer.username);
                    Log.i("candy", viewHolder.author.getText().toString());
                    viewHolder.replyButton.setTag(messageContainer);


                    if (viewHolder.votes != null) {
                        viewHolder.votes.setText(Integer.toString(messageContainer.vote));
                    }
                    if (messageContainer.messageBuilder == null) {
                        Log.i("candy", "(messageContainer.messageBuilder == null");
                        viewHolder.message.setText(messageContainer.message);
                    } else {
                        viewHolder.message.setText(messageContainer.messageBuilder);
                        Log.i("candy", "test");
                    }
                }
            }



            Log.i("setButtons", "reseting colors");
            //queryVotes();
            Log.i("zxc", Integer.toString(dataSet.size()));
            Log.i("setButtons", "setting position: " + Integer.toString(position) + " messageContainer.userUpVoted: " +
            Boolean.toString(messageContainer.userUpVoted));

            if (messageContainer.userUpVoted){
                if (viewHolder.upVote!=null){
                    viewHolder.upVote.setBackgroundResource(R.drawable.ic_thumbs_up_hand_symbol_activated);
                }

            } else if (messageContainer.userDwnVoted){
                if (viewHolder.downVote!=null){

                    viewHolder.downVote.setBackgroundResource(R.drawable.ic_thumbs_dwn_hand_symbol_activated);
                }
            }

            if (setInvis) {
                if (viewHolder.replyButton != null) {
                    viewHolder.replyButton.setVisibility(View.INVISIBLE);
                }
                if (viewHolder.downVote != null) {
                    viewHolder.downVote.setVisibility(View.INVISIBLE);
                }
                if (viewHolder.upVote != null) {
                    viewHolder.upVote.setVisibility(View.INVISIBLE);
                }
                if (viewHolder.votes != null) {
                    viewHolder.votes.setVisibility(View.INVISIBLE);
                }
            }
        }
        return convertView;
    }



    private void disableParentScrolling(EditText reply) {
        reply.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
    }


    private void setItemTag(View convertView){

        viewHolder = new ViewCache();
        viewHolder.author = (TextView) convertView.findViewById(R.id.username);
        viewHolder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageContainer.username != null) {
                    Intent intent = new Intent(mContext,
                            ProfileReadOnlyActivity.class);
                    intent.putExtra("name", messageContainer.username);
                    mContext.startActivity(intent);
                }
            }
        });
        viewHolder.message = (TextView) convertView.findViewById(R.id.message);
        viewHolder.replyButton = (TextView) convertView.findViewById(R.id.reply);
        viewHolder.replyButton.setTag(messageContainer);
        viewHolder.upVote = (Button) convertView.findViewById(R.id.upVoteMsg);
        viewHolder.downVote = (Button) convertView.findViewById(R.id.downVoteMsg);
        viewHolder.votes = (TextView) convertView.findViewById(R.id.votesMsg);
        messageContainer.setVoteView(viewHolder.votes);
        viewHolder.votes.setText(Integer.toString(messageContainer.vote));
        Log.i("candy", "setting item tag vote tv: " + viewHolder.votes.getText().toString());
        viewHolder.upVote.setTag(messageContainer);
        viewHolder.downVote.setTag(messageContainer);
        messageContainer.setButtons(viewHolder.upVote,viewHolder.downVote);
        convertView.setTag(viewHolder);
    }

}
