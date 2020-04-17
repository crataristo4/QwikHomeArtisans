package com.artisans.qwikhomeservices.activities.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding activitySplashScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.fadein, R.anim.explode);
        super.onCreate(savedInstanceState);
        activitySplashScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        activitySplashScreenBinding.imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_top));
        activitySplashScreenBinding.textView2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_bottom));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Opens the Welcome Screen Activity once the time elapses
                startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                finish();
            }
        }, 3000);
    }


}
