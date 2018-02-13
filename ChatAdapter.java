package com.parse.ideanetwork;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class ChatAdapter extends ArrayAdapter<String> {

    String item;
    Context context;
    LayoutInflater inflater;
    ChatAdapter.ViewHolder viewHolder;
    ArrayList<Integer> unreadMsgCount;
    ArrayList<ChatMesssageMap> maps;





    public ChatAdapter(@NonNull Context context, @LayoutRes int resource,  @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public class ViewHolder{
        TextView tv;
        TextView unreadMsgs;
    }

    public void setUnreadMsgCount(ArrayList<Integer> unreadMsgs){
        this.unreadMsgCount = unreadMsgs;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        item = getItem(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.tv, parent, false);
            viewHolder = new ChatAdapter.ViewHolder();
            viewHolder.tv = (TextView)convertView.findViewById(R.id.textView3);
            viewHolder.unreadMsgs = (TextView)convertView.findViewById(R.id.textView4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = new ChatAdapter.ViewHolder();
        }

        if (viewHolder.tv!=null) {
            viewHolder.tv.setText(item);
        }
        Log.i("lastuser", "unread msgs: "+Integer.toString(unreadMsgCount.get(position)));
        if (viewHolder.unreadMsgs !=null) {
            viewHolder.unreadMsgs.setText(Integer.toString(unreadMsgCount.get(position)) + " new message(s).");
        }
        return convertView;
    }

    public void setChatMessageMaps(ArrayList<ChatMesssageMap> maps){
        this.maps = maps;
    }
}
