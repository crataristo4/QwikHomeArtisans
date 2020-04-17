package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding activityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Intent intent = getIntent();

        String position = intent.getStringExtra("position");
        String name = intent.getStringExtra("name");
        String about = intent.getStringExtra("about");
        String image = intent.getStringExtra("image");

        activityDetailsBinding.name.setText(name);
        activityDetailsBinding.about.setText(about);

        //MainActivity.retrieveSingleUserDetails(position,activityDetailsBinding.name,activityDetailsBinding.about,activityDetailsBinding.itemImage);
    }
}
