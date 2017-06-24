package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.adapters.ExpandableAdapter;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.TrainingPointsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class TrainingCourseDetailActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    TextView txtData, txtNumber1, txtNumber2;
    ImageView imgTraining;
    ExpandableListView list;

    String position = "";

    JSONObject json;

    ArrayList<TrainingPointsModel> arrayOfData = new ArrayList<>();

    ArrayList<String> arrayOfPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_course_detail);

        position = getIntent().getExtras().getString("Position");
        init();
        toolbarOperations();

        new setData().execute();

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

    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#000000"));
        txtData = (TextView) findViewById(R.id.txt);
        imgTraining = (ImageView) findViewById(R.id.imgTraining);
        txtNumber1 = (TextView) findViewById(R.id.txtNumber1);
        txtNumber2 = (TextView) findViewById(R.id.txtNumber2);
        list = (ExpandableListView) findViewById(R.id.list);
        list.setClickable(false);
        list.setEnabled(false);

    }

    private void toolbarOperations() {
        try {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
            collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
        } catch (Exception e) {
            Log.v("ExceptionCollapsing", "" + e);
        }
    }


    public static String AssetJSONFile(Context context, String filename) throws Exception {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }


    public String getFile(String position) {
        String filename = "";
        switch (Integer.parseInt(position)) {
            case 0:
                filename = "phptraining.txt";
                getSupportActionBar().setTitle("PHP Training");
                break;
            case 1:
                filename = "webdevelopmenttraining.txt";
                getSupportActionBar().setTitle("Web Development Training");
                break;
            case 2:
                filename = "androidtraining.txt";
                getSupportActionBar().setTitle("Android Training");
                break;
            case 3:
                filename = "javatraining.txt";
                getSupportActionBar().setTitle("Java Training");
                break;
            case 4:
                filename = "seotraining.txt";
                getSupportActionBar().setTitle("SEO Training");
                break;
            case 5:
                filename = "advancephptraining.txt";
                getSupportActionBar().setTitle("Advance PHP Training");
                break;
            case 6:
                filename = "iphonetraining.txt";
                getSupportActionBar().setTitle("IPhone Training");
                break;
            case 7:
                filename = "htmltraining.txt";
                getSupportActionBar().setTitle("HTML & CSS Training");
                break;
            case 8:
                filename = "wordpresstraining.txt";
                getSupportActionBar().setTitle("Wordpress Training");
                break;
            case 9:
                filename = "ctraining.txt";
                getSupportActionBar().setTitle("C Training");
                break;
            case 10:
                filename = "cpp.txt";
                getSupportActionBar().setTitle("C++ Training");
                break;
            case 11:
                filename = "cakephptraining.txt";
                getSupportActionBar().setTitle("Cake Php Training");
                break;
            case 12:
                filename = "magentotraining.txt";
                getSupportActionBar().setTitle("Magento Training");
                break;
            case 13:
                filename="digital_marketing.txt";
                getSupportActionBar().setTitle(getString(R.string.title_digital_marketing));
                break;
            case 14:
                filename="advance_java.txt";
                getSupportActionBar().setTitle(getString(R.string.title_adv_java));
                break;
            case 15:
                filename="advance_android.txt";
                getSupportActionBar().setTitle(getString(R.string.title_adv_android));
                break;
            case 16:
                filename="testing_manual.txt";
                getSupportActionBar().setTitle(getString(R.string.title_testing_manual));
                break;
            case 17:
                filename="testing_automation.txt";
                getSupportActionBar().setTitle(getString(R.string.title_testing_automation));
                break;
            case 18:
                filename="laravel.txt";
                getSupportActionBar().setTitle(getString(R.string.title_laravel));
                break;
            case 19:
                filename="ruby_on_rails.txt";
                getSupportActionBar().setTitle(getString(R.string.title_ruby_rails));
                break;
            case 20:
                filename="python.txt";
                getSupportActionBar().setTitle(getString(R.string.title_python));
                break;
            case 21:
                filename="graphic_designing.txt";
                getSupportActionBar().setTitle(getString(R.string.title_graphic_design));
                break;
            default:
                break;

        }

        return filename;
    }


    public Integer getImage(String position) {
        int image = 0;
        switch (Integer.parseInt(position)) {

            case 0:
                image = R.drawable.training_php;
                break;
            case 1:
                image = R.drawable.training_web_development;
                break;
            case 2:
                image = R.drawable.training_android;
                break;
            case 3:
                image = R.drawable.training_java;
                break;
            case 4:
                image = R.drawable.training_seo;
                break;
            case 5:
                image = R.drawable.training_advance_php;
                break;
            case 6:
                image = R.drawable.training_iphone;
                break;
            case 7:
                image = R.drawable.training_html;
                break;
            case 8:
                image = R.drawable.training_wordpress;
                break;
            case 9:
                image = R.drawable.training_c;
                break;
            case 10:
                image = R.drawable.training_cpp;
                break;
            case 11:
                image = R.drawable.training_cakephp;
                break;
            case 12:
                image = R.drawable.training_magento;
                break;
            case 13:
                image = R.drawable.training_digital_marketing;
                break;
            case 14:
                image = R.drawable.training_adv_java;
                break;
            case 15:
                image = R.drawable.training_adv_android;
                break;
            case 16:
                image = R.drawable.training_testing_manual;
                break;
            case 17:
                image = R.drawable.training_testing_automation;
                break;
            case 18:
                image=R.drawable.training_laravel;
                break;
            case 19:
                image=R.drawable.training_ruby_on_rails;
                break;
            case 20:
                image=R.drawable.training_python;
                break;
            case 21:
                image=R.drawable.training_graphic_designing;
                break;
            default:
                break;

        }

        return image;
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

    public class setData extends AsyncTask<Void, Void, Void> {

        String data = "";
        Dialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new MyProgressDialog(TrainingCourseDetailActivity.this).getDialog();
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                json = new JSONObject(AssetJSONFile(TrainingCourseDetailActivity.this, getFile(position)));
                // json = new JSONObject(getResources().getString(R.string.php_training));
                data = json.getString("description");

                JSONArray array = json.getJSONArray("points");


                for (int i = 0; i < array.length(); i++) {

                    JSONObject data = array.getJSONObject(i);
                    String heading = data.getString("heading");

                    arrayOfPoints = new ArrayList<>();
                    JSONArray points = data.getJSONArray("point");


                    for (int j = 0; j < points.length(); j++) {
                        JSONObject pointsdata = points.getJSONObject(j);
                        String point = pointsdata.getString("data");
                        arrayOfPoints.add(point);
                    }

                    TrainingPointsModel model = new TrainingPointsModel();

                    model.setHeading(heading);
                    model.setArrayOfPoints(arrayOfPoints);

                    arrayOfData.add(model);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progress.dismiss();

//          String s = Html.fromHtml(data.toString()) + "";

            imgTraining.setImageResource(getImage(position));

//            AdapterParentTraining adapter = new AdapterParentTraining(ActivityTraining.this, arrayOfData, data);
//            list.setAdapter(adapter);


            ExpandableAdapter adapter = new ExpandableAdapter(TrainingCourseDetailActivity.this, arrayOfData, data);
            list.setAdapter(adapter);
//


            Thread expand = new Thread() {

                @Override
                public void run() {
                    super.run();
                    synchronized (this) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < arrayOfData.size(); i++) {
                                        list.expandGroup(i);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            expand.start();

        }
    }
}
