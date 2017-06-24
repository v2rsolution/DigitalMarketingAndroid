package com.wscubetech.seovideotutorials.model;

import java.io.Serializable;

/**
 * Created by wscubetech on 4/4/17.
 */

public class MyProfileOtherDetailModel implements Serializable {
    String title="", initial=""; //Q-> Ques  A->Ans   N->Notification  L->Log out
    int resDrawable=0, resInitialBgRes=0;

    public int getResInitialBgRes() {
        return resInitialBgRes;
    }

    public void setResInitialBgRes(int resInitialBgRes) {
        this.resInitialBgRes = resInitialBgRes;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResDrawable() {
        return resDrawable;
    }

    public void setResDrawable(int resDrawable) {
        this.resDrawable = resDrawable;
    }
}
