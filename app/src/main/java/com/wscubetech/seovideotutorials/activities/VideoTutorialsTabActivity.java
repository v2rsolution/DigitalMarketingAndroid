/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.SubCategoryVideosAdapter;
import com.wscubetech.seovideotutorials.adapters.ViewPagerAdapter;
import com.wscubetech.seovideotutorials.custom.CustomFont;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.fragments.VideoTutorialFragment;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.utils.AdClass;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;
import com.wscubetech.seovideotutorials.utils.OfflineResponse;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;

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

public class VideoTutorialsTabActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;

    Toolbar toolbar;
    TextView txtHeader;
    ImageView imgSearch;
    public static ImageView imgMore;
    PopupMenu popupMenu;
    String response = "";

    public static VideoTutorialsTabActivity activity;
    FloatingActionButton fabFilter;

    ArrayList<SubCategoryModel> arraySubCategoryModel = new ArrayList<>();
    SubCategoryVideosAdapter adapter;

    RecyclerView recyclerView;
    DialogMsg dialogMsg;
    boolean active;

    Dialog progress;

    String subCatId = "", subCatName = "", subCatHindiCount = "", subCatEnglishCount = "";
    int totalCount = 0;

    AdClass ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_tutorials_tab);
        init();
        activity = this;
        dialogMsg = new DialogMsg(this);

        subCatId = getIntent().getExtras().getString("SubCatId");
        subCatName = getIntent().getExtras().getString("SubCatName");
        subCatHindiCount = getIntent().getExtras().getString("SubCatHindiCount");
        subCatEnglishCount = getIntent().getExtras().getString("SubCatEnglishCount");

        toolbarOperation();

        clearOldDataIfInternetFound();

        try {
            int total = Integer.parseInt(subCatHindiCount) + Integer.parseInt(subCatEnglishCount);
            if (total == 0) {
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    okHttpViewVideoCountAll();
                } else {
                    setUpHeadersAndPager();
                }
            } else {
                setUpHeadersAndPager();
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
            setUpHeadersAndPager();
        }

        fabFilter.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        imgMore.setOnClickListener(this);

        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }

        firstTimeDemonstration();
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

    private void setUpHeadersAndPager() {
        setTabHeadersForOffline();
        setUpViewPager();

        /*if (prefs.getData("TutorialsFirstTime").equals("")) {
            intro();
        }*/
    }

    private void init() {
        fabFilter = (FloatingActionButton) findViewById(R.id.fabFilter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        imgSearch = (ImageView) toolbar.findViewById(R.id.imgSearch);
        imgSearch.setVisibility(View.VISIBLE);

        imgMore = (ImageView) toolbar.findViewById(R.id.imgMore);
        //imgMore.setVisibility(View.VISIBLE);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText(subCatId.equals("") ? getString(R.string.title_video_tutorials) : subCatName);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_tile_0));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void clearOldDataIfInternetFound() {
        if (new ConnectionDetector(VideoTutorialsTabActivity.activity).isConnectingToInternet()) {
            OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.activity, "VideoList");
            offlineResponse.setResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + "0", "");
            offlineResponse.setResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + "1", "");
            offlineResponse.setResponse(OfflineResponse.VIDEO_TUTORIALS + "_" + subCatId + "_" + "2", "");
        }
    }

    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(VideoTutorialFragment.newInstance("0", subCatId), "All");
        String tabHeaders[] = getTabHeaders();

        if (Constants.SEO_CAT_ID.equals(Constants.DIGITAL_MARKETING_ID)) {
            viewPagerAdapter.addFragment(VideoTutorialFragment.newInstance("2", subCatId), tabHeaders[0]);
            viewPagerAdapter.addFragment(VideoTutorialFragment.newInstance("1", subCatId), tabHeaders[1]);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            viewPagerAdapter.addFragment(VideoTutorialFragment.newInstance("", subCatId), "All Video Tutorials");
            tabLayout.setVisibility(View.GONE);
        }
        if (totalCount > 0) {
            txtHeader.setText(txtHeader.getText().toString().trim() + "(" + totalCount + ")");
        }
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setTabHeadersForOffline() {
        OfflineResponse response = new OfflineResponse(this, "TabHeaders");
        String tabHeaders[] = getTabHeaders();
        response.setResponse("HindiHeader_" + subCatId + "_" + Constants.SEO_CAT_ID, subCatHindiCount.equals("0") ? (tabHeaders[0].equals("") ? "Hindi" : tabHeaders[0]) : "Hindi (" + subCatHindiCount + ")");
        response.setResponse("EnglishHeader_" + subCatId + "_" + Constants.SEO_CAT_ID, subCatEnglishCount.equals("0") ? (tabHeaders[1].equals("") ? "English" : tabHeaders[1]) : "English (" + subCatEnglishCount + ")");
    }

    private String[] getTabHeaders() {
        OfflineResponse response = new OfflineResponse(this, "TabHeaders");
        String arrHeaders[] = new String[2];
        arrHeaders[0] = response.getResponse("HindiHeader_" + subCatId + "_" + Constants.SEO_CAT_ID);
        arrHeaders[1] = response.getResponse("EnglishHeader_" + subCatId + "_" + Constants.SEO_CAT_ID);
        return arrHeaders;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabFilter:
                showSubCatDialog();
                break;
            case R.id.imgSearch:
                Intent intent = new Intent(this, SearchVideoActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(this, imgSearch, "ic_search");
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.imgMore:
                viewMoreOptions();
                break;
        }
    }

    private void showSubCatDialog() {
        Dialog dialog = new MyDialog(this).getMyDialog(R.layout.dialog_card_filter_sub_categories);
        ProgressWheel progressWheel;
        recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        progressWheel = (ProgressWheel) dialog.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialog.show();
        active = true;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                active = false;
            }
        });

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            viewSubCategoriesOkHttp(progressWheel);
        } else {
            getOfflineData();
        }

        firstTimeDemonstration();

    }

    private void firstTimeDemonstration() {
        final GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "FirstTimeUse");
        String firstTime = prefs.getData("FirstTimeVideoList").trim();
        if (firstTime.length() < 1) {
            TapTargetView.showFor(this,
                    TapTarget.forView(imgSearch, "Search Tutorials", "Tap on search icon to search for video tutorials")
                            // All options below are optional
                            .outerCircleColor(R.color.color_tile_0)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.9f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(android.R.color.white)   // Specify a color for the target circle
                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                            .titleTextColor(android.R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(android.R.color.white)  // Specify the color of the description text
                            .textTypeface(CustomFont.setFontRegular(getAssets()))  // Specify a typeface for the text
                            .dimColor(android.R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(false)
                            .transparentTarget(true)// Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(35),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            prefs.putData("FirstTimeVideoList", "no");
                        }

                        @Override
                        public void onOuterCircleClick(TapTargetView view) {
                            super.onOuterCircleClick(view);
                            onTargetClick(view);
                        }
                    });
        }


    }

    public void getOfflineData() {
        if (active) {
            OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.this, "VideosSubCategoriesList");
            this.response = offlineResponse.getResponse(OfflineResponse.INTERVIEW_QUES_1 + "_" + Constants.SEO_CAT_ID + "_" + "VideoList");
            if (this.response.trim().length() < 1) {
                response = getString(R.string.networkError);
            }
            handleResponse(null);
        }
    }

    public void viewSubCategoriesOkHttp(final ProgressWheel progressWheel) {
        progressWheel.setVisibility(View.VISIBLE);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.viewSubCategoriesVideos).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEY_SUB_CAT, Constants.SEO_CAT_ID);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.v("ViewSubCatUrl", url);
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

                    if (active) {
                        OfflineResponse offlineResponse = new OfflineResponse(VideoTutorialsTabActivity.this, "VideosSubCategoriesList");
                        offlineResponse.setResponse(OfflineResponse.INTERVIEW_QUES_1 + "_" + Constants.SEO_CAT_ID + "_" + "VideoList", response);
                        handleResponse(progressWheel);
                    }
                }
            }
        });
    }


    public void handleResponse(final ProgressWheel progressWheel) {
        VideoTutorialsTabActivity.this.runOnUiThread(new Runnable() {
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
                                if (array.length() > 0) {
                                    arraySubCategoryModel.clear();
                                    SubCategoryModel model = new SubCategoryModel();
                                    model.setSubCatId("");
                                    model.setSubCatTitle("All");
                                    model.setSubCatImage("");
                                    model.setSubCatHindiCount("0");
                                    model.setSubCatEnglishCount("0");
                                    arraySubCategoryModel.add(model);
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    SubCategoryModel model = new SubCategoryModel();
                                    model.setSubCatId(obj.getString(Constants.KEY_SUB_CAT_ID));
                                    model.setSubCatTitle(obj.getString(Constants.KEY_SUB_CAT_TITLE));
                                    model.setSubCatImage(obj.getString(Constants.KEY_SUB_CAT_IMAGE));
                                    model.setSubCatEnglishCount(obj.getString("english_video"));
                                    model.setSubCatHindiCount(obj.getString("hindi_video"));
                                    model.setTotal(String.valueOf(Integer.parseInt(model.getSubCatHindiCount()) + Integer.parseInt(model.getSubCatEnglishCount())));
                                    arraySubCategoryModel.add(model);
                                }

                                if (active) {
                                    adapter = new SubCategoryVideosAdapter(VideoTutorialsTabActivity.this, arraySubCategoryModel);
                                    recyclerView.setAdapter(adapter);
                                    notifyCalculateTotal();
                                }

                            } else {
                                Toast.makeText(VideoTutorialsTabActivity.this, "No sub categories found on server database", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(VideoTutorialsTabActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
                            Log.v("ParsingException", "" + e);
                        }
                    }

                    if (active && progressWheel != null)
                        progressWheel.setVisibility(View.GONE);

                } catch (Exception e) {

                }
            }
        });
    }

    private void notifyCalculateTotal() {
        try {
            int total = Integer.parseInt(subCatHindiCount) + Integer.parseInt(subCatEnglishCount);
            if (arraySubCategoryModel.size() > 0) {
                SubCategoryModel model = arraySubCategoryModel.get(0);
                model.setTotal(String.valueOf(total));
                model.setSubCatEnglishCount(subCatEnglishCount);
                model.setSubCatHindiCount(subCatHindiCount);
                arraySubCategoryModel.set(0, model);
                if (adapter != null)
                    adapter.notifyItemChanged(0);
            }
        } catch (Exception e) {

        }
    }

    private void okHttpViewVideoCountAll() {
        progress = new MyProgressDialog(this).getDialog();
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel(Constants.KEY_COURSE_ID, Constants.SEO_CAT_ID));
        OkHttpCalls calls = new OkHttpCalls(Urls.viewVideoCountAll, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponseVideoCount(true, "");
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = res.body().string();
                handleResponseVideoCount(false, response);
            }
        });
    }

    private void handleResponseVideoCount(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                if (failed) {
                    setUpHeadersAndPager();
                    Toast.makeText(VideoTutorialsTabActivity.this, getString(R.string.networkError), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getInt("response") == 1) {
                            int hindiCount = 0, englishCount = 0;
                            hindiCount = json.getInt("hindi_video");
                            englishCount = json.getInt("english_video");
                            totalCount = hindiCount + englishCount;

                            subCatHindiCount = String.valueOf(hindiCount);
                            subCatEnglishCount = String.valueOf(englishCount);

                            setUpHeadersAndPager();
                        }
                    } catch (Exception e) {
                        Log.v("JsonExceptionViewTotal", "" + e);
                    }
                }
            }
        });
    }


    private void viewMoreOptions() {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(this, imgMore);
            popupMenu.getMenuInflater().inflate(R.menu.menu_video_tutorials_more, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
        }

        Menu menu = popupMenu.getMenu();
        MenuItem itemMostViewed = menu.findItem(R.id.itemMostViewed);
        itemMostViewed.setVisible(new ConnectionDetector(getApplicationContext()).isConnectingToInternet());

        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        byte flag = 1;
        switch (item.getItemId()) {
            case R.id.itemNewest:
                flag = 1;
                break;
            case R.id.itemOldest:
                flag = 2;
                break;
            case R.id.itemMostViewed:
                flag = 3;
                break;

        }

        item.setChecked(true);
        VideoTutorialFragment fragment1, fragment2;
        if (Constants.SEO_CAT_ID.equals(Constants.DIGITAL_MARKETING_ID)) {
            fragment1 = (VideoTutorialFragment) viewPagerAdapter.getItem(0);
            fragment2 = (VideoTutorialFragment) viewPagerAdapter.getItem(1);
            fragment1.sortAccordingly(flag);
            fragment2.sortAccordingly(flag);
        } else {
            fragment1 = (VideoTutorialFragment) viewPagerAdapter.getItem(0);
            fragment1.sortAccordingly(flag);
        }

        return true;
    }
}
