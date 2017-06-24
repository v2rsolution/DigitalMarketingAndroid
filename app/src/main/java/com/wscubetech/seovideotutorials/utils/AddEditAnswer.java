package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.AnswerListActivity;
import com.wscubetech.seovideotutorials.adapters.QuesListAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 22/4/17.
 */

public class AddEditAnswer {

    Activity act;
    QuestionListModel answerModel; //for edit operation
    UserModel userModel;

    Dialog progress;
    ArrayList<QuestionListModel> arrayAnswerModel = new ArrayList<>();
    QuesListAdapter adapter;
    int position;

    public AddEditAnswer(Activity act, Dialog progress) {
        this.act = act;
        this.progress = progress;
        userModel = new UserDetailsPrefs(act).getUserModel();
    }

    public AddEditAnswer(Activity act, Dialog progress, ArrayList<QuestionListModel> arrayAnswerModel, int position, QuesListAdapter adapter) {
        this(act, progress);
        this.answerModel = arrayAnswerModel.get(position);
        this.arrayAnswerModel = arrayAnswerModel;
        this.adapter = adapter;
        this.position = position;
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    public void showDialogAddEdit(final QuestionListModel quesModel) {
        if (userModel.getUserId().trim().length() < 1) {
            EssentialLogin essentialLogin = new EssentialLogin(act);
            essentialLogin.showQuesAnsPostDialog();
            return;
        }
        final Dialog dialogAnswer = new MyDialog(act).getMyDialog(R.layout.dialog_post_answer);
        TextView txtQuesTitle = (TextView) dialogAnswer.findViewById(R.id.txtQuesTitle);
        final TextInputLayout inpAnswer = (TextInputLayout) dialogAnswer.findViewById(R.id.inpAnswer);
        final EditText etAnswer = (EditText) dialogAnswer.findViewById(R.id.etAnswer);
        TextView txtPostAnswer = (TextView) dialogAnswer.findViewById(R.id.txtPostAnswer);

        if (answerModel != null) {
            etAnswer.setText(answerModel.getQuesTitle());
            etAnswer.setSelection(etAnswer.getText().toString().trim().length());
            txtPostAnswer.setText("Update");
        } else {
            txtPostAnswer.setText("Post");
        }

        txtPostAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strAns = etAnswer.getText().toString().trim();
                if (strAns.length() < 1) {
                    inputOperationError(inpAnswer, etAnswer, "Please input an answer to post");
                    return;
                }

                if (!new ConnectionDetector(act).isConnectingToInternet()) {
                    Toast.makeText(act, act.getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                    return;
                }

                dialogAnswer.dismiss();
                okHttpPostAnswer(strAns, quesModel);

            }
        });

        ValidationsListeners listeners = new ValidationsListeners(act);
        etAnswer.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpAnswer));
        txtQuesTitle.setText(quesModel.getQuesTitle());

        dialogAnswer.show();
    }

    private void okHttpPostAnswer(final String strAnswer, final QuestionListModel quesModel) {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("ques_answer", strAnswer));
        arrayKeyValueModel.add(new KeyValueModel("ans_user_id", userModel.getUserId()));
        arrayKeyValueModel.add(new KeyValueModel("ans_ques_id", quesModel.getQuesId()));

        String url;
        if (answerModel == null) {
            url = Urls.ADD_ANSWER;
        } else {
            url = Urls.EDIT_ANSWER;
            arrayKeyValueModel.add(new KeyValueModel("answer_id", answerModel.getQuesId()));
        }

        Log.v("UrlAddEditAns",url);

        OkHttpCalls calls = new OkHttpCalls(url, arrayKeyValueModel);
        calls.initiateCallPost(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (act instanceof AnswerListActivity && answerModel == null) {
                    AnswerListActivity activity = (AnswerListActivity) act;
                    activity.handleResponsePostAnswer(true, "");
                } else if (act instanceof AnswerListActivity && answerModel != null) {
                    handleResponse(true, "");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (act instanceof AnswerListActivity && answerModel == null) {
                    AnswerListActivity activity = (AnswerListActivity) act;
                    activity.handleResponsePostAnswer(false, response.body().string());
                } else if (act instanceof AnswerListActivity && answerModel != null) {
                    answerModel.setQuesTitle(strAnswer);
                    handleResponse(false, response.body().string());
                }
            }
        });
    }

    private void handleResponse(final boolean failed, final String response) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogMsg dialogMsg = new DialogMsg(act);
                progress.dismiss();
                if (failed) {
                    dialogMsg.showNetworkErrorDialog(act.getString(R.string.networkError));
                } else {
                    try {
                        Log.v("ResponseAnsEdit", response);
                        JSONObject json = new JSONObject(response);
                        if (json.getInt("status") == 1) {
                            arrayAnswerModel.set(position, answerModel);
                            adapter.notifyItemChanged(position);
                        } else {
                            dialogMsg.showGeneralErrorDialog("Some error occurred");
                        }
                    } catch (Exception e) {
                        dialogMsg.showGeneralErrorDialog("Parsing error");
                    }

                }
            }
        });
    }


}
