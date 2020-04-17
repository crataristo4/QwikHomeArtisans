package com.artisans.qwikhomeservices.activities.home.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.serviceTypes.AllBarbersActivity;
import com.artisans.qwikhomeservices.databinding.FragmentHomeBinding;
import com.artisans.qwikhomeservices.utils.MyConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding fragmentHomeBinding;
    private Intent intent;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fragmentHomeBinding.mMaterialCard1.setOnClickListener(v -> {

            intent = new Intent(getContext(), AllBarbersActivity.class);
            intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.WOMEN_HAIR_STYLIST);
            startActivity(intent);


        });

        fragmentHomeBinding.mMaterialCard2.setOnClickListener(v -> {

            intent = new Intent(getContext(), AllBarbersActivity.class);
            intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.INTERIOR_DERCORATOR);
            startActivity(intent);

        });

        fragmentHomeBinding.mMaterialCard3.setOnClickListener(v -> {

            intent = new Intent(getContext(), AllBarbersActivity.class);
            intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.BARBERS);
            startActivity(intent);


        });

    }
}
