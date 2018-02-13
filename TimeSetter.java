package com.parse.ideanetwork;


import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeSetter {
    Long presentTime;
    Long beforeTime;
    public static final long timeOffset =5000 ;


    public TimeSetter(){
        timeSet();
    }

    private void timeSet(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(tz);
        Log.i("timeSet","presentTime: " + Long.toString(Calendar.getInstance(tz).getTime().getTime()));
        presentTime  = Calendar.getInstance(tz).getTime().getTime();
        beforeTime = new Date(presentTime-IdeaTreeActivity.intervalQuery).getTime()-timeOffset;
        Log.i("timeSet","beforeTime: " + beforeTime.toString());
    }

    public static long getTime(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(tz);
        return Calendar.getInstance(tz).getTime().getTime();
    }
}
