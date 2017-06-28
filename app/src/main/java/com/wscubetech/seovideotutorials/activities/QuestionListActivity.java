/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.pnikosis.materialishprogress.ProgressWheel;
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
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.EssentialLogin;
import com.wscubetech.seovideotutorials.utils.FirstTimeTargetView;
import com.wscubetech.seovideotutorials.utils.NoRecordFound;
import com.wscubetech.seovideotutorials.utils.OfflineResponse;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QuestionListActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    TextView txtHeader;
    RecyclerView recyclerView;

    ProgressWheel progressWheel;
    ProgressWheel progressBarBottom;

    ArrayList<QuestionListModel> arrayQuesListModel = new ArrayList<>();
    QuesListAdapter adapter;

    Boolean active;
    String response = "";
    int pageNo = 1, totalPages = 1;
    Boolean isLoading = true;

    AdClass ad;

    UserModel userModel;
    
    String comingFrom = "";//all-> all questions   myQuestions-> my posted questions    myAnswers-> my posted answers
    
    String msgNotFound = ""; 

    DialogMsg dialogMsg;

    LinearLayoutManager layoutManager;

    FloatingActionButton fabAdd;

    //Tag Searching layouts
    RelativeLayout relSearchByTag;
    ImageView imgSearch, imgCross;
    MultiAutoCompleteTextView autoTag;
    String tagArray[], selectedTags = "";

    String flagFilter = "1";// 1->Trending;
    /*1 -> Trending (Default)
    2 -> Most Popular
    3 -> Most Recent
    4 -> Most Answered
    5 -> Unanswered
    6 -> My Questions (user_id)
    7 -> My Answers (user_id)*/

    //filter
    Spinner spinner;

    Dialog progress;

    public static QuestionListActivity activity;

    int noOfTimesFirstTime = 1, firstTimeLoad = 0;

    NoRecordFound noRecordFound;

    public static int flagActivityInAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        init();
        active = true;
        activity = this;
        noRecordFound = new NoRecordFound(this);
        noRecordFound.hideUi();

        comingFrom = getIntent().getExtras().getString("ComingFrom");

        flagFilter = comingFrom.equalsIgnoreCase("all") ? "1" : comingFrom.equalsIgnoreCase("myQuestions") ? "5" : "6";


        userModel = new UserDetailsPrefs(this).getUserModel();

        toolbarOperation();
        onClickListeners();

        dialogMsg = new DialogMsg(this);
        progress = new MyProgressDialog(this).getDialog();

        adapter = new QuesListAdapter(this, arrayQuesListModel, "QuestionList");
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        //Log.v("...", "Last Item Wow !");
                        //Do pagination.. i.e. fetch new data
                        if (pageNo <= totalPages && isLoading) {
                            isLoading = false;
                            recyclerView.scrollToPosition(arrayQuesListModel.size() - 1);
                            progressBarBottom.setVisibility(View.VISIBLE);
                            if (new ConnectionDetector(QuestionListActivity.this).isConnectingToInternet()) {
                                okHttpViewQuestionList();
                            } else {
                                getOfflineData();
                            }
                        }
                    }

                }
            }
        });

        fillSpinnerItems();
        autoCompleteTagListener();

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            okHttpGetTags();
        }

        if (comingFrom.equalsIgnoreCase("all"))
            filterQuestionList();


        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ad.resumeAd();
        if (flagActivityInAnswer == 1) {
            flagActivityInAnswer = 0;
            filterQuestionList();
        }
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
    protected void onStop() {
        active = false;
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    public void init() {
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        progressWheel = (ProgressWheel) findViewById(R.id.progressBar);
        progressBarBottom = (ProgressWheel) findViewById(R.id.progressBarBottom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        relSearchByTag = (RelativeLayout) findViewById(R.id.relSearchByTag);
        imgCross = (ImageView) findViewById(R.id.imgCross);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        autoTag = (MultiAutoCompleteTextView) findViewById(R.id.autoTag);
        autoTag.setThreshold(1);
        autoTag.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        spinner = (Spinner) findViewById(R.id.spinner);

    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        int colorTheme = ContextCompat.getColor(this, R.color.color_tile_4);
        toolbar.setBackgroundColor(colorTheme);
        txtHeader.setText("Questions");
        msgNotFound = comingFrom.equals("all") ? "Oops!\nNo questions there to entertain" : "Oops!\nNo questions posted yet by you";

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBarBottom.setBarColor(colorTheme);
        progressWheel.setBarColor(colorTheme);
    }

    private void onClickListeners() {
        fabAdd.setOnClickListener(this);
        imgCross.setOnClickListener(this);
    }

    private void fillSpinnerItems() {
        ArrayList<String> arrayTitles = new ArrayList<>();
        arrayTitles.add("Trending");
        arrayTitles.add("Most Popular");
        arrayTitles.add("Most Recent");
        arrayTitles.add("Most Answered");
        arrayTitles.add("Unanswered");
        if (userModel.getUserId().length() > 0) {
            arrayTitles.add("My Questions");
            arrayTitles.add("My Answers");
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.row_text_view, arrayTitles);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstTimeLoad != 0) {
                    flagFilter = (i + 1) + "";
                    filterQuestionList();
                } else {
                    firstTimeLoad = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (comingFrom.equalsIgnoreCase("myQuestions")) {
            flagFilter = "6";
            firstTimeLoad = 1;
            spinner.setSelection(5);
        } else if (comingFrom.equalsIgnoreCase("myAnswers")) {
            flagFilter = "7";
            firstTimeLoad = 1;
            spinner.setSelection(6);
        }
    }

    private void firstTimeDemonstration(View view, String title, String description, int radius) {

        String prefName = "FirstTimeQuestionList";

        final FirstTimeTargetView firstTimeTargetView = new FirstTimeTargetView(this, prefName);
        firstTimeTargetView.firstTimeDemonstration(view, title, description, radius, false, new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                if (noOfTimesFirstTime <= 2) {
                    noOfTimesFirstTime += 1;
                    switch (noOfTimesFirstTime) {
                        case 2:
                            firstTimeDemonstration(spinner, "Filter Questions", "Tap here to filter questions", 80);
                            break;
                        case 3:
                            firstTimeDemonstration(fabAdd, "Post Question", "Tap here to post your question", 60);
                            break;
                    }
                } else {
                    firstTimeTargetView.firstTimeCompletion();
                }
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                onTargetClick(view);
            }

        });

    }


    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "QuestionList");
        this.response = offlineResponse.getResponse(OfflineResponse.QUES_LIST + flagFilter + "_" + selectedTags + "_" + pageNo);
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    private void okHttpViewQuestionList() {
        if (pageNo == 1)
            progressWheel.setVisibility(View.VISIBLE);
        else
            progressBarBottom.setVisibility(View.VISIBLE);

        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("user_id", userModel.getUserId()));
        arrayKeyValueModel.add(new KeyValueModel("ques_main_cat_id", Constants.SEO_CAT_ID));
        arrayKeyValueModel.add(new KeyValueModel("page_no", String.valueOf(pageNo)));
        arrayKeyValueModel.add(new KeyValueModel("flag", flagFilter));
        arrayKeyValueModel.add(new KeyValueModel("filter_tag", selectedTags));

        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_POSTED_QUESTIONS, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(active)
                    Toast.makeText(getApplicationContext(),getString(R.string.networkError),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                response = Html.fromHtml(res.body().string()).toString();
                Log.v("ResponseQuestions", response);
                OfflineResponse offlineResponse = new OfflineResponse(QuestionListActivity.this, "QuestionList");
                offlineResponse.setResponse(OfflineResponse.QUES_LIST + flagFilter + "_" + selectedTags + "_" + pageNo, response);
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
                                    model.setQuesId(obj.getString("ques_id"));
                                    model.setQuesTitle(obj.getString("user_question"));
                                    model.setQuesDate(obj.getString("ques_time"));
                                    model.setTags(obj.getString("question_tags"));
                                    model.setAnsCount(obj.getString("total_answer"));
                                    model.setTotalViews(Integer.parseInt(obj.getString("total_views")));

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

                                    arrayQuesListModel.add(model);

                                }

                                if (active) {
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                    pageNo += 1;
                                    if (pageNo <= totalPages)
                                        isLoading = true;
                                }
                            } else {

                                if (active) {
                                    noRecordFound.showNoRecordFound(msgNotFound);
                                }

                            }
                        } catch (Exception e) {
                            Toast.makeText(QuestionListActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
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

    private void okHttpGetTags() {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("tag_main_cat_id", Constants.SEO_CAT_ID));
        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_TAGS, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponseViewTags(true, "");
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = Html.fromHtml(res.body().string()).toString();
                handleResponseViewTags(false, response);
            }
        });
    }

    private void handleResponseViewTags(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                if (failed) {
                    Toast.makeText(QuestionListActivity.this, getString(R.string.networkError), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Log.v("ResponseTags", response);
                        JSONObject json = new JSONObject(response);
                        if (json.getInt("response") == 1) {
                            String message = json.getString("message").trim();
                            if (message.length() > 1) {
                                tagArray = message.toLowerCase().split(",");
                                ArrayAdapter adapter = new ArrayAdapter(QuestionListActivity.this, R.layout.row_auto_complete_tag, tagArray);
                                autoTag.setAdapter(adapter);
                            }
                        }
                    } catch (Exception e) {
                        Log.v("TagParsing", "" + e);
                    }
                    firstTimeDemonstration(relSearchByTag, "Tag Search", "Tap here to search on the basis of tags", 80);
                }
            }
        });
    }

    private void autoCompleteTagListener() {

        autoTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString().trim();
                imgCross.setVisibility(str.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        autoTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String commaSeparatedTags = autoTag.getText().toString().trim().replaceAll(" ", "");
                    if (commaSeparatedTags.length() > 1) {
                        commaSeparatedTags = commaSeparatedTags.substring(0, commaSeparatedTags.length() - 1);
                        selectedTags = commaSeparatedTags;

                        View view = QuestionListActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        filterQuestionList();
                        //Toast.makeText(QuestionListActivity.this, commaSeparatedTags, Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        autoTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (tagArray != null && tagArray.length > 0) {
                    selectedTags = autoTag.getText().toString().trim().replaceAll(" ", "");
                    selectedTags = selectedTags.substring(0, selectedTags.length() - 1);

                    imgCross.setVisibility(selectedTags.trim().length() > 0 ? View.VISIBLE : View.GONE);

                    //Toast.makeText(QuestionListActivity.this,selectedTags,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void filterQuestionList() {
        noRecordFound.hideUi();
        pageNo = 1;
        totalPages = 1;
        isLoading = true;
        arrayQuesListModel.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (new ConnectionDetector(QuestionListActivity.this).isConnectingToInternet()) {
            okHttpViewQuestionList();
        } else {
            getOfflineData();
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
            case R.id.fabAdd:
                if (userModel.getUserId().trim().length() < 1) {
                    EssentialLogin essentialLogin = new EssentialLogin(this);
                    essentialLogin.showQuesAnsPostDialog();
                    return;
                }
                Intent intent = new Intent(this, PostQuestionActivity.class);
                intent.putExtra("QuesModel", new QuestionListModel());
                startActivity(intent);
                break;
            case R.id.imgCross:
                selectedTags = "";
                autoTag.setText(selectedTags);
                imgCross.setVisibility(View.GONE);
                break;
        }
    }
}
