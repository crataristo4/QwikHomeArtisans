package com.artisans.qwikhomeservices.activities.home.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.ActivityDesignStyleBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.GetDateTime;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class AddDesignOrStyleActivity extends AppCompatActivity {

    private static final String TAG = "JobTypesActivity";
    String dateTime;
    private ActivityDesignStyleBinding activityDesignStyleBinding;
    private AppCompatImageView styleItemPhoto;
    private TextInputLayout txtStyleName, txtPrice;
    private StorageReference mStorageReference;
    private Uri uri;
    private DatabaseReference serviceTypeDbRef, activityDbRef;
    private String price;
    private String uid, style, getImageUploadUri, accountType, userImage, userName;
    private static int INTERVAL = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState != null) {
            Objects.requireNonNull(txtStyleName.getEditText()).setText(savedInstanceState.getString(MyConstants.STYLE));
            Objects.requireNonNull(txtPrice.getEditText()).setText(savedInstanceState.getString(MyConstants.PRICE));
            Glide.with(AddDesignOrStyleActivity.this)
                    .load((Uri) savedInstanceState.getParcelable(MyConstants.IMAGE_URL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(styleItemPhoto);

        }

        super.onCreate(savedInstanceState);

        activityDesignStyleBinding = DataBindingUtil.setContentView(this, R.layout.activity_design_style);
        mStorageReference = FirebaseStorage.getInstance().getReference("photos");
        serviceTypeDbRef = FirebaseDatabase.getInstance()
                .getReference("Styles");

        activityDbRef = FirebaseDatabase.getInstance()
                .getReference("Activity");


        intViews();

    }

    private void intViews() {

        activityDesignStyleBinding.txtDes.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blinking_text));

        txtPrice = activityDesignStyleBinding.textInputLayoutPrice;
        txtStyleName = activityDesignStyleBinding.txtInputLayoutStyle;
        styleItemPhoto = activityDesignStyleBinding.imgStylePhoto;

        uid = MainActivity.uid;

        activityDesignStyleBinding.imgStylePhoto.setOnClickListener(v -> openGallery());

        activityDesignStyleBinding.btnAdd.setOnClickListener(this::validateInputs);


    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 16)
                .start(AddDesignOrStyleActivity.this);
    }

    private void validateInputs(View v) {
        style = Objects.requireNonNull(txtStyleName.getEditText()).getText().toString();
        price = String.valueOf(Objects.requireNonNull(txtPrice.getEditText()).getText());

        if (style.trim().isEmpty()) {
            txtStyleName.setErrorEnabled(true);
            txtStyleName.setError("must include a style");
        } else {
            txtStyleName.setErrorEnabled(false);
        }
        if (price.trim().isEmpty()) {
            txtPrice.setErrorEnabled(true);
            txtPrice.setError("invalid price");
        } else {
            txtPrice.setErrorEnabled(false);
        }

        if (uri == null) {
            DisplayViewUI.displayToast(this, "Please select a photo to upload");

        }


        if (!style.trim().isEmpty() && uri != null
                && !price.trim().isEmpty()) {
            uploadFile();

        }
    }

    private void uploadFile() {
        if (uri != null) {
            ProgressDialog progressDialog = DisplayViewUI.displayProgress(this, "adding item please wait...");
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
                    DisplayViewUI.displayToast(AddDesignOrStyleActivity.this, Objects.requireNonNull(task.getException()).getMessage());

                }
                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Uri downLoadUri = task.getResult();
                    assert downLoadUri != null;

                    getImageUploadUri = downLoadUri.toString();


                    try {
                        Calendar calendar = Calendar.getInstance();
                        Date today = calendar.getTime();
                        SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.ENGLISH);
                        dateTime = GetDateTime.getFormattedDate(today);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    StylesItemModel itemModel = new StylesItemModel(Integer.parseInt(price),
                            style,
                            getImageUploadUri,
                            userImage,
                            userName,
                            ServerValue.TIMESTAMP,
                            accountType);
                    String randomUID = serviceTypeDbRef.push().getKey();

                    assert randomUID != null;
                    serviceTypeDbRef.child(uid).child(randomUID).setValue(itemModel).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {

                            //create an activity node for viewers
                            activityDbRef.child(randomUID).setValue(itemModel);

                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, "Successful");

                            startActivity(new Intent(AddDesignOrStyleActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();


                        } else {
                            progressDialog.dismiss();
                            DisplayViewUI.displayToast(this, Objects.requireNonNull(task1.getException()).getMessage());

                        }

                    });

                } else {
                    progressDialog.dismiss();
                    DisplayViewUI.displayToast(this, Objects.requireNonNull(task.getException()).getMessage());

                }

            });
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

                Glide.with(AddDesignOrStyleActivity.this)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(styleItemPhoto);

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
        if (mBackPressed + INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();

            return;
        }else {
            DisplayViewUI.displayToast(this, "Press back again to exit");


        }

        mBackPressed = System.currentTimeMillis();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(MyConstants.STYLE, style);
        outState.putString(MyConstants.PRICE, String.valueOf(price));
        outState.putParcelable(MyConstants.IMAGE_URL, uri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Objects.requireNonNull(txtStyleName.getEditText()).setText(savedInstanceState.getString(MyConstants.STYLE));
        Objects.requireNonNull(txtPrice.getEditText()).setText(savedInstanceState.getString(MyConstants.PRICE));
        Glide.with(AddDesignOrStyleActivity.this)
                .load((Uri) savedInstanceState.getParcelable(MyConstants.IMAGE_URL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(styleItemPhoto);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //get user details
        accountType = MainActivity.serviceType;
        userName = MainActivity.fullName;
        userImage = MainActivity.imageUrl;

    }




}
