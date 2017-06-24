package com.wscubetech.seovideotutorials.user_model;

import java.io.Serializable;

/**
 * Created by wscubetech on 28/3/17.
 */

public class UserModel implements Serializable {
    String userId = "", userName = "", userEmail = "", userPassword = "", userImage = "";
    boolean googleLogIn = false;
    boolean notify=true;

    public UserModel() {

    }

    public UserModel(String userName, String userEmail, String userPass, boolean googleLogIn,boolean notify) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPass;
        this.googleLogIn = googleLogIn;
        this.notify=notify;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isPasswordBlank() {
        return userPassword.trim().equals("");
    }

    public boolean isGoogleLogIn() {
        return googleLogIn;
    }

    public void setGoogleLogIn(boolean googleLogIn) {
        this.googleLogIn = googleLogIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
