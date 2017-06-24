package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.ImageEnlargeActivity;
import com.wscubetech.seovideotutorials.model.StudyMaterialModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 29/3/17.
 */

public class StudyMaterialAnsPagerAdapter extends PagerAdapter {

    LayoutInflater inflater;
    ArrayList<StudyMaterialModel> arrayModel = new ArrayList<>();
    Activity act;
    int position;

    String response;

    public StudyMaterialAnsPagerAdapter(Activity act, ArrayList<StudyMaterialModel> arrayModel) {
        this.act = act;
        this.arrayModel = arrayModel;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return arrayModel.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.row_study_material_ans_view_pager, container, false);
        initAndSet(v, position);
        container.addView(v);
        return v;
    }

    private void initAndSet(View v, int position) {
        TextView txtAns, txtQuesTitle;
        final ImageView imgAns;
        TextView txtQuesSerialNo;
        txtQuesSerialNo = (TextView) v.findViewById(R.id.txtQuesSerialNo);
        txtAns = (TextView) v.findViewById(R.id.txtAnswer);
        txtQuesTitle = (TextView) v.findViewById(R.id.txtQuesTitle);
        imgAns = (ImageView) v.findViewById(R.id.imgAns);


        StudyMaterialModel model = arrayModel.get(position);
        txtQuesTitle.setText(model.getStudyQues());
        txtAns.setText(model.getStudyAns());
        txtQuesSerialNo.setText(String.valueOf(position + 1));


        final String imagePath = Urls.imageUrl + model.getStudyImage();
        if (model.getStudyImage().trim().length() > 1) {
            Glide.with(act).load(imagePath).placeholder(R.drawable.img_placeholder).thumbnail(0.1f).into(imgAns);
            imgAns.setVisibility(View.VISIBLE);
            imgAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(act, ImageEnlargeActivity.class);
                    intent.putExtra("ImagePath", imagePath);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(act, imgAns, "imgAns");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        act.startActivity(intent, optionsCompat.toBundle());
                    } else {
                        act.startActivity(intent);
                    }
                }
            });
        } else {
            imgAns.setVisibility(View.GONE);
        }
        //Log.v("ImageUrl", Urls.imageUrl + "studymaterial/" + model.getStudyImage());
    }


}
