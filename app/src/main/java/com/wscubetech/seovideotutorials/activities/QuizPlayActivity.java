/*Designed and Developed by V2R Solution*/

package com.wscubetech.seovideotutorials.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.TestListAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.QuizModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.model.TestPaperModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.NoRecordFound;
import com.wscubetech.seovideotutorials.utils.OfflineResponse;

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

public class QuizPlayActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader;

    //Question Part
    TextView txtQuesTitle, txtQuesSerialNo, txtTimer;

    //Options Part
    TextView txtOption1, txtOption2, txtOption3, txtOption4, txtOption5;
    ImageView imgOption1, imgOption2, imgOption3, imgOption4, imgOption5;
    LinearLayout linOption1, linOption2, linOption3, linOption4, linOption5;

    CardView card3, card4, card5;

    public long TIMER_TOTAL_DURATION = 1000 * 60 * 10; //10 min
    public static final long INTERVAL = 1000; //1 sec
    short min = 0, sec = 0;

    RelativeLayout relInstructions, relQuizPlay;
    LinearLayout linInstructionCards;
    TextView txtStart, txtTotalQuestions, txtDuration;

    ArrayList<QuizModel> arrayQuizModel = new ArrayList<>();

    DialogMsg dialogMsg;
    ProgressWheel progressWheel;

    Boolean active;
    String response = "";

    TextView txtPrevious, txtNext;

    //TestPaperModel paperModel;
    SubCategoryModel subCategoryModel;
    int currentIndex = 0;
    String userInput = "";

    Animation animationSlideDownFast;

    int result = 0;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NumberFormatException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_play);

        dialogMsg = new DialogMsg(this);

        init();

        active = true;

        onClickListeners();

        currentIndex = 0;
        /*if (comingFromRetakeQuiz()) {

            paperModel = (TestPaperModel) getIntent().getExtras().getSerializable("TestPaperModel");
            min = Short.parseShort(paperModel.getPaperDuration());
            result = 1;
            TIMER_TOTAL_DURATION = 1000 * 60 * min;

            relInstructions.setVisibility(View.VISIBLE);
            linInstructionCards.startAnimation(AnimationUtils.loadAnimation(QuizPlayActivity.this, R.anim.slide_down));
            animationSlideDownFast = AnimationUtils.loadAnimation(QuizPlayActivity.this, R.anim.slide_down_fast);
            txtTotalQuestions.setText("" + arrayQuizModel.size());
            txtDuration.setText(paperModel.getPaperDuration());

        } else {*/

        try {
            arrayQuizModel = new ArrayList<>();
            subCategoryModel=(SubCategoryModel)getIntent().getExtras().getSerializable("SubCategoryModel");
            //paperModel = (TestPaperModel) getIntent().getExtras().getSerializable("TestPaperModel");
            min = Short.parseShort(subCategoryModel.getSubCatQuizTime());
            TIMER_TOTAL_DURATION = 1000 * 60 * min;
        }catch (Exception e){
            Log.v("Exception: ",""+e);
        }

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            viewQuizQuestionsOkHttp();
        } else {
            getOfflineData();
        }


        toolbarOperation();

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

    @Override
    protected void onDestroy() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        super.onDestroy();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        progressWheel = (ProgressWheel) findViewById(R.id.progressBar);

        relInstructions = (RelativeLayout) findViewById(R.id.relInstructions);
        relQuizPlay = (RelativeLayout) findViewById(R.id.relQuizPlay);

        linInstructionCards = (LinearLayout) findViewById(R.id.linInstructionCards);
        txtStart = (TextView) findViewById(R.id.txtStart);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtTotalQuestions = (TextView) findViewById(R.id.txtTotalQuestions);
        txtPrevious = (TextView) findViewById(R.id.txtPrevious);
        txtNext = (TextView) findViewById(R.id.txtNext);

        txtQuesSerialNo = (TextView) findViewById(R.id.txtQuesSerialNo);
        txtQuesTitle = (TextView) findViewById(R.id.txtQuesTitle);
        txtTimer = (TextView) findViewById(R.id.txtTimer);

        txtOption1 = (TextView) findViewById(R.id.txtOption1);
        txtOption2 = (TextView) findViewById(R.id.txtOption2);
        txtOption3 = (TextView) findViewById(R.id.txtOption3);
        txtOption4 = (TextView) findViewById(R.id.txtOption4);
        txtOption5 = (TextView) findViewById(R.id.txtOption5);

        imgOption1 = (ImageView) findViewById(R.id.imgOption1);
        imgOption2 = (ImageView) findViewById(R.id.imgOption2);
        imgOption3 = (ImageView) findViewById(R.id.imgOption3);
        imgOption4 = (ImageView) findViewById(R.id.imgOption4);
        imgOption5 = (ImageView) findViewById(R.id.imgOption5);

        linOption1 = (LinearLayout) findViewById(R.id.linOption1);
        linOption2 = (LinearLayout) findViewById(R.id.linOption2);
        linOption3 = (LinearLayout) findViewById(R.id.linOption3);
        linOption4 = (LinearLayout) findViewById(R.id.linOption4);
        linOption5 = (LinearLayout) findViewById(R.id.linOption5);

        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.color_tile_2));
        txtHeader.setText(subCategoryModel.getSubCatTitle());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onClickListeners() {
        linOption1.setOnClickListener(this);
        linOption2.setOnClickListener(this);
        linOption3.setOnClickListener(this);
        linOption4.setOnClickListener(this);
        linOption5.setOnClickListener(this);

        txtStart.setOnClickListener(this);
        txtPrevious.setOnClickListener(this);
        txtNext.setOnClickListener(this);
    }

    /*public boolean comingFromRetakeQuiz() {
        try {
            arrayQuizModel = (ArrayList<QuizModel>) getIntent().getExtras().getSerializable("ArrayQuizModel");
            if (arrayQuizModel.size() > 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }*/

    public void clearAllClickUiDisplay() {
        linOption1.setBackgroundResource(android.R.color.white);
        linOption2.setBackgroundResource(android.R.color.white);
        linOption3.setBackgroundResource(android.R.color.white);
        linOption4.setBackgroundResource(android.R.color.white);
        linOption5.setBackgroundResource(android.R.color.white);

        imgOption1.setBackgroundResource(R.color.colorOptionGrey);
        imgOption2.setBackgroundResource(R.color.colorOptionGrey);
        imgOption3.setBackgroundResource(R.color.colorOptionGrey);
        imgOption4.setBackgroundResource(R.color.colorOptionGrey);
        imgOption5.setBackgroundResource(R.color.colorOptionGrey);

        imgOption1.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption2.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption3.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption4.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption5.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "QuizQuestionList");
        response = offlineResponse.getResponse(OfflineResponse.QUIZ_QUES + subCategoryModel.getSubCatId());
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewQuizQuestionsOkHttp() {

        progressWheel.setVisibility(View.VISIBLE);
        relInstructions.setVisibility(View.GONE);

        HttpUrl.Builder builder = HttpUrl.parse(Urls.viewQuizTestQuestionsNew).newBuilder();
        builder.addQueryParameter(Constants.KEY_SUB_CAT_ID, subCategoryModel.getSubCatId());
        String url = builder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                Toast.makeText(getApplicationContext(),getString(R.string.networkError),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                if (res.isSuccessful()) {
                    response = Html.fromHtml(res.body().string()).toString();
                    Log.v("ResponsePostSuccess", response);
                    OfflineResponse offlineResponse = new OfflineResponse(QuizPlayActivity.this, "QuizQuestionList");
                    offlineResponse.setResponse(OfflineResponse.QUIZ_QUES + subCategoryModel.getSubCatId(), response);
                    handleResponse();
                }
            }
        });

    }

    public void handleResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (response.equalsIgnoreCase(getString(R.string.networkError))) {
                        if (active)
                            dialogMsg.showNetworkErrorDialog(response);
                    } else {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getInt("response") == 1) {
                                result = 1;
                                JSONArray array = json.getJSONArray("message");
                                if (array.length() > 0)
                                    arrayQuizModel.clear();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    QuizModel model = new QuizModel();
                                    model.setId(obj.getString(Constants.KEY_QUESTION_ID));
                                    model.setQues(obj.getString(Constants.KEY_QUESTION_TITLE));
                                    model.setOption1(obj.getString(Constants.KEY_QUESTION_OPTION_1));
                                    model.setOption2(obj.getString(Constants.KEY_QUESTION_OPTION_2));
                                    model.setOption3(obj.getString(Constants.KEY_QUESTION_OPTION_3));
                                    model.setOption4(obj.getString(Constants.KEY_QUESTION_OPTION_4));
                                    model.setOption5(obj.getString(Constants.KEY_QUESTION_OPTION_5));
                                    model.setCorrectAnswerSerialNo(obj.getString(Constants.KEY_CORRECT_ANS));
                                    model.setCodeFlag(Integer.parseInt(obj.getString(Constants.KEY_CODE_FLAG)));
                                    arrayQuizModel.add(model);
                                }

                                if (active) {
                                    relInstructions.setVisibility(View.VISIBLE);
                                    linInstructionCards.startAnimation(AnimationUtils.loadAnimation(QuizPlayActivity.this, R.anim.slide_down));
                                    animationSlideDownFast = AnimationUtils.loadAnimation(QuizPlayActivity.this, R.anim.slide_down_fast);

                                    txtTotalQuestions.setText("" + arrayQuizModel.size());
                                    txtDuration.setText(subCategoryModel.getSubCatQuizTime());

                                }
                            } else {
                                result = 0;
                                if (active) {
                                    NoRecordFound noRecordFound = new NoRecordFound(QuizPlayActivity.this);
                                    noRecordFound.showNoRecordFound("Oops!\nNo quiz found to entertain you");
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(QuizPlayActivity.this,"Parsing error occurred",Toast.LENGTH_LONG).show();
                            Log.v("ParsingException", "" + e);
                        }
                    }

                    if (active)
                        progressWheel.setVisibility(View.GONE);

                } catch (Exception e) {

                }
            }
        });
    }

    public void viewQuestionAndThenOption() {
        userInput = "";
        enableDisableOptionsAndKeepValues(null);
        final QuizModel quizModel = arrayQuizModel.get(currentIndex);
        txtPrevious.setVisibility(currentIndex == 0 ? View.INVISIBLE : View.VISIBLE);
        txtNext.setText(currentIndex == arrayQuizModel.size() - 1 ? "Submit" : "Next");
        txtNext.setBackgroundResource(currentIndex==arrayQuizModel.size()-1?R.drawable.btn_primary_selector_rectangle_curved:R.drawable.btn_selector_teal_rectangle_curved);

        //If number of options are different
        if (quizModel.getOption5().trim().length() < 1) {
            linOption5.setVisibility(View.GONE);
            card5.setVisibility(View.GONE);
        }

        if (quizModel.getOption4().trim().length() < 1) {
            linOption4.setVisibility(View.GONE);
            card4.setVisibility(View.GONE);
        }

        if (quizModel.getOption3().trim().length() < 1) {
            card3.setVisibility(View.GONE);
            linOption3.setVisibility(View.GONE);
        }

        relQuizPlay.setVisibility(View.VISIBLE);
        txtQuesTitle.setGravity(quizModel.getCodeFlag() == 0 ? Gravity.CENTER : Gravity.LEFT);
        txtQuesTitle.setText(quizModel.getQues());
        txtQuesTitle.startAnimation(animationSlideDownFast);

        int currentQuesIndex = currentIndex + 1;
        txtQuesSerialNo.setText(currentQuesIndex + " / " + arrayQuizModel.size());

        final Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (this) {
                    try {
                        wait(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enableDisableOptionsAndKeepValues(quizModel);
                                if (currentIndex < arrayQuizModel.size())
                                    maintainUserInput();
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        };
        thread.start();

    }

    public void enableDisableOptionsAndKeepValues(QuizModel quizModel) {

        if (quizModel == null) {
            clearAllClickUiDisplay();

            txtOption1.setText("");
            txtOption2.setText("");
            txtOption3.setText("");
            txtOption4.setText("");
            txtOption5.setText("");

            txtOption1.setEnabled(false);
            txtOption2.setEnabled(false);
            txtOption3.setEnabled(false);
            txtOption4.setEnabled(false);
            txtOption5.setEnabled(false);

            linOption1.setVisibility(View.VISIBLE);
            linOption2.setVisibility(View.VISIBLE);
            linOption3.setVisibility(View.VISIBLE);
            linOption4.setVisibility(View.VISIBLE);
            linOption5.setVisibility(View.VISIBLE);

            card3.setVisibility(View.VISIBLE);
            card4.setVisibility(View.VISIBLE);
            card5.setVisibility(View.VISIBLE);

        } else {
            txtOption1.setEnabled(true);
            txtOption2.setEnabled(true);
            txtOption3.setEnabled(true);
            txtOption4.setEnabled(true);
            txtOption5.setEnabled(true);

            txtOption1.setText(quizModel.getOption1());
            txtOption2.setText(quizModel.getOption2());
            txtOption3.setText(quizModel.getOption3());
            txtOption4.setText(quizModel.getOption4());
            txtOption5.setText(quizModel.getOption5());
        }
    }

    @Override
    public void onBackPressed() {
        if (result == 1) {
            dialogMsg = new DialogMsg(this);
            dialogMsg.showSureQuitDialog("Are you sure you want to quit the quiz?", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogMsg.getDialog().dismiss();
                    QuizPlayActivity.this.finish();
                }
            });
        } else {
            super.onBackPressed();
        }
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
        switch (view.getId()) {
            case R.id.linOption1:
                clearAllClickUiDisplay();
                if (userInput.equalsIgnoreCase("1")) {
                    userInput = "";
                } else {
                    linOption1.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                    imgOption1.setBackgroundResource(R.color.colorOptionYellow);
                    imgOption1.setImageResource(R.drawable.ic_check_box_black_24dp);
                    userInput = "1";
                }
                break;
            case R.id.linOption2:
                clearAllClickUiDisplay();
                if (userInput.equalsIgnoreCase("2")) {
                    userInput = "";
                } else {
                    linOption2.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                    imgOption2.setBackgroundResource(R.color.colorOptionYellow);
                    imgOption2.setImageResource(R.drawable.ic_check_box_black_24dp);
                    userInput = "2";
                }
                break;
            case R.id.linOption3:
                clearAllClickUiDisplay();
                if (userInput.equalsIgnoreCase("3")) {
                    userInput = "";
                } else {
                    linOption3.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                    imgOption3.setBackgroundResource(R.color.colorOptionYellow);
                    imgOption3.setImageResource(R.drawable.ic_check_box_black_24dp);
                    userInput = "3";
                }
                break;
            case R.id.linOption4:
                clearAllClickUiDisplay();
                if (userInput.equalsIgnoreCase("4")) {
                    userInput = "";
                } else {
                    linOption4.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                    imgOption4.setBackgroundResource(R.color.colorOptionYellow);
                    imgOption4.setImageResource(R.drawable.ic_check_box_black_24dp);
                    userInput = "4";
                }
                break;
            case R.id.linOption5:
                clearAllClickUiDisplay();
                if (userInput.equalsIgnoreCase("5")) {
                    userInput = "";
                } else {
                    linOption5.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                    imgOption5.setBackgroundResource(R.color.colorOptionYellow);
                    imgOption5.setImageResource(R.drawable.ic_check_box_black_24dp);
                    userInput = "5";
                }
                break;

            case R.id.txtStart:
                relInstructions.setVisibility(View.GONE);
                runCountDownTimer();
                viewQuestionAndThenOption();
                break;

            case R.id.txtNext:
                if (currentIndex < arrayQuizModel.size()) {
                    setUserInputInQuizArray(userInput);
                    currentIndex += 1;
                    if (currentIndex < arrayQuizModel.size())
                        viewQuestionAndThenOption();
                    else {
                        goToResultsActivity();
                    }
                }
                break;

            case R.id.txtPrevious:
                if (currentIndex > 0) {
                    currentIndex -= 1;
                    viewQuestionAndThenOption();
                }
                break;
        }
    }

    public void maintainUserInput() {
        clearAllClickUiDisplay();
        QuizModel model = arrayQuizModel.get(currentIndex);
        userInput = model.getUserInput();
        switch (userInput) {
            case "1":
                linOption1.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                imgOption1.setBackgroundResource(R.color.colorOptionYellow);
                imgOption1.setImageResource(R.drawable.ic_check_box_black_24dp);
                break;
            case "2":
                linOption2.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                imgOption2.setBackgroundResource(R.color.colorOptionYellow);
                imgOption2.setImageResource(R.drawable.ic_check_box_black_24dp);
                break;
            case "3":
                linOption3.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                imgOption3.setBackgroundResource(R.color.colorOptionYellow);
                imgOption3.setImageResource(R.drawable.ic_check_box_black_24dp);
                break;
            case "4":
                linOption4.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                imgOption4.setBackgroundResource(R.color.colorOptionYellow);
                imgOption4.setImageResource(R.drawable.ic_check_box_black_24dp);
                break;
            case "5":
                linOption5.setBackgroundResource(R.drawable.bg_option_box_yellow_boundary);
                imgOption5.setBackgroundResource(R.color.colorOptionYellow);
                imgOption5.setImageResource(R.drawable.ic_check_box_black_24dp);
                break;
        }
    }

    public void setUserInputInQuizArray(String userInput) {
        QuizModel model = arrayQuizModel.get(currentIndex);
        model.setUserInput(userInput);
        model.setTimerValue(txtTimer.getText().toString().trim());
        Log.v("UserInput_", "QuestionIndex_" + currentIndex);
        arrayQuizModel.set(currentIndex, model);
    }

    public void runCountDownTimer() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (this) {
                    try {
                        wait(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countDownTimer = new CountDownTimer(TIMER_TOTAL_DURATION, INTERVAL) {
                                    @Override
                                    public void onTick(long l) {
                                        sec -= 1;
                                        if (sec < 0) {
                                            sec = 59;
                                            min -= 1;
                                        }

                                        String strSec = String.valueOf(sec);
                                        String strMin = String.valueOf(min);

                                        strSec = strSec.length() < 2 ? "0" + strSec : strSec;
                                        strMin = strMin.length() < 2 ? "0" + strMin : strMin;

                                        txtTimer.setText(strMin + ":" + strSec);

                                        if (strMin.equalsIgnoreCase("00") && strSec.equalsIgnoreCase("30")) {
                                            txtTimer.setTextColor(ContextCompat.getColor(QuizPlayActivity.this, R.color.colorOptionRed));
                                            txtTimer.startAnimation(AnimationUtils.loadAnimation(QuizPlayActivity.this, R.anim.zoom_in_from_right));
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        goToResultsActivity();
                                    }
                                };
                                countDownTimer.start();
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        };
        thread.start();

    }

    public void goToResultsActivity() {
        countDownTimer.cancel();
        Intent intent = new Intent(QuizPlayActivity.this, ResultActivity.class);
        intent.putExtra("ArrayQuizModel", arrayQuizModel);
        intent.putExtra("SubCategoryModel", subCategoryModel);
        startActivity(intent);
        finish();
    }
}
