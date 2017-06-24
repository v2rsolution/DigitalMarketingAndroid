package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.PlacedStudentModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 14/3/17.
 */

public class PlacedStudentsAdapter extends RecyclerView.Adapter<PlacedStudentsAdapter.ViewHolder> {

    Activity act;
    ArrayList<PlacedStudentModel> arrayModel = new ArrayList<>();
    public static final String mainImageUrl = "http://www.wscubetech.com/api/";

    public PlacedStudentsAdapter(Activity act, ArrayList<PlacedStudentModel> arrayModel) {
        this.act = act;
        this.arrayModel = arrayModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(act).inflate(R.layout.row_placed_student, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlacedStudentModel model = arrayModel.get(position);
        holder.txtName.setText(model.getName().trim());
        try {
            //Log.v("ImageUrl", mainImageUrl + "../placement/" + model.getImage().trim());
            Glide.with(act).load(mainImageUrl + "../placement/" + model.getImage()).thumbnail(0.1f).placeholder(R.drawable.dummy_user_image).into(holder.imgStudent);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return arrayModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtTitle;
        ImageView imgStudent;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            imgStudent = (ImageView) v.findViewById(R.id.imgStudent);
        }
    }
}
