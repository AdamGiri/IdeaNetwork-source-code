package com.parse.ideanetwork;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;



public class FriendsListAdapter extends ArrayAdapter<String> {

    Context context;
    LayoutInflater inflater;
    List<String> friendNameArray;
    List<String> friendStatusArray;
    FriendsListAdapter.ViewHolder viewHolder;
    String friends;
    String status;

    public FriendsListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> friendNameArray,
                              List<String> friendStatusArray) {
        super(context, resource, friendNameArray);
        this.context = context;
        this.friendNameArray = friendNameArray;
        this.friendStatusArray = friendStatusArray;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder{
        TextView nameTv;
        TextView statusTv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        friends = getItem(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.friends_status, parent, false);
            viewHolder = new FriendsListAdapter.ViewHolder();
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.friendName);
            viewHolder.statusTv = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FriendsListAdapter.ViewHolder) convertView.getTag();
        }

        Log.i("getView",friends);
        viewHolder.nameTv.setText(friends);
        viewHolder.statusTv.setText(friendStatusArray.get(position));
        return convertView;
    }
}
