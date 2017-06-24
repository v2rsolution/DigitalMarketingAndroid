package com.wscubetech.seovideotutorials.user_model;

import android.app.Activity;
import android.text.Html;
import android.util.Log;

import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.activities.MyProfileActivity;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 6/4/17.
 */

public class ViewUserDetailsServer {
    Activity activity;
    UserModel userModel;

    boolean active;

    public ViewUserDetailsServer(Activity activity, boolean active) {
        this.activity = activity;
        this.active = active;
        userModel = new UserDetailsPrefs(activity).getUserModel();
    }

    public void okHttpViewUserDetailsAndSave() {
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("seo_users_id", userModel.getUserId()));
        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_PROFILE, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = Html.fromHtml(res.body().string()).toString();
                try {
                    Log.v("ProfileResponse", response);
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("result") == 1) {
                        JSONObject obj = json.getJSONObject("data");
                        userModel.setUserName(obj.getString("seo_users_name"));
                        userModel.setUserImage(obj.getString("seo_users_image"));
                        userModel.setUserEmail(obj.getString("seo_users_email"));
                        userModel.setUserPassword(obj.getString("seo_users_password").trim());
                        userModel.setGoogleLogIn(obj.getString("flag").equals("1"));
                        userModel.setNotify(obj.getString("seo_notification_status").trim().equalsIgnoreCase("1"));
                        UserDetailsPrefs prefs = new UserDetailsPrefs(activity);
                        prefs.setUserModel(userModel);

                        if (active) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (activity instanceof HomeActivity) {
                                        ((HomeActivity) activity).viewUpdatedProfileDetails();
                                    } else if (activity instanceof MyProfileActivity) {
                                        ((MyProfileActivity) activity).getSetUserDetails();
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.v("ProfileParsing", "" + e);
                }
            }
        });
    }

}
