/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wscubetech.seovideotutorials.R;

public class ImageEnlargeActivity extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enlarge);
        img = (ImageView) findViewById(R.id.img);

        String imagePath = getIntent().getExtras().getString("ImagePath");
        Glide.with(this).load(imagePath).placeholder(R.drawable.img_placeholder).into(img);
    }
}
