package com.wscubetech.seovideotutorials.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.wscubetech.seovideotutorials.user_model.UserModel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wscubetech on 5/4/17.
 */

public class QuestionListModel implements Serializable {

    //The model will be used for both questions and answers

    String quesId="",quesTitle="",quesDate="",ansCount="0",likeCount="0",tags="";//comma separated;
    int totalLikes=0,totalDislikes=0,totalViews=0;
    UserModel userModel;

    int liked=0; //0->Nothing,  1->liked,   2->2-Disliked

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalDislikes() {
        return totalDislikes;
    }

    public void setTotalDislikes(int totalDislikes) {
        this.totalDislikes = totalDislikes;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getLikeCount() {
        return likeCount;
    }


    public String getTags() {
        return tags.trim();
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAnsCount() {
        return ansCount;
    }

    public void setAnsCount(String ansCount) {
        this.ansCount = ansCount;
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public String getQuesTitle() {
        return quesTitle.trim();
    }

    public void setQuesTitle(String quesTitle) {
        this.quesTitle = quesTitle;
    }

    public String getQuesDate() {
        return dateFormat(quesDate);
    }

    public void setQuesDate(String quesDate) {
        this.quesDate = quesDate;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    private String dateFormat(String date) {
        Date da;
        String parsedDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            da = sdf.parse(date);
            sdf = new SimpleDateFormat("MMM d, yyyy");
            parsedDate = sdf.format(da);
            return parsedDate;
        } catch (ParseException e) {
            Log.v("DateParse", "" + e);
        }
        return "" + new Date();
    }


}
