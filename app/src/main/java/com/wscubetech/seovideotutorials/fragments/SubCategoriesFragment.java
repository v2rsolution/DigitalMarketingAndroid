package com.wscubetech.seovideotutorials.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.adapters.SubCategoryAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
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

/**
 * Created by wscubetech on 11/3/17.
 */

public class SubCategoriesFragment extends Fragment implements View.OnClickListener {


    RecyclerView recyclerView;

    ArrayList<SubCategoryModel> arraySubCategoryModel = new ArrayList<>();
    SubCategoryAdapter adapter;

    DialogMsg dialogMsg;
    ProgressWheel progressWheel;

    Boolean active;
    String response = "", title = "";
    String url = "", flagIqTechnical = "1";
    int colorRes = 0;

    View view;

    //AdClass ad;


    public static Fragment newInstance(String title) {
        Fragment fragment = new SubCategoriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_categories_interview, container, false);
        view.setOnClickListener(this);
        this.view = view;
        init(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //ad.resumeAd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        active = false;
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (HomeActivity.fragmentFlag == 0) {
            HomeActivity.fragmentSet();
            dialogMsg = new DialogMsg(HomeActivity.activity);

            active = true;

            title = this.getArguments().getString("Title");
            HomeActivity.txtHeader.setText(title);

            decideComingFrom();

            if (new ConnectionDetector(HomeActivity.activity).isConnectingToInternet()) {
                viewSubCategoriesOkHttp();
            } else {
                getOfflineData();
            }
        }
        /*ad = new AdClass(HomeActivity.activity, view);
        if (new ConnectionDetector(HomeActivity.activity).isConnectingToInternet()) {
            ad.showAd();
        }*/
    }

    private void init(View v) {
        progressWheel = (ProgressWheel) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onClick(View view) {

    }

    public void decideComingFrom() {
        switch (title) {
            case "Interview Questions":
                url = Urls.viewSubCategoriesIq;
                flagIqTechnical = "1";
                colorRes = R.color.color_tile_1;
                break;
            case "Quiz Tests":
                url = Urls.viewSubCategoriesQuiz;
                colorRes = R.color.color_tile_2;
                break;
            case "Technical Terms":
                url = Urls.viewSubCategoriesIq;
                flagIqTechnical = "2";
                colorRes = R.color.color_tile_3;
                break;
            case "Study Material":
                url = Urls.viewSubCategoriesStudyMaterial;
                colorRes = R.color.color_tile_5;
                break;
        }
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(HomeActivity.activity, "SubCategoriesList");
        this.response = offlineResponse.getResponse(OfflineResponse.INTERVIEW_QUES_1 + "_" + title.replaceAll(" ", "_"));
        if (this.response.trim().length() < 1) {
            if (active)
                response = getString(R.string.networkError);
        }
        handleResponse();
    }

    public void viewSubCategoriesOkHttp() {
        progressWheel.setBarColor(ContextCompat.getColor(HomeActivity.activity, colorRes));
        progressWheel.setVisibility(View.VISIBLE);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_SUB_CAT, Constants.SEO_CAT_ID);
        urlBuilder.addQueryParameter("question_type", flagIqTechnical);
        url = urlBuilder.build().toString();

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
                    OfflineResponse offlineResponse = new OfflineResponse(HomeActivity.activity, "SubCategoriesList");
                    offlineResponse.setResponse(OfflineResponse.INTERVIEW_QUES_1 + "_" + title.replaceAll(" ", "_"), response);
                    handleResponse();
                }
            }
        });
    }


    public void handleResponse() {
        if (HomeActivity.activity != null && active) {
            HomeActivity.activity.runOnUiThread(new Runnable() {
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
                                        arraySubCategoryModel.clear();

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        SubCategoryModel model = new SubCategoryModel();
                                        model.setSubCatId(obj.getString(Constants.KEY_SUB_CAT_ID));
                                        model.setSubCatTitle(obj.getString(Constants.KEY_SUB_CAT_TITLE));
                                        model.setSubCatImage(obj.getString(Constants.KEY_SUB_CAT_IMAGE));
                                        model.setSubCatQuizTime(obj.getString(Constants.KEY_SUB_CAT_QUIZ_TIME));
                                        model.setSubCatFlag(flagIqTechnical);
                                        arraySubCategoryModel.add(model);
                                    }

                                    if (active) {
                                        adapter = new SubCategoryAdapter(HomeActivity.activity, arraySubCategoryModel, colorRes);
                                        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
                                        recyclerView.setAdapter(animationAdapter);
                                    }
                                } else {

                                    if (active) {
                                        NoRecordFound noRecordFound = new NoRecordFound(HomeActivity.activity, view);
                                        noRecordFound.showNoRecordFound("Sorry!\nNo sub categories found on server database");
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
    }
}
