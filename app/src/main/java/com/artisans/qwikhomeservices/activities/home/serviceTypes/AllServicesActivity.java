package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

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
    private String accountType;
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
                    accountType = MyConstants.BARBERS;
                    setTitle(accountType);

                    break;
                case MyConstants.WOMEN_HAIR_STYLIST:
                    accountType = MyConstants.WOMEN_HAIR_STYLIST;
                    setTitle(accountType);

                    break;
                case MyConstants.INTERIOR_DERCORATOR:
                    accountType = MyConstants.INTERIOR_DERCORATOR;
                    setTitle(accountType);

                    break;

                case MyConstants.CARPENTERS:
                    accountType = MyConstants.CARPENTERS;
                    setTitle(accountType);

                    break;
                case MyConstants.MECHANICS:
                    accountType = MyConstants.MECHANICS;
                    setTitle(accountType);

                    break;
                case MyConstants.PEST_CONTROLS:
                    accountType = MyConstants.PEST_CONTROLS;
                    setTitle(accountType);

                    break;
                case MyConstants.PLUMBERS:
                    accountType = MyConstants.PLUMBERS;
                    setTitle(accountType);

                    break;
                case MyConstants.TILERS:
                    accountType = MyConstants.TILERS;
                    setTitle(accountType);

                    break;
                case MyConstants.TV_INSTALLERS:
                    accountType = MyConstants.TV_INSTALLERS;
                    setTitle(accountType);

                    break;
                case MyConstants.WELDERS:
                    accountType = MyConstants.WELDERS;
                    setTitle(accountType);

                    break;
                case MyConstants.ROLLERS:
                    accountType = MyConstants.ROLLERS;
                    setTitle(accountType);

                    break;
                case MyConstants.GARDENERS:
                    accountType = MyConstants.GARDENERS;
                    setTitle(accountType);

                    break;
                case MyConstants.PAINTERS:
                    accountType = MyConstants.PAINTERS;
                    setTitle(accountType);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + getIntent.getStringExtra(MyConstants.ACCOUNT_TYPE));
            }

        }

        Log.i("initRecyclerView: ", accountType);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("Services").child("ServiceType");
        db.keepSynced(true);

        recyclerView = allServicesBinding.rvAllBarbers;
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //querying the database BY NAME
        Query query = db.orderByChild("accountType").equalTo(accountType);

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
