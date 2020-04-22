package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.AllServicesAdapter;
import com.artisans.qwikhomeservices.databinding.ActivityAllServicesBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class AllServicesActivity extends AppCompatActivity {
    private ActivityAllServicesBinding allServicesBinding;
    private AllServicesAdapter adapter;
    private RecyclerView recyclerView;
    private String serviceType;
    private GridLayoutManager gridLayoutManager;
    private Parcelable mParcelable;
    private Bundle mBundle;

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
                case MyConstants.CARPENTERS:
                    serviceType = MyConstants.CARPENTERS;
                    setTitle(serviceType);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + getIntent.getStringExtra(MyConstants.ACCOUNT_TYPE));
            }

        }


        DatabaseReference allServicesDbRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(MyConstants.SERVICES)
                .child(serviceType);
        allServicesDbRef.keepSynced(true);

        // TODO: 19-Apr-20  change if crush
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("Services").child("ServiceType");

        recyclerView = allServicesBinding.rvAllBarbers;
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //querying the database BY NAME
        Query query = db.orderByChild("accountType").equalTo(serviceType);

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

        adapter = new AllServicesAdapter(options, AllServicesActivity.this);

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

    // TODO: 19-Apr-20 save item state 
}
