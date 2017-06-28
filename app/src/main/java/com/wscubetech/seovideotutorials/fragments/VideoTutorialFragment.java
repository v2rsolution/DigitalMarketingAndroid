package com.wscubetech.seovideotutorials.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.VideoTutorialsTabActivity;
import com.wscubetech.seovideotutorials.adapters.VideoListAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.VideoModel;
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

public class VideoTutorialFragment extends Fragment implements View.OnClickListener {

    String englishHindi = "1"; //1 for English, 2 for Hindi
    String subCatId = "";

    RecyclerView recyclerView;
    ArrayList<VideoModel> arrayVideoModel = new ArrayList<>();
    VideoListAdapter adapter;

    DialogMsg dialogMsg;
    ProgressWheel progressWheel;


    Boolean active;
    String response = "";
    View view;

    public static Fragment newInstance(String englishHindi, String subCatId) {
        Fragment fragment = new VideoTutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putString("EnglishHindi", englishHindi);
        bundle.putString("SubCatId", subCatId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video_tutorial, container, false);
        view.setOnClickListener(this);
        init(view);
        return view;
    }

    private void init(View v) {
        progressWheel = (ProgressWheel) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(VideoTutorialsTabActivity.activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        englishHindi = VideoTutorialFragment.this.getArguments().getString("EnglishHindi");
        subCatId = VideoTutorialFragment.this.getArguments().getString("SubCatId");
        active = true;
        //dialogMsg = new DialogMsg(VideoTutorialsTabActivity.activity);

        if (new ConnectionDetector(VideoTutorialsTabActivity.activity).isConnectingToInternet()) {
            if (active) {
                OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.activity, "VideoList");
                this.response = offlineResponse.getResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + englishHindi);
                if (this.response.trim().length() < 1) {
                    viewVideoListOkHttp();
                } else {
                    getOfflineData();
                }
            }
        } else {
            getOfflineData();
        }
    }

    @Override
    public void onStop() {
        active = false;
        super.onStop();
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.activity, "VideoList");
        this.response = offlineResponse.getResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + englishHindi);
        if (this.response.trim().length() < 1) {
            if (active)
                response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewVideoListOkHttp() {
        progressWheel.setVisibility(View.VISIBLE);

        //Urls.viewVideoList
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewVideoList).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_COURSE_ID, Constants.SEO_CAT_ID);
        urlBuilder.addQueryParameter(Constants.KEY_LANGUAGE, englishHindi);
        urlBuilder.addQueryParameter(Constants.KEY_SUB_CAT_ID, subCatId);
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
                if (active)
                    Toast.makeText(getActivity(), getString(R.string.networkError), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {

                response = Html.fromHtml(res.body().string()).toString();
                Log.v("ResponsePostSuccess", response);
                if (active) {
                    OfflineResponse offlineResponse = new OfflineResponse(getActivity(), "VideoList");
                    offlineResponse.setResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + englishHindi, response);
                    handleResponse();
                }

            }
        });
    }


    public void handleResponse() {
        VideoTutorialsTabActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (response.equalsIgnoreCase(getString(R.string.networkError))) {
                        if (active) {
                            NoRecordFound noRecordFound = new NoRecordFound(VideoTutorialsTabActivity.activity, view);
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
                                    adapter = new VideoListAdapter(getActivity(), arrayVideoModel);
                                    ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
                                    recyclerView.setAdapter(animationAdapter);
                                }
                            } else {

                                if (active) {
                                    NoRecordFound noRecordFound = new NoRecordFound(VideoTutorialsTabActivity.activity, view);
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

                if (active)
                    progressWheel.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
