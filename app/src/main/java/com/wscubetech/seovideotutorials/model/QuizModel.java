package com.wscubetech.seovideotutorials.model;

import android.text.Html;

import java.io.Serializable;

/**
 * Created by wscubetech on 23/8/16.
 */
public class QuizModel implements Serializable {
    String id="",ques="",option1="",option2="",option3="",option4="",option5="";
    String correctAnswerSerialNo="", userInput="",timerValue="00:00";
    int codeFlag=0; //0->center-align  1->left-align

    public int getCodeFlag() {
        return codeFlag;
    }

    public void setCodeFlag(int codeFlag) {
        this.codeFlag = codeFlag;
    }

    public String getTimerValue() {
        return timerValue;
    }

    public void setTimerValue(String timerValue) {
        this.timerValue = timerValue;
    }

    public String getCorrectAnswerSerialNo() {
        return correctAnswerSerialNo;
    }

    public void setCorrectAnswerSerialNo(String correctAnswerSerialNo) {
        this.correctAnswerSerialNo = correctAnswerSerialNo;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = filteredString(ques);
    }

    public String getOption1() {
        return option1.trim();
    }

    public void setOption1(String option1) {
        this.option1 = filteredString(option1);
    }

    public String getOption2() {
        return option2.trim();
    }

    public void setOption2(String option2) {
        this.option2 = filteredString(option2);
    }

    public String getOption3() {
        return option3.trim();
    }

    public void setOption3(String option3) {
        this.option3 = filteredString(option3);
    }

    public String getOption4() {
        return option4.trim();
    }

    public void setOption4(String option4) {
        this.option4 = filteredString(option4);
    }

    public String getOption5() {
        return option5.trim();
    }

    public void setOption5(String option5) {
        this.option5 = filteredString(option5);
    }

    private String filteredString(String str){
        return Html.fromHtml(str).toString().trim();
    }
}
