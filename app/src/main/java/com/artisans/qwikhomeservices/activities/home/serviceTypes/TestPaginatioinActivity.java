package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.os.Bundle;
import android.os.Handler;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.ItemStyleAdapter;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestPaginatioinActivity extends AppCompatActivity {
    final int pagePerLimit = 3;
    int currentItem, totalItem = 0, scrolledOutItem, lastVisibleItem;
    boolean isScrolled = false, isMaxData = false;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ItemStyleAdapter itemStyleAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String lastKey = "", lastNode = "";
    private List<StylesItemModel> stylesItemModelList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_paginatioin);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        assert firebaseUser != null;
        String uid = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Styles").child(uid);
        databaseReference.keepSynced(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAsh, R.color.colorOrange);
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            loadData();
        }, 3000);

        stylesItemModelList = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

                //refresh data
                DisplayViewUI.displayToast(TestPaginatioinActivity.this, "refreshing data");
            }

        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolled = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem = layoutManager.getChildCount();
                totalItem = layoutManager.getItemCount();
                scrolledOutItem = layoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {

                    if (isScrolled && (currentItem + scrolledOutItem == totalItem) && lastKey != null) {
                        isScrolled = false;
                        //fetchData...
                        DisplayViewUI.displayToast(TestPaginatioinActivity.this, "fetching data...");

                        // TODO: 12-Apr-20  paginate through items...


                    }

                }

            }
        });


    }


    private void loadData() {
        Query query;

        query = databaseReference.orderByChild("price");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        lastKey = ds.getKey();

                        StylesItemModel stylesItemModel = ds.getValue(StylesItemModel.class);
                        stylesItemModelList.add(stylesItemModel);

                    }

                }
                itemStyleAdapter = new ItemStyleAdapter(TestPaginatioinActivity.this, stylesItemModelList);

                recyclerView.setAdapter(itemStyleAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                isScrolled = false;
            }
        });


    }


}
