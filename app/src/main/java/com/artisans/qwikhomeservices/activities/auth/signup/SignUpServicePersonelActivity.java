package com.artisans.qwikhomeservices.activities.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.ActivitySignUpServicePersonelBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUpServicePersonelActivity extends AppCompatActivity {

    public static final String ACCOUNT_TYPE = "accountType";
    private static final String FULLNAME = "fullName";
    private static final String EMAIL = "email";
    private String getAccountType;
    private TextInputLayout txtFullName, txtEmail;
    private Spinner spinnerAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Objects.requireNonNull(txtEmail.getEditText()).setText(savedInstanceState.getString(EMAIL));
            Objects.requireNonNull(txtFullName.getEditText()).setText(savedInstanceState.getString(FULLNAME));
        }

        super.onCreate(savedInstanceState);
        ActivitySignUpServicePersonelBinding activitySignUpServicePersonelBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_sign_up_service_personel);

        txtEmail = activitySignUpServicePersonelBinding.txtEmailLayout;
        txtFullName = activitySignUpServicePersonelBinding.txtfullNameLayout;
        spinnerAccountType = activitySignUpServicePersonelBinding.spinnerAccountType;


    }

    public void gotoNext(View view) {

        if (view.isEnabled()) {

            validateAndProceed();

        }


    }

    private void validateAndProceed() {
        String getFullName = Objects.requireNonNull(txtFullName.getEditText()).getText().toString();
        String getEmail = Objects.requireNonNull(txtEmail.getEditText()).getText().toString();

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

        getAccountType = (String) spinnerAccountType.getSelectedItem();

        if (!txtFullName.getEditText().getText().toString().isEmpty()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()
                && !txtEmail.getEditText().getText().toString().isEmpty()) {
            txtFullName.setErrorEnabled(false);
            txtEmail.setErrorEnabled(true);

            Intent gotoCompleteSignUp = new Intent(SignUpServicePersonelActivity.this, SignupCompleteActivity.class);
            gotoCompleteSignUp.putExtra("name", getFullName);
            gotoCompleteSignUp.putExtra("email", getEmail);
            gotoCompleteSignUp.putExtra("accountType", getAccountType);
            startActivity(gotoCompleteSignUp);
            finish();

        }


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FULLNAME, Objects.requireNonNull(txtFullName.getEditText()).getText().toString());
        outState.putString(EMAIL, Objects.requireNonNull(txtEmail.getEditText()).getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Objects.requireNonNull(txtEmail.getEditText()).setText(savedInstanceState.getString(EMAIL));
        Objects.requireNonNull(txtFullName.getEditText()).setText(savedInstanceState.getString(FULLNAME));
    }


}
