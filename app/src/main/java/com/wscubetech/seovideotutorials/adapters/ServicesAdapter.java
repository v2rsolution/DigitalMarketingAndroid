package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.MainActivity;
import com.wscubetech.seovideotutorials.activities.ServiceDetailActivity;
import com.wscubetech.seovideotutorials.model.ServiceModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 3/6/16.
 */
public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

    Activity activity;
    ArrayList<ServiceModel> arrayServiceModel = new ArrayList<>();

    public ServicesAdapter(Activity activity, ArrayList<ServiceModel> arrayServiceModel) {
        this.activity = activity;
        this.arrayServiceModel = arrayServiceModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.row_services, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ServiceModel model = arrayServiceModel.get(position);
        holder.txtService.setText(model.getServiceName());
        holder.imgService.setImageResource(model.getServiceImage());
        holder.linService.setBackgroundResource(model.getServiceDrawableBg());

        holder.linService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ServiceDetailActivity.class);
                intent.putExtra("ServiceModel", model);
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayServiceModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linService;
        TextView txtService;
        ImageView imgService;

        public ViewHolder(View v) {
            super(v);
            linService = (LinearLayout) v.findViewById(R.id.linService);
            txtService = (TextView) v.findViewById(R.id.txtService);
            imgService = (ImageView) v.findViewById(R.id.imgService);
        }
    }
}
