package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.AllBarbersAdapter;
import com.artisans.qwikhomeservices.databinding.ActivityAllServicesBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

//TODO change class name
public class AllServicesActivity extends AppCompatActivity {
    private ActivityAllServicesBinding allServicesBinding;
    private AllBarbersAdapter adapter;
    private RecyclerView recyclerView;
    private String serviceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allServicesBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_services);

        initRecyclerView();

    }

    private void initRecyclerView() {

        Intent getIntent = getIntent();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent != null) {

            switch (Objects.requireNonNull(getIntent.getStringExtra(MyConstants.ACCOUNT_TYPE))) {
                case MyConstants.BARBERS:
                    serviceType = MyConstants.BARBERS;
                    setTitle(serviceType);

                    break;
                case MyConstants.WOMEN_HAIR_STYLIST:
                    serviceType = MyConstants.WOMEN_HAIR_STYLIST;
                    setTitle(serviceType);

                    break;
                case MyConstants.INTERIOR_DERCORATOR:
                    serviceType = MyConstants.INTERIOR_DERCORATOR;
                    setTitle(serviceType);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + getIntent.getStringExtra(MyConstants.ACCOUNT_TYPE));
            }

        }


        DatabaseReference allBarbersDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(MyConstants.SERVICES)
                .child(serviceType);
        allBarbersDbRef.keepSynced(true);

        recyclerView = allServicesBinding.rvAllBarbers;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //querying the database BY NAME
        Query query = allBarbersDbRef.orderByChild("name");

        FirebaseRecyclerOptions<ServicePerson> options =
                new FirebaseRecyclerOptions.Builder<ServicePerson>().setQuery(query,
                        ServicePerson.class)
                        .build();

        //DISPLAY different layout for screen orientation
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        } else {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        }

        adapter = new AllBarbersAdapter(options, AllServicesActivity.this);

        recyclerView.setAdapter(adapter);


    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
