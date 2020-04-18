package com.artisans.qwikhomeservices.activities.auth.signup;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.FragmentPhoneNumberBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PhoneNumberFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private String mGetFirstName, mGetLatName, mGetAccountType, mGetAbout, mGetFullName;
    private FragmentPhoneNumberBinding fragmentPhoneNumberBinding;
    private String mVerificationCode;
    private DatabaseReference servicePersonDbRef;
    private GoogleApiClient mGoogleApiClient;
    private CountryCodePicker countryCodePicker;
    private ProgressBar loading;
    private TextInputLayout txtPhoneNumber, txtVerifyCode;
    private int RESOLVE_HINT = 2;
    private String getPhone;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    private String number;
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
                Objects.requireNonNull(txtVerifyCode.getEditText()).setText(code);

                verifyCode(code);
            }


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            DisplayViewUI.displayToast(getContext(), e.getMessage());

        }
    };


    public PhoneNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentPhoneNumberBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_number, container, false);
        return fragmentPhoneNumberBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();


        Bundle getBundle = getArguments();

        if (getBundle != null) {

            mGetAccountType = getBundle.getString(MyConstants.ACCOUNT_TYPE);
            mGetFirstName = getBundle.getString(MyConstants.FIRST_NAME);
            mGetLatName = getBundle.getString(MyConstants.LAST_NAME);
            mGetAbout = getBundle.getString(MyConstants.ABOUT);
            mGetFullName = getBundle.getString(MyConstants.FULL_NAME);

            DisplayViewUI.displayToast(getActivity(), mGetAccountType + " " +
                    mGetFirstName + " " + mGetLatName + " " + mGetAbout + " " + mGetFullName);
        }

        countryCodePicker = fragmentPhoneNumberBinding.ccp;
        loading = fragmentPhoneNumberBinding.progressBarVerify;
        txtPhoneNumber = fragmentPhoneNumberBinding.textInputLayoutPhone;
        txtVerifyCode = fragmentPhoneNumberBinding.textInputLayoutConfirmCode;
        servicePersonDbRef = FirebaseDatabase.getInstance().getReference("Services");

        countryCodePicker.registerCarrierNumberEditText(txtPhoneNumber.getEditText());
        //countryCodePicker.setNumberAutoFormattingEnabled(true);

        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                .addConnectionCallbacks(this)
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        getHintPhoneNumber();

        fragmentPhoneNumberBinding.btnRegisterPhoneNumber.setOnClickListener(v -> {

            String getPhoneNumber = Objects.requireNonNull(txtPhoneNumber.getEditText()).getText().toString();
            if (!getPhoneNumber.trim().isEmpty()) {

                if (DisplayViewUI.isNetworkConnected(getContext())) {
                    getPhone = countryCodePicker.getFormattedFullNumber();
                    sendVerificationCode(getPhone);
                    showHideLayout();
                } else {
                    DisplayViewUI.displayAlertDialogMsg(getContext(), Objects.requireNonNull(getContext()).getResources().getString(R.string.noInternet), "ok",
                            (dialog, which) -> dialog.dismiss());
                }
            } else if (getPhoneNumber.trim().isEmpty()) {
                txtPhoneNumber.setErrorEnabled(true);
                txtPhoneNumber.setError("number required");
            } else {
                txtPhoneNumber.setErrorEnabled(false);
            }

        });

        fragmentPhoneNumberBinding.btnVerify.setOnClickListener(v -> {

            try {
                String getCodeFromUser = Objects.requireNonNull(txtVerifyCode.getEditText()).getText().toString();
                if (!getCodeFromUser.trim().isEmpty() && getCodeFromUser.length() == 6) {

                    verifyCode(getCodeFromUser);


                } else if (getCodeFromUser.length() < 6) {
                    txtVerifyCode.setErrorEnabled(true);
                    txtVerifyCode.setError("code too short");
                } else {
                    txtVerifyCode.setErrorEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

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

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                mCallbacks);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationCode, code);

        signInWithCredentials(phoneAuthCredential);
    }

    private void signInWithCredentials(PhoneAuthCredential phoneAuthCredential) {

        loading.setVisibility(View.VISIBLE);


        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = firebaseAuth.getCurrentUser();

                uid = firebaseAuth.getUid();
                assert uid != null;
                Log.i("uid  ", uid);

                loading.setVisibility(View.GONE);
                number = user.getPhoneNumber();

                //todo create account with details using dbref
                Map<String, Object> servicePerson = new HashMap<>();
                servicePerson.put("firstName", mGetFirstName);
                servicePerson.put("lastName", mGetLatName);
                servicePerson.put("fullName", mGetFullName);
                servicePerson.put("about", mGetAbout);
                servicePerson.put("uid", uid);
                servicePerson.put("serviceType", mGetAccountType);
                servicePerson.put("mobileNumber", number);

                Log.i("onComplete: ", task.getResult().getUser().getPhoneNumber());
                Log.i("onComplete: ", number);

                servicePersonDbRef.child(mGetAccountType).child(uid).setValue(servicePerson);
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Objects.requireNonNull(getContext()).startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();


            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                loading.setVisibility(View.GONE);
                DisplayViewUI.displayToast(getActivity(), task.getException().getMessage());
            }

        });

    }

    private void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void showHideLayout() {
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.GONE);
            fragmentPhoneNumberBinding.constrainLayoutConfrimNumber.setVisibility(View.GONE);
            fragmentPhoneNumberBinding.constrainLayoutVerifyPhone.setVisibility(View.VISIBLE);
        }, 7000);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    String getProvider = Objects.requireNonNull(credential).getId();

                    if (DisplayViewUI.isNetworkConnected(getContext())) {
                        sendVerificationCode(getProvider);
                        showHideLayout();
                    } else {
                        DisplayViewUI.displayToast(getActivity(), "No internet");
                    }


                }

            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient.stopAutoManage(Objects.requireNonNull(getActivity()));
    }

}
