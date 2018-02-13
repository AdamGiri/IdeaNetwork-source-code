package com.parse.ideanetwork;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchOptionsAdapter extends ArrayAdapter<SearchOption> {

    Context context;
    LayoutInflater inflater;


    public SearchOptionsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<SearchOption> objects) {
        super(context, resource, objects);
        this.context = context;
    }
    public class ViewHolder{
        TextView label;
        ImageView icon;
        ConstraintLayout constraintLayout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchOption item = (SearchOption) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.search_option, parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.label = (TextView) convertView.findViewById(R.id.label);
            viewHolder.constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.searchOption);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (item!=null){
        if (viewHolder.label !=null) {
            viewHolder.label.setText(item.label);
            viewHolder.label.setTag(item);
        }

        if (viewHolder.icon !=null) {

            item.findDrawable(viewHolder.icon);
            viewHolder.icon.setTag(item);
        }

            if (viewHolder.constraintLayout != null) {
                viewHolder.constraintLayout.setTag(item);
            }
        }
        return convertView;
    }
}
