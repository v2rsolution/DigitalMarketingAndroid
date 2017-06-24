package com.wscubetech.seovideotutorials.utils;


import android.util.Log;

import com.wscubetech.seovideotutorials.model.KeyValueModel;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by wscubetech on 4/10/16.
 */
public class OkHttpCalls {

    String strUrl = "";
    ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();

    Call call;

    public OkHttpCalls(String strUrl, ArrayList<KeyValueModel> arrayKeyValueModel) {
        this.strUrl = strUrl;
        this.arrayKeyValueModel = arrayKeyValueModel;
    }

    public void initiateCall(Callback callback) {
        HttpUrl.Builder builder = HttpUrl.parse(strUrl).newBuilder();
        for (KeyValueModel model : arrayKeyValueModel)
            builder.addQueryParameter(model.getKey(), model.getValue());

        String url = builder.build().toString();

        Log.v("Url", url);

        Request request = new Request.Builder().url(url).build();

        OkHttpClient client = new OkHttpClient();

        call = client.newCall(request);

        call.enqueue(callback);

    }

    public void initiateCallPost(Callback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (KeyValueModel model : arrayKeyValueModel)
            builder.addFormDataPart(model.getKey(), model.getValue());

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(strUrl)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        call = client.newCall(request);

        call.enqueue(callback);
    }

    public void initiateCallWithImageAndOtherValues(File sourceImageFile, Callback callback) {

        Request request = new Request.Builder()
                .url(strUrl)
                .post(prepareRequestBody(sourceImageFile, arrayKeyValueModel))
                .build();

        OkHttpClient client = new OkHttpClient();

        call = client.newCall(request);

        call.enqueue(callback);

    }

    public RequestBody prepareRequestBody(File sourceImageFile, ArrayList<KeyValueModel> arrayKeyValueModel) {
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        String imagePath = sourceImageFile.getAbsolutePath();
        String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (KeyValueModel model : arrayKeyValueModel) {
            if (model.isFile()) {
                builder.addFormDataPart(model.getKey(), filename, RequestBody.create(MEDIA_TYPE_PNG, sourceImageFile));
            } else {
                builder.addFormDataPart(model.getKey(), model.getValue());
            }
        }

        RequestBody requestBody = builder.build();

        return requestBody;
    }

    public Call getCall() {
        return call;
    }

}
