package com.artisans.qwikhomeservices.activities.customeractivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.artisans.qwikhomeservices.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerLogin extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "CustomerLogin";


    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    FirebaseUser firebaseUser;
    //Run on UI
    private Runnable sendUpdatesToUI = () -> showSettingDialog();
    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
//                    updateGPSStatus("GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    showSettingDialog();
//                    updateGPSStatus("GPS is Disabled in your device");
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };
    private TextInputLayout UserEmail, UserPassword;
    private Button LoginButton;
    private TextView Needaccount, forgotpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

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

        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
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
                                startActivity(new Intent(CustomerLogin.this, MainActivityCustomers.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                                loadingbar.dismiss();
                            } else {
                                loadingbar.dismiss();
                                new AlertDialog.Builder(CustomerLogin.this)
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
                            Toast.makeText(CustomerLogin.this, "Error:" + "" + message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    });

        }
    }


    private void SendUsertoRegisterActivity() {
        // send user to register activity
        Intent registeruser = new Intent(CustomerLogin.this, CustomerRegister.class);
        startActivity(registeruser);
    }


    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(CustomerLogin.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(CustomerLogin.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }


    /*  Show Popup to access Customer Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CustomerLogin.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(CustomerLogin.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(CustomerLogin.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            final LocationSettingsStates state = result1.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
//                        updateGPSStatus("GPS is Enabled in your device");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(CustomerLogin.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check for the integer request code originally supplied to startResolutionForResult().
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.e("Settings", "Result OK");
//                        updateGPSStatus("GPS is Enabled in your device");
                    //startLocationUpdates();
                    break;
                case RESULT_CANCELED:
                    Log.e("Settings", "Result Cancel");
//                        updateGPSStatus("GPS is Disabled in your device");
                    break;
            }
        }
    }


    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
//                    updateGPSStatus("Location Permission denied.");
                    Log.i(TAG, "onRequestPermissionsResult: denies");
                    Toast.makeText(CustomerLogin.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Intent intent = new Intent();
                intent.putExtra("Longitude", mLastLocation.getLongitude());
                intent.putExtra("Latitude", mLastLocation.getLatitude());
                Log.i(TAG, "lat: " + mLastLocation.getLatitude() + " longitude " + mLastLocation.getLongitude());
                setResult(1, intent);
                finish();

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
