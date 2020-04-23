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
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
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
                activitySignInWithPhoneNumberBinding.btnVerify.setText(R.string.plsWait);

                // verifyCode(code);
            } else {
                activitySignInWithPhoneNumberBinding.btnVerify.setText(R.string.verify);
            }


        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
                DisplayViewUI.displayToast(SignInWithPhoneNumberActivity.this, e.getMessage());
                activitySignInWithPhoneNumberBinding.txtResendCode.setVisibility(View.VISIBLE);


            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
                DisplayViewUI.displayToast(SignInWithPhoneNumberActivity.this, e.getMessage());
                activitySignInWithPhoneNumberBinding.txtResendCode.setVisibility(View.GONE);


            }
            DisplayViewUI.displayToast(SignInWithPhoneNumberActivity.this, e.getMessage());
            activitySignInWithPhoneNumberBinding.txtResendCode.setVisibility(View.VISIBLE);
        }
    };
    String phoneNumber = "+16505554567";
    String smsCode = "123456";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    private String getPhone, getPhoneNumber;
    private TextView txtResend;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInWithPhoneNumberBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in_with_phone_number);
        firebaseAuth = FirebaseAuth.getInstance();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

// Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);

        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                this, /* activity */
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Instant verification is applied and a credential is directly returned.
                        // ...
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
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

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    // ...
                });

        countryCodePicker = activitySignInWithPhoneNumberBinding.ccp;
        loading = activitySignInWithPhoneNumberBinding.progressBarVerify;
        txtPhoneNumber = activitySignInWithPhoneNumberBinding.textInputLayoutPhone;
        txtVerifyCode = activitySignInWithPhoneNumberBinding.textInputLayoutConfirmCode;

        countryCodePicker.registerCarrierNumberEditText(txtPhoneNumber.getEditText());
        countryCodePicker.setNumberAutoFormattingEnabled(true);

        activitySignInWithPhoneNumberBinding.btnRegisterPhoneNumber.setOnClickListener(v -> {

            getPhoneNumber = Objects.requireNonNull(txtPhoneNumber.getEditText()).getText().toString();
            if (!getPhoneNumber.trim().isEmpty()) {

                if (DisplayViewUI.isNetworkConnected(SignInWithPhoneNumberActivity.this)) {
                    getPhone = countryCodePicker.getFormattedFullNumber();
                    //  sendVerificationCode(phoneNumber);//TODO: 22-Apr-20  change args to input
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
                    // verifyCode(smsCode); // TODO: 22-Apr-20  change args to input

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
                number,   // Phone number to verify
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
        }, 10000);
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
