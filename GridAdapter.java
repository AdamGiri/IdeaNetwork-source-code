package com.parse.ideanetwork;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;


public class GridAdapter extends ArrayAdapter<MainIdea> {
    Context context;
    LayoutInflater inflater;
    ArrayList<MainIdea> mainideas;
    int resource;
    boolean search = false;
    boolean isTopIdea;

    public GridAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<MainIdea> mainideas,
                       Boolean isTopIdea) {
        super(context, resource,mainideas);
        this.mainideas = mainideas;
        this.context = context;
        this.isTopIdea = isTopIdea;

    }

    public class ViewHolder{
        TextView description;
        TextView author;
        ImageView deleteView;
        ImageView bookmark;
        TextView voteView;
        ImageView collate;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MainIdea mainIdea = (MainIdea) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(context);

                resource = R.layout.mainidea;
                Log.i("gridadapter","using R.layout.mainidea");

            convertView = inflater.inflate(resource, parent, false);

            viewHolder.description = (TextView)convertView.findViewById(R.id.description);
            viewHolder.author = (TextView)convertView.findViewById(R.id.author);
            viewHolder.voteView = (TextView)convertView.findViewById(R.id.voteCount);

                viewHolder.deleteView = (ImageView) convertView.findViewById(R.id.deleteView);

            viewHolder.bookmark = (ImageView) convertView.findViewById(R.id.bookmark);
            viewHolder.collate = (ImageView) convertView.findViewById(R.id.collate);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
            viewHolder.collate.setTag(mainIdea);

            viewHolder.deleteView.setTag(mainIdea);

            viewHolder.bookmark.setTag(mainIdea);

        if (isTopIdea || !mainIdea.author.equals(ParseUser.getCurrentUser().getUsername())){
            viewHolder.deleteView.setVisibility(View.INVISIBLE);
        }

        if (mainIdea.isBookmarked){
            viewHolder.bookmark.setBackgroundResource(R.drawable.ic_unbookmark);
        }

        viewHolder.voteView.setText(Integer.toString(mainIdea.totalVote));
        viewHolder.description.setText(mainIdea.summary);
        viewHolder.description.setTag(mainIdea.ideaTreeID);
        viewHolder.author.setText(mainIdea.author);
        viewHolder.author.setTag(mainIdea.author);
        return convertView;
    }

}

