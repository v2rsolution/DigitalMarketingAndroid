package com.wscubetech.seovideotutorials.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.adapters.TrainingCourseAdapter;
import com.wscubetech.seovideotutorials.model.TrainingModel;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by wscubetech on 14/3/17.
 */

public class TrainingCoursesFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    String title = "";
    ArrayList<TrainingModel> arrayTrainingModel = new ArrayList<>();
    TrainingCourseAdapter adapter;
    ProgressWheel progressBar;

    public static int lastPosition = 0;

    public static Fragment newInstance(String title) {
        Fragment fragment = new TrainingCoursesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_courses, container, false);
        init(view);
        view.setOnClickListener(this);
        return view;
    }

    private void init(View v) {
        progressBar = (ProgressWheel) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (arrayTrainingModel.size() < 1 && adapter == null) {
            title = this.getArguments().getString("Title");
            HomeActivity.txtHeader.setText(title);
            new LoadInBackground().execute();
        }else{
            if(arrayTrainingModel.size()>0 && adapter!=null && adapter.getItemCount()>0){
                recyclerView.smoothScrollToPosition(lastPosition);
            }
        }
    }


    @Override
    public void onClick(View view) {

    }

    private class LoadInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            arrayTrainingModel.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] arrayTitles = getResources().getStringArray(R.array.array_training_courses);

            String[] arrayDesc = {
                    "PHP (recursive acronym for PHP: Hypertext Pre-processor) is a widely-used open source general-purpose scripting language that is especially suited for web development and can be embedded into HTML.",
                    "For Us our Students is our top priority. The training programme and curriculum has designed in such a smart way that the students could get familiar with industrial professionalism since the beginning of the training and till the completion of the curriculum.",
                    "Android software development is the process by which new applications are created for the Android operating system. Applications are usually developed in the Java programming language using the Android Software Development Kit.",
                    "An introduction to the basic Java programs construct with some brief history and building the first Java programs.",
                    "SEO stands for “search engine optimization.” It is the process of getting traffic from the “free,” “organic,” “editorial” or “natural” search results on search engines.",
                    "We Offer the Professional Course in Advance PHP for fresher as well as working professionals. We will provide you best quality training in Advance PHP. This course is basically for those students who want to became a Web Developer.",
                    "With plans to slowly retire the long-used Objective-C, Apple has introduced a new programming language, called Swift, for designing apps and applications to run on Apple iOS devices and Apple Macintosh computers.",
                    "HTML (the Hypertext Markup Language) and CSS (Cascading Style Sheets) are two of the core technologies for building Web pages. HTML provides the structure of the page, CSS the (visual and aural) layout, for a variety of devices.",
                    "WordPress is an online, open source website creation tool written in PHP. But in non-geek speak, it’s probably the easiest and most powerful blogging and website content management system (or CMS) in existence today.",
                    "An introduction to the basic C program construct with some brief history and building the first C program.",
                    "C++ is a general-purpose programming language. It has imperative, object-oriented and generic programming features",
                    "IIP Academy Offer the Professional Course in CakePHP for fresher as well as working professionals. We will provide you best quality training in CakePHP.",
                    "Magento's eCommerce training helps marketers, product managers, designers, and developers build and manage better stores. Here's how it works.",
                    "Devising strategies to drive online traffic to the company website. Tracking conversion rates and making improvements to the website. Utilising a range of techniques including paid search, SEO and PPC.",
                    "Advanced Java Tutorial is aimed towards the Developer who already has learned the Fundamentals of Java Programming. This course provides Struts Framework, Web Application Component development with Servlets, JSP and XML.",
                    "Learn about various advanced android topics such as Firebase Cloud Messaging integration, Third party library integration, Google Places api, location tracking etc and and boost your hirability",
                    "Manual testing is the process of manually testing software for defects. It requires a tester to play the role of an end user whereby they utilise most of the application's features to ensure correct behavior.",
                    "Automated testing tools are capable of executing tests, reporting outcomes and comparing results with earlier test runs. Tests carried out with these tools can be run repeatedly, at any time of day. The method or process being used to implement automation is called a test automation framework.",
                    "Laravel is a powerful MVC PHP framework with expressive, elegant syntax. We believe development must be an enjoyable, creative experience to be truly fulfilling. Laravel is accessible, yet powerful, providing powerful tools needed for large, robust applications.",
                    "Rails is a software library that extends the ruby programming language.Rails is also an MVC (model, view, controller) framework where all layers are provided by Rails, as opposed to reliance on other, additional frameworks to achieve full MVC support.",
                    "Python is a general-purpose interpreted, interactive, object-oriented, and high-level programming language. It's fun to work in Python because it allows you to think about the problem rather than focusing on the syntax.",
                    "Graphic design, also known as communication design, is the art and practice of planning and projecting ideas and experiences with visual and textual content."
            };


            int[] arrayImage = {
                    R.drawable.ic_training_php,
                    R.drawable.ic_training_web_development,
                    R.drawable.ic_training_android,
                    R.drawable.ic_training_java,
                    R.drawable.ic_training_seo,
                    R.drawable.ic_training_adv_php,
                    R.drawable.ic_training_iphone,
                    R.drawable.ic_training_html_css,
                    R.drawable.ic_training_wordpress,
                    R.drawable.ic_training_c,
                    R.drawable.ic_training_cpp,
                    R.drawable.ic_training_cake_php,
                    R.drawable.ic_training_magento,
                    R.drawable.ic_training_digital_marketing,
                    R.drawable.ic_training_adv_java,
                    R.drawable.ic_training_adv_android,
                    R.drawable.ic_training_testing_manual,
                    R.drawable.ic_training_testing_automation,
                    R.drawable.ic_training_laravel,
                    R.drawable.ic_training_ruby_on_rails,
                    R.drawable.ic_training_python,
                    R.drawable.ic_training_graphic_designing
            };

            for (int i = 0; i < arrayTitles.length; i++) {
                TrainingModel model = new TrainingModel();
                model.setName(arrayTitles[i]);
                model.setDescription(arrayDesc[i]);
                model.setDuration("2 Months");
                model.setEligibility("BCA, B.Tech, B.Sc.IT, MCA, M.Sc.IT, M.Tech, PGDCA");
                model.setImage(arrayImage[i]);
                arrayTrainingModel.add(model);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new TrainingCourseAdapter(getActivity(), arrayTrainingModel, getActivity().getSupportFragmentManager());
            ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
            recyclerView.setAdapter(animationAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }
}
