package com.wscubetech.seovideotutorials.user_model;

import android.app.Activity;
import android.content.Context;

import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;

/**
 * Created by wscubetech on 28/3/17.
 */

public class UserDetailsPrefs {

    Context act;
    GetSetSharedPrefs prefs;

    public UserDetailsPrefs(Context act) {
        this.act = act;
        prefs = new GetSetSharedPrefs(act, "UserDetails");
    }

    public UserModel getUserModel() {
        UserModel model = new UserModel();
        model.setUserId(prefs.getData("USER_ID").trim());
        model.setUserName(prefs.getData("USER_NAME").trim());
        model.setUserEmail(prefs.getData("USER_EMAIL").trim());
        model.setUserPassword(prefs.getData("USER_PASS").trim());
        model.setUserImage(prefs.getData("USER_IMAGE").trim());
        model.setGoogleLogIn(prefs.getData("USER_GOOGLE").equals("1"));
        model.setNotify(prefs.getData("USER_NOTIFY").equals("1"));
        return model;
    }

    public void setUserModel(UserModel model) {
        prefs.putData("USER_ID", model.getUserId().trim());
        prefs.putData("USER_NAME", model.getUserName().trim());
        prefs.putData("USER_EMAIL", model.getUserEmail().trim());
        prefs.putData("USER_PASS", model.getUserPassword().trim());
        prefs.putData("USER_IMAGE", model.getUserImage().trim());
        prefs.putData("USER_GOOGLE", model.isGoogleLogIn() ? "1" : "0");
        prefs.putData("USER_NOTIFY", model.isNotify() ? "1" : "0");
    }
}
