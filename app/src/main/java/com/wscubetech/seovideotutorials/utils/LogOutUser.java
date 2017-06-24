package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;

/**
 * Created by wscubetech on 11/4/17.
 */

public class LogOutUser {
    Activity act;
    UserModel userModel;

    public LogOutUser(Activity act){
        this.act=act;
        userModel=new UserDetailsPrefs(act).getUserModel();
    }

    public void sureLogOutDialog() {
        final DialogMsg dialogMsg = new DialogMsg(act);
        dialogMsg.showSureQuitDialog("Are you sure you want to log out?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMsg.getDialog().dismiss();
                signOutAndGoToHome();
            }
        });
    }

    private void signOutAndGoToHome() {
        userModel = new UserModel();
        UserDetailsPrefs prefs = new UserDetailsPrefs(act);
        prefs.setUserModel(userModel);
        if (HomeActivity.activity != null)
            HomeActivity.activity.finish();

        Toast.makeText(act, "Logged out successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(act, HomeActivity.class);
        act.startActivity(intent);
        act.finish();

    }

}
