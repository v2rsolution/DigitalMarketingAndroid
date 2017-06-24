package com.wscubetech.seovideotutorials.model;

/**
 * Created by wscubetech on 24/8/16.
 */
public class ResultCardModel {

    String title="",score="";

    //Resources
    int resBg=0,resSrc=0,resTextColor=0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getResBg() {
        return resBg;
    }

    public void setResBg(int resBg) {
        this.resBg = resBg;
    }

    public int getResSrc() {
        return resSrc;
    }

    public void setResSrc(int resSrc) {
        this.resSrc = resSrc;
    }

    public int getResTextColor() {
        return resTextColor;
    }

    public void setResTextColor(int resTextColor) {
        this.resTextColor = resTextColor;
    }
}
