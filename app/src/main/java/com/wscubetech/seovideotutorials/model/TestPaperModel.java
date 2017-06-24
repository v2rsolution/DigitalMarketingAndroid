package com.wscubetech.seovideotutorials.model;

import java.io.Serializable;

/**
 * Created by wscubetech on 23/8/16.
 */
public class TestPaperModel implements Serializable {
    String paperId="",paperTitle="",paperDuration="";

    public String getPaperDuration() {
        return paperDuration;
    }

    public void setPaperDuration(String paperDuration) {
        this.paperDuration = paperDuration;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }
}
