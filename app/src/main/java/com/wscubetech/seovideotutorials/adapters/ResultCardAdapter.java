package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.ResultCardModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 24/8/16.
 */
public class ResultCardAdapter extends RecyclerView.Adapter<ResultCardAdapter.ViewHolder> {

    ArrayList<ResultCardModel> arrayResultCardModel = new ArrayList<>();
    Activity activity;

    public ResultCardAdapter(Activity activity, ArrayList<ResultCardModel> arrayResultCardModel) {
        this.activity = activity;
        this.arrayResultCardModel = arrayResultCardModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_result_cards, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResultCardModel model = arrayResultCardModel.get(position);
        holder.txtTitle.setText(model.getTitle());
        holder.txtTitle.setTextColor(ContextCompat.getColor(activity, model.getResTextColor()));

        holder.txtScore.setText(model.getScore());
        holder.txtScore.setBackgroundResource(model.getResBg());

        holder.imgIcon.setBackgroundResource(model.getResBg());
        holder.imgIcon.setImageResource(model.getResSrc());
    }

    @Override
    public int getItemCount() {
        return arrayResultCardModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtScore;
        ImageView imgIcon;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtScore = (TextView) v.findViewById(R.id.txtScore);
            imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
        }
    }
}
