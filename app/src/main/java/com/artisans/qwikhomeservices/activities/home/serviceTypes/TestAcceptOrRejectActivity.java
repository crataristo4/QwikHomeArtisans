package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.adapters.TestAcceptAdatapter;
import com.artisans.qwikhomeservices.models.RequestModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TestAcceptOrRejectActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    TestAcceptAdatapter customerRequestSent;
    SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_accept_or_reject);
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Requests");
        databaseReference.keepSynced(true);


        loadData();
    }

    private void loadData() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Query query;
        String uid = MainActivity.uid;
        query = databaseReference.orderByChild("receiverId").equalTo(uid);

        FirebaseRecyclerOptions<RequestModel> options = new FirebaseRecyclerOptions.Builder<RequestModel>().
                setQuery(query, RequestModel.class).build();
        customerRequestSent = new TestAcceptAdatapter(options, this.getSupportFragmentManager());
        recyclerView.setAdapter(customerRequestSent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        customerRequestSent.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customerRequestSent.stopListening();
    }
}
