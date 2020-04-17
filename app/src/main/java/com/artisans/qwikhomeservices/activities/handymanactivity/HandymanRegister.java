package com.artisans.qwikhomeservices.activities.handymanactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.customeractivity.CustomerLogin;
import com.artisans.qwikhomeservices.models.Customer;
import com.bumptech.glide.Glide;
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

public class HandymanRegister extends AppCompatActivity {

    private static final String TAG = "Register";
    private final String urlShortNer = "https://bit.ly/2I6aStr";
    String spinnercode;
    FirebaseUser firebaseUser;
    String CurrentUserid, email, password, confirmpassword, number, occupation, fullname;
    CircleImageView imgProfile;
    private Spinner spinner1;
    private Uri resultUri;
    private String getImageUri = "";
    private StorageReference mStorageReferenceForPhoto;
    private TextInputLayout UserFullname, UserEmail, UserPassword, UserVPassword, UserNumber;
    private Button createaccountbutton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private DatabaseReference handyMenDbRef;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_register);


        mAuth = FirebaseAuth.getInstance();
        mStorageReferenceForPhoto = FirebaseStorage.getInstance().getReference().child(
                "userPhotos");
//vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        handyMenDbRef = FirebaseDatabase.getInstance().getReference("HandyMen");
        UserFullname = findViewById(R.id.TxtFullName);
        UserNumber = findViewById(R.id.TxtMobileNumberSignup);
        UserEmail = findViewById(R.id.TxtEmailsignup);
        UserPassword = findViewById(R.id.TxtPasswordsignup);
        UserVPassword = findViewById(R.id.TxtConfirmPasswordSignup);
        createaccountbutton = findViewById(R.id.btnregisterSignup);
        loadingbar = new ProgressDialog(this);
        imgProfile = findViewById(R.id.imgProfile);

        //spinner code
        spinner1 = findViewById(R.id.spinner1);
        // addListenerOnButton();
        spinner1.setOnItemSelectedListener(new HandymanRegister.CustomOnItemSelectedListener());

        createaccountbutton.setOnClickListener(v -> {
            if (resultUri != null) {

                createnewaccount();
            } else {

                new AlertDialog.Builder(HandymanRegister.this)
                        .setTitle("Warning")
                        .setMessage("Must add a photo ")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).create().show();

            }

        });
    }

    private void createnewaccount() {
        fullname = UserFullname.getEditText().getText().toString();
        email = UserEmail.getEditText().getText().toString();
        password = UserPassword.getEditText().getText().toString();
        confirmpassword = UserVPassword.getEditText().getText().toString();
        number = UserNumber.getEditText().getText().toString();
        occupation = spinnercode;

        final String key = handyMenDbRef.getRef().getKey();


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

                        //class created within the files
                        final Customer customer = new Customer(CurrentUserid, email, number, fullname, occupation, urlShortNer, "No details", 0, 0);

                        firebaseUser.sendEmailVerification().addOnCompleteListener(task14 -> {
                            if (task14.isSuccessful()) {
                                //thumb file
                                final File thumb_imageFile = new File(resultUri.getPath());

                                //  compress itemImage file to bitmap surrounding with try catch
                                byte[] thumbBytes = new byte[0];
                                try {
                                    Bitmap thumb_imageBitmap =
                                            new Compressor(HandymanRegister.this)
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
                                        final Customer customer1 =
                                                new Customer(CurrentUserid, email, number, fullname, occupation, getImageUri, "", 0, 0);
                                        handyMenDbRef.child(occupation).child(CurrentUserid).setValue(customer1).addOnCompleteListener(task1 -> {

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
                                                new AlertDialog.Builder(HandymanRegister.this)
                                                        .setMessage("Hello" + " " + fullname + " " + "\n" + "an email verification link has been sent to " + email + "\n" +
                                                                "please verify to continue")
                                                        .setPositiveButton("OK",
                                                                (dialog, which) -> {
//
                                                                    dialog.dismiss();

                                                                    startActivity(new Intent(HandymanRegister.this, CustomerLogin.class));
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
                                Toast toast = Toast.makeText(HandymanRegister.this, " Error: " + task14.getException().getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                        });


                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(HandymanRegister.this, "error occurred" + message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }


                });

            } else {
                Toast toast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        }


    }

    public void selectPhoto(View view) {

        openGallery();

    }


    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(HandymanRegister.this);

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

                Glide.with(HandymanRegister.this).load(resultUri).into(imgProfile);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                loadingbar.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }


    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            spinnercode = parent.getItemAtPosition(pos).toString();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
}
