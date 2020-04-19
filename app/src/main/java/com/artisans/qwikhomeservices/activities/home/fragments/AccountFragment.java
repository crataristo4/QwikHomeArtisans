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
import com.artisans.qwikhomeservices.databinding.FragmentAccountBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
   /* private static final String TAG = "AccountFragment";
    String uid, accountType, name, about, imageUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference serviceAccountDbRef, serviceTypeDbRef;
    private FragmentAccountBinding accountBinding;
    private TextView txtName, txtServiceType, txtAbout;
    private CircleImageView mPhoto;*/

    private FragmentAccountBinding accountBinding;
    private DatabaseReference databaseReference;
    //private StylesAdapter adapter;
    private AllBarbersAdapter adapter;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        accountBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);

        /*if (savedInstanceState != null) {
            accountBinding.txtAboutPerson.setText(savedInstanceState.getString(MyConstants.ABOUT));
            accountBinding.txtName.setText(savedInstanceState.getString(MyConstants.NAME));
            accountBinding.txtAccountType.setText(savedInstanceState.getString(MyConstants.ACCOUNT_TYPE));

            Glide.with(getActivity())
                    .load(savedInstanceState.getString(MyConstants.IMAGE_URL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(accountBinding.imgProfilePhoto);
        }*/
        return accountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        RecyclerView rv = accountBinding.rvbb;
        rv.setHasFixedSize(true);
        // databaseReference = FirebaseDatabase.getInstance().getReference().child("Styles");
        // TODO: 18-Apr-20 change dbref
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Services").child("ServiceType");

        databaseReference.keepSynced(true);

        //querying the database BY NAME
        Query query = databaseReference.orderByChild("accountType").equalTo("Barbers");

  /*      PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(3)
                .build();

        DatabasePagingOptions<StylesItemModel> databasePagingOptions = new DatabasePagingOptions.Builder<StylesItemModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, StylesItemModel.class)
                .build();
*/

        FirebaseRecyclerOptions<ServicePerson> options =
                new FirebaseRecyclerOptions.Builder<ServicePerson>().setQuery(query,
                        ServicePerson.class)
                        .build();

        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        } else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        }


        adapter = new AllBarbersAdapter(options, getContext());
        rv.setAdapter(adapter);


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
       /* outState.putString(MyConstants.ACCOUNT_TYPE, MainActivity.serviceType);
        outState.putString(MyConstants.NAME, MainActivity.name);
        outState.putString(MyConstants.ABOUT, MainActivity.about);
        outState.putString(MyConstants.IMAGE_URL, MainActivity.imageUrl);*/
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
