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

import java.util.List;


public class TipAdapter extends ArrayAdapter<Tip> {

    LayoutInflater layoutInflater;

    public TipAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Tip> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
    }

    public class ViewHolder{
        TextView tv1;
        TextView tv2;
        ImageView imageView;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Tip tip = (Tip) getItem(position);
        TipAdapter.ViewHolder viewholder = new ViewHolder();
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.tip_container,parent,false);
            viewholder.tv1 = (TextView)convertView.findViewById(R.id.tipInfo1);
            viewholder.tv2 = (TextView)convertView.findViewById(R.id.tipInfo2);
            viewholder.imageView = (ImageView) convertView.findViewById(R.id.tipPic);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder)convertView.getTag();
        }

        if (tip!=null) {
            if (viewholder.tv1 != null) {
                viewholder.tv1.setText(tip.initialMsg);
            }
            if (viewholder.tv2 != null) {
                viewholder.tv2.setText(tip.finalMsg);
            }
            if (viewholder.imageView != null) {
                viewholder.imageView.setBackgroundResource(tip.image);
            }
        }
        return convertView;
    }
}
