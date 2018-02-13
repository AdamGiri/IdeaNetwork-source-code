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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class SkillsAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater inflater;
    List<String> objects;
    SkillsAdapter.ViewHolder viewHolder;
    String skills;
    boolean readOnly;
    public ArrayList<EditText> ets;
    public ArrayList<Button> saves;

    public SkillsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects
    ,Boolean readOnly) {
        super(context, resource, objects);
        this.context = context;
        this.readOnly=readOnly;
        this.objects = objects;
        inflater = LayoutInflater.from(context);
        ets = new ArrayList<>();
        saves = new ArrayList<>();
    }

    public class ViewHolder{
       EditText et;
       TextView tv;
       Button add;
        TextView tvRO;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        skills = getItem(position);
        viewHolder = new SkillsAdapter.ViewHolder();
        Log.i("getView", "pos " + Integer.toString(position));
        if (convertView == null) {
            Log.i("getView", "convertView == null");
            Log.i("getView", "position>0");
            if (!readOnly) {
                convertView = inflater.inflate(R.layout.skills_edittext, parent, false);
                viewHolder.et = (EditText) convertView.findViewById(R.id.editText);

                viewHolder.add = (Button) convertView.findViewById(R.id.save);
            } else {
                convertView = inflater.inflate(R.layout.skills_ro, parent, false);
                viewHolder.tvRO = (TextView) convertView.findViewById(R.id.tvRO);
            }
            convertView.setTag(viewHolder);
        } else {
            Log.i("getView", "convertView != null");
            viewHolder = (SkillsAdapter.ViewHolder) convertView.getTag();
        }

        if (!readOnly) {
            if (viewHolder.et != null) {
                ets.add(viewHolder.et);
                saves.add(viewHolder.add);
                Log.i("getView", "viewHolder.et != null");
                viewHolder.et.setText(skills);
                viewHolder.et.setTag(position);
                viewHolder.add.setTag(viewHolder.et);

            }


        }else {
            viewHolder.tvRO.setText(skills);
        }
        return convertView;
    }
    }
