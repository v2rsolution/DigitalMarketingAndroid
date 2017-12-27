package com.wscubetech.seovideotutorials.fragments;


import android.app.Activity;
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
import java.util.Collections;
import java.util.regex.Pattern;

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
    ArrayList<String> arrayVideoIds = new ArrayList<>();
    String videoIds = ""; //comma separated filling for youtube data API 3.0
    String arrVideoIds[];

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

        if (arrayVideoModel.size() < 1) {
            if (new ConnectionDetector(VideoTutorialsTabActivity.activity).isConnectingToInternet()) {
                if (active) {
                    viewVideoListOkHttp();
                }
            } else {
                if (active) {
                    OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.activity, "VideoList");
                    this.response = offlineResponse.getResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + Constants.SEO_CAT_ID + "_" + englishHindi);
                    getOfflineData();
                }

            }
        }

    }

    @Override
    public void onStop() {
        active = false;
        super.onStop();
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.activity, "VideoList");
        this.response = offlineResponse.getResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + Constants.SEO_CAT_ID + "_" + englishHindi);
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

        //Log.v("SubCatId",subCatId);
        Log.d("UrlVideos_API", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                try {
                    Activity act = getActivity();
                    if (act != null) {
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.networkError), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {

                response = Html.fromHtml(res.body().string()).toString();
                Log.d("ResponsePostSuccess", response);
                if (active) {
                    OfflineResponse offlineResponse = new OfflineResponse(getActivity(), "VideoList");
                    offlineResponse.setResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + Constants.SEO_CAT_ID + "_" + englishHindi, response);
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
                            //Log.v("Response...",response);
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

                                    videoIds += "," + model.getVideoLink();
                                }

                                Log.v("VideoCount_", arrayVideoModel.size() + "");


                                if (videoIds.length() > 1)
                                    videoIds = videoIds.substring(1);

                                arrVideoIds = videoIds.split(Pattern.quote(","));

                                Log.v("VideoCount_video_ids", arrVideoIds.length + "");

                                if (active) {
                                    /*adapter = new VideoListAdapter(getActivity(), arrayVideoModel);
                                    ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
                                    recyclerView.setAdapter(animationAdapter);*/

                                    if (new ConnectionDetector(getActivity()).isConnectingToInternet())
                                        decideNoOfPagesVideos();
                                    else
                                        sortAccordingly((byte)1);

                                    /*if (arrVideoIds.length > 0)
                                        getNoOfViewsForVideo(0);*/
                                }
                            } else {

                                if (active) {
                                    NoRecordFound noRecordFound = new NoRecordFound(VideoTutorialsTabActivity.activity, view);
                                    noRecordFound.showNoRecordFound("Sorry!\nNo video tutorials available for this course");
                                }

                            }
                        } catch (Exception e) {
                            Log.v("ParsingException", "" + e);
                        }
                    }


                } catch (Exception e) {
                    Log.v("ExceptionParsing", "" + e);
                }


            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    //40 per page
    private void decideNoOfPagesVideos() {
        arrayVideoIds.clear();
        String arrVideoIds[] = videoIds.split(",");
        int noOfPages = 1;
        int count = 0;
        if (arrVideoIds.length > 40) {
            String ids = "";
            for (String id : arrVideoIds) {


                count += 1;
                if (count % 41 == 0) {
                    noOfPages += 1;
                    if (ids.length() > 1)
                        arrayVideoIds.add(ids.substring(1));
                    ids = "";
                }

                ids += "," + id;

            }


            if (noOfPages > 1 && ids.length() > 1)
                arrayVideoIds.add(ids.substring(1));

        } else {
            if (arrVideoIds.length > 0)
                arrayVideoIds.add(videoIds);
        }

        if (arrayVideoIds.size() > 0)
            getNoOfViewsForVideo(0);

    }

    private void getNoOfViewsForVideo(final int pos) {
        for (String id : arrayVideoIds) {
            String ids[] = id.split(",");
            int i = 1;
            for (String str : ids) {
                //Log.v("Id: "+i, str);
                i += 1;
            }
            Log.v("Id_length", ids.length + "");
        }
        //Log.v("Ids...",arrayVideoIds.get(pos));
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.YOU_TUBE_DATA_API).newBuilder();
        urlBuilder.addQueryParameter("id", arrayVideoIds.get(pos));
        String url = urlBuilder.build().toString();
        Log.v("UrlVideosAndCount", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                try {
                    Activity act = getActivity();
                    if (act != null) {
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressWheel.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), getString(R.string.networkError), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onResponse(Call call, final Response res) throws IOException {

                final String response = Html.fromHtml(res.body().string()).toString();
                //Log.v("ResponseYouTubeDataApi", response);
                if (active) {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray array = json.getJSONArray("items");
                        for (int i = 0; i < array.length(); i++) {
                            final int k = i;
                            JSONObject obj = array.getJSONObject(i);
                            JSONObject objStats = obj.getJSONObject("statistics");
                            final long views = Long.parseLong(objStats.getString("viewCount"));
                            VideoTutorialsTabActivity.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (active) {
                                        if (pos == 0)
                                            updateVideoModelViews(k, views);
                                        else if (pos == 1)
                                            updateVideoModelViews(40 + k, views);
                                        else if (pos == 2)
                                            updateVideoModelViews(40 + 40 + k, views);
                                    }
                                }
                            });
                        }

                    } catch (Exception e) {
                        Log.v("ExceptionParseYoutube", "" + e);
                    }

                }

                if (pos < arrayVideoIds.size() - 1) {
                    int position = pos + 1;
                    getNoOfViewsForVideo(position);
                } else {
                    VideoTutorialsTabActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (active) {
                                progressWheel.setVisibility(View.GONE);
                                sortAccordingly((byte) 1);
                                //Log.v("VideoLinkLast", arrayVideoModel.get(arrayVideoModel.size() - 1).getVideoLink());
                            }
                        }
                    });
                }


            }
        });
    }

    private void updateVideoModelViews(int pos, long views) {
        //Log.v("Position: ", pos + "");
        VideoModel model = arrayVideoModel.get(pos);
        model.setVideoViews(views);
        arrayVideoModel.set(pos, model);

    }

    private void setItemsInRecyclerView() {
        if (active && arrayVideoModel.size() > 0) {
            progressWheel.setVisibility(View.GONE);
            if (adapter == null) {
                adapter = new VideoListAdapter(getActivity(), arrayVideoModel);
                ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
                recyclerView.setAdapter(animationAdapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    //flag=> 1->Newest First, 2->Oldest First, 3->Most Viewed First
    public void sortAccordingly(byte flag) {
        VideoModel.flag = flag;
        if (arrayVideoModel.size() > 1) {
            VideoTutorialsTabActivity.imgMore.setVisibility(View.VISIBLE);
            Collections.sort(arrayVideoModel);
        } else {
            VideoTutorialsTabActivity.imgMore.setVisibility(View.GONE);
        }
        setItemsInRecyclerView();
    }


}
