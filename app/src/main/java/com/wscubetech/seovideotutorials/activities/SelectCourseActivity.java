package com.wscubetech.seovideotutorials.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.adapters.CourseAdapter;
import com.wscubetech.seovideotutorials.model.CourseModel;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;
import com.wscubetech.seovideotutorials.utils.GridSpacingItemDecoration;

import java.util.ArrayList;

public class SelectCourseActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<CourseModel> arrayCourseModel = new ArrayList<>();
    CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_select_course);
        init();
        decideIfCourseAlreadySelected();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.dim_7), true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == arrayCourseModel.size() - 1 ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void decideIfCourseAlreadySelected() {
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "CourseSelection");
        String selectedCourseId = prefs.getData("SelectedCourseId");
        if (selectedCourseId.trim().length() > 0) {
            proceedWithCourse(selectedCourseId);
        } else {
            setCourseItemsInRecyclerView();
        }
    }

    private void setCourseItemsInRecyclerView() {
        arrayCourseModel.clear();
        for (int i = 0; i < 5; i++) {
            CourseModel model = new CourseModel();
            switch (i) {
                case 0:
                    model.courseId = Constants.DIGITAL_MARKETING_ID;
                    model.courseName = "Digital Marketing";
                    model.courseIcon = R.drawable.img_select_course_digital_marketing;
                    model.courseRes = R.drawable.bg_select_course_digital_marketing;
                    break;
                case 1:
                    model.courseId = Constants.JAVA_ID;
                    model.courseName = "Java";
                    model.courseIcon = R.drawable.img_select_course_java;
                    model.courseRes = R.drawable.bg_select_course_java;
                    break;
                case 2:
                    model.courseId = Constants.ANDROID_ID;
                    model.courseName = "Android";
                    model.courseIcon = R.drawable.img_select_course_android;
                    model.courseRes = R.drawable.bg_select_course_android;
                    break;
                case 3:
                    model.courseId = Constants.SOFTWARE_TESTING_ID;
                    model.courseName = "Software Testing";
                    model.courseIcon = R.drawable.img_select_course_testing;
                    model.courseRes = R.drawable.bg_select_course_testing;
                    break;
                case 4:
                    model.courseId = Constants.WEBSITE_DEVELOPMENT_ID;
                    model.courseName = "Web Designing & Development";
                    model.courseIcon = R.drawable.img_select_course_web;
                    model.courseRes = R.drawable.bg_select_course_web;
                    break;
            }
            arrayCourseModel.add(model);
        }
        adapter = new CourseAdapter(this, arrayCourseModel);
        recyclerView.setAdapter(adapter);
    }

    public void onClickCourse(int position) {
        CourseModel courseModel = arrayCourseModel.get(position);
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "CourseSelection");
        prefs.putData("SelectedCourseId", courseModel.courseId);
        prefs.putData("SelectedCourseName", courseModel.courseName);
        proceedWithCourse(courseModel.courseId);
    }

    private void proceedWithCourse(String selectedCourseId) {
        Constants.SEO_CAT_ID = selectedCourseId;
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


}
