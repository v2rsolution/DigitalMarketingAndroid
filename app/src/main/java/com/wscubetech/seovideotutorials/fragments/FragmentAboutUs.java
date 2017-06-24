package com.wscubetech.seovideotutorials.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.HomeActivity;

public class FragmentAboutUs extends Fragment {

    public static Fragment newInstance(String title){
        Fragment fragment=new FragmentAboutUs();
        Bundle bundle=new Bundle();
        bundle.putString("Title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    TextView heading, about_us_text_first, about_us_text_second, about_us_text_third, how_we_work, listen, plan,
            design, execute;


    public FragmentAboutUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = (ViewGroup) inflater.inflate(R.layout.fragment_about_us, container, false);

        init(v);
        String first = "<font color='#152c34'> WsCube Tech Is </font>";
        String second = "<font color='#10a3b3'> A Place Where </font>";
        String third = "<font color='#152c34'> We Convert </font>";
        String forth = "<font color='#ff4b34'> Your Dream Into Reality </font>";

        about_us_text_second.setText(Html.fromHtml(first + second + "<br>" + third + forth));

        how_we_work.setText(Html.fromHtml("<b> How </b>" + "We Work"));

        SpannableString ss1 = new SpannableString(getResources().getString(R.string.we_listen_to_you));
        ss1.setSpan(new RelativeSizeSpan(1.5f), 3, 9, 0); // set size
        listen.setText(ss1);

        SpannableString ss2 = new SpannableString(getResources().getString(R.string.we_plan_your_work));
        ss2.setSpan(new RelativeSizeSpan(1.5f), 3, 7, 0); // set size
        plan.setText(ss2);

        SpannableString ss3 = new SpannableString(getResources().getString(R.string.we_design_creatively));
        ss3.setSpan(new RelativeSizeSpan(1.5f), 3, 9, 0); // set size
        design.setText(ss3);

        SpannableString ss4 = new SpannableString(getResources().getString(R.string.we_execute_publish_maintain));
        ss4.setSpan(new RelativeSizeSpan(1.5f), 3, 10, 0); // set size
        execute.setText(ss4);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        HomeActivity.txtHeader.setText(this.getArguments().getString("Title"));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void init(View v) {
        heading = (TextView) v.findViewById(R.id.heading);
        about_us_text_first = (TextView) v.findViewById(R.id.about_us_text_first);
        about_us_text_second = (TextView) v.findViewById(R.id.about_us_text_second);
        about_us_text_third = (TextView) v.findViewById(R.id.about_us_text_third);
        how_we_work = (TextView) v.findViewById(R.id.how_we_work);
        listen = (TextView) v.findViewById(R.id.listen);
        plan = (TextView) v.findViewById(R.id.plan);
        design = (TextView) v.findViewById(R.id.design);
        execute = (TextView) v.findViewById(R.id.execute);


        /*about_us_text_first.setTypeface(CustomFont.setFontRegular(getActivity().getAssets()));
        about_us_text_second.setTypeface(CustomFont.setFontRegular(getActivity().getAssets()));
        about_us_text_third.setTypeface(CustomFont.setFontRegular(getActivity().getAssets()));*/
    }

}
