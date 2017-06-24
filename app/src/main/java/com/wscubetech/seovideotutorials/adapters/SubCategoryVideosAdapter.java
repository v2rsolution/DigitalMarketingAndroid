package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.VideoTutorialsTabActivity;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 25/3/17.
 */

public class SubCategoryVideosAdapter extends RecyclerView.Adapter<SubCategoryVideosAdapter.ViewHolder> {

    ArrayList<SubCategoryModel> arraySubCatModel = new ArrayList<>();
    Activity act;

    public SubCategoryVideosAdapter(Activity act, ArrayList<SubCategoryModel> arraySubCatModel) {
        this.act = act;
        this.arraySubCatModel = arraySubCatModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(act).inflate(R.layout.row_sub_category_videos, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubCategoryModel model = arraySubCatModel.get(position);
        holder.txtSubCatTitle.setText(model.getSubCatTitle() + (model.getTotal().equals("0") ? "" : " (" + model.getTotal() + ")"));
        holder.viewLine.setVisibility(position == arraySubCatModel.size() - 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return arraySubCatModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linMain;
        TextView txtSubCatTitle;
        View viewLine;

        public ViewHolder(View v) {
            super(v);
            viewLine = v.findViewById(R.id.viewLine);
            linMain = (LinearLayout) v.findViewById(R.id.linMain);
            txtSubCatTitle = (TextView) v.findViewById(R.id.txtSubCatTitle);

            linMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.linMain:
                    int position = getAdapterPosition();
                    Intent intent = new Intent(act, VideoTutorialsTabActivity.class);
                    intent.putExtra("SubCatId", arraySubCatModel.get(position).getSubCatId());
                    intent.putExtra("SubCatName", arraySubCatModel.get(position).getSubCatTitle());
                    intent.putExtra("SubCatHindiCount", arraySubCatModel.get(position).getSubCatHindiCount());
                    intent.putExtra("SubCatEnglishCount", arraySubCatModel.get(position).getSubCatEnglishCount());
                    act.startActivity(intent);
                    act.finish();
                    break;
            }
        }
    }

}
