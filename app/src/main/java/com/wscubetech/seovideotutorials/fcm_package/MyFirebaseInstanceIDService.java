package com.wscubetech.seovideotutorials.fcm_package;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;
import com.wscubetech.seovideotutorials.utils.SendDeviceIdToServer;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wscubetech on 26/11/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    UserModel userModel;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //Getting registration token
        userModel = new UserDetailsPrefs(getApplicationContext()).getUserModel();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.v(TAG, "Refreshed_token: " + refreshedToken);
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(getApplicationContext(), "Device");
        prefs.putData("DeviceId", refreshedToken);
        sendRegistrationToServer();
    }

    private void sendRegistrationToServer() {
        SendDeviceIdToServer server = new SendDeviceIdToServer(this);
        server.sendDeviceIdAndNotifyStatusToServer();
    }


}
