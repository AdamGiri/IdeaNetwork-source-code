package com.parse.ideanetwork.PushNotifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ideanetwork.IdeaTreeActivity;


public class CustomPushReceiver extends ParsePushBroadcastReceiver {

    public CustomPushReceiver(){
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        if (intent != null){
            Intent intent1 = new Intent(context, IdeaTreeActivity.class);
            Log.i("onpushreceive",intent.getStringExtra("KEY_PUSH_DATA"));
            intent1.putExtra("newIdeaID",intent.getStringExtra("KEY_PUSH_DATA"));
            context.startActivity(intent1);
        }
    }
}
