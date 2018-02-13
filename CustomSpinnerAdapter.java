package com.parse.ideanetwork;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;



public class CustomSpinnerAdapter extends ArrayAdapter {

    LayoutInflater inflater;

    public CustomSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource,objects);
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder{
        TextView tv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String item = (String)getItem(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.spinner_tv,parent,false);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.spinnerTV);
            convertView.setTag(viewHolder);
        } else {
            viewHolder =  (ViewHolder) convertView.getTag();
        }

        if (viewHolder.tv !=null){
            viewHolder.tv.setText(item);
        }

        return convertView;
    }
}
