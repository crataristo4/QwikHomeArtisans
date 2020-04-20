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
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.adapters.StylesAdapter;
import com.artisans.qwikhomeservices.databinding.FragmentAccountBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;


public class AccountFragment extends Fragment {
    private FragmentAccountBinding accountBinding;
    private DatabaseReference databaseReference;
    private StylesAdapter adapter;
    //private AllBarbersAdapter adapter;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        accountBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        return accountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        RecyclerView rv = accountBinding.rvbb;
        rv.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Styles").child(MainActivity.uid);
        /*// TODO: 18-Apr-20 change dbref
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Services").child("ServiceType");*/

        databaseReference.keepSynced(true);

        /*//querying the database BY NAME
       // Query query = databaseReference.orderByChild("accountType").equalTo("Barbers");
*/
        Query query = databaseReference.orderByChild("price");
        FirebaseRecyclerOptions<StylesItemModel> options =
                new FirebaseRecyclerOptions.Builder<StylesItemModel>().setQuery(query,
                        StylesItemModel.class)
                        .build();

        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        } else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        }


        adapter = new StylesAdapter(options);
        rv.setAdapter(adapter);


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
