package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;

/**
 * Created by wscubetech on 19/9/16.
 */
public class OfflineResponse {
    String prefName;
    Activity activity;
    GetSetSharedPrefs prefs;

    public static final String VIDEO_TUTORIALS="VideoTutorials";

    public static final String INTERVIEW_QUES_1="InterviewSubCat";
    public static final String INTERVIEW_QUES_2="InterviewQues_";

    public static final String QUES_LIST="QuesList_";
    public static final String ANS_LIST="AnsList_";

    public static final String STUDY_MATERIAL="StudyMaterial_";


    public static final String QUIZ_TESTS="QuizTests";
    public static final String QUIZ_QUES="QuizQues_";

    public static final String PLACED_STUDENTS="PlacedStudents";
    public static final String NOTIFICATIONS="Notifications";

    public OfflineResponse(Activity activity,String prefName){
        this.prefName=prefName;
        this.activity=activity;
        prefs=new GetSetSharedPrefs(activity,prefName);
    }

    public void setResponse(String key,String response){
        prefs.putData(key,response);
    }

    public String getResponse(String key){
        return prefs.getData(key);
    }

}
