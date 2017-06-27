package com.wscubetech.seovideotutorials.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.wscubetech.seovideotutorials.adapters.PlacedStudentsAdapter;
import com.wscubetech.seovideotutorials.model.PlacedStudentModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.GridSpacingItemDecoration;
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

/**
 * Created by wscubetech on 14/3/17.
 */

public class PlacedStudentsFragment extends Fragment implements View.OnClickListener {

    View view;
    RecyclerView recyclerView;
    boolean active;
    ProgressWheel progressBar;

    ArrayList<PlacedStudentModel> arrayModel = new ArrayList<>();
    PlacedStudentsAdapter adapter;
    String response = "", title = "";

    public static Fragment newInstance(String title) {
        Fragment fragment = new PlacedStudentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_placed_students, container, false);
        view = v;
        view.setOnClickListener(this);
        init(v);
        return v;
    }

    private void init(View v) {
        progressBar = (ProgressWheel) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.activity, 2));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.dim_7), true));
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        title = this.getArguments().getString("Title");
        HomeActivity.txtHeader.setText(title);
        if (new ConnectionDetector(HomeActivity.activity).isConnectingToInternet()) {
            viewPlacedStudentsOkHttp();
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
        OfflineResponse offlineResponse = new OfflineResponse(HomeActivity.activity, "PlacedStudents");
        this.response = offlineResponse.getResponse(OfflineResponse.PLACED_STUDENTS);
        if (this.response.trim().length() < 1) {
            if (active)
                response = getString(R.string.networkError);
        }
        handleResponse(response);
    }

    private void viewPlacedStudentsOkHttp() {
        progressBar.setVisibility(View.VISIBLE);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.VIEW_PLACED_STUDENTS).newBuilder();
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
                OfflineResponse offlineResponse = new OfflineResponse(HomeActivity.activity, "PlacedStudents");
                offlineResponse.setResponse(OfflineResponse.PLACED_STUDENTS, response);
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
                                JSONArray array = json.getJSONArray("message");
                                if (array.length() > 0)
                                    arrayModel.clear();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    PlacedStudentModel model = new PlacedStudentModel(obj.getString("client_name"), obj.getString("client_image"));
                                    arrayModel.add(model);
                                }

                                if (active) {
                                    adapter = new PlacedStudentsAdapter(HomeActivity.activity, arrayModel);
                                    recyclerView.setAdapter(adapter);
                                }
                            } else {

                                if (active) {
                                    NoRecordFound noRecordFound = new NoRecordFound(HomeActivity.activity, view);
                                    noRecordFound.showNoRecordFound("No students found on server");
                                }

                            }
                        } catch (Exception e) {
                            Log.v("ParsingException", "" + e);
                        }
                    }


                } catch (Exception e) {

                }

                if (active)
                    progressBar.setVisibility(View.GONE);
            }


        });
    }

    @Override
    public void onClick(View view) {

    }
}
