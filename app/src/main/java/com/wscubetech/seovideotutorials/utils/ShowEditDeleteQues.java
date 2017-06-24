package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.PostQuestionActivity;
import com.wscubetech.seovideotutorials.activities.QuestionListActivity;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 22/4/17.
 */

public class ShowEditDeleteQues {

    Activity act;
    QuestionListModel quesModel;
    Dialog progress;
    DialogMsg dialogMsg;

    public ShowEditDeleteQues(Activity act, QuestionListModel quesModel) {
        this.act = act;
        this.quesModel = quesModel;
        progress = new MyProgressDialog(act).getDialog();
        dialogMsg = new DialogMsg(act);
    }

    public void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(act, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
        ForceShowPopUpIcon.forceShowNow(popup);
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.itemEdit:
                    Intent intent = act.getIntent();
                    intent.setClass(act,PostQuestionActivity.class);
                    intent.putExtra("QuesModel", quesModel);
                    act.startActivity(intent);
                    break;
                case R.id.itemDelete:
                    okHttpDeleteQuestion();
                    break;
            }
            return false;
        }
    }

    private void okHttpDeleteQuestion() {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("question_id", quesModel.getQuesId()));
        OkHttpCalls calls = new OkHttpCalls(Urls.DELETE_QUESTION, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponseDelete(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("DeleteAns", response.body().string());
                handleResponseDelete(false);
            }
        });
    }

    private void handleResponseDelete(final boolean failed) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress.isShowing())
                    progress.dismiss();
                if (failed) {
                    dialogMsg.showNetworkErrorDialog(act.getString(R.string.networkError));
                } else {
                    dialogMsg.showSuccessDialog("Question successfully deleted", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogMsg.getDialog().dismiss();
                            if (QuestionListActivity.activity != null)
                                QuestionListActivity.activity.finish();

                            Intent intent = act.getIntent();
                            intent.setClass(act, QuestionListActivity.class);
                            act.startActivity(intent);
                            act.finish();
                        }
                    });
                }
            }
        });
    }
}
