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
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.adapters.NotificationAdapter;
import com.wscubetech.seovideotutorials.model.NotificationModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
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
 * Created by wscubetech on 27/3/17.
 */

public class NotificationsFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    View view;
    boolean active;
    String title = "";
    ProgressWheel progressWheel;
    String response = "";

    ArrayList<NotificationModel> arrayNotificationModel = new ArrayList<>();
    NotificationAdapter adapter;

    public static Fragment newInstance(String title) {
        Fragment fragment = new NotificationsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        view.setOnClickListener(this);
        this.view = view;
        init(view);
        return view;
    }

    private void init(View v) {
        progressWheel = (ProgressWheel) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (HomeActivity.fragmentFlag == 0) {
            HomeActivity.fragmentSet();
            active = true;
            title = this.getArguments().getString("Title");
            HomeActivity.txtHeader.setText(title);
            if (new ConnectionDetector(HomeActivity.activity).isConnectingToInternet()) {
                viewNotificationsOkHttp();
            } else {
                getOfflineData();
            }
        }
    }

    public void getOfflineData() {
        OfflineResponse offlineResponse = new OfflineResponse(getActivity(), "NotificationList");
        this.response = offlineResponse.getResponse(OfflineResponse.NOTIFICATIONS);
        if (this.response.trim().length() < 1) {
            if (active)
                response = getString(R.string.networkError);
        }
        handleResponse(response);
    }

    private void viewNotificationsOkHttp() {
        progressWheel.setVisibility(View.VISIBLE);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.VIEW_NOTIFICATIONS).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                Toast.makeText(getActivity(),getString(R.string.networkError),Toast.LENGTH_LONG).show();
                //getOfflineData();
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                response = Html.fromHtml(res.body().string()).toString();
                //Log.v("ResponsePostSuccess", response);
                OfflineResponse offlineResponse = new OfflineResponse(getActivity(), "NotificationList");
                offlineResponse.setResponse(OfflineResponse.NOTIFICATIONS, response);
                handleResponse(response);
            }
        });
    }

    private void handleResponse(final String response) {
        HomeActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (response.equalsIgnoreCase(getString(R.string.networkError))) {
                        if (active) {
                            NoRecordFound noRecordFound = new NoRecordFound(HomeActivity.activity, view);
                            noRecordFound.showNoRecordFound(getString(R.string.networkError));
                        }
                    } else {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getInt("response") == 1) {
                                JSONArray array = json.getJSONArray("msg");
                                if (array.length() > 0)
                                    arrayNotificationModel.clear();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    NotificationModel model = new NotificationModel();
                                    model.setNotificationId(obj.getString("notification_id"));
                                    model.setNotificationTitle(obj.getString("notification_title"));
                                    model.setNotificationFor(obj.getString("notification_for"));

                                    SubCategoryModel subCategoryModel = new SubCategoryModel();
                                    subCategoryModel.setSubCatId(obj.getString("sub_category_id"));
                                    subCategoryModel.setSubCatTitle(obj.getString("title"));
                                    subCategoryModel.setSubCatImage(obj.getString("image"));
                                    subCategoryModel.setSubCatFlag(model.getNotificationFor().equals("2") ? "1" : "2");
                                    subCategoryModel.setSubCatQuizTime(obj.getString("quiz_time"));

                                    model.setSubCategoryModel(subCategoryModel);

                                    arrayNotificationModel.add(model);
                                }

                                if (active) {
                                    adapter = new NotificationAdapter(HomeActivity.activity, arrayNotificationModel);
                                    ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
                                    recyclerView.setAdapter(animationAdapter);
                                }
                            } else {

                                if (active) {
                                    NoRecordFound noRecordFound = new NoRecordFound(HomeActivity.activity, view);
                                    noRecordFound.showNoRecordFound("No notifications posted yet");
                                }

                            }
                        } catch (Exception e) {
                            Log.v("ParsingException", "" + e);
                        }
                    }


                } catch (Exception e) {

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
