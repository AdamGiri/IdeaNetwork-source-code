package com.parse.ideanetwork.Collation;


import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ideanetwork.R;


import java.util.ArrayList;

public class CollationListAdapterMain extends ArrayAdapter<SavedList>{
        Context context;
        LayoutInflater inflater;
        ArrayList<SavedList> savedLists;
        int resource;


        public CollationListAdapterMain(@NonNull Context context, @LayoutRes int resource, ArrayList<SavedList> savedLists ) {
            super(context, resource,savedLists);
            this.savedLists = savedLists;
            this.context = context;

        }

        public class ViewHolder{
            TextView author;
            TextView edit;
            TextView delete;
            TextView title;
            ConstraintLayout constraintLayout;

        }

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final SavedList savedList = (SavedList) getItem(position);
            CollationListAdapterMain.ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new CollationListAdapterMain.ViewHolder();
                inflater = LayoutInflater.from(context);
                resource = R.layout.collated_list_main;
                convertView = inflater.inflate(resource, parent, false);
                viewHolder.title = (TextView)convertView.findViewById(R.id.listTitle);
                viewHolder.author = (TextView)convertView.findViewById(R.id.listAuthor);
                viewHolder.edit = (TextView)convertView.findViewById(R.id.edit);
                viewHolder.delete = (TextView)convertView.findViewById(R.id.deleteCollated);

                viewHolder.constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.cLayout);

                convertView.setTag(viewHolder);
            } else {
                viewHolder =   (CollationListAdapterMain.ViewHolder)convertView.getTag();
            }
            Log.i("getView",savedList.mainTitle);
            viewHolder.title.setText(savedList.mainTitle);
            viewHolder.title.setTag(savedList);
            viewHolder.edit.setTag(savedList);
            viewHolder.delete.setTag(savedList);
            viewHolder.author.setText(savedList.author);
            viewHolder.author.setTag(savedList);

            viewHolder.constraintLayout.setTag(savedList);

            return convertView;
        }




}


