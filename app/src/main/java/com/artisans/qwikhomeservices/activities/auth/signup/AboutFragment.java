package com.artisans.qwikhomeservices.activities.auth.signup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.FragmentAboutBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";
    private DatabaseReference serviceAccountDbRef, serviceTypeDbRef;
    private StorageReference mStorageReference;
    private FragmentAboutBinding fragmentAboutBinding;
    private String about, getImageUri, uid, serviceType, mGetFirstName, mGetLatName, mGetAccountType, mGetFullName;
    private Uri uri;
    private CircleImageView profileImage;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentAboutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        return fragmentAboutBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle getBundle = getArguments();
        if (getBundle != null) {
            uid = MainActivity.uid;
            mGetAccountType = getBundle.getString(MyConstants.ACCOUNT_TYPE);
            mGetFirstName = getBundle.getString(MyConstants.FIRST_NAME);
            mGetLatName = getBundle.getString(MyConstants.LAST_NAME);
            mGetFullName = getBundle.getString(MyConstants.FULL_NAME);

        }

        profileImage = fragmentAboutBinding.imgUploadPhoto;
        //service type database
        serviceTypeDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Services")
                .child("ServiceType")
                .child(uid);
        mStorageReference = FirebaseStorage.getInstance().getReference("photos");
        fragmentAboutBinding.btnFinish.setOnClickListener(this::onClick);
        fragmentAboutBinding.fabUploadPhoto.setOnClickListener(v -> openGallery());
        profileImage.setOnClickListener(v -> openGallery());

    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 16)
                .start(Objects.requireNonNull(getContext()), this);
    }

    private void onClick(View view) {
        TextInputLayout txtAbout = fragmentAboutBinding.txtAbout;
        about = Objects.requireNonNull(txtAbout.getEditText()).getText().toString();
        if (uri == null) {
            DisplayViewUI.displayToast(getActivity(), "Please select a photo to upload");

        }

        if (about.trim().isEmpty()) {
            txtAbout.setError("about required");
            txtAbout.setErrorEnabled(true);
        } else {
            txtAbout.setErrorEnabled(false);
        }
        if (!about.trim().isEmpty() && about.length() < 15) {
            txtAbout.setError("content too less");
            txtAbout.setErrorEnabled(true);
        } else {
            txtAbout.setErrorEnabled(false);

        }
        if (!about.trim().isEmpty() && about.length() > 15 && uri != null) {

            updateAccount();

        }

    }

    private void updateAccount() {
        if (uri != null) {
            ProgressDialog progressDialog = DisplayViewUI.displayProgress(getActivity(), "please wait...");
            progressDialog.show();

            final File thumb_imageFile = new File(Objects.requireNonNull(uri.getPath()));

            try {
                Bitmap thumb_imageBitmap = new Compressor(Objects.requireNonNull(getContext()))
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
                    Log.d(TAG, "then: " + Objects.requireNonNull(task.getException()).getMessage());

                }
                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downLoadUri = task.getResult();
                    assert downLoadUri != null;
                    getImageUri = downLoadUri.toString();

                    String mobileNumber = MainActivity.firebaseUser.getPhoneNumber();
                    String uid = MainActivity.uid;

                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:MM a");
                    String dateJoined = dateFormat.format(Calendar.getInstance().getTime());

                    ServicePerson servicePerson = new ServicePerson(uid,
                            mGetFirstName,
                            mGetLatName,
                            mGetFullName,
                            about,
                            mobileNumber,
                            mGetAccountType,
                            getImageUri, dateJoined);

                    serviceTypeDbRef.setValue(servicePerson).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(getActivity(), "Successfully updated");
                            Objects.requireNonNull(getActivity()).finish();


                        } else {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(getActivity(), Objects.requireNonNull(task1.getException()).getMessage());

                        }

                    });

                } else {
                    progressDialog.dismiss();
                    DisplayViewUI.displayToast(getActivity(), Objects.requireNonNull(task.getException()).getMessage());

                }

            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                assert result != null;
                uri = result.getUri();
                Log.i(TAG, "URI: " + uri);
                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                DisplayViewUI.displayToast(getActivity(), error);
            }
        }

    }
}
