package com.artisans.qwikhomeservices.activities.home.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.EditItemBottomSheet;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.VerifyPhoneBottomSheet;
import com.artisans.qwikhomeservices.databinding.FragmentEditProfileBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding fragmentEditProfileBinding;
    private ProfilePhotoEditFragment profilePhotoEditFragment = new ProfilePhotoEditFragment();
    private Uri uri;
    private long mLastClickTime = 0;
    private OnFragmentInteractionListener mListener;
    private CircleImageView profileImage;
    private StorageReference mStorageReference;
    private DatabaseReference serviceAccountDbRef;
    private String uid, getImageUri, name;



    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Profile");
        // Inflate the layout for this fragment
        fragmentEditProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        return fragmentEditProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStorageReference = FirebaseStorage.getInstance().getReference("photos");
        uid = MainActivity.uid;
        serviceAccountDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Services")
                .child("ServiceType")
                .child(uid);
        fragmentEditProfileBinding
                .fabUploadPhoto.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));
        profileImage = fragmentEditProfileBinding.imgUploadPhoto;
        profileImage.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));

        fragmentEditProfileBinding.imgUploadPhoto.setOnClickListener(v -> {
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.scale_out)
                    .replace(R.id.containerSettings, profilePhotoEditFragment)
                    .addToBackStack(null)
                    .commit();

        });

        fragmentEditProfileBinding.fabUploadPhoto.setOnClickListener(v -> openGallery());
        fragmentEditProfileBinding.txtFirstName.setText(MainActivity.firstName);
        fragmentEditProfileBinding.txtLastName.setText(MainActivity.lastName);
        fragmentEditProfileBinding.txtAboutUser.setText(MainActivity.about);
        Glide.with(view.getContext()).load(MainActivity.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(fragmentEditProfileBinding.imgUploadPhoto);

        fragmentEditProfileBinding.txtPhoneNumber.setText(MainActivity.firebaseUser.getPhoneNumber());
        fragmentEditProfileBinding.firstNameLayout.setOnClickListener(//open bottom sheet to edit name
                this::onClick);
        fragmentEditProfileBinding.lastNameLayout.setOnClickListener(//open bottom sheet to edit name
                this::onClick);


        fragmentEditProfileBinding.aboutLayout.setOnClickListener(
                //open bottom sheet to edit about
                this::onClick);
        fragmentEditProfileBinding.editPhoneLayout.setOnClickListener(this::onClick);

    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 16)
                .start(Objects.requireNonNull(getContext()), this);
    }

    public void onClick(View v) {
        Bundle bundle = new Bundle();
        EditItemBottomSheet editItemBottomSheet = new EditItemBottomSheet();
        VerifyPhoneBottomSheet verifyPhoneBottomSheet = new VerifyPhoneBottomSheet();

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        if (v.getId() == R.id.firstNameLayout) {

            name = String.valueOf(fragmentEditProfileBinding.txtFirstName.getText());
            bundle.putString(MyConstants.FIRST_NAME, name);
                editItemBottomSheet.setArguments(bundle);
            editItemBottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), MyConstants.FIRST_NAME);

        } else if (v.getId() == R.id.lastNameLayout) {

            name = String.valueOf(fragmentEditProfileBinding.txtLastName.getText());
            bundle.putString(MyConstants.LAST_NAME, name);
            editItemBottomSheet.setArguments(bundle);
            editItemBottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), MyConstants.LAST_NAME);

        } else if (v.getId() == R.id.aboutLayout) {

            String getAbout = String.valueOf(fragmentEditProfileBinding.txtAboutUser.getText());
            bundle.putString(MyConstants.ABOUT, getAbout);
            editItemBottomSheet.setArguments(bundle);
            editItemBottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), MyConstants.ABOUT);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            getActivity();
            if (resultCode == Activity.RESULT_OK) {
                assert result != null;
                uri = result.getUri();

                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImage);

                uploadFile();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                String error = result.getError().getMessage();
                DisplayViewUI.displayToast(getActivity(), error);
            }
        }
    }


    private void uploadFile() {
        if (uri != null) {
            ProgressDialog progressDialog = DisplayViewUI.displayProgress(getActivity(), "updating profile picture please wait...");
            progressDialog.show();


            //  file path for the itemImage
            final StorageReference fileReference = mStorageReference.child(uid + "." + uri.getLastPathSegment());

            fileReference.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    DisplayViewUI.displayToast(getActivity(), Objects.requireNonNull(task.getException()).getMessage());

                }
                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Uri downLoadUri = task.getResult();
                    assert downLoadUri != null;
                    getImageUri = downLoadUri.toString();

                    Map<String, Object> updatePhoto = new HashMap<>();
                    updatePhoto.put("image", getImageUri);

                    serviceAccountDbRef.updateChildren(updatePhoto).addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(getActivity(), "Successful");
                        } else {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(getActivity(), Objects.requireNonNull(task12.getException()).getMessage());

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
