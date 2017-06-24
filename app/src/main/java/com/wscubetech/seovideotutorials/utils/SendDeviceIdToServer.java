package com.wscubetech.seovideotutorials.utils;

import android.content.Context;
import android.util.Log;

import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wscubetech on 25/4/17.
 */

public class SendDeviceIdToServer {
    Context context;

    public SendDeviceIdToServer(Context context) {
        this.context = context;
    }

    public void sendDeviceIdAndNotifyStatusToServer() {
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(context, "Device");
        String deviceIdToken = prefs.getData("DeviceId");
        UserModel userModel = new UserDetailsPrefs(context).getUserModel();
        if (new ConnectionDetector(context).isConnectingToInternet()) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.addRegId).newBuilder();
            urlBuilder.addQueryParameter(Constants.Reg_ID, deviceIdToken);
            urlBuilder.addQueryParameter(Constants.USER_ID, userModel.getUserId());
            urlBuilder.addQueryParameter(Constants.Reg_ID_Flag,Constants.Reg_ID_Flag_Value);
            String notify = userModel.isNotify() ? "1" : "0";
            Log.v("NotifyOnOff", notify);
            urlBuilder.addQueryParameter(Constants.NOTIFY_0_1, notify);
            String url = urlBuilder.build().toString();

            final Request request = new Request.Builder()
                    .url(url)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.v("Failure", "" + e);
                }

                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    Log.v("ResponseDid", res.body().string());
                }
            });
        }
    }
}
