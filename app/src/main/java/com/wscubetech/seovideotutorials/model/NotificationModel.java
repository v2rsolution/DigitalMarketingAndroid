package com.wscubetech.seovideotutorials.model;

import java.io.Serializable;

/**
 * Created by wscubetech on 27/3/17.
 */

public class NotificationModel implements Serializable {
    String notificationId="",notificationTitle="",notificationImage="";
    String notificationFor="0";
    // 1->Video Tutorial
    // 2->Interview Ques
    // 3->Quiz Test
    // 4->Technical Terms
    // 5->Study Material
    // 6->Answer posted for specific user's posted question
    // 7->Some questions posted

    SubCategoryModel subCategoryModel;

    public SubCategoryModel getSubCategoryModel() {
        return subCategoryModel;
    }

    public void setSubCategoryModel(SubCategoryModel subCategoryModel) {
        this.subCategoryModel = subCategoryModel;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationFor() {
        return notificationFor;
    }

    public void setNotificationFor(String notificationFor) {
        this.notificationFor = notificationFor;
    }
}
