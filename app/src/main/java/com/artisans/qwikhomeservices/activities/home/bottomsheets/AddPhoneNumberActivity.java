package com.artisans.qwikhomeservices.activities.home.bottomsheets;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.LayoutAddPhoneBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AddPhoneNumberActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private LayoutAddPhoneBinding layoutAddPhoneBinding;
    private TextInputLayout txtPhoneNumber, txtVerifyCode;
    private int RESOLVE_HINT = 2;
    private String getPhone;

    private String mVerificationCode;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationCode = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //this method automatically handles the code sent
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
                Log.i("VerificationCode: ", code);
                layoutAddPhoneBinding.textInputLayoutConfirmCode.getEditText().setText(code);
            }


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            DisplayViewUI.displayToast(AddPhoneNumberActivity.this, e.getMessage());

        }
    };
    private DatabaseReference userDbRef;
    private GoogleApiClient mGoogleApiClient;
    private CountryCodePicker countryCodePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutAddPhoneBinding = DataBindingUtil.setContentView(this, R.layout.layout_add_phone);
        txtPhoneNumber = layoutAddPhoneBinding.textInputLayoutPhone;
        txtVerifyCode = layoutAddPhoneBinding.textInputLayoutConfirmCode;
        countryCodePicker = layoutAddPhoneBinding.ccp;

        countryCodePicker.registerCarrierNumberEditText(txtPhoneNumber.getEditText());
        countryCodePicker.setNumberAutoFormattingEnabled(true);

        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        getHintPhoneNumber();

        layoutAddPhoneBinding.btnRegisterPhoneNumber.setOnClickListener(v -> {

            String getPhoneNumber = Objects.requireNonNull(txtPhoneNumber.getEditText()).getText().toString();
            if (!getPhoneNumber.trim().isEmpty()) {


                getPhone = countryCodePicker.getFormattedFullNumber();
                Log.i("Number : ", getPhone);
                sendVerificationCode(getPhone);

                new Handler().postDelayed(() -> layoutAddPhoneBinding.progressBarVerify.setVisibility(View.VISIBLE), 3000);

                layoutAddPhoneBinding.constrainLayoutConfrimNumber.setVisibility(View.GONE);
                layoutAddPhoneBinding.constrainLayoutVerifyPhone.setVisibility(View.VISIBLE);

            }

        });


    }


    private void sendVerificationCode(String number) {
//        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationCode, code);
        //method to sign in user
        //signInWithPhoneNumber();

        if (code.equals(phoneAuthCredential.getSmsCode())) {
            //do something
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string
                    Objects.requireNonNull(txtPhoneNumber.getEditText()).setText(Objects.requireNonNull(credential).getId());
                    String getProvider = credential.getId();

                    Log.i("onActivityResult: ", getProvider);
                    sendVerificationCode(getProvider);

                }

            }
        }
    }


}
