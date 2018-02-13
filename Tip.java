package com.parse.ideanetwork;





public class Tip {

    String initialMsg;
    String finalMsg;
    String tag;
    int image;

    public Tip(String initialMsg,String finalMsg,String tag){
        this.initialMsg = initialMsg;
        this.finalMsg= finalMsg;
        this.tag = tag;

    }

    public void setImage(int image){
        this.image = image;
    }
}
