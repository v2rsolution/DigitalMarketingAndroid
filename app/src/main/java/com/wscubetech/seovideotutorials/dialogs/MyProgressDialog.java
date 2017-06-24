package com.wscubetech.seovideotutorials.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.AnswerListActivity;
import com.wscubetech.seovideotutorials.activities.PostQuestionActivity;
import com.wscubetech.seovideotutorials.activities.QuestionListActivity;
import com.wscubetech.seovideotutorials.activities.VideoTutorialsTabActivity;
import com.wscubetech.seovideotutorials.custom.CustomFont;


/**
 * Created by wscubetech on 7/5/16.
 */
public class MyProgressDialog {

    Activity act;
    Dialog dialog;
    ProgressWheel progressWheel;

    public MyProgressDialog(Activity act) {
        this.act = act;
        dialog = new MyDialog(act).getMyDialog(R.layout.dialog_please_wait);
        progressWheel = (ProgressWheel) dialog.findViewById(R.id.progressBar);
        dialog.setCancelable(false);

        if (act instanceof QuestionListActivity || act instanceof AnswerListActivity || act instanceof PostQuestionActivity) {
            progressWheel.setBarColor(ContextCompat.getColor(act, R.color.color_tile_4));
        } else if (act instanceof VideoTutorialsTabActivity) {
            progressWheel.setBarColor(ContextCompat.getColor(act, R.color.color_tile_0));
        } else {
            progressWheel.setBarColor(ContextCompat.getColor(act, R.color.colorPrimary));
        }
    }

    public Dialog getDialog() {
        return dialog;
    }
}
