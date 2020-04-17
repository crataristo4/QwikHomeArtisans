package com.artisans.qwikhomeservices.activities.handymanactivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.HandyManRequestReceived;
import com.artisans.qwikhomeservices.models.Customer;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivityHandyMen extends AppCompatActivity {
    private static final String TAG = "MainActivityHandyMen";
    HandyManRequestReceived adapter;
    FirebaseUser firebaseUser;
    String userId;
    private DatabaseReference requests;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_handy_men);

        float[] results = new float[1];
        Location.distanceBetween(5.594759, -0.223371, 5.596091, -0.223362, results);
        float distanceInMeters = results[0];
        boolean isWithinRange = distanceInMeters < 145;

        if (!isWithinRange) {

            Log.i(TAG, "cannot check in from this location: ");
            //makeToast("cannot check in from this location ");
        } else {
            // makeToast("Can check in");
            Log.i(TAG, " can check in ");
        }

        Log.i(TAG, "onLocationChanged --- distance in meters: " + distanceInMeters);


        initViews();


        setUpRecycler();
    }

    private void initViews() {
        requests = FirebaseDatabase.getInstance().getReference().child("Requests");
        requests.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        userId = firebaseUser.getUid();


    }

    private void setUpRecycler() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewShowRequest);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        //now set the drawable of the item decorator
        try {
            itemDecoration.setDrawable(
                    ContextCompat.getDrawable(MainActivityHandyMen.this, R.drawable.recycler_divider)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }


        Query query = requests.orderByChild("handyManId").equalTo(userId);

        FirebaseRecyclerOptions<Customer> options = new FirebaseRecyclerOptions.Builder<Customer>().
                setQuery(query, Customer.class).build();

        adapter = new HandyManRequestReceived(options);


        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        //notify data change
        adapter.notifyDataSetChanged();

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
