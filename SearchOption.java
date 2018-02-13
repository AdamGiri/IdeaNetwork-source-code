package com.parse.ideanetwork;


import android.widget.ImageView;

public class SearchOption {

    String label;


    public SearchOption(String label){
        this.label = label;
    }

    public void findDrawable(ImageView icon) {
        switch (label) {
            case "Politics":
                icon.setBackgroundResource(R.drawable.ic_politics);
                break;
            case "Software":
                icon.setBackgroundResource(R.drawable.ic_software);
                break;
            case "Literature":
                icon.setBackgroundResource(R.drawable.ic_literature);
                break;
            case "Science & Tech":
                icon.setBackgroundResource(R.drawable.ic_science);
                break;
            case "Social":
                icon.setBackgroundResource(R.drawable.ic_social);
                break;
            case "Film/TV":
                icon.setBackgroundResource(R.drawable.ic_film);
                break;
            case "Engineering":
                icon.setBackgroundResource(R.drawable.ic_engineering);
                break;
            case "Lifestyle":
                icon.setBackgroundResource(R.drawable.ic_lifestyle);
                break;
            case "Fiction":
                icon.setBackgroundResource(R.drawable.ic_fiction);
                break;
            case "Art":
                icon.setBackgroundResource(R.drawable.ic_art);
                break;
        }
    }
}
