package com.artisans.qwikhomeservices.activities.auth.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.auth.login.LoginActivity;
import com.artisans.qwikhomeservices.databinding.ActivitySignupCompleteBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupCompleteActivity extends AppCompatActivity {
    public static final String PASS = "pass";
    public static final String CONFIRM_PASS = "confirmPass";
    private String name, email, accountType, currentUserId;
    private TextInputLayout txtPass, txtConfirmPass;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private Vibrator vibrator;
    private DatabaseReference usersDbRef, serviceAccountDbRef;
    private ActivitySignupCompleteBinding activitySignupCompleteBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Objects.requireNonNull(txtPass.getEditText()).setText(savedInstanceState.getString(PASS));
            Objects.requireNonNull(txtConfirmPass.getEditText()).setText(savedInstanceState.getString(CONFIRM_PASS));
        }

        super.onCreate(savedInstanceState);
        activitySignupCompleteBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup_complete);

        txtConfirmPass = activitySignupCompleteBinding.ConfirmPasswordLayout;
        txtPass = activitySignupCompleteBinding.PasswordLayout;

        Intent getNameEmailIntent = getIntent();
        if (getNameEmailIntent != null) {
            name = getNameEmailIntent.getStringExtra("name");
            email = getNameEmailIntent.getStringExtra("email");
            accountType = getNameEmailIntent.getStringExtra("accountType");
        }

        mAuth = FirebaseAuth.getInstance();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // usersDbRef = FirebaseDatabase.getInstance().getReference("Users");
        serviceAccountDbRef = FirebaseDatabase.getInstance().getReference("Services");


    }

    public void completeSignUp(View view) {

        String password = Objects.requireNonNull(txtPass.getEditText()).getText().toString();
        String confirmpassword = Objects.requireNonNull(txtConfirmPass.getEditText()).getText().toString();

        if (password.length() < 8) {
            txtPass.setErrorEnabled(true);
            txtPass.setError("password too short");
        } else {
            txtPass.setErrorEnabled(false);

        }

        if (confirmpassword.length() < 8) {
            txtConfirmPass.setErrorEnabled(true);
            txtConfirmPass.setError("password too short");
        } else {
            txtConfirmPass.setErrorEnabled(false);

        }

        if (!password.equals(confirmpassword)) {
            txtPass.setErrorEnabled(true);
            txtPass.setError("passwords do not match");
        } else {
            txtPass.setErrorEnabled(false);

        }

        if (password.equals(confirmpassword) && password.length() >= 8) {

            createAccount(email, password);
        }

    }

    private void createAccount(String email, String password) {

        final ProgressDialog loading = DisplayViewUI.displayProgress(this,
                "Please wait while we create your account...");
        loading.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;
                currentUserId = firebaseUser.getUid();

                // Customer customer = new Customer(currentUserId,email,name);
                ServicePerson servicePerson = new ServicePerson(currentUserId, name, email, accountType);
                Map<String, Object> serviceType = new HashMap<>();
                serviceType.put("accountType", accountType);
                serviceType.put("name", name);

                //send email verification to user
                firebaseUser.sendEmailVerification();

                //create a service type to identify all services
                serviceAccountDbRef.child("ServiceType").child(currentUserId).setValue(serviceType);

                //create user details
                serviceAccountDbRef.child(accountType).child(currentUserId).setValue(servicePerson);


                //vibrates to alert success for android M and above
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot
                            (2000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //vibrate below android M
                    vibrator.vibrate(2000);

                }
                DisplayViewUI.displayAlertDialogMsg(SignupCompleteActivity.this,
                        "Hello" + " " + name + " " + "\n" + "an email verification link has been sent to " + email + "\n" +
                                "please verify to continue",
                        "ok", (dialog, which) -> {
                            dialog.dismiss();

                            Intent gotoLogin = new Intent(SignupCompleteActivity.this, LoginActivity.class);

                            startActivity(gotoLogin);
                            finish();

                        });

            } else {

                loading.dismiss();
                String error = task.getException().getMessage();
                DisplayViewUI.displayToast(this, error);

            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PASS, Objects.requireNonNull(txtPass.getEditText()).getText().toString());
        outState.putString(CONFIRM_PASS, Objects.requireNonNull(txtConfirmPass.getEditText()).getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Objects.requireNonNull(txtPass.getEditText()).setText(savedInstanceState.getString(PASS));
        Objects.requireNonNull(txtConfirmPass.getEditText()).setText(savedInstanceState.getString(CONFIRM_PASS));
    }
}
