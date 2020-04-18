package com.artisans.qwikhomeservices.activities.home.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.FragmentProfilePhotoEditBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilePhotoEditFragment extends Fragment {

    FragmentProfilePhotoEditBinding fragmentProfilePhotoEditBinding;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference serviceTypeDbRef;


    public ProfilePhotoEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Profile photo");
        // Inflate the layout for this fragment
        fragmentProfilePhotoEditBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_photo_edit, container, false);
        return fragmentProfilePhotoEditBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentProfilePhotoEditBinding.imgEditPhoto.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_in));

        String imageUrl = MainActivity.imageUrl;
        if (imageUrl == null) {

            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(getActivity().getResources().getDrawable(R.drawable.photoe))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(fragmentProfilePhotoEditBinding.imgEditPhoto);
        } else {
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(MainActivity.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(fragmentProfilePhotoEditBinding.imgEditPhoto);
        }


        //TODO : UPDATE SERVICE TYPE USER PHOTO ON PICTURE CHANGE

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
