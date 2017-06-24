package com.wscubetech.seovideotutorials.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.StudyMaterialAnsPagerAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.StudyMaterialModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.utils.AdClass;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.OfflineResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudyMaterialAnsActivity extends AppCompatActivity {

    TextView txtHeader, txtMsg;
    //TextView txtAns, txtQuesNo, txtQuesTitle;
    ImageView imgAns;
    Toolbar toolbar;

    SubCategoryModel subCategoryModel;
    ArrayList<StudyMaterialModel> arrayModel = new ArrayList<>();
    StudyMaterialModel studyMaterialModel;
    int serialNo;
    String response;

    ViewPager viewPager;
    StudyMaterialAnsPagerAdapter pagerAdapter;

    boolean active;
    DialogMsg dialogMsg;
    AdClass ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_material_ans);
        init();
        active = true;

        dialogMsg = new DialogMsg(this);


        getSetDetails();
        toolbarOperation();

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
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    private void init() {
        /*txtQuesTitle = (TextView) findViewById(R.id.txtQuesTitle);
        txtQuesNo = (TextView) findViewById(R.id.txtQuesNo);
        txtAns = (TextView) findViewById(R.id.txtAnswer);*/
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        imgAns = (ImageView) findViewById(R.id.imgAns);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        txtHeader.setText(arrayModel.size() == 0 ? subCategoryModel.getSubCatTitle() : arrayModel.get(serialNo).getStudyQues());
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_tile_5));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
    }

    private void getSetDetails() {
        arrayModel = (ArrayList<StudyMaterialModel>) getIntent().getExtras().getSerializable("ArrayStudyMaterialModel");
        subCategoryModel = (SubCategoryModel) getIntent().getExtras().getSerializable("SubCategoryModel");
        //studyMaterialModel = (StudyMaterialModel) getIntent().getExtras().getSerializable("StudyMaterialModel");
        serialNo = getIntent().getExtras().getInt("SerialNo");
        /*txtQuesNo.setText(serialNo + ".");
        txtQuesTitle.setText(studyMaterialModel.getStudyQues());*/

        pagerAdapter = new StudyMaterialAnsPagerAdapter(this, arrayModel);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(serialNo, true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                txtHeader.setText(arrayModel.get(position).getStudyQues());
                decideFooterDisplayMsg(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        decideFooterDisplayMsg(serialNo);
    }

    private void decideFooterDisplayMsg(int position) {
        if (position == arrayModel.size() - 1) {
            txtMsg.setText("Swipe right to go to previous page");
        } else {
            txtMsg.setText("Swipe left to go to next page");
        }
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "StudyMaterial");
        this.response = offlineResponse.getResponse(OfflineResponse.STUDY_MATERIAL + "_" + studyMaterialModel.getStudyId());
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    private void okHttpViewAns() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewStudyMaterialAns).newBuilder();
        urlBuilder.addQueryParameter("study_material_id", studyMaterialModel.getStudyId());

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
                getOfflineData();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                response = Html.fromHtml(res.body().string()).toString();
                //Log.v("ResponsePostSuccess", response);
                OfflineResponse offlineResponse = new OfflineResponse(StudyMaterialAnsActivity.this, "StudyMaterial");
                offlineResponse.setResponse(OfflineResponse.STUDY_MATERIAL + "_" + studyMaterialModel.getStudyId(), response);
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
                                JSONObject obj = json.getJSONObject("message");
                                String ans = "", ansCode = "";
                                ans = obj.getString("study_ques_ans").trim();
                                ansCode = obj.getString("coding").trim().replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ").replaceAll("&ldquo;", "\"").replaceAll("&rdquo;", "\"").replaceAll("&amp;", "&");

                                //txtAns.setText(ans + "\n" + ansCode);

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
