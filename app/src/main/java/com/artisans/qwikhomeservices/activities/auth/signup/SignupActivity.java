package com.artisans.qwikhomeservices.activities.auth.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.auth.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout txtFullName, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        txtEmail = findViewById(R.id.txtEmailLayout);
        txtFullName = findViewById(R.id.txtfullNameLayout);

    }

    public void gotoLogin(View view) {
        startActivity(
                new Intent(SignupActivity.this, LoginActivity.class)
        );
    }

    public void gotoNext(View view) {

        validateAndProceed();


    }

    private void validateAndProceed() {
        String getFullName = txtFullName.getEditText().getText().toString();
        String getEmail = txtEmail.getEditText().getText().toString();

        if (getFullName.trim().isEmpty()) {
            txtFullName.setErrorEnabled(true);
            txtFullName.setError("Full name required");
        } else {
            txtFullName.setErrorEnabled(false);
        }

        if (getEmail.trim().isEmpty()) {
            txtEmail.setErrorEnabled(true);
            txtEmail.setError("Email required");
        } else {
            txtEmail.setErrorEnabled(false);
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            txtEmail.setErrorEnabled(true);
            txtEmail.setError("invalid email");
        } else {
            txtEmail.setErrorEnabled(false);
        }

        if (!txtFullName.getEditText().getText().toString().isEmpty()
                && !txtEmail.getEditText().getText().toString().isEmpty()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            txtFullName.setErrorEnabled(false);
            txtEmail.setErrorEnabled(true);

            Intent gotoCompleteSignUp = new Intent(SignupActivity.this, SignupCompleteActivity.class);
            gotoCompleteSignUp.putExtra("name", getFullName);
            gotoCompleteSignUp.putExtra("email", getEmail);
            startActivity(gotoCompleteSignUp);
            finish();

        }


    }


    @Override
    protected void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getSharedPreferences("namePrefs",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fullName", txtFullName.getEditText().getText().toString());
            editor.putString("email", txtEmail.getEditText().getText().toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("namePrefs",
                    MODE_PRIVATE);
            String name = sharedPreferences.getString("fullName", "");
            String email = sharedPreferences.getString("email", "");

            txtFullName.getEditText().setText(name);
            txtEmail.getEditText().setText(email);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
