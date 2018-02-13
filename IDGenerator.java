package com.parse.ideanetwork;

import java.util.Random;

//generates ids for IdeaTrees
public class IDGenerator {
    private  static final char[] symbols;
    private final Random rand = new Random();
    private  final char[] buf = new char[10];


    static{
        StringBuilder tmp = new StringBuilder();
        for (char ch ='0';ch<'9';ch++){
            tmp.append(ch);
        }
        for (char ch ='a'; ch<'z';ch++){
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }

    public String generateID(){
        for (int i=0;i<10;i++){
           buf[i] = symbols[rand.nextInt(10)];
        }
        return new String(buf);
    }
}
