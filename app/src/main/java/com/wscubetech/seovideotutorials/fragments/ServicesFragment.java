package com.wscubetech.seovideotutorials.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.adapters.ServicesAdapter;
import com.wscubetech.seovideotutorials.model.ServiceModel;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by wscubetech on 3/6/16.
 */
public class ServicesFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<ServiceModel> arrayServiceModel = new ArrayList<>();
    ServicesAdapter adapter;
    String title="";

    public static Fragment newInstance(String title){
        Fragment fragment=new ServicesFragment();
        Bundle bundle=new Bundle();
        bundle.putString("Title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        view.setOnClickListener(this);
        init(view);
        return view;
    }

    private void init(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        title=this.getArguments().getString("Title");
        HomeActivity.txtHeader.setText(title);
        arrayServiceModel=new ArrayList<>();
        fillArray();
        adapter = new ServicesAdapter(getActivity(), arrayServiceModel);
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setDuration(300);
        recyclerView.setAdapter(alphaAdapter);
    }


    public void fillArray() {
        arrayServiceModel=new ArrayList<>();

        String[] arrayTitles = getResources().getStringArray(R.array.services_titles);
        String[] arrayDesc = getResources().getStringArray(R.array.service_desc_array);
        int[] arrayImages = {
                R.drawable.service_icon_web_designing,
                R.drawable.service_icon_web_development,
                R.drawable.service_icon_android_app,
                R.drawable.service_icon_iphone_app,
                R.drawable.service_icon_search_engine_optimization,
                R.drawable.service_icon_ecommerce,
                R.drawable.service_icon_psd_to_html_conversion,
                R.drawable.service_icon_seo_package,
                R.drawable.service_icon_mobile_responsive,
                R.drawable.service_icon_web_hosting,
                R.drawable.service_icon_domain_registration
        };
        int[] arrayMainImages = {
                R.drawable.service_main_website_design,
                R.drawable.service_main_website_design,
                R.drawable.service_main_android,
                R.drawable.service_main_iphone,
                R.drawable.service_main_seo,
                R.drawable.service_main_ecommerce,
                R.drawable.service_main_psd_to_html,
                R.drawable.service_main_seo_packages,
                R.drawable.service_main_mobile_responsive,
                R.drawable.service_main_web_hosting,
                R.drawable.service_main_domain
        };
        int[] arrayBgDrawables = {
                R.drawable.selector_bg_home_item_2,
                R.drawable.selector_bg_home_item_1,
                R.drawable.selector_bg_home_item_3,
                R.drawable.selector_bg_home_item_5,
                R.drawable.selector_bg_home_item_10,
                R.drawable.selector_bg_home_item_6,
                R.drawable.selector_bg_home_item_4,
                R.drawable.selector_bg_home_item_7,
                R.drawable.selector_bg_home_item_8,
                R.drawable.selector_bg_home_item_9,
                R.drawable.selector_bg_home_item_11
        };

        for (int i = 0; i < arrayTitles.length; i++) {
            ServiceModel model = new ServiceModel();
            model.setServiceName(arrayTitles[i]);
            model.setServiceDescription(arrayDesc[i]);
            model.setServiceImage(arrayImages[i]);
            model.setServiceDrawableBg(arrayBgDrawables[i]);
            model.setServiceMainImage(arrayMainImages[i]);
            arrayServiceModel.add(model);
        }

    }

    @Override
    public void onClick(View view) {

    }
}
