package com.parse.ideanetwork;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendReqAdapter extends ArrayAdapter {

    LayoutInflater inflater;
    List<String> friendsRequests;
    FriendsRequests friendReq;

    public FriendReqAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<String> objects
    ,FriendsRequests friendReq) {
        super(context, resource, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        friendsRequests = objects;
        this.friendReq = friendReq;
    }

    public class ViewHolder{
        TextView friendReqName;
        TextView add;
        TextView decline;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        String item = (String) getItem(position);
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.friend_req_item,parent,false);
            viewHolder.friendReqName = (TextView) convertView.findViewById(R.id.friendName);
            viewHolder.add = (TextView)convertView.findViewById(R.id.addFriend);
            viewHolder.decline = (TextView)convertView.findViewById(R.id.declineFriend);
            convertView.setTag(viewHolder);
        } else {
            viewHolder =  (FriendReqAdapter.ViewHolder) convertView.getTag();
        }

        if (viewHolder.friendReqName!=null){
            viewHolder.friendReqName.setText(item);
        }

        if (viewHolder.add != null) {
            viewHolder.add.setTag(position);
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)viewHolder.add.getTag();
                    String name = friendsRequests.get(position);
                    removeFriend(position);
                    if (friendsRequests.isEmpty()){
                        friendReq.notifyParse(true,name,true);
                    } else {
                        friendReq.notifyParse(false,name,true);
                    }

                }
            });
        }

        if (viewHolder.decline != null) {
            viewHolder.decline.setTag(position);
            viewHolder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)viewHolder.decline.getTag();
                    String name = friendsRequests.get(position);
                    removeFriend(position);
                    friendReq.notifyParse(true,name,false);

                }
            });
        }
        return convertView;
    }

    public void removeFriend(int position){
        friendsRequests.remove(position);
        notifyDataSetChanged();
    }


}
