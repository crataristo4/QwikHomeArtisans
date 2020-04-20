package com.artisans.qwikhomeservices.activities.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.ActivitySignInWithPhoneNumberBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInWithPhoneNumberActivity extends AppCompatActivity {
    private static int INTERVAL = 3000;
    private long mBackPressed;
    private String mGetFirstName, mGetLatName, mGetAccountType, mGetAbout, mGetFullName;
    private ActivitySignInWithPhoneNumberBinding activitySignInWithPhoneNumberBinding;
    private String mVerificationCode;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private CountryCodePicker countryCodePicker;
    private ProgressBar loading;
    private TextInputLayout txtPhoneNumber, txtVerifyCode;
    private int RESOLVE_HINT = 2;
    private String getPhone;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationCode = s;
            mToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //this method automatically handles the code sent
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Objects.requireNonNull(txtVerifyCode.getEditText()).setText(code);

                verifyCode(code);
            }


        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            DisplayViewUI.displayToast(SignInWithPhoneNumberActivity.this, e.getMessage());
            activitySignInWithPhoneNumberBinding.txtResendCode.setVisibility(View.VISIBLE);
        }
    };
    private TextView txtResend;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInWithPhoneNumberBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in_with_phone_number);
        firebaseAuth = FirebaseAuth.getInstance();

        countryCodePicker = activitySignInWithPhoneNumberBinding.ccp;
        loading = activitySignInWithPhoneNumberBinding.progressBarVerify;
        txtPhoneNumber = activitySignInWithPhoneNumberBinding.textInputLayoutPhone;
        txtVerifyCode = activitySignInWithPhoneNumberBinding.textInputLayoutConfirmCode;

        countryCodePicker.registerCarrierNumberEditText(txtPhoneNumber.getEditText());
        countryCodePicker.setNumberAutoFormattingEnabled(true);


        activitySignInWithPhoneNumberBinding.btnRegisterPhoneNumber.setOnClickListener(v -> {

            String getPhoneNumber = Objects.requireNonNull(txtPhoneNumber.getEditText()).getText().toString();
            if (!getPhoneNumber.trim().isEmpty()) {

                if (DisplayViewUI.isNetworkConnected(SignInWithPhoneNumberActivity.this)) {
                    getPhone = countryCodePicker.getFormattedFullNumber();
                    sendVerificationCode(getPhone);
                    showHideLayout();
                } else {
                    DisplayViewUI.displayAlertDialogMsg(SignInWithPhoneNumberActivity.this, getResources().getString(R.string.noInternet), "ok",
                            (dialog, which) -> dialog.dismiss());
                }
            } else if (getPhoneNumber.trim().isEmpty()) {
                txtPhoneNumber.setErrorEnabled(true);
                txtPhoneNumber.setError("number required");
            } else {
                txtPhoneNumber.setErrorEnabled(false);
            }

        });

        activitySignInWithPhoneNumberBinding.btnVerify.setOnClickListener(v -> {
            String getCodeFromUser = Objects.requireNonNull(txtVerifyCode.getEditText()).getText().toString();
                if (!getCodeFromUser.trim().isEmpty() && getCodeFromUser.length() == 6) {
                    verifyCode(getCodeFromUser);

                }

            if (getCodeFromUser.length() < 6) {
                    txtVerifyCode.setErrorEnabled(true);
                    txtVerifyCode.setError("code too short");
                }


        });

        activitySignInWithPhoneNumberBinding.txtResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendToken(getPhone, mToken);
            }
        });


    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(this),               // Activity (for callback binding)
                mCallbacks);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationCode, code);

        signInWithCredentials(phoneAuthCredential);
    }

    private void resendToken(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks, token);

    }

    private void signInWithCredentials(PhoneAuthCredential phoneAuthCredential) {

        loading.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loading.setVisibility(View.GONE);

                user = firebaseAuth.getCurrentUser();
                uid = firebaseAuth.getUid();

                Intent intent = new Intent(SignInWithPhoneNumberActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();


            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                loading.setVisibility(View.GONE);
                activitySignInWithPhoneNumberBinding.txtResendCode.setVisibility(View.VISIBLE);
                DisplayViewUI.displayToast(SignInWithPhoneNumberActivity.this, task.getException().getMessage());
            }

        });

    }

    private void showHideLayout() {
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.GONE);
            activitySignInWithPhoneNumberBinding.constrainLayoutConfrimNumber.setVisibility(View.GONE);
            activitySignInWithPhoneNumberBinding.constrainLayoutVerifyPhone.setVisibility(View.VISIBLE);
        }, 7000);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + INTERVAL > System.currentTimeMillis()) {

            finish();
        } else {

            DisplayViewUI.displayToast(this, "Press back again to exit");
        }
        mBackPressed = System.currentTimeMillis();

    }
}
