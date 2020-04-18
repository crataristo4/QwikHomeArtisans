package com.artisans.qwikhomeservices.activities.auth.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.auth.resetpass.ResetPasswordActivity;
import com.artisans.qwikhomeservices.activities.auth.signup.SignUpServicePersonelActivity;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mLoginEmail, mLoginPassword;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String currentUserId, passAccountTypeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        mLoginEmail = findViewById(R.id.txtEmailLayout);
        mLoginPassword = findViewById(R.id.txtPasswordLayout);

        /*Intent intent = getIntent();
        if (intent != null) {
            passAccountTypeValue = intent.getStringExtra("accountType");
        }*/

    }

    public void forgotPassword(View view) {
        startActivity(
                new Intent(LoginActivity.this, ResetPasswordActivity.class)
        );
    }

    public void gotoSignUp(View view) {
        startActivity(
                new Intent(LoginActivity.this, SignUpServicePersonelActivity.class)
        );
    }

    public void gotoMainPage(View view) {
        validateLogin(view);
    }

    private void validateLogin(View view) {
        String email = mLoginEmail.getEditText().getText().toString();
        String password = mLoginPassword.getEditText().getText().toString();

        if (password.trim().isEmpty()) {
            mLoginPassword.setErrorEnabled(true);
            mLoginPassword.setError("password required");
        } else {
            mLoginPassword.setErrorEnabled(false);
        }
        if (email.trim().isEmpty()) {
            mLoginEmail.setErrorEnabled(true);
            mLoginEmail.setError("email required");
        } else {
            mLoginEmail.setErrorEnabled(false);
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            // loadingbar.setTitle("");
            final ProgressDialog loading = DisplayViewUI.displayProgress(view.getContext(), "Please wait...");
            loading.show();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                assert firebaseUser != null;
                                //currentUserId = firebaseUser.getUid();

                                Intent gotoAbout = new Intent(LoginActivity.this, MainActivity.class);
                                //gotoAbout.putExtra("userId", currentUserId);
                                // gotoAbout.putExtra("accountType", passAccountTypeValue);

                                startActivity(gotoAbout
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                                loading.dismiss();
                            } else {
                                loading.dismiss();
                                DisplayViewUI.displayAlertDialogMsg(view.getContext(), "Hello" + " " + email + " "
                                        + " " + "\n" + "please check your email  " + "\n" +
                                        "to verify and continue", "ok", (dialog, which) -> dialog.dismiss());

                            }


                        } else {
                            loading.dismiss();
                            String message = task.getException().getMessage();
                            DisplayViewUI.displayToast(view.getContext(), message);
                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
