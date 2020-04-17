package com.artisans.qwikhomeservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.activities.welcome.WelcomeActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Forgotpassword extends AppCompatActivity {
    private Button Resetpasswordbutton;
    private TextInputLayout ResetEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);


        Resetpasswordbutton = findViewById(R.id.btnRecoverPassword);
        ResetEmail = findViewById(R.id.TxtResetPasswordEmail);

        Resetpasswordbutton.setOnClickListener(v -> {
            String Useremail = ResetEmail.getEditText().getText().toString();

            if ((TextUtils.isEmpty(Useremail))) {
                Toast.makeText(Forgotpassword.this, "Please Enter Email .", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.sendPasswordResetEmail(Useremail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Forgotpassword.this, "Please Check Your Email address", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(Forgotpassword.this, WelcomeActivity.class));


                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(Forgotpassword.this, "Error:" + "" + message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                });

            }

        });

    }
}
