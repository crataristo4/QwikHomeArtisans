package com.artisans.qwikhomeservices.activities.auth.signup;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.ActivitySignInWithPhoneNumberBinding;

public class SignInWithPhoneNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        ActivitySignInWithPhoneNumberBinding activitySignInWithPhoneNumberBinding = DataBindingUtil.
                setContentView(this, R.layout.activity_sign_in_with_phone_number);

        NameFragment nameFragment = new NameFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.container, nameFragment)
                .addToBackStack("nameFragment")
                .commit();
    }
}
