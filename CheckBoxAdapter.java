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
import android.widget.CheckBox;

import java.util.ArrayList;

public class CheckBoxAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    Context context;
    ArrayList<Boolean> tagSelected;

    public CheckBoxAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<String>objects) {
        super(context, resource, objects);
        this.context = context;
        tagSelected = new ArrayList<>();

    }

    public class ViewHolder{
      CheckBox checkedTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("checkboxadapter", Integer.toString(position));
        String item = getItem(position);
        CheckBoxAdapter.ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.checkbox, parent, false);
            viewHolder.checkedTextView = (CheckBox) convertView.findViewById(R.id.checkedTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (viewHolder.checkedTextView !=null){
            viewHolder.checkedTextView.setText(item);
        }



        return convertView;
    }
}
