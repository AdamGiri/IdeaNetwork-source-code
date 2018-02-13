package com.parse.ideanetwork.Collation;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.ideanetwork.Messaging.PopUp;
import com.parse.ideanetwork.ProfileReadOnlyActivity;
import com.parse.ideanetwork.R;

import java.util.ArrayList;


public class CollationAdapter extends ArrayAdapter<CollatedIdea> {

    Context context;
    ArrayList<CollatedIdea> collatedIdeas;
    LayoutInflater inflater;
    int resource;
    CollationAdapter.ViewHolder viewHolder;

    public CollationAdapter(@NonNull Context context, @LayoutRes int resource,
                            @NonNull ArrayList<CollatedIdea> objects) {
        super(context, resource,objects);
        this.collatedIdeas = objects;
        this.context = context;
    }

    public class ViewHolder{
        TextView description;
        TextView author;
        TextView tapForMore;
        TextView votes;
        Button upVote;
        Button downVote;
        Button chat;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final CollatedIdea collatedIdea = (CollatedIdea) getItem(position);


        if (convertView == null){
            viewHolder = new CollationAdapter.ViewHolder();
            inflater = LayoutInflater.from(context);
            resource = R.layout.collated_idea;
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.tapForMore = (TextView)convertView.findViewById(R.id.tapForMore);
            viewHolder.description = (TextView)convertView.findViewById(R.id.collatedDescription);
            viewHolder.author = (TextView)convertView.findViewById(R.id.collatedAuthor);
            viewHolder.votes = (TextView)convertView.findViewById(R.id.collatedVotes);
            viewHolder.upVote = (Button) convertView.findViewById(R.id.upVoteCollated);
            viewHolder.downVote = (Button) convertView.findViewById(R.id.downVoteCollated);
            viewHolder.chat = (Button) convertView.findViewById(R.id.chatCollated);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CollationAdapter.ViewHolder)convertView.getTag();
        }


        viewHolder.description.setText(collatedIdea.title);
        viewHolder.author.setText(collatedIdea.author);
        viewHolder.author.setTag(collatedIdea.author);
        viewHolder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfileClick(v);
            }
        });
        viewHolder.upVote.setTag(collatedIdea);
        viewHolder.downVote.setTag(collatedIdea);
        viewHolder.votes.setText(collatedIdea.votes.toString());
        viewHolder.chat.setTag(collatedIdea);
        Log.i("getView", "within get view");
        collatedIdea.tapForMore = viewHolder.tapForMore;
        collatedIdea.summary = viewHolder.description;
        collatedIdea.setVoteView(viewHolder.votes);
        collatedIdea.setButtons(viewHolder.upVote,viewHolder.downVote);
        Log.i("collatedIdea.title","desc " + collatedIdea.description);
        if (!collatedIdea.descriptionText.equals("")){

            collatedIdea.tapForMore.setVisibility(View.VISIBLE);
        } else {
            collatedIdea.tapForMore.setVisibility(View.INVISIBLE);
        }
        viewHolder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = collatedIdea.descriptionText;
                if (s != null && !s.equals("")) {
                    Log.i("description", s);
                    PopUp descriptionPop = new PopUp(context);
                    descriptionPop.activateDescriptionPopUp();
                    descriptionPop.description.setText(s);

                } else {
                    Toast.makeText(context, "This idea has no further description.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    private void setProfileClick(View v) {
        Log.i("setProfileClick", v.getTag().toString());
        Intent intent = new Intent(context,
                ProfileReadOnlyActivity.class);
        intent.putExtra("name",v.getTag().toString());
        context.startActivity(intent);
    }
}
