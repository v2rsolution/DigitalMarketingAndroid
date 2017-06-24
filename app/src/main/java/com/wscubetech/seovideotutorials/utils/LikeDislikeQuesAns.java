package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.util.Log;

import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.model.KeyValueModel;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 15/4/17.
 */

public class LikeDislikeQuesAns {

    Activity act;

    public LikeDislikeQuesAns(Activity act) {
        this.act = act;
    }

    //likeDislikeFlag => 1 for ques,  2 for answer
    //likeDislikeStatus => 0->default,  1-> Like,  2->Dislike
    public void okHttpLikeDislike(String quesAnsId,String userId,String likeDislikeFlag,String likeDislikeStatus){
        ArrayList<KeyValueModel> arrayKeyValueModel=new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("question_answer_id",quesAnsId));
        arrayKeyValueModel.add(new KeyValueModel("user_id",userId));
        arrayKeyValueModel.add(new KeyValueModel("like_dislike_flag",likeDislikeFlag));
        arrayKeyValueModel.add(new KeyValueModel("like_dislike_status",likeDislikeStatus));
        OkHttpCalls calls=new OkHttpCalls(Urls.LIKE_DISLIKE_QUES_ANS,arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response=res.body().string();
                Log.v("ResponseLikeDislike",response);
            }
        });
    }

}
