package com.artisans.qwikhomeservices.activities.home.fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.AllBarbersAdapter;
import com.artisans.qwikhomeservices.databinding.FragmentAllBarbersBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllBarbersFragment extends Fragment {
    FragmentAllBarbersBinding fragmentAllBarbersBinding;
    AllBarbersAdapter allBarbersAdapter;
    RecyclerView rvAllBarbers;
    private DatabaseReference allBarbersDbRef;


    public AllBarbersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAllBarbersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_barbers, container, false);
        // Inflate the layout for this fragment
        return fragmentAllBarbersBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();

    }

    private void initRecyclerView() {

        allBarbersDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(MyConstants.SERVICES)
                .child(MyConstants.BARBERS);
        allBarbersDbRef.keepSynced(true);

        rvAllBarbers = fragmentAllBarbersBinding.rvAllBarbers;

        //querying the database base of the time posted
        Query query = allBarbersDbRef.orderByChild("name");

        FirebaseRecyclerOptions<ServicePerson> options =
                new FirebaseRecyclerOptions.Builder<ServicePerson>().setQuery(query,
                        ServicePerson.class)
                        .build();

        allBarbersAdapter = new AllBarbersAdapter(options, getContext());

        rvAllBarbers.setAdapter(allBarbersAdapter);


    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            rvAllBarbers.setLayoutManager(new GridLayoutManager(getContext(), 3));


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            rvAllBarbers.setLayoutManager(new GridLayoutManager(getContext(), 2));


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        allBarbersAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        allBarbersAdapter.stopListening();
    }
}
