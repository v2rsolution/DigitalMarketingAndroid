package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.app.Dialog;
import android.text.Html;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.model.KeyValueModel;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 11/4/17.
 */

public class ViewQuesTagsFromServer {

    Activity act;
    Dialog dialogProgress;
    String tagArray[];
    AutoCompleteTextView autoTag;
    AdapterView.OnItemClickListener onItemClickListener;

    public ViewQuesTagsFromServer(Activity act) {
        this.act = act;
    }

    public ViewQuesTagsFromServer(Activity act, Dialog dialogProgress) {
        this(act);
        this.dialogProgress = dialogProgress;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setAutoCompleteTextView(AutoCompleteTextView autoTag) {
        this.autoTag = autoTag;
    }

    public String[] getTagArray() {
        return tagArray;
    }

    public void setTagArray(String[] tagArray) {
        this.tagArray = tagArray;
    }

    public AutoCompleteTextView getAutoTag() {
        return autoTag;
    }

    public void setAutoTag(AutoCompleteTextView autoTag) {
        this.autoTag = autoTag;
    }

    public void okHttpGetTags() {
        if (dialogProgress != null)
            dialogProgress.show();

        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("tag_main_cat_id", Constants.SEO_CAT_ID));
        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_TAGS, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponseViewTags(true, "");
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = Html.fromHtml(res.body().string()).toString();
                handleResponseViewTags(false, response);
            }
        });
    }

    public void handleResponseViewTags(final boolean failed, final String response) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogProgress != null && dialogProgress.isShowing())
                    dialogProgress.dismiss();
                if (failed) {
                    Toast.makeText(act, act.getString(R.string.networkError), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Log.v("ResponseTags", response);
                        JSONObject json = new JSONObject(response);
                        if (json.getInt("response") == 1) {
                            String message = json.getString("message").trim();
                            if (message.length() > 1) {
                                tagArray = message.toLowerCase().split(",");
                                ArrayAdapter adapter = new ArrayAdapter(act, R.layout.row_auto_complete_tag, tagArray);
                                autoTag.setAdapter(adapter);
                                autoTag.setOnItemClickListener(onItemClickListener);
                            }
                        }
                    } catch (Exception e) {
                        Log.v("TagParsing", "" + e);
                    }
                }
            }
        });
    }


}
