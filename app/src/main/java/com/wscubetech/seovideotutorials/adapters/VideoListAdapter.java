package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.VideoTutorialDetailYoutubeActivity;
import com.wscubetech.seovideotutorials.model.VideoModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 20/8/16.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    ArrayList<VideoModel> arrayVideoModel = new ArrayList<>();
    Activity activity;

    public VideoListAdapter(Activity activity, ArrayList<VideoModel> arrayVideoModel) {
        this.activity = activity;
        this.arrayVideoModel = arrayVideoModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_video_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VideoModel model = arrayVideoModel.get(position);
        holder.txtTitle.setText(model.getVideoTitle().trim());
        holder.txtDescription.setText(model.getVideoDescription().trim());
        holder.txtDuration.setText(model.getVideoDuration().trim());
        holder.txtViews.setVisibility(model.getVideoViews()==0?View.GONE:View.VISIBLE);
        holder.txtViews.setText(model.getVideoViews()+" views");

        Glide.with(activity)
                .load(Urls.imageUrl+model.getVideoImage())
                .crossFade(400)
                .placeholder(R.color.color_tile_0)
                .thumbnail(0.1f)
                .into(holder.imgThumbnail);

        holder.linParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, VideoTutorialDetailYoutubeActivity.class);
                intent.putExtra("VideoModel", model);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayVideoModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDescription, txtDuration,txtViews;
        ImageView imgThumbnail;
        LinearLayout linParent;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtDescription = (TextView) v.findViewById(R.id.txtDescription);
            txtDuration = (TextView) v.findViewById(R.id.txtDuration);
            txtViews=(TextView)v.findViewById(R.id.txtViews);
            imgThumbnail = (ImageView) v.findViewById(R.id.imgThumbnail);
            linParent = (LinearLayout) v.findViewById(R.id.linParent);
        }
    }
}
