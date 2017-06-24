/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.TestListAdapter;
import com.wscubetech.seovideotutorials.adapters.VideoListAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.TestPaperModel;
import com.wscubetech.seovideotutorials.model.VideoModel;
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

public class TestListingActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtHeader;
    RecyclerView recyclerView;

    ArrayList<TestPaperModel> arrayPaperModel = new ArrayList<>();
    TestListAdapter adapter;

    DialogMsg dialogMsg;
    ProgressWheel progressWheel;

    Boolean active;
    String response = "";

    AdClass ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_listing);
        dialogMsg = new DialogMsg(this);

        init();
        toolbarOperation();

        active = true;

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            viewTestListOkHttp();
        } else {
            getOfflineData();
        }

        ad=new AdClass(this);
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


    public void init() {
        progressWheel = (ProgressWheel) findViewById(R.id.progressBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText(getString(R.string.title_quiz_test));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        active = false;
        if (ad != null)
            ad.destroyAd();
        super.onStop();
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(this, "QuizTestList");
        this.response = offlineResponse.getResponse(OfflineResponse.QUIZ_TESTS);
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewTestListOkHttp() {
        progressWheel.setVisibility(View.VISIBLE);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewSeoPapersTest).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_COURSE_ID, Constants.SEO_CAT_ID);
        String url = urlBuilder.build().toString();

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
                if (res.isSuccessful()) {
                    response = Html.fromHtml(res.body().string()).toString();
                    Log.v("ResponsePostSuccess", response);
                    OfflineResponse offlineResponse = new OfflineResponse(TestListingActivity.this, "QuizTestList");
                    offlineResponse.setResponse(OfflineResponse.QUIZ_TESTS, response);
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
                                JSONArray array = json.getJSONArray("message");
                                if (array.length() > 0)
                                    arrayPaperModel.clear();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    TestPaperModel model = new TestPaperModel();
                                    model.setPaperId(obj.getString("paper_id"));
                                    model.setPaperTitle(obj.getString("paper_name"));
                                    model.setPaperDuration(obj.getString("duration"));
                                    arrayPaperModel.add(model);
                                }
                                if (active) {
                                    adapter = new TestListAdapter(TestListingActivity.this, arrayPaperModel);
                                    ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
                                    recyclerView.setAdapter(animationAdapter);
                                }
                            } else {

                                if (active) {
                                    NoRecordFound noRecordFound = new NoRecordFound(TestListingActivity.this);
                                    noRecordFound.showNoRecordFound("Sorry!\nNo quiz tests found on server database");
                                }

                            }
                        } catch (Exception e) {
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
