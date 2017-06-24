package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.QuizPlayActivity;
import com.wscubetech.seovideotutorials.model.TestPaperModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 23/8/16.
 */
public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.ViewHolder> {

    ArrayList<TestPaperModel> arrayTestPaperModel = new ArrayList<>();
    Activity activity;


    public TestListAdapter(Activity activity, ArrayList<TestPaperModel> arrayTestPaperModel) {
        this.activity = activity;
        this.arrayTestPaperModel = arrayTestPaperModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_test_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TestPaperModel model = arrayTestPaperModel.get(position);
        holder.txtTestTitle.setText(model.getPaperTitle());
        holder.txtTakeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, QuizPlayActivity.class);
                intent.putExtra("TestPaperModel", model);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayTestPaperModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTakeQuiz, txtTestTitle;

        public ViewHolder(View v) {
            super(v);
            txtTakeQuiz = (TextView) v.findViewById(R.id.txtTakeQuiz);
            txtTestTitle = (TextView) v.findViewById(R.id.txtTestTitle);
        }
    }
}
