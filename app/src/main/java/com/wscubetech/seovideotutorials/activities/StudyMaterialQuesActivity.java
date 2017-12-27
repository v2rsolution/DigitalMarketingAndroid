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
import com.wscubetech.seovideotutorials.adapters.StudyMaterialQuesAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.StudyMaterialModel;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudyMaterialQuesActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    TextView txtHeader;

    ArrayList<StudyMaterialModel> arrayQuesModel = new ArrayList<>();
    StudyMaterialQuesAdapter adapter;

    DialogMsg dialogMsg;
    ProgressWheel progressWheel, progressBarBottom;

    Boolean active;
    String response = "";
    int pageNo = 1, totalPages = 1;
    Boolean isLoading = true;

    AdClass ad;

    SubCategoryModel subCategoryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_material_ques);
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

                    Log.v("Count", visibleItemCount + "\n" + totalItemCount + pastVisibleItems);
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        //Log.v("...", "Last Item Wow !");
                        //Do pagination.. i.e. fetch new data
                        if (pageNo <= totalPages && isLoading) {
                            isLoading = false;
                            recyclerView.scrollToPosition(arrayQuesModel.size() - 1);
                            progressBarBottom.setVisibility(View.VISIBLE);
                            if (new ConnectionDetector(StudyMaterialQuesActivity.this).isConnectingToInternet()) {
                                viewStudyMaterialQuesOkHttp();
                            } else {
                                getOfflineData();
                            }
                        }
                    }

                }
            }
        });

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            viewStudyMaterialQuesOkHttp();
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
        adapter = new StudyMaterialQuesAdapter(StudyMaterialQuesActivity.this, arrayQuesModel, subCategoryModel);
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
        int colorTheme = ContextCompat.getColor(this, R.color.color_tile_5);
        toolbar.setBackgroundColor(colorTheme);
        txtHeader.setText(subCategoryModel.getSubCatTitle());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBarBottom.setBarColor(colorTheme);
        progressWheel.setBarColor(colorTheme);
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "StudyMaterialQuesList");
        this.response = offlineResponse.getResponse(OfflineResponse.STUDY_MATERIAL + subCategoryModel.getSubCatId() + "_" + pageNo + "_" + Constants.SEO_CAT_ID);
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewStudyMaterialQuesOkHttp() {

        if (pageNo == 1)
            progressWheel.setVisibility(View.VISIBLE);
        else
            progressBarBottom.setVisibility(View.VISIBLE);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewStudyMaterialQues).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_CAT_ID, Constants.SEO_CAT_ID);
        urlBuilder.addQueryParameter(Constants.KEY_PAGE_NO, "" + pageNo);
        urlBuilder.addQueryParameter(Constants.KEY_SUB_CAT_ID_INTERVIEW, subCategoryModel.getSubCatId());

        String url = urlBuilder.build().toString();
        Log.v("UrlStudyMaterial", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (active) {
                                progressWheel.setVisibility(View.GONE);
                                progressBarBottom.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), getString(R.string.networkError), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                if (res.isSuccessful()) {
                    response = Html.fromHtml(res.body().string()).toString();
                    Log.v("ResponsePostSuccess", response);
                    OfflineResponse offlineResponse = new OfflineResponse(StudyMaterialQuesActivity.this, "StudyMaterialQuesList");
                    offlineResponse.setResponse(OfflineResponse.STUDY_MATERIAL + subCategoryModel.getSubCatId() + "_" + pageNo + "_" + Constants.SEO_CAT_ID, response);
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
                    Log.v("ResponseStudyQues", response);
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
                                    StudyMaterialModel model = new StudyMaterialModel();
                                    model.setStudyId(obj.getString("study_material_id"));
                                    model.setStudyQues(obj.getString("question"));
                                    String ans = "", ansCode = "";
                                    ans = obj.getString("answer").trim();
                                    ansCode = obj.getString("coding").trim().replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ").replaceAll("&ldquo;", "\"").replaceAll("&rdquo;", "\"").replaceAll("&amp;", "&");
                                    model.setStudyAns(ans + "\n\n" + ansCode);
                                    model.setStudyImage(obj.getString("study_image"));
                                    model.setSubCatIconImage(subCategoryModel.getSubCatImage());
                                    //model.setAnsCode(obj.getString(Constants.KEY_ANS_CODE).replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ").replaceAll("&ldquo;", "\"").replaceAll("&rdquo;", "\"").replaceAll("&amp;", "&"));
                                    arrayQuesModel.add(model);
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
                                    NoRecordFound noRecordFound = new NoRecordFound(StudyMaterialQuesActivity.this);
                                    noRecordFound.showNoRecordFound("Sorry!\nNo study material found on server database");
                                }

                            }
                        } catch (Exception e) {
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
