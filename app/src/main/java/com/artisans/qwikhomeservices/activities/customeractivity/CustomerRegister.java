package com.artisans.qwikhomeservices.activities.customeractivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.models.Customer;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CustomerRegister extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int UPDATE_INTERVAL = 1000;
    public static final int FAST_INTERVAL = 1000;
    public static final int PLAYSERVICES_RESOLUTION = 101;
    public static final int DISPLACEMENT = 10;
    public static final int MYPERMISSIONREQUEST = 100;
    private static final String TAG = "Register";
    private static GoogleApiClient googleApiClient;
    private final String urlShortNer = "https://bit.ly/2I6aStr";
    FirebaseUser firebaseUser;
    String CurrentUserid, email, password, confirmpassword, number, occupation, fullname;
    CircleImageView imgProfile;
    private LocationRequest locationRequest;
    private TextInputLayout UserFullname, UserEmail, UserPassword, UserVPassword, UserNumber;
    private Button createaccountbutton;
    private FirebaseAuth mAuth;
    private Uri resultUri;
    private String getImageUri = "";
    private StorageReference mStorageReferenceForPhoto;
    private ProgressDialog loadingbar;
    private DatabaseReference UserRef;
    private Vibrator vibrator;
    private double latitude, longitude;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        setUpLocation();
        mAuth = FirebaseAuth.getInstance();
//vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        //creates a storage like data for check in photos to be stored into
        mStorageReferenceForPhoto = FirebaseStorage.getInstance().getReference().child(
                "userPhotos");
        UserFullname = findViewById(R.id.TxtFullName);
        UserNumber = findViewById(R.id.TxtMobileNumberSignup);
        UserEmail = findViewById(R.id.TxtEmailsignup);
        UserPassword = findViewById(R.id.TxtPasswordsignup);
        UserVPassword = findViewById(R.id.TxtConfirmPasswordSignup);
        createaccountbutton = findViewById(R.id.btnregisterSignup);
        loadingbar = new ProgressDialog(this);
        imgProfile = findViewById(R.id.imgProfile);


        createaccountbutton.setOnClickListener(v -> {
            if (resultUri != null) {

                createnewaccount();
            } else {

                new AlertDialog.Builder(CustomerRegister.this)
                        .setTitle("Warning")
                        .setMessage("Must add a photo ")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).create().show();

            }

        });
    }


    public void selectPhoto(View view) {

        openGallery();

    }


    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(CustomerRegister.this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                resultUri = result.getUri();
                //  userImage.setImageURI(uri);

                Glide.with(CustomerRegister.this).load(resultUri).into(imgProfile);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                loadingbar.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);


        } else {
            if (checkPlayServices()) {
                buildGoogleClient();
                createLocationRequest();
                displayLocation();
            }
        }

    }

    private synchronized void buildGoogleClient() {
        //build the google client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAYSERVICES_RESOLUTION).show();
            else {
                makeToast("This device is not supported");
                finish();
            }
            return false;

        }
        return true;
    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

        }
    }

    private void createLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MYPERMISSIONREQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPlayServices()) {
                    buildGoogleClient();
                    createLocationRequest();
                    displayLocation();
                    createaccountbutton.setEnabled(true);
                }
            } else {
                makeToast("Cannot register please enable your gps");
                createaccountbutton.setEnabled(false);
            }
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    private void createnewaccount() {
        if (createaccountbutton.isEnabled()) {
            fullname = UserFullname.getEditText().getText().toString();
            email = UserEmail.getEditText().getText().toString();
            password = UserPassword.getEditText().getText().toString();
            confirmpassword = UserVPassword.getEditText().getText().toString();
            number = UserNumber.getEditText().getText().toString();

            if (TextUtils.isEmpty(fullname)) {
                Toast.makeText(this, "pls enter First Name", Toast.LENGTH_LONG).show();
            }


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "pls enter valid Email", Toast.LENGTH_LONG).show();
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "pls enter Password", Toast.LENGTH_LONG).show();
            }

            if (TextUtils.isEmpty(confirmpassword)) {
                Toast.makeText(this, "pls Confirm Password", Toast.LENGTH_LONG).show();
            }

            if (TextUtils.isEmpty(number)) {
                Toast.makeText(this, "pls enter Phone number", Toast.LENGTH_LONG).show();
            }


            if (!TextUtils.isEmpty(fullname) && !TextUtils.isEmpty(email)
                    && !TextUtils.isEmpty(number) && !TextUtils.isEmpty(password)
                    && !TextUtils.isEmpty(confirmpassword)) {
                if (password.equals(confirmpassword) && password.length() < 8) {
                    Toast.makeText(this, "pls enter a password with at least 8 characters ", Toast.LENGTH_LONG).show();
                }


                if (!TextUtils.isEmpty(number) && number.length() < 10) {
                    Toast.makeText(this, "Please ,phone number field only receives 10 digits", Toast.LENGTH_LONG).show();
                }
                if (!TextUtils.isEmpty(number) && number.length() > 10) {
                    Toast.makeText(this, "Please ,phone number field only receives 10 digits", Toast.LENGTH_LONG).show();
                }

                if (password.equals(confirmpassword) && password.length() >= 8 && number.length() == 10) {

                    loadingbar.setMessage("Please wait while we  create your new account");
                    loadingbar.show();
                    loadingbar.setCancelable(false);

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            firebaseUser = mAuth.getCurrentUser();
                            CurrentUserid = firebaseUser.getUid();


                            firebaseUser.sendEmailVerification().addOnCompleteListener(task14 -> {
                                if (task14.isSuccessful()) {

                                    //thumb file
                                    final File thumb_imageFile = new File(resultUri.getPath());

                                    //  compress itemImage file to bitmap surrounding with try catch
                                    byte[] thumbBytes = new byte[0];
                                    try {
                                        Bitmap thumb_imageBitmap =
                                                new Compressor(CustomerRegister.this)
                                                        .setQuality(100)
                                                        .compressToBitmap(thumb_imageFile);

                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        thumb_imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                        thumbBytes = byteArrayOutputStream.toByteArray();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //                file path for the itemImage
                                    final StorageReference fileReference =
                                            mStorageReferenceForPhoto.child(CurrentUserid + "." + resultUri.getLastPathSegment());
                                    fileReference.putFile(resultUri).continueWithTask(task13 -> {
                                        if (!task13.isSuccessful()) {
                                            //throw task.getException();
                                            Log.d(TAG, "then: " + task13.getException().getMessage());

                                        }
                                        return fileReference.getDownloadUrl();
                                    }).addOnCompleteListener(task12 -> {

                                        if (task12.isSuccessful()) {
                                            Uri downLoadUri = task12.getResult();
                                            assert downLoadUri != null;
                                            getImageUri = downLoadUri.toString();

//class created within the files
                                            final Customer customer = new Customer(CurrentUserid, email, number, fullname, occupation, getImageUri, "", latitude, longitude);
                                            UserRef.child(CurrentUserid).setValue(customer).addOnCompleteListener(task1 -> {

                                                if (task1.isSuccessful()) {
                                                    loadingbar.dismiss();
                                                    //vibrates to alert success for android M and above
                                                    if (Build.VERSION.SDK_INT >= 26) {
                                                        vibrator.vibrate(VibrationEffect.createOneShot
                                                                (2000, VibrationEffect.DEFAULT_AMPLITUDE));
                                                    } else {
                                                        //vibrate below android M
                                                        vibrator.vibrate(2000);

                                                    }
                                                    new AlertDialog.Builder(CustomerRegister.this)
                                                            .setMessage("Hello" + " " + fullname + " " + "\n" + "an email verification link has been sent to " + email + "\n" +
                                                                    "please verify to continue")
                                                            .setPositiveButton("OK",
                                                                    (dialog, which) -> {
//
                                                                        dialog.dismiss();

                                                                        startActivity(new Intent(CustomerRegister.this, CustomerLogin.class));
                                                                        finish();


                                                                    })
                                                            .create()
                                                            .show();

                                                } else {
                                                    loadingbar.dismiss();
                                                    Log.d(TAG, "onComplete: " + task1.getException().getMessage());
                                                }

                                            });


                                        }


                                    });


                                } else {
                                    Toast toast = Toast.makeText(CustomerRegister.this, " Error: " + task14.getException().getMessage(), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                }
                            });


                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(CustomerRegister.this, "error occurred" + message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }


                    });

                } else {
                    Toast toast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


            }


        } else {
            makeToast("Enable GPS location");
        }

    }
}
