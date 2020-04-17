package com.artisans.qwikhomeservices.activities.handymanactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HandyManLogin extends AppCompatActivity {

    FirebaseUser firebaseUser;
    private TextInputLayout UserEmail, UserPassword;
    private Button LoginButton;
    private TextView Needaccount, forgotpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handy_man_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        UserEmail = findViewById(R.id.TxtLoginEmail);
        UserPassword = findViewById(R.id.TxtLoginPassword);
        LoginButton = findViewById(R.id.btnlogin);
        Needaccount = findViewById(R.id.Txtregisterlogin);
        // forgotpassword = findViewById(R.id.Txtforgotpassword);
        loadingbar = new ProgressDialog(this);

        Needaccount.setOnClickListener(v -> SendUsertoRegisterActivity());

        LoginButton.setOnClickListener(v -> AllowUsertoLogin());

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("loginEmail", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", UserEmail.getEditText().getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("loginEmail", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        UserEmail.getEditText().setText(email);
    }

    private void AllowUsertoLogin() {
        final String email = UserEmail.getEditText().getText().toString();
        String password = UserPassword.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            loadingbar.setTitle("");
            loadingbar.setMessage("Please wait");
            loadingbar.show();
            loadingbar.setCancelable(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(HandyManLogin.this, MainActivityHandyMen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                                loadingbar.dismiss();
                            } else {
                                loadingbar.dismiss();
                                new AlertDialog.Builder(HandyManLogin.this)
                                        .setMessage("Hello" + " " + email + " "
                                                + " " + "\n" + "please check your email  " + "\n" +
                                                "to continue")
                                        .setPositiveButton("OK",
                                                (dialog, which) -> dialog.dismiss())
                                        .create()
                                        .show();
                            }


                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(HandyManLogin.this, "Error:" + "" + message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    });

        }
    }


    private void SendUsertoRegisterActivity() {
        // send user to register activity
        Intent registeruser = new Intent(HandyManLogin.this, HandymanRegister.class);
        startActivity(registeruser);
    }
}
