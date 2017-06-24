/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.VideoListAdapter;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
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

public class SearchVideoActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    ImageView imgBack;
    EditText etSearch;

    String response;
    ProgressWheel progressWheel;
    RecyclerView recyclerView;
    ArrayList<VideoModel> arrayVideoModel = new ArrayList<>();
    ArrayList<VideoModel> arraySearchedModel = new ArrayList<>();
    VideoListAdapter adapter;

    Dialog progress;
    boolean active;

    NoRecordFound noRecordFound;

    AdClass ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);
        init();
        noRecordFound = new NoRecordFound(SearchVideoActivity.this);
        active = true;
        onClickListeners();
        textChangeListeners();

        progress = new MyProgressDialog(this).getDialog();

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            viewVideoListOkHttp();
        } else {
            getOfflineData();
        }

        textChangeListeners();
        searchBoxMessage();

        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
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
    protected void onStop() {
        active = false;
        super.onStop();
    }

    private void init() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressWheel = (ProgressWheel) findViewById(R.id.progressBar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
    }

    private void onClickListeners() {
        imgBack.setOnClickListener(this);
    }

    private void textChangeListeners() {
        etSearch.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String input = editable.toString().trim();
        recyclerView.setVisibility(View.GONE);
        if (arrayVideoModel.size() > 0) {
            searchNow(input);
        } else {
            noRecordFound.showNoRecordFound("No video tutorials found");
        }
    }

    private void searchBoxMessage() {
        noRecordFound.showNoRecordFound("Please type in the search box to search for a video tutorial");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(SearchVideoActivity.this, "VideoList");
        this.response = offlineResponse.getResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + "" + "_" + "0");
        if (this.response.trim().length() < 1) {
            response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewVideoListOkHttp() {
        progress.show();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewVideoList).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_COURSE_ID, Constants.SEO_CAT_ID);
        urlBuilder.addQueryParameter(Constants.KEY_LANGUAGE, "0");
        String url = urlBuilder.build().toString();

        Log.v("UrlVideos", url);
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
                Log.v("ResponsePostSuccess", response);
                if (active) {
                    OfflineResponse offlineResponse = new OfflineResponse(SearchVideoActivity.this, "VideoList");
                    offlineResponse.setResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + "" + "_" + "0", response);
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
                        if (active) {
                            noRecordFound.showNoRecordFound(getString(R.string.networkError));
                        }
                    } else {
                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.getInt("response") == 1) {
                                JSONArray array = json.getJSONArray("message");
                                if (array.length() > 0)
                                    arrayVideoModel.clear();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    VideoModel model = new VideoModel();
                                    model.setVideoId(obj.getString(Constants.KEY_VIDEO_ID));
                                    model.setVideoTitle(obj.getString(Constants.KEY_VIDEO_TITLE));
                                    model.setVideoDescription(obj.getString(Constants.KEY_VIDEO_DESCRIPTION));
                                    model.setVideoLink(obj.getString(Constants.KEY_VIDEO_LINK));
                                    model.setVideoImage(obj.getString(Constants.KEY_VIDEO_IMAGE));
                                    model.setVideoDuration(obj.getString(Constants.KEY_VIDEO_DURATION));
                                    arrayVideoModel.add(model);
                                }

                                if (active) {
                                    adapter = new VideoListAdapter(SearchVideoActivity.this, arrayVideoModel);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    if (arrayVideoModel.size() == 0) {
                                        noRecordFound.showNoRecordFound("No video tutorials found");
                                    } else {
                                        noRecordFound.hideUi();
                                    }
                                }

                            } else {

                                if (active) {
                                    noRecordFound.showNoRecordFound("Sorry!\nNo video tutorials found on server database");
                                }

                            }
                        } catch (Exception e) {
                            Log.v("ParsingException", "" + e);
                        }
                    }


                } catch (Exception e) {
                    Log.v("ExceptionParsing", "" + e);
                }

                if (active) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                }
            }
        });
    }

    private void searchNow(String input) {
        arraySearchedModel.clear();
        progressWheel.setVisibility(View.VISIBLE);
        for (VideoModel videoModel : arrayVideoModel) {
            String title, description;
            title = videoModel.getVideoTitle().trim();
            description = videoModel.getVideoDescription().trim();
            if (title.toLowerCase().contains(input.toLowerCase())) {
                arraySearchedModel.add(videoModel);
            } else if (description.toLowerCase().contains(input.toLowerCase())) {
                arraySearchedModel.add(videoModel);
            }
        }

        if (active) {
            adapter = new VideoListAdapter(this, arraySearchedModel);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            //recyclerView.bringToFront();

            if (arraySearchedModel.size() == 0) {
                noRecordFound.showNoRecordFound("No result found for " + input);
            } else {
                noRecordFound.hideUi();
            }

            progressWheel.setVisibility(View.GONE);

        }
    }


}
