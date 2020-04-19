package com.artisans.qwikhomeservices.activities.home.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.ActivityAboutBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AboutActivity extends AppCompatActivity {
    public static final String ABOUT = "about";
    private static final String TAG = "AboutActivity";
    private String uid, about, getImageUri, accountType;
    private ActivityAboutBinding activityAboutBinding;
    private TextInputLayout txtAbout;
    private CircleImageView profileImage;
    private DatabaseReference serviceAccountDbRef, serviceTypeDbRef;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            activityAboutBinding.textInputLayoutAbout.getEditText()
                    .setText(savedInstanceState.getString(ABOUT));
        }

        activityAboutBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            return;
        }
        uid = mFirebaseUser.getUid();

        accountType = MainActivity.serviceType;
        //service type database
        serviceTypeDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Services")
                .child("ServiceType")
                .child(uid);
/*
        serviceTypeDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                accountType = (String) dataSnapshot.child("accountType").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

*/
        serviceAccountDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Services")
                .child(accountType)
                .child(uid);


        profileImage = activityAboutBinding.imgUploadPhoto;

        serviceAccountDbRef.keepSynced(true);
        mStorageReference = FirebaseStorage.getInstance().getReference("photos");

        txtAbout = activityAboutBinding.textInputLayoutAbout;
        activityAboutBinding.txtRecommend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blinking_text));

        activityAboutBinding.fabUploadPhoto.setOnClickListener(v -> openGallery());
        profileImage.setOnClickListener(v -> openGallery());

        activityAboutBinding.btnNext.setOnClickListener(this::validateInputs);
        activityAboutBinding.innerInputAbout.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                validateInputs(v);
            }
            return true;
        });


    }

    private void openGallery() {
        CropImage.activity()
                .setAspectRatio(16, 16)
                .start(AboutActivity.this);
    }

    private void validateInputs(View v) {
        about = Objects.requireNonNull(txtAbout.getEditText()).getText().toString();

        if (about.trim().isEmpty()) {
            txtAbout.setErrorEnabled(true);
            txtAbout.setError("field cannot be empty");
        } else {
            txtAbout.setErrorEnabled(false);
        }

        if (uri == null) {
            DisplayViewUI.displayToast(this, "Please select a photo to upload");

        }

        if (!about.trim().isEmpty() && uri != null) {

            uploadFile();
        }
    }

    private void uploadFile() {
        if (uri != null) {
            ProgressDialog progressDialog = DisplayViewUI.displayProgress(this, "please wait...");
            progressDialog.show();

            final File thumb_imageFile = new File(Objects.requireNonNull(uri.getPath()));

            try {
                Bitmap thumb_imageBitmap = new Compressor(this)
                        .setMaxHeight(130)
                        .setMaxWidth(13)
                        .setQuality(100)
                        .compressToBitmap(thumb_imageFile);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //                file path for the itemImage
            final StorageReference fileReference = mStorageReference.child(uid + "." + uri.getLastPathSegment());

            fileReference.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    //throw task.getException();
                    Log.d(TAG, "then: " + task.getException().getMessage());

                }
                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downLoadUri = task.getResult();
                    assert downLoadUri != null;
                    getImageUri = downLoadUri.toString();

                    Map<String, Object> updateProfile = new HashMap<>();
                    updateProfile.put("image", getImageUri);
                    updateProfile.put("about", about);

                    Map<String, Object> updateServiceTypeWithPhoto = new HashMap<>();
                    updateServiceTypeWithPhoto.put("image", getImageUri);


                    serviceAccountDbRef.updateChildren(updateProfile).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {

                            serviceTypeDbRef.updateChildren(updateServiceTypeWithPhoto);

                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, "Successfully updated");

                            Intent addItemIntent = new Intent(AboutActivity.this, AddDesignOrStyleActivity.class);
                            addItemIntent.putExtra(MyConstants.ACCOUNT_TYPE, accountType);
                            addItemIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(addItemIntent);
                            finish();


                        } else {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, task1.getException().getMessage());

                        }

                    });

                } else {
                    progressDialog.dismiss();
                    DisplayViewUI.displayToast(this, task.getException().getMessage());

                }

            });
        }
    }

    private void updateServiceAccount(String about) {
        HashMap<String, Object> updateAccount = new HashMap<>();
        updateAccount.put("about", about);

        ProgressDialog loading = DisplayViewUI.displayProgress(AboutActivity.this, "Updating account");
        loading.show();

        serviceAccountDbRef.updateChildren(updateAccount).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                loading.dismiss();
                DisplayViewUI.displayToast(AboutActivity.this, "Successfully updated");

                startActivity(new Intent(AboutActivity.this, AddDesignOrStyleActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();

            } else {
                DisplayViewUI.displayToast(AboutActivity.this, task.getException().getMessage());
            }

        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString(ABOUT, activityAboutBinding.textInputLayoutAbout.getEditText().getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Objects.requireNonNull(activityAboutBinding.textInputLayoutAbout.getEditText())
                .setText(savedInstanceState.getString(ABOUT));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getSharedPreferences("about",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("about", Objects.requireNonNull(txtAbout.getEditText()).getText().toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("about",
                    MODE_PRIVATE);
            String about = sharedPreferences.getString("about", "");

            Objects.requireNonNull(txtAbout.getEditText()).setText(about);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                uri = result.getUri();

                Glide.with(AboutActivity.this)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImage);

                // uploadFile();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                DisplayViewUI.displayToast(this, error);
            }
        }

    }


    @Override
    public void onBackPressed() {

    }
}
