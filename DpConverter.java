package com.parse.ideanetwork;


import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;



public class DpConverter {
    private Context mContext;
    private Resources resources;
    private DisplayMetrics metrics;


    public DpConverter(Context context){
        mContext = context;
        resources = mContext.getResources();
        metrics = resources.getDisplayMetrics();
    }

    public  double convertDpToPixel(double dp){

        double px = dp * ((double)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public double convertPixelToDp(double px){
        double dp = px / ((double)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
