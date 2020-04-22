package com.artisans.qwikhomeservices.activities.home.about;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.auth.signup.NameFragment;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;

import java.util.Objects;


public class FinishAccountSetUpActivity extends AppCompatActivity {

    private long mBackPressed;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_account_set_up);

        NameFragment nameFragment = new NameFragment();
        FragmentManager fragmentManager = Objects.requireNonNull(this).getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .add(R.id.containerFragment, nameFragment)
                .addToBackStack("nameFragment")
                .commit();
    }

    @Override
    public void onBackPressed() {
        int INTERVAL = 3000;
        if (mBackPressed + INTERVAL > System.currentTimeMillis()) {

        } else {
            DisplayViewUI.displayToast(this, "Please complete account set up");

        }

    }
}
