/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.QuesListAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.AdClass;
import com.wscubetech.seovideotutorials.utils.AddEditAnswer;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.FirstTimeTargetView;
import com.wscubetech.seovideotutorials.utils.LikeDislikeQuesAns;
import com.wscubetech.seovideotutorials.utils.LoadUserImage;
import com.wscubetech.seovideotutorials.utils.NoRecordFound;
import com.wscubetech.seovideotutorials.utils.OfflineResponse;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.ShowEditDeleteQues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnswerListActivity extends AppCompatActivity implements View.OnClickListener, ShineButton.OnCheckedChangeListener, View.OnLongClickListener {

    QuestionListModel quesModel;

    RelativeLayout relMain;
    Toolbar toolbar;
    TextView txtHeader;
    TextView txtQuesTitle, txtQuesBy, txtDate, txtAnswerCount, txtLikes, txtDislikes,txtNumViews;
    RecyclerView recyclerView;
    AutoLabelUI tagView;
    ImageView imgUser, imgMore;

    ProgressWheel progressWheel;
    ProgressWheel progressBarBottom;

    Boolean active;
    String response = "";
    int pageNo = 1, totalPages = 1;
    Boolean isLoading = true;

    ArrayList<QuestionListModel> arrayAnswerModel = new ArrayList<>();
    QuesListAdapter adapter;

    AdClass ad;

    UserModel userModel;

    DialogMsg dialogMsg;
    Dialog progress;

    LinearLayoutManager layoutManager;

    FloatingActionButton fabAdd;

    ShineButton btnLike, btnDislike;

    public static AnswerListActivity activity;

    NoRecordFound noRecordFound;


    LikeDislikeQuesAns likeDislikeQuesAns = new LikeDislikeQuesAns(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        init();
        active = true;
        toolbarOperation();
        activity = this;
        noRecordFound = new NoRecordFound(this);
        noRecordFound.hideUi();
        userModel = new UserDetailsPrefs(this).getUserModel();
        getQuesDetail();

        dialogMsg = new DialogMsg(this);
        progress = new MyProgressDialog(this).getDialog();

        adapter = new QuesListAdapter(this, arrayAnswerModel, "AnswerList", quesModel);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                Log.v("Count", visibleItemCount + "\n" + totalItemCount + "\n" + pastVisibleItems);

                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    //Log.v("...", "Last Item Wow !");
                    //Do pagination.. i.e. fetch new data
                    if (pageNo <= totalPages && isLoading) {
                        isLoading = false;
                        recyclerView.scrollToPosition(arrayAnswerModel.size() - 1);
                        progressBarBottom.setVisibility(View.VISIBLE);
                        if (new ConnectionDetector(AnswerListActivity.this).isConnectingToInternet()) {
                            okHttpViewAnswers();
                        } else {
                            getOfflineData();
                        }
                    }
                }

            }

        });

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            okHttpViewAnswers();
            okHttpViewQuesCountIncrement();
        } else {
            getOfflineData();
        }

        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }


        onClickListeners();
        firstTimeShow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ad.resumeAd();
    }

    @Override
    protected void onDestroy() {
        ad.destroyAd();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ad.pauseAd();
        super.onPause();
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

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        tagView = (AutoLabelUI) findViewById(R.id.tagView);
        txtQuesTitle = (TextView) findViewById(R.id.txtQuesTitle);
        txtQuesBy = (TextView) findViewById(R.id.txtQuesBy);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtAnswerCount = (TextView) findViewById(R.id.txtAnswerCount);
        txtLikes = (TextView) findViewById(R.id.txtLikes);
        txtDislikes = (TextView) findViewById(R.id.txtDislikes);
        txtNumViews=(TextView)findViewById(R.id.txtNumViews);
        relMain = (RelativeLayout) findViewById(R.id.relMain);
        imgMore = (ImageView) findViewById(R.id.imgMore);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnLike = (ShineButton) findViewById(R.id.btnLike);
        btnDislike = (ShineButton) findViewById(R.id.btnDislike);
        btnLike.init(this);
        btnDislike.init(this);

        progressWheel = (ProgressWheel) findViewById(R.id.progressBar);
        progressBarBottom = (ProgressWheel) findViewById(R.id.progressBarBottom);

        imgUser = (ImageView) findViewById(R.id.imgUser);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText("Answers");
        int colorTheme = ContextCompat.getColor(this, R.color.color_tile_4);
        toolbar.setBackgroundColor(colorTheme);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void firstTimeShow() {
        final FirstTimeTargetView firstTimeTargetView = new FirstTimeTargetView(this, "FirstTimeAnswerList");
        firstTimeTargetView.firstTimeDemonstration(fabAdd, "Post Answer", "Tap here to post your answer", 60, false, new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                firstTimeTargetView.firstTimeCompletion();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                onTargetClick(view);
            }
        });
    }


    private void getQuesDetail() {
        quesModel = (QuestionListModel) getIntent().getExtras().getSerializable("QuesModel");
        txtQuesTitle.setText(quesModel.getQuesTitle());
        txtQuesBy.setText("By " + quesModel.getUserModel().getUserName());
        txtDate.setText(quesModel.getQuesDate());
        txtAnswerCount.setText(quesModel.getAnsCount());
        txtLikes.setText(String.valueOf(quesModel.getTotalLikes()));
        txtDislikes.setText(String.valueOf(quesModel.getTotalDislikes()));
        txtNumViews.setText(String.valueOf(quesModel.getTotalViews()));

        if (quesModel.getUserModel().getUserImage().trim().length() > 1)
            new LoadUserImage().loadImageInImageView(this, Urls.imageUrl + quesModel.getUserModel().getUserImage(), imgUser);

        String arrayTags[] = quesModel.getTags().split(",");
        Log.v("Tags", quesModel.getTags());
        if (quesModel.getTags().trim().length() > 0) {
            tagView.setVisibility(View.VISIBLE);
            for (String tag : arrayTags) {
                tagView.addLabel(tag);
            }
        } else {
            tagView.setVisibility(View.GONE);
        }

        if (userModel.getUserId().trim().length() < 1) {
            btnLike.setEnabled(false);
            btnDislike.setEnabled(false);
        } else {
            btnLike.setEnabled(true);
            btnDislike.setEnabled(true);
            btnLike.setChecked(quesModel.getLiked() == 1);
            btnDislike.setChecked(quesModel.getLiked() == 2);
        }

        imgMore.setVisibility(userModel.getUserId().trim().equals(quesModel.getUserModel().getUserId().trim()) ? View.VISIBLE : View.GONE);
    }

    private void onClickListeners() {
        fabAdd.setOnClickListener(this);
        btnLike.setOnCheckStateChangeListener(this);
        btnDislike.setOnCheckStateChangeListener(this);
        imgMore.setOnClickListener(this);
        relMain.setOnLongClickListener(this);
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "AnswerList");
        this.response = offlineResponse.getResponse(OfflineResponse.ANS_LIST + quesModel.getQuesId() + "_" + pageNo);
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    private void okHttpViewAnswers() {
        noRecordFound.hideUi();

        if (pageNo == 1)
            progressWheel.setVisibility(View.VISIBLE);
        else
            progressBarBottom.setVisibility(View.VISIBLE);

        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("ans_ques_id", quesModel.getQuesId()));
        arrayKeyValueModel.add(new KeyValueModel("user_id", userModel.getUserId()));

        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_POSTED_ANSWERS, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(active)
                    Toast.makeText(getApplicationContext(),getString(R.string.networkError),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                response = Html.fromHtml(res.body().string()).toString();
                Log.v("ResponseAnswers", response);
                OfflineResponse offlineResponse = new OfflineResponse(AnswerListActivity.this, "AnswerList");
                offlineResponse.setResponse(OfflineResponse.ANS_LIST + quesModel.getQuesId() + "_" + pageNo, response);
                handleResponse();
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
                                totalPages = json.getInt("total_page");
                                JSONArray array = json.getJSONArray("message");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    QuestionListModel model = new QuestionListModel();
                                    model.setQuesId(obj.getString("answer_id"));
                                    model.setQuesTitle(obj.getString("ques_answer"));
                                    model.setQuesDate(obj.getString("ans_time"));
                                    //model.setTags(obj.getString("question_tags"));

                                    try {
                                        model.setTotalLikes(obj.getInt("total_likes"));
                                        model.setTotalDislikes(obj.getInt("total_dislikes"));
                                        model.setLikeCount("" + (model.getTotalLikes() - model.getTotalDislikes()));

                                        if (userModel.getUserId().trim().length() > 0) {
                                            model.setLiked(obj.getInt("liked"));
                                        }

                                    } catch (JSONException e) {
                                        Log.v("JsonLikesDislikes", "" + e);

                                    } catch (NumberFormatException e) {
                                        Log.v("NumberLikesDislikes", "" + e);
                                    }

                                    UserModel userModel = new UserModel();
                                    userModel.setUserId(obj.getString("seo_users_id"));
                                    userModel.setUserName(obj.getString("seo_users_name"));
                                    userModel.setUserEmail(obj.getString("seo_users_email"));
                                    userModel.setUserImage(obj.getString("seo_users_image"));

                                    model.setUserModel(userModel);

                                    arrayAnswerModel.add(model);

                                }

                                if (active) {
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                    pageNo += 1;
                                    if (pageNo <= totalPages)
                                        isLoading = true;

                                    //update answer count
                                    txtAnswerCount.setText("" + arrayAnswerModel.size());
                                }
                            } else {

                                if (active) {
                                    noRecordFound.showNoRecordFound("No answers posted yet for this question");
                                }

                            }
                        } catch (Exception e) {
                            Toast.makeText(AnswerListActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
                            Log.v("ParsingException", "" + e);
                        }
                    }

                    if (active) {
                        progressWheel.setVisibility(View.GONE);
                        progressBarBottom.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void okHttpViewQuesCountIncrement() {
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("ques_id", quesModel.getQuesId()));
        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_QUES_COUNT_INCREMENT, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("ResponseView", response.body().string());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            case R.id.fabAdd:
                AddEditAnswer addEditAnswer = new AddEditAnswer(this, progress);
                addEditAnswer.showDialogAddEdit(quesModel);
                break;
            case R.id.imgMore:
                onLongClick(relMain);
                break;
        }
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }


    public void handleResponsePostAnswer(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                Log.v("ResponsePostAns", response);
                if (failed) {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.networkError));
                } else {
                    try {
                        JSONObject json = new JSONObject(response);

                    } catch (Exception e) {

                    }
                    dialogMsg.showSuccessDialog("Your answer is posted successfully", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogMsg.getDialog().dismiss();

                            //get back to page no 1 and start loading
                            pageNo = 1;
                            totalPages = 1;
                            isLoading = true;
                            arrayAnswerModel.clear();
                            adapter.notifyDataSetChanged();
                            okHttpViewAnswers();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(View view, boolean checked) {
        switch (view.getId()) {
            case R.id.btnLike:
                if (userModel.getUserId().trim().length() < 1) {
                    Toast.makeText(this, "Please log in to like or dislike a question", Toast.LENGTH_LONG).show();
                    return;
                }
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    if (checked && btnDislike.isChecked()) {
                        updateDislikeCount(txtDislikes, false);
                        btnDislike.setChecked(false);
                    }
                    updateLikeCount(txtLikes, checked);

                    //"1" for ques like or dislike, not answer
                    likeDislikeQuesAns.okHttpLikeDislike(quesModel.getQuesId(), userModel.getUserId(), "1", checked ? "1" : "0");
                } else {
                    btnLike.setChecked(!checked);
                }
                break;
            case R.id.btnDislike:
                if (userModel.getUserId().trim().length() < 1) {
                    Toast.makeText(this, "Please log in to like or dislike a question", Toast.LENGTH_LONG).show();
                    return;
                }
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    if (checked && btnLike.isChecked()) {
                        updateLikeCount(txtLikes, false);
                        btnLike.setChecked(false);
                    }

                    updateDislikeCount(txtDislikes, checked);

                    //"1" for ques like or dislike, not answer
                    likeDislikeQuesAns.okHttpLikeDislike(quesModel.getQuesId(), userModel.getUserId(), "1", checked ? "2" : "0");

                } else {
                    btnDislike.setChecked(!checked);
                }
                break;
        }
    }

    private void updateLikeCount(TextView txt, boolean checked) {
        int totalLikes = quesModel.getTotalLikes();
        totalLikes = checked ? totalLikes + 1 : totalLikes - 1;
        quesModel.setTotalLikes(totalLikes);
        txt.setText(String.valueOf(totalLikes));

        someActivityPerformed();
    }

    private void updateDislikeCount(TextView txt, boolean checked) {
        int totalDislikes = quesModel.getTotalDislikes();
        totalDislikes = checked ? totalDislikes + 1 : totalDislikes - 1;
        quesModel.setTotalDislikes(totalDislikes);
        txt.setText(String.valueOf(totalDislikes));

        someActivityPerformed();
    }


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.relMain:
                if (userModel.getUserId().trim().equals(quesModel.getUserModel().getUserId().trim())) {
                    ShowEditDeleteQues editDelete = new ShowEditDeleteQues(this,quesModel);
                    editDelete.showPopupMenu(imgMore);
                }
                break;
        }
        return false;
    }

    private void someActivityPerformed() {
        QuestionListActivity.flagActivityInAnswer = 1;
    }
}
