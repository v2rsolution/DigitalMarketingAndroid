package com.wscubetech.seovideotutorials.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.MyValidations;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wscubetech on 23/3/17.
 */

public class ContactUsFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private GoogleMap gMap;
    SupportMapFragment mapFragment;
    String title;
    ScrollView scrollView;
    ImageView imgTransparent;

    EditText etName, etEmail, etPhone, etWebsiteName, etMessage;
    TextView txtSubmit;
    DialogMsg dialogMsg;
    Dialog progress;
    RadioButton radioTechnical, radioTraining;

    public static Fragment newInstance(String title) {
        Fragment fragment = new ContactUsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        v.setOnClickListener(this);
        init(v);
        return v;
    }

    private void init(View v) {
        scrollView = (ScrollView) v.findViewById(R.id.scrollView1);
        imgTransparent = (ImageView) v.findViewById(R.id.imgTransparent);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        etName = (EditText) v.findViewById(R.id.etName);
        etEmail = (EditText) v.findViewById(R.id.etEmail);
        etMessage = (EditText) v.findViewById(R.id.etMessage);
        etPhone = (EditText) v.findViewById(R.id.etPhone);
        etWebsiteName = (EditText) v.findViewById(R.id.etWebsiteName);
        txtSubmit = (TextView) v.findViewById(R.id.txtSubmit);
        radioTechnical = (RadioButton) v.findViewById(R.id.radioTechnical);
        radioTraining = (RadioButton) v.findViewById(R.id.radioTraining);
    }

    private void touchHandleMapAndScrollView() {
        imgTransparent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        dialogMsg = new DialogMsg(HomeActivity.activity);
        progress = new MyProgressDialog(HomeActivity.activity).getDialog();

        title = this.getArguments().getString("Title");
        HomeActivity.txtHeader.setText(title);

        mapFragment.getMapAsync(this);

        touchHandleMapAndScrollView();

        txtSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSubmit:
                String strName = "", strEmail = "", strPhone = "", strWebsite = "", strMessage = "";
                strName = etName.getText().toString().trim();
                strEmail = etEmail.getText().toString().trim();
                strPhone = etPhone.getText().toString().trim();
                strWebsite = etWebsiteName.getText().toString().trim();
                strMessage = etMessage.getText().toString().trim();

                Toast toast = Toast.makeText(HomeActivity.activity, "", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);

                MyValidations validations = new MyValidations(HomeActivity.activity);
                if (strName.length() < 1 || !validations.checkName(strName)) {
                    toast.setText("Please enter a valid name");
                    toast.show();
                    return;
                }

                if (!validations.checkEmail(strEmail)) {
                    toast.setText("Please enter a valid e-mail");
                    toast.show();
                    return;
                }

                if (!validations.checkMobile(strPhone)) {
                    toast.setText("Please enter a valid phone number");
                    toast.show();
                    return;
                }

                if (strMessage.length() < 1) {
                    toast.setText("Please enter some Message");
                    toast.show();
                    return;
                }

               // String subject=radioTechnical.isChecked()?"Technical Support":"Training";

                String subject="";

                if (new ConnectionDetector(HomeActivity.activity).isConnectingToInternet()) {
                    okHttpSendQuery(toast, strName, strEmail, strPhone, strWebsite, subject, strMessage);
                } else {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }

                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;


        try {
            LatLng latLng = new LatLng(26.2737116,73.0305749);
            gMap.addMarker(new MarkerOptions().position(latLng)
                    .title("WsCube Tech"));


            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        } catch (Exception e) {
            Toast.makeText(HomeActivity.activity, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    private void okHttpSendQuery(final Toast toast, String... str) {
        progress.show();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.SEND_QUERY_CONTACT_US).newBuilder();
        urlBuilder.addQueryParameter("name", str[0]);
        urlBuilder.addQueryParameter("email", str[1]);
        urlBuilder.addQueryParameter("phone", str[2]);
        urlBuilder.addQueryParameter("website", str[3]);
        urlBuilder.addQueryParameter("subject", str[4]);
        urlBuilder.addQueryParameter("message", str[5]);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                HomeActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress.isShowing())
                            progress.dismiss();
                        toast.setText(getString(R.string.networkError));
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                HomeActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress.isShowing())
                            progress.dismiss();
                        toast.setText("Your message is successfully sent, We will contact you soon");
                        toast.show();
                        etName.setText("");
                        etEmail.setText("");
                        etPhone.setText("");
                        etWebsiteName.setText("");
                        etMessage.setText("");
                    }
                });
            }
        });
    }
}
