package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.QuizModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 24/8/16.
 */
public class ReviewAdapter extends PagerAdapter {

    Activity activity;
    ArrayList<QuizModel> arrayQuizModel = new ArrayList<>();

    LayoutInflater inflater;

    //Question Part
    TextView txtQuesTitle, txtQuesSerialNo, txtTimer;

    //Options Part
    TextView txtOption1, txtOption2, txtOption3, txtOption4, txtOption5;
    ImageView imgOption1, imgOption2, imgOption3, imgOption4, imgOption5;
    LinearLayout linOption1, linOption2, linOption3, linOption4, linOption5;

    CardView card3, card4, card5;

    View viewUserInput1, viewUserInput2, viewUserInput3, viewUserInput4, viewUserInput5;

    public ReviewAdapter(Activity activity, ArrayList<QuizModel> arrayQuizModel) {
        this.activity = activity;
        this.arrayQuizModel = arrayQuizModel;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return arrayQuizModel.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.row_review, container, false);
        initAndSet(v, position);
        container.addView(v);
        return v;
    }

    public void initAndSet(View v, final int position) {

        txtQuesSerialNo = (TextView) v.findViewById(R.id.txtQuesSerialNo);
        txtQuesTitle = (TextView) v.findViewById(R.id.txtQuesTitle);
        txtTimer = (TextView) v.findViewById(R.id.txtTimer);

        txtOption1 = (TextView) v.findViewById(R.id.txtOption1);
        txtOption2 = (TextView) v.findViewById(R.id.txtOption2);
        txtOption3 = (TextView) v.findViewById(R.id.txtOption3);
        txtOption4 = (TextView) v.findViewById(R.id.txtOption4);
        txtOption5 = (TextView) v.findViewById(R.id.txtOption5);

        imgOption1 = (ImageView) v.findViewById(R.id.imgOption1);
        imgOption2 = (ImageView) v.findViewById(R.id.imgOption2);
        imgOption3 = (ImageView) v.findViewById(R.id.imgOption3);
        imgOption4 = (ImageView) v.findViewById(R.id.imgOption4);
        imgOption5 = (ImageView) v.findViewById(R.id.imgOption5);

        linOption1 = (LinearLayout) v.findViewById(R.id.linOption1);
        linOption2 = (LinearLayout) v.findViewById(R.id.linOption2);
        linOption3 = (LinearLayout) v.findViewById(R.id.linOption3);
        linOption4 = (LinearLayout) v.findViewById(R.id.linOption4);
        linOption5 = (LinearLayout) v.findViewById(R.id.linOption5);

        card3 = (CardView) v.findViewById(R.id.card3);
        card4 = (CardView) v.findViewById(R.id.card4);
        card5 = (CardView) v.findViewById(R.id.card5);

        viewUserInput1 = v.findViewById(R.id.viewUserInput1);
        viewUserInput2 = v.findViewById(R.id.viewUserInput2);
        viewUserInput3 = v.findViewById(R.id.viewUserInput3);
        viewUserInput4 = v.findViewById(R.id.viewUserInput4);
        viewUserInput5 = v.findViewById(R.id.viewUserInput5);

        linOption1.setVisibility(View.VISIBLE);
        linOption2.setVisibility(View.VISIBLE);
        linOption3.setVisibility(View.VISIBLE);
        linOption4.setVisibility(View.VISIBLE);
        linOption5.setVisibility(View.VISIBLE);

        card3.setVisibility(View.VISIBLE);
        card4.setVisibility(View.VISIBLE);
        card5.setVisibility(View.VISIBLE);

        setUiQuesCard(position);
        setUiOptionCards(position);
    }

    public void setUiQuesCard(int position) {
        QuizModel model = arrayQuizModel.get(position);
        txtQuesSerialNo.setText((position + 1) + " / " + getCount());
        txtTimer.setText(model.getTimerValue());
        txtQuesTitle.setText(model.getQues());
    }

    public void setUiOptionCards(int position) {
        clearAllClickUiDisplay();
        QuizModel model = arrayQuizModel.get(position);
        txtOption1.setText(model.getOption1());
        txtOption2.setText(model.getOption2());
        txtOption3.setText(model.getOption3());
        txtOption4.setText(model.getOption4());
        txtOption5.setText(model.getOption5());

        if (model.getOption5().trim().equalsIgnoreCase("")) {
            linOption5.setVisibility(View.GONE);
            card5.setVisibility(View.GONE);
        }

        if (model.getOption4().trim().equalsIgnoreCase("")) {
            linOption4.setVisibility(View.GONE);
            card4.setVisibility(View.GONE);
        }

        if (model.getOption3().trim().equalsIgnoreCase("")) {
            linOption3.setVisibility(View.GONE);
            card3.setVisibility(View.GONE);
        }

        switch (model.getUserInput()) {
            case "1":
                setUiCorrectOptionAnswer(imgOption1, linOption1, viewUserInput1, 2);
                break;
            case "2":
                setUiCorrectOptionAnswer(imgOption2, linOption2, viewUserInput2, 2);
                break;
            case "3":
                setUiCorrectOptionAnswer(imgOption3, linOption3, viewUserInput3, 2);
                break;
            case "4":
                setUiCorrectOptionAnswer(imgOption4, linOption4, viewUserInput4, 2);
                break;
            case "5":
                setUiCorrectOptionAnswer(imgOption5, linOption5, viewUserInput5, 2);
                break;
        }

        switch (model.getCorrectAnswerSerialNo()) {
            case "1":
                setUiCorrectOptionAnswer(imgOption1, linOption1, viewUserInput1, 1);
                break;
            case "2":
                setUiCorrectOptionAnswer(imgOption2, linOption2, viewUserInput2, 1);
                break;
            case "3":
                setUiCorrectOptionAnswer(imgOption3, linOption3, viewUserInput3, 1);
                break;
            case "4":
                setUiCorrectOptionAnswer(imgOption4, linOption4, viewUserInput4, 1);
                break;
            case "5":
                setUiCorrectOptionAnswer(imgOption5, linOption5, viewUserInput5, 1);
                break;
        }

    }

    //flag=1 correct (green)
    //flag=2 wrong (red)
    public void setUiCorrectOptionAnswer(ImageView imageView, LinearLayout linearLayout, View view, int flag) {
        if (flag == 1) {
            imageView.setBackgroundResource(R.color.colorTeal);
            imageView.setImageResource(R.drawable.ic_check_box_black_24dp);
            linearLayout.setBackgroundResource(R.drawable.bg_option_box_teal_boundary);
        } else {
            imageView.setBackgroundResource(R.color.colorOptionRed);
            linearLayout.setBackgroundResource(R.drawable.bg_option_box_red_boundary);
            view.setVisibility(View.VISIBLE);
        }
    }

    public void clearAllClickUiDisplay() {
        linOption1.setBackgroundResource(android.R.color.white);
        linOption2.setBackgroundResource(android.R.color.white);
        linOption3.setBackgroundResource(android.R.color.white);
        linOption4.setBackgroundResource(android.R.color.white);
        linOption5.setBackgroundResource(android.R.color.white);

        imgOption1.setBackgroundResource(R.color.colorOptionGrey);
        imgOption2.setBackgroundResource(R.color.colorOptionGrey);
        imgOption3.setBackgroundResource(R.color.colorOptionGrey);
        imgOption4.setBackgroundResource(R.color.colorOptionGrey);
        imgOption5.setBackgroundResource(R.color.colorOptionGrey);

        imgOption1.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption2.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption3.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption4.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        imgOption5.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);

        viewUserInput1.setVisibility(View.GONE);
        viewUserInput2.setVisibility(View.GONE);
        viewUserInput3.setVisibility(View.GONE);
        viewUserInput4.setVisibility(View.GONE);
        viewUserInput5.setVisibility(View.GONE);
    }
}
