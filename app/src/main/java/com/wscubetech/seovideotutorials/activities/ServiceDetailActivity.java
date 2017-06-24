/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.adapters.AdapterServiceDetailExtraBullets;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.ServiceModel;

import java.util.ArrayList;

public class ServiceDetailActivity extends AppCompatActivity {

    ServiceModel serviceModel;
    ImageView imgService;
    TextView txtNumber1, txtNumber2;
    TextView txtDescription;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Bitmap bmp;
    NestedScrollView nestedScrollView;

    Dialog progress;

    public static final String SEO_PACKAGE_URL="http://www.wscubetech.com/affordable-seo-packages.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        init();
        toolbarOperations();

        txtNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent phone = new Intent(Intent.ACTION_VIEW);
                    phone.setData(Uri.parse("tel:" + getString(R.string.training_number_1)));
                    phone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(phone);
                } catch (Exception e) {

                }
            }
        });

        txtNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent phone = new Intent(Intent.ACTION_VIEW);
                    phone.setData(Uri.parse("tel:" + getString(R.string.training_number_2)));
                    phone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(phone);
                } catch (Exception e) {

                }
            }
        });

        new LoadInBackground().execute();
    }

    private void init() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#000000"));
        imgService = (ImageView) findViewById(R.id.imgService);
        txtDescription = (TextView) findViewById(R.id.txtDescription);

        txtNumber1 = (TextView) findViewById(R.id.txtNumber1);
        txtNumber2 = (TextView) findViewById(R.id.txtNumber2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
    }

    private void toolbarOperations() {
        try {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.BLACK);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
            collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
        } catch (Exception e) {
            Log.v("ExceptionCollapsing", "" + e);
        }
    }

    private void getServiceInfo() {
        String strDesc=serviceModel.getServiceDescription().toString().trim();
        if(serviceModel.getServiceName().equalsIgnoreCase("SEO Packages")){
            strDesc+="\n"+SEO_PACKAGE_URL;
        }
        txtDescription.setText(strDesc);
        Linkify.addLinks(txtDescription,Linkify.WEB_URLS);
        imgService.setImageResource(serviceModel.getServiceMainImage());
        collapsingToolbarLayout.setTitle(serviceModel.getServiceName());
        collapsingToolbarLayout.setContentScrimResource(serviceModel.getServiceDrawableBg());
        collapsingToolbarLayout.setStatusBarScrimResource(serviceModel.getServiceDrawableBg());
    }

    private class LoadInBackground extends AsyncTask<Void, Void, Void> {

        int visibility = View.VISIBLE;
        ArrayList<String> arrayList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new MyProgressDialog(ServiceDetailActivity.this).getDialog();
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            serviceModel = (ServiceModel) getIntent().getExtras().getSerializable("ServiceModel");

            arrayList = new ArrayList<>();
            if (serviceModel.getServiceName().toLowerCase().contains("psd to html")) {
                arrayList.add("Improvement in the project design and development");
                arrayList.add("Rapid and simple assistance");
                arrayList.add("Complete customer satisfaction");
                arrayList.add("Design completion control");
                arrayList.add("Professional look for the website");
                visibility = View.VISIBLE;
            } else if (serviceModel.getServiceName().toLowerCase().contains("android")) {
                arrayList.add("Location based services by integrating GPS and Google maps");
                arrayList.add("Integration of developed Application with social media site");
                arrayList.add("Application suitable for mobile as well as Tabs");
                arrayList.add("Large data management application");
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            getServiceInfo();

            recyclerView.setVisibility(visibility);
            if (visibility == View.VISIBLE) {
                AdapterServiceDetailExtraBullets adapter = new AdapterServiceDetailExtraBullets(ServiceDetailActivity.this, arrayList);
                recyclerView.setAdapter(adapter);
            }

            nestedScrollView.scrollTo(0, 0);

            progress.dismiss();
        }
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
