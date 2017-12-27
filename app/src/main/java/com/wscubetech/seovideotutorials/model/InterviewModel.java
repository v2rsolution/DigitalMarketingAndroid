package com.wscubetech.seovideotutorials.model;

import android.text.Html;

/**
 * Created by wscubetech on 22/8/16.
 */
public class InterviewModel {
    String ques="",ans="",ansCode="";
    String quesCode="";

    public String getQuesCode() {
        return quesCode;
    }

    public void setQuesCode(String quesCode) {
        this.quesCode = quesCode;
    }

    public String getAnsCode() {
        return ansCode;
    }

    public void setAnsCode(String ansCode) {
        this.ansCode = filteredString(ansCode);
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = filteredString(ques);
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = filteredString(ans);
    }

    private String filteredString(String str){
        return Html.fromHtml(str).toString().trim();
    }
}
