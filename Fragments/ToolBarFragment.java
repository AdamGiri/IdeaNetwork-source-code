package com.parse.ideanetwork.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ideanetwork.R;


@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ToolBarFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.toolbar_fragment,container,false);
    }
}
