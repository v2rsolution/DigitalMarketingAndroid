package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.ResultCardAdapter;
import com.wscubetech.seovideotutorials.custom.CustomFont;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.InterviewModel;
import com.wscubetech.seovideotutorials.model.QuizModel;
import com.wscubetech.seovideotutorials.model.ResultCardModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.model.TestPaperModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;
import com.wscubetech.seovideotutorials.utils.MyValidations;
import com.wscubetech.seovideotutorials.utils.NoRecordFound;
import com.wscubetech.seovideotutorials.utils.ValidationsListeners;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader;
    DialogMsg dialogMsg;

    ArrayList<QuizModel> arrayQuizModel = new ArrayList<>();
    //TestPaperModel paperModel;
    SubCategoryModel subCategoryModel;

    ArrayList<ResultCardModel> arrayResultCardModel = new ArrayList<>();
    RecyclerView recyclerView;
    ResultCardAdapter adapter;

    TextView txtRetake, txtReview, txtEmailResult, txtPaperTitle;
    LinearLayout linCongrats;

    String jsonString = "";

    Boolean active;

    Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        dialogMsg = new DialogMsg(this);
        init();
        active = true;

        toolbarOperation();
        onClickListeners();
        getSetResultDetailsOfGamePlay();

        /*if (new ConnectionDetector(this).isConnectingToInternet())
            wait3SecThenShowDialog();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        txtPaperTitle = (TextView) findViewById(R.id.txtPaperTitle);
        txtRetake = (TextView) findViewById(R.id.txtRetake);
        txtReview = (TextView) findViewById(R.id.txtReview);
        txtEmailResult = (TextView) findViewById(R.id.txtEmailResult);

        linCongrats = (LinearLayout) findViewById(R.id.linCongrats);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText(getString(R.string.title_result));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public ResultCardModel getResultCardModel(String title, String score, int resBg, int resSrc, int resTxtColor) {
        ResultCardModel model = new ResultCardModel();
        model.setTitle(title);
        model.setScore(score);
        model.setResBg(resBg);
        model.setResSrc(resSrc);
        model.setResTextColor(resTxtColor);
        return model;
    }

    public void getSetResultDetailsOfGamePlay() {
        int noOfCorrect = 0, noOfIncorrect = 0, noOfQuestions = 0, noOfAttempted = 0;
        try {
            subCategoryModel=(SubCategoryModel)getIntent().getExtras().getSerializable("SubCategoryModel");
            txtPaperTitle.setText(subCategoryModel.getSubCatTitle());

            arrayQuizModel = (ArrayList<QuizModel>) getIntent().getExtras().getSerializable("ArrayQuizModel");
            noOfQuestions = arrayQuizModel.size();
            noOfAttempted = noOfQuestions;
            for (QuizModel model : arrayQuizModel) {
                if (model.getUserInput().equalsIgnoreCase("")) {
                    noOfAttempted -= 1;
                } else if (model.getUserInput().equalsIgnoreCase(model.getCorrectAnswerSerialNo())) {
                    noOfCorrect += 1;
                } else {
                    noOfIncorrect += 1;
                }
            }


            //prepare json string result
            try {
                JSONObject json = new JSONObject();
                json.put("Total_Questions", String.valueOf(noOfQuestions));
                json.put("Total_Correct", String.valueOf(noOfCorrect));
                json.put("Total_Incorrect",String.valueOf(noOfIncorrect));
                json.put("Attempted_Questions",String.valueOf(noOfAttempted));
                json.put("Net_Score",String.valueOf(noOfCorrect));

                jsonString = json.toString();

                Log.d("Json",jsonString);
            } catch (Exception e) {

            }

            arrayResultCardModel.clear();
            arrayResultCardModel.add(getResultCardModel("Total Questions:", "" + noOfQuestions, R.drawable.bg_total_ques, R.drawable.ic_total_ques, R.color.result_total_question));
            arrayResultCardModel.add(getResultCardModel("Total Correct:", "" + noOfCorrect, R.drawable.bg_correct, R.drawable.ic_correct, R.color.result_correct));
            arrayResultCardModel.add(getResultCardModel("Total Incorrect:", "" + noOfIncorrect, R.drawable.bg_incorrect, R.drawable.ic_incorrect, R.color.result_incorrect));
            arrayResultCardModel.add(getResultCardModel("Attempted Questions:", "" + noOfAttempted, R.drawable.bg_attempted_ques, R.drawable.ic_attempted_ques, R.color.result_attempt));
            arrayResultCardModel.add(getResultCardModel("Net Score:", "" + noOfCorrect, R.drawable.bg_net_score, R.drawable.ic_netscore, R.color.result_net_score));

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_overshoot);
            linCongrats.startAnimation(animation);

            adapter = new ResultCardAdapter(this, arrayResultCardModel);
            ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
            recyclerView.setAdapter(animationAdapter);

        } catch (Exception e) {

        }
    }

    public void onClickListeners() {
        txtRetake.setOnClickListener(this);
        txtReview.setOnClickListener(this);
        txtEmailResult.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.txtRetake:
                finish();
                break;
            case R.id.txtReview:
                intent = new Intent(this, ReviewActivity.class);
                intent.putExtra("ArrayQuizModel", arrayQuizModel);
                startActivity(intent);
                break;
            case R.id.txtEmailResult:
                showDialogEmail();
                break;
        }
    }

    public void wait3SecThenShowDialog() {
        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (this) {
                    try {
                        wait(3000);
                    } catch (Exception e) {

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialogEmail();
                        }
                    });
                }
            }
        };
        th.start();
    }

    public void showDialogEmail() {
        if (active) {
            final Dialog dialog = new MyDialog(this).getMyDialog(R.layout.dialog_email);
            dialog.setCancelable(true);
            final TextInputLayout inpEmail;
            final EditText etEmail;
            TextView txtSend, txtSkip;
            LinearLayout linParent;
            inpEmail = (TextInputLayout) dialog.findViewById(R.id.inpEmail);
            etEmail = (EditText) dialog.findViewById(R.id.etEmail);
            txtSend = (TextView) dialog.findViewById(R.id.txtSend);
            txtSkip = (TextView) dialog.findViewById(R.id.txtSkip);
            linParent = (LinearLayout) dialog.findViewById(R.id.linParent);

            final MyValidations validations = new MyValidations(this);

            final GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "EmailPrefs");
            if (prefs.getData("EmailSaved").trim().length() > 1) {
                etEmail.setText(prefs.getData("EmailSaved").trim());
                etEmail.setSelection(etEmail.getText().toString().trim().length());
            }

            txtSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            txtSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = etEmail.getText().toString().trim();
                    if (!validations.checkEmail(email)) {
                        inputOperationError(inpEmail, etEmail, "Please enter a valid email");
                    } else {
                        if (new ConnectionDetector(ResultActivity.this).isConnectingToInternet()) {
                            dialog.dismiss();
                            prefs.putData("EmailSaved", email);

                            progressDialog = new MyProgressDialog(ResultActivity.this).getDialog();
                            progressDialog.show();

                            sendResultToEmailOkHttp(email);
                        } else {
                            dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                        }
                    }


                }
            });

            inpEmail.setTypeface(CustomFont.setFontRegular(getAssets()));
            etEmail.setTypeface(CustomFont.setFontRegular(getAssets()));

            ValidationsListeners listeners = new ValidationsListeners(this);
            etEmail.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpEmail));

            dialog.show();
        }
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    public void sendResultToEmailOkHttp(String email) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.sendResultToEmail).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_EMAIL_ID, email);
        urlBuilder.addQueryParameter(Constants.KEY_PAPER_TITLE, subCategoryModel.getSubCatTitle());
        urlBuilder.addQueryParameter(Constants.KEY_RESULT, jsonString);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse(getString(R.string.networkError));
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                if (res.isSuccessful()) {
                    String response = Html.fromHtml(res.body().string()).toString();
                    Log.v("ResponsePostSuccess", response);
                    handleResponse(response);
                }
            }
        });
    }

    public void handleResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (response.equalsIgnoreCase(getString(R.string.networkError))) {
                        if (active) {
                            progressDialog.dismiss();
                            dialogMsg.showNetworkErrorDialog(response);
                        }
                    } else {
                        try {
                            Log.v("Response", "" + response);
                            if (active) {
                                progressDialog.dismiss();
                                dialogMsg.showSuccessOrLoadingDialog();
                            }
                        } catch (Exception e) {
                            Log.v("ParsingException", "" + e);
                        }
                    }

                } catch (Exception e) {

                }
            }
        });
    }
}
