package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.LoginActivity;
import com.wscubetech.seovideotutorials.activities.QuestionListActivity;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;

/**
 * Created by wscubetech on 12/4/17.
 */

public class EssentialLogin {

    Activity act;

    public EssentialLogin(Activity act){
        this.act=act;
    }

    public void showQuesAnsPostDialog(){
        final Dialog dialog = new MyDialog(act).getMyDialog(R.layout.dialog_ques_ans_coming_soon);
        TextView txtLogin, txtCancel;
        txtLogin = (TextView) dialog.findViewById(R.id.txtLogin);
        txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(act, LoginActivity.class);
                act.startActivity(intent);
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
