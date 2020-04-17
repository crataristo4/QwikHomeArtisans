package com.artisans.qwikhomeservices.activities.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.auth.signup.SignUpServicePersonelActivity;
import com.artisans.qwikhomeservices.adapters.SlidePagerAdapter;
import com.artisans.qwikhomeservices.databinding.ActivityOnboardingScreenBinding;

import me.relex.circleindicator.CircleIndicator;

public class OnboardingScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Removes anything relating to a title
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //Splash Screen activity displays in a full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityOnboardingScreenBinding activityOnboardingScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding_screen);

        CircleIndicator indicator = activityOnboardingScreenBinding.slideDots;
        ViewPager viewPager = activityOnboardingScreenBinding.viewPager;
        SlidePagerAdapter slidePagerAdapter = new SlidePagerAdapter(this);

        viewPager.setAdapter(slidePagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setBackgroundColor(getResources().getColor(R.color.amber));
    }

    public void loginSignUpAction(View view) {

        if (view.getId() == R.id.btnNext) {
            startActivity(
                    new Intent(OnboardingScreenActivity.this, SignUpServicePersonelActivity.class)
            );
        }
    }
}
