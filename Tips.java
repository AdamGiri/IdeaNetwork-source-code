package com.parse.ideanetwork;


import android.content.Context;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.ParseUser;

import java.util.ArrayList;

public class Tips extends ListView implements AdapterView.OnItemClickListener{

    String activity;
    ArrayList<Tip> tiplist;
    Context context;
    TipAdapter tipAdapter;
    RelativeLayout.LayoutParams params;


    public Tips(Context context) {
        super(context);
        this.context = context;

        tiplist = new ArrayList<>();
        tipAdapter = new TipAdapter(context,R.layout.tip_container,tiplist);
        setAdapter(tipAdapter);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,MainActivity.screenHeight/2,0,0);
        setOnItemClickListener(this);
        this.setDivider(null);
    }

    public void setActivity(String activity){
        this.activity = activity;
        chooseTips();

    }

    private void chooseTips() {
        int height;
        switch (activity){
            case "HomePage":
                createStandardTips();
                break;
            case "Search" :
                createStandardTips();
                height = (int)MainActivity.screenHeight*3/5;
                params.setMargins(0,height,0,0);
                break;
            case "Library" :
                createStandardTips();
                height = (int)MainActivity.screenHeight*3/5;
                params.setMargins(0,height,0,0);
                break;
            case "Map":
                if (!ParseUser.getCurrentUser().getBoolean("showDragTip")){
                Tip dragTip = new Tip("Tap, hold and drag an idea to create a new branch.","","DragTip");
                tiplist.add(dragTip);
                 height = (int)MainActivity.screenHeight*3/4;
                params.setMargins(0,height,0,0);
                tipAdapter.notifyDataSetChanged();
                }
                break;
        }

    }

    public void createStandardTips(){
        if (!ParseUser.getCurrentUser().getBoolean("showNavTip")){
            Tip navTip = new Tip("Tap an idea to goto it's mind map.","","NavTip");
            tiplist.add(navTip);
        }
        if (!ParseUser.getCurrentUser().getBoolean("showExtractTip")){
            Tip collateTip = new Tip("Tap ","to extract top voted ideas from a mind map.","CollateTip");
            collateTip.setImage(R.drawable.ic_extract);
            tiplist.add(collateTip);
        }


        tipAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (tiplist!=null){
            if (!tiplist.isEmpty()){
                removeTipFromParse(tiplist.get(position));
                tiplist.remove(position);
                tipAdapter.notifyDataSetChanged();

            }
        }

    }

    private void removeTipFromParse(Tip tip) {
        Log.i("removeTIp","tag " + tip.tag);
        switch (tip.tag){
            case "NavTip":
                ParseUser.getCurrentUser().put("showNavTip",true);
                break;
            case "CollateTip":
                Log.i("removeTIp","show extraced called");
                ParseUser.getCurrentUser().put("showExtractTip",true);
                break;
            case "DragTip":
                ParseUser.getCurrentUser().put("showDragTip",true);
                break;
        }
        ParseUser.getCurrentUser().saveInBackground();
    }
}
