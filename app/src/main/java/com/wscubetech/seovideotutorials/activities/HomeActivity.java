/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.HomeTileAdapter;
import com.wscubetech.seovideotutorials.custom.CustomFont;
import com.wscubetech.seovideotutorials.custom.CustomTypefaceSpan;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.fragments.ContactUsFragment;
import com.wscubetech.seovideotutorials.fragments.FragmentAboutUs;
import com.wscubetech.seovideotutorials.fragments.NotificationsFragment;
import com.wscubetech.seovideotutorials.fragments.PlacedStudentsFragment;
import com.wscubetech.seovideotutorials.fragments.ServicesFragment;
import com.wscubetech.seovideotutorials.fragments.SubCategoriesFragment;
import com.wscubetech.seovideotutorials.fragments.SuggestionsFragment;
import com.wscubetech.seovideotutorials.fragments.TrainingCoursesFragment;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.NotificationModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.user_model.ViewUserDetailsServer;
import com.wscubetech.seovideotutorials.utils.AdClass;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;
import com.wscubetech.seovideotutorials.utils.GridSpacingItemDecoration;
import com.wscubetech.seovideotutorials.utils.InitializeFragment;
import com.wscubetech.seovideotutorials.utils.LoadUserImage;
import com.wscubetech.seovideotutorials.utils.LogOutUser;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.RecyclerTouchListener;
import com.wscubetech.seovideotutorials.utils.SendDeviceIdToServer;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static Toolbar toolbar;
    public static TextView txtHeader;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;

    public static HomeActivity activity;
    AdClass ad;

    UserModel userModel;

    public static int fragmentFlag = 0;

    boolean active;

    //Header Nav Drawer
    View headerView;
    LinearLayout linWsCubeTech, linMyProfile;
    TextView txtUserName, txtUserEmail;
    ImageView imgUser;

    String greetingId = "", greetingImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_home);
        init();
        active = true;
        userModel = new UserDetailsPrefs(this).getUserModel();
        activity = this;
        toolbarOperation();
        navigationDrawerOperation();

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.dim_7), true));
        setUpMainItemsRecyclerView();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                clickItem(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }

        checkIfLoggedInAndUpdateDeviceId();
        checkIfComingFromNotification();

    }

    public static void fragmentSet() {
        fragmentFlag = 1;
    }

    public static void fragmentReset() {
        fragmentFlag = 0;
    }


    @Override
    protected void onResume() {
        super.onResume();
        ad.resumeAd();

        sendProfileDetailRequest();
        decideRateDialogShowUp();
    }

    @Override
    protected void onDestroy() {
        ad.destroyAd();
        active = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ad.pauseAd();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    private void decideRateDialogShowUp() {
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "TimesOfUse");
        String timesUse = prefs.getData(Constants.TIMES_USE);
        if (timesUse.trim().length() < 1) {
            timesUse = "1";
        } else {
            try {
                int times = Integer.parseInt(timesUse);
                if (times > 0) {
                    if (times % 8 == 0) {
                        DialogMsg dialogRate = new DialogMsg(this);
                        dialogRate.showRateUsDialog();
                    }
                    times += 1;
                    timesUse = String.valueOf(times);
                }
            } catch (Exception e) {
                Log.v("ExceptionRateUsDialog", "" + e);
            }
        }
        prefs.putData(Constants.TIMES_USE, timesUse);
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText(getString(R.string.app_name));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = CustomFont.setFontRegular(getAssets());
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void navigationDrawerOperation() {

        //font typeface set
        Menu navMenu = navigationView.getMenu();
        for (int i = 0; i < navMenu.size(); i++) {
            MenuItem mi = navMenu.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }


        //My Profile menu in nav drawer based on user log in
        navMenu.findItem(R.id.itemMyProfileDrawer).setVisible(userModel.getUserId().trim().length() >= 1);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                String title = "";
                switch (menuItem.getItemId()) {

                    case R.id.itemHome:
                        clearAllBackStack();
                        txtHeader.setText(getString(R.string.app_name));
                        toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
                        break;

                    case R.id.itemMyProfileDrawer:
                        if (linMyProfile != null)
                            onClick(linMyProfile);
                        break;

                    case R.id.itemVideoTutorials:
                        clickItem(0);
                        break;

                    case R.id.itemInterviewQues:
                        clearAllBackStack();
                        clickItem(1);
                        break;

                    case R.id.itemQuizTest:
                        clearAllBackStack();
                        clickItem(2);
                        break;
                    case R.id.itemTechnicalTerms:
                        clearAllBackStack();
                        clickItem(3);
                        break;
                    case R.id.itemQuesAns:
                        clickItem(4);
                        break;
                    case R.id.itemNotifications:
                        fragmentReset();
                        clearAllBackStack();
                        gotToNotificationFragment();
                        break;

                    case R.id.itemShare:
                        String msg = "Learn digital marketing and explore possibilities in the world of Internet Marketing";
                        String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, msg + "\n\nDownload the app now\n" + appUrl);
                        startActivity(intent);
                        break;
                    case R.id.itemRateUs:
                        final String appPackageName = getPackageName();
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException e) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;
                    case R.id.itemContactUs:
                        clearAllBackStack();
                        clickItem(8);
                        break;
                    case R.id.itemAboutUs:
                        clearAllBackStack();
                        title = getString(R.string.title_about_us);
                        showFragment(FragmentAboutUs.newInstance(title), title);
                        break;

                    case R.id.itemSuggestions:
                        clearAllBackStack();
                        title = getString(R.string.title_suggestions);
                        toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
                        showFragment(SuggestionsFragment.newInstance(title), title);
                        break;

                }

                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        decideHeaderLayoutWhetherLoggedIn();
    }

    private void decideHeaderLayoutWhetherLoggedIn() {
        headerView = navigationView.getHeaderView(0);
        linMyProfile = (LinearLayout) headerView.findViewById(R.id.linMyProfile);
        linWsCubeTech = (LinearLayout) headerView.findViewById(R.id.linWsCubeTech);
        if (userModel.getUserId().trim().length() > 0) {
            txtUserName = (TextView) headerView.findViewById(R.id.txtUserName);
            txtUserEmail = (TextView) headerView.findViewById(R.id.txtUserEmail);
            imgUser = (ImageView) headerView.findViewById(R.id.imgUser);

            txtUserName.setText(userModel.getUserName());
            txtUserEmail.setText(userModel.getUserEmail());

            linMyProfile.setOnClickListener(this);

        } else {
            linWsCubeTech.setVisibility(View.VISIBLE);
            linMyProfile.setVisibility(View.GONE);
        }
    }

    private void clearAllBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
            manager.popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        txtHeader.setText(getString(R.string.app_name));
        toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        super.onBackPressed();
    }

    private void setUpMainItemsRecyclerView() {
        String[] arrayTitles = getResources().getStringArray(R.array.array_home_tiles);
        int arrayBgColors[] = getResources().getIntArray(R.array.array_home_tiles_colors);
        ArrayList<Integer> arrayImageRes = new ArrayList<>();
        arrayImageRes.add(R.drawable.ic_tile_video_tutorial);
        arrayImageRes.add(R.drawable.ic_tile_interview_ques);
        arrayImageRes.add(R.drawable.ic_tile_quiz);
        arrayImageRes.add(R.drawable.ic_tile_technical_terms);
        arrayImageRes.add(R.drawable.ic_tile_ques_ans);
        arrayImageRes.add(R.drawable.ic_tile_study_material);
        arrayImageRes.add(R.drawable.ic_tile_training_courses);
        arrayImageRes.add(R.drawable.ic_tile_our_services);
        arrayImageRes.add(R.drawable.ic_tile_contact_us);
        arrayImageRes.add(R.drawable.ic_tile_placed_students);
        //arrayImageRes.add(R.drawable.ic_tile_students_view);
        HomeTileAdapter adapter = new HomeTileAdapter(this, arrayTitles, arrayBgColors, arrayImageRes);
        recyclerView.setAdapter(adapter);

    }

    private void clickItem(int position) {
        String title = "";
        switch (position) {
            case 0:
                Intent intent = new Intent(HomeActivity.this, VideoTutorialsTabActivity.class);
                intent.putExtra("SubCatId", "");
                intent.putExtra("SubCatName", "");
                intent.putExtra("SubCatHindiCount", "0");
                intent.putExtra("SubCatEnglishCount", "0");
                startActivity(intent);
                break;
            case 1:
                fragmentReset();
                title = getString(R.string.title_interview_ques);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_1));
                showFragment(SubCategoriesFragment.newInstance(title), title);
                break;
            case 2:
                fragmentReset();
                title = getString(R.string.title_quiz_test);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_2));
                showFragment(SubCategoriesFragment.newInstance(title), title);
                break;
            case 3:
                fragmentReset();
                title = getString(R.string.title_technical_terms);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_3));
                showFragment(SubCategoriesFragment.newInstance(title), title);
                break;
            case 4:
                intent = new Intent(HomeActivity.this, QuestionListActivity.class);
                intent.putExtra("ComingFrom", "all");
                startActivity(intent);
                break;
            case 5:
                fragmentReset();
                title = getString(R.string.title_study_material);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_5));
                showFragment(SubCategoriesFragment.newInstance(title), title);
                break;
            case 6:
                title = getString(R.string.title_training_courses);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_6));
                showFragment(TrainingCoursesFragment.newInstance(title), title);
                break;
            case 7:
                title = getString(R.string.title_our_services);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_7));
                showFragment(ServicesFragment.newInstance(title), title);
                break;
            case 8:
                title = getString(R.string.title_contact_us);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_8));
                showFragment(ContactUsFragment.newInstance(title), title);
                break;
            case 9:
                title = getString(R.string.title_placed_students);
                toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.color_tile_9));
                showFragment(PlacedStudentsFragment.newInstance(title), title);
                break;
        }
    }

    private void showFragment(Fragment fragment, String title) {
        new InitializeFragment(this, "add", "yes", title).initFragment(fragment, getSupportFragmentManager());
    }

    private void checkIfComingFromNotification() {

        try {
            NotificationModel model = (NotificationModel) getIntent().getExtras().getSerializable("NotificationModel");
            if (model != null) {
                if (model.getNotificationFor().equalsIgnoreCase("6")) {
                    QuestionListModel quesModel = (QuestionListModel) getIntent().getExtras().getSerializable("QuesModel");
                    Intent intent = new Intent(HomeActivity.this, AnswerListActivity.class);
                    intent.putExtra("QuesModel", quesModel);
                    startActivity(intent);
                } else {
                    gotToNotificationFragment();
                }
            }

        } catch (Exception e) {
            Log.v("ExceptionComing", "" + e);

            //calling greeting image to view when there is no notification
            okHttpViewGreeting();
        }
    }

    private void gotToNotificationFragment() {
        String title = getString(R.string.title_notifications);
        toolbar.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        showFragment(NotificationsFragment.newInstance(title), title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(userModel.getUserId().equals("") ? R.menu.menu_before_login : R.menu.menu_after_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.itemLogin:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.itemRegister:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.itemMyProfile:
                intent = new Intent(this, MyProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.itemLogOut:
                LogOutUser logOutUser = new LogOutUser(this);
                logOutUser.sureLogOutDialog();
                break;
            case R.id.itemWeb:
                intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.360digitalgyan.com"));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linMyProfile:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void sendProfileDetailRequest() {
        if (new ConnectionDetector(this).isConnectingToInternet() && userModel != null && userModel.getUserId().trim().length() > 0) {
            ViewUserDetailsServer detailsServer = new ViewUserDetailsServer(this, active);
            detailsServer.okHttpViewUserDetailsAndSave();
        }
    }

    public void viewUpdatedProfileDetails() {
        userModel = new UserDetailsPrefs(this).getUserModel();
        if (active) {
            txtUserName.setText(userModel.getUserName());
            txtUserEmail.setText(userModel.getUserEmail());
            try {
                //Log.v("UrlImage", Urls.imageUrl + userModel.getUserImage());
                if (userModel.getUserImage().trim().length() > 1) {
                    new LoadUserImage().loadImageInImageView(this, Urls.imageUrl + userModel.getUserImage(), imgUser);
                }
            } catch (Exception e) {
                Log.v("ExceptionProfileDetail", "" + e);
            }
        }
    }

    private void okHttpViewGreeting() {
        final GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "Greeting");
        //prefs.putData("ShowGreeting_4","");
        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_GREETING, new ArrayList<KeyValueModel>());
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = res.body().string();
                try {
                    Log.v("GreetingResponse", response);
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("response") == 1) {
                        JSONObject obj = json.getJSONObject("message");
                        greetingId = obj.getString("greetings_id");
                        greetingImage = obj.getString("greetings_image");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String showGreeting = prefs.getData("ShowGreeting_" + greetingId).trim();
                            if (showGreeting.length() < 1) {
                                prefs.putData("ShowGreeting_" + greetingId, "no");
                                /*Intent intent = new Intent(HomeActivity.this, GreetingActivity.class);
                                intent.putExtra("ImagePath", Urls.imageUrl + greetingImage);
                                startActivity(intent);*/
                                if (active && greetingId.trim().length() > 0 && greetingImage.trim().length() > 1)
                                    showGreetingDialog(Urls.imageUrl + greetingImage);
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.v("GreetingException", "" + e);
                }
            }
        });

    }

    private void showGreetingDialog(String imagePath) {
        final Dialog dialog = new MyDialog(this).getMyDialog(R.layout.dialog_greeting);
        dialog.setCancelable(false);
        ImageView imgGreeting = (ImageView) dialog.findViewById(R.id.imgGreeting);
        ImageView imgCross = (ImageView) dialog.findViewById(R.id.imgCross);
        Glide.with(getApplicationContext()).load(imagePath).placeholder(R.drawable.img_placeholder).thumbnail(0.1f).into(imgGreeting);
        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (active)
            dialog.show();
    }

    private void checkIfLoggedInAndUpdateDeviceId() {
        if (userModel.getUserId().trim().length() > 0) {
            SendDeviceIdToServer server = new SendDeviceIdToServer(this);
            server.sendDeviceIdAndNotifyStatusToServer();
        }
    }

}
