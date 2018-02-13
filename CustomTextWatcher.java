package com.parse.ideanetwork;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

public class CustomTextWatcher implements TextWatcher {

    public int maxChars = 106;
    TextView textCount;
    TextView summary;
    public boolean maxCharsReached;
    public int charsLeft = maxChars;


    public CustomTextWatcher (TextView textCount,TextView summary){
        this.textCount =  textCount;
        this.summary = summary;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textCount.setVisibility(View.VISIBLE);
        charsLeft = maxChars - summary.getText().toString().
                length();
        textCount.setText(Integer.toString(charsLeft) + " chars left.");

        if (charsLeft < 0) {
            maxCharsReached = true;
        } else {
            maxCharsReached = false;
        }
    }
}
