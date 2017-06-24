/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.adapters.ReviewAdapter;
import com.wscubetech.seovideotutorials.model.QuizModel;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader;
    ViewPager viewPager;

    ArrayList<QuizModel> arrayQuizModel = new ArrayList<>();
    ReviewAdapter adapter;

    TextView txtPrevious, txtNext;
    int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        init();
        toolbarOperation();

        getQuizModelAndSetAdapter();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                previousNextAdjustment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        previousNextAdjustment();
        txtPrevious.setOnClickListener(this);
        txtNext.setOnClickListener(this);
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        txtNext = (TextView) findViewById(R.id.txtNext);
        txtPrevious = (TextView) findViewById(R.id.txtPrevious);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

    }

    public void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText(getString(R.string.title_review));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void previousNextAdjustment() {
        txtPrevious.setVisibility(currentPosition == 0 ? View.INVISIBLE : View.VISIBLE);
        txtNext.setText(currentPosition == arrayQuizModel.size() - 1 ? "Finish" : "Next");
    }

    public void getQuizModelAndSetAdapter() {
        arrayQuizModel = (ArrayList<QuizModel>) getIntent().getExtras().getSerializable("ArrayQuizModel");

        adapter = new ReviewAdapter(this, arrayQuizModel);
        viewPager.setAdapter(adapter);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtPrevious:
                currentPosition -= 1;
                viewPager.setCurrentItem(currentPosition);
                break;
            case R.id.txtNext:
                if (currentPosition != arrayQuizModel.size() - 1) {
                    currentPosition += 1;
                    viewPager.setCurrentItem(currentPosition);
                } else {
                    finish();
                }
                break;
        }
    }
}
