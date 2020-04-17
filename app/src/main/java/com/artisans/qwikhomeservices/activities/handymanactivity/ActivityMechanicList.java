package com.artisans.qwikhomeservices.activities.handymanactivity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.HandyManTypesAdapter;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityMechanicList extends AppCompatActivity {

    private static final String TAG = "ActivityMechanicList";
    HandyManTypesAdapter adapter;
    private DatabaseReference mechanicDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);

        mechanicDbRef = FirebaseDatabase.getInstance().getReference().child("HandyMen").child("Mechanic");
        mechanicDbRef.keepSynced(true);
        setUpRecycler();
    }


    private void setUpRecycler() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewMechanic);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        //now set the drawable of the item decorator
        try {
            itemDecoration.setDrawable(
                    ContextCompat.getDrawable(ActivityMechanicList.this, R.drawable.recycler_divider)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }


        FirebaseRecyclerOptions<ServicePerson> options = new FirebaseRecyclerOptions.Builder<ServicePerson>().
                setQuery(mechanicDbRef, ServicePerson.class).build();

        adapter = new HandyManTypesAdapter(options);


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
