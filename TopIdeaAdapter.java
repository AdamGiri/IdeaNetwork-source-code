package com.parse.ideanetwork;


import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TopIdeaAdapter extends ArrayAdapter<MainIdea> {
    Context context;
    LayoutInflater inflater;
    ArrayList<MainIdea> mainideas;
    int resource;

    public TopIdeaAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<MainIdea> mainideas ) {
        super(context, resource,mainideas);
        this.mainideas = mainideas;
        this.context = context;

    }

    public class ViewHolder{
        TextView description;
        TextView author;
        TextView title;
        TextView totalVote;
        TextView bookmark;
        ImageView deleteView;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MainIdea mainIdea = (MainIdea) getItem(position);
        TopIdeaAdapter.ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new TopIdeaAdapter.ViewHolder();
            inflater = LayoutInflater.from(context);
            resource = R.layout.top_idea;
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.totalVote = (TextView)convertView.findViewById(R.id.totalVote);
            viewHolder.bookmark = (TextView)convertView.findViewById(R.id.bookmark);
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.description = (TextView)convertView.findViewById(R.id.description);
            viewHolder.author = (TextView)convertView.findViewById(R.id.author);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TopIdeaAdapter.ViewHolder)convertView.getTag();
        }

        //viewHolder.collate.setTag(mainIdea.ideaTreeID);
        viewHolder.bookmark.setTag(mainIdea);
        viewHolder.totalVote.setText(Integer.toString(mainIdea.totalVote));
        viewHolder.title.setText(mainIdea.summary);
        viewHolder.description.setText(mainIdea.description);
        viewHolder.description.setTag(mainIdea.ideaTreeID);
        viewHolder.author.setText(mainIdea.author);
        viewHolder.author.setTag(mainIdea.ideaTreeID);
        return convertView;
    }

}
