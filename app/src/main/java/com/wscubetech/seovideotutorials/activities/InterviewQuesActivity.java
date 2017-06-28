/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.InterviewQuesAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.InterviewModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.utils.AdClass;
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

public class InterviewQuesActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    TextView txtHeader;

    ArrayList<InterviewModel> arrayInterviewModel = new ArrayList<>();
    InterviewQuesAdapter adapter;

    DialogMsg dialogMsg;
    ProgressWheel progressWheel, progressBarBottom;

    Boolean active;
    String response = "";
    int pageNo = 1, totalPages = 1;
    Boolean isLoading = true;

    AdClass ad;

    SubCategoryModel subCategoryModel;
    String msgNotFound = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_ques);
        init();

        subCategoryModel = (SubCategoryModel) getIntent().getExtras().getSerializable("SubCategoryModel");

        active = true;
        dialogMsg = new DialogMsg(this);

        toolbarOperation();

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
                            recyclerView.scrollToPosition(arrayInterviewModel.size() - 1);
                            progressBarBottom.setVisibility(View.VISIBLE);
                            if (new ConnectionDetector(InterviewQuesActivity.this).isConnectingToInternet()) {
                                viewInterviewQuesOkHttp();
                            } else {
                                getOfflineData();
                            }
                        }
                    }

                }
            }
        });

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            viewInterviewQuesOkHttp();
        } else {
            getOfflineData();
        }

        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }
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
        adapter = new InterviewQuesAdapter(InterviewQuesActivity.this, arrayInterviewModel, subCategoryModel.getSubCatFlag());
        //ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    public void init() {
        progressWheel = (ProgressWheel) findViewById(R.id.progressBar);
        progressBarBottom = (ProgressWheel) findViewById(R.id.progressBarBottom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        int colorTheme = ContextCompat.getColor(this, subCategoryModel.getSubCatFlag().equals("1") ? R.color.color_tile_1 : R.color.color_tile_3);
        toolbar.setBackgroundColor(colorTheme);
        txtHeader.setText(subCategoryModel.getSubCatTitle());

        msgNotFound = subCategoryModel.getSubCatFlag().equals("1") ? "Sorry!\nNo interview questions found on server database" : "Sorry!\nNo technical terms found on server database";

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBarBottom.setBarColor(colorTheme);
        progressWheel.setBarColor(colorTheme);
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "InterviewQuestionsList");
        this.response = offlineResponse.getResponse(OfflineResponse.INTERVIEW_QUES_2 + subCategoryModel.getSubCatId() + "_" + subCategoryModel.getSubCatFlag() + "_" + pageNo);
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewInterviewQuesOkHttp() {

        if (pageNo == 1)
            progressWheel.setVisibility(View.VISIBLE);
        else
            progressBarBottom.setVisibility(View.VISIBLE);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewInterviewQues).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_CAT_ID, Constants.SEO_CAT_ID);
        urlBuilder.addQueryParameter(Constants.KEY_PAGE_NO, "" + pageNo);
        urlBuilder.addQueryParameter(Constants.KEY_SUB_CAT_ID_INTERVIEW, subCategoryModel.getSubCatId());
        urlBuilder.addQueryParameter("question_type", subCategoryModel.getSubCatFlag());

        String url = urlBuilder.build().toString();
        Log.v("UrlIQ", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                Toast.makeText(InterviewQuesActivity.this,getString(R.string.networkError),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                response = Html.fromHtml(res.body().string()).toString();
                Log.v("ResponsePostSuccess", response);
                OfflineResponse offlineResponse = new OfflineResponse(InterviewQuesActivity.this, "InterviewQuestionsList");
                offlineResponse.setResponse(OfflineResponse.INTERVIEW_QUES_2 + subCategoryModel.getSubCatId() + "_" + subCategoryModel.getSubCatFlag() + "_" + pageNo, response);
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
                                    InterviewModel model = new InterviewModel();
                                    model.setQues(obj.getString(Constants.KEY_QUES).trim());
                                    model.setAns(obj.getString(Constants.KEY_ANS).trim());
                                    model.setAnsCode(obj.getString(Constants.KEY_ANS_CODE).trim().replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ").replaceAll("&ldquo;", "\"").replaceAll("&rdquo;", "\"").replaceAll("&amp;", "&"));
                                    arrayInterviewModel.add(model);
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
                                    NoRecordFound noRecordFound = new NoRecordFound(InterviewQuesActivity.this);
                                    noRecordFound.showNoRecordFound(msgNotFound);
                                }

                            }
                        } catch (Exception e) {
                            Toast.makeText(InterviewQuesActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
