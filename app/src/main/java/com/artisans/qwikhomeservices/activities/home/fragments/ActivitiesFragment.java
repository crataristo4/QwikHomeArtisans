package com.artisans.qwikhomeservices.activities.home.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.ActivityItemAdapter;
import com.artisans.qwikhomeservices.databinding.FragmentActivitiesBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivitiesFragment extends Fragment {

    private static final String KEY = "key";
    private Bundle mBundleState;
    private static final String TAG = "ActivityFragment";
    private FragmentActivitiesBinding fragmentActivitiesBinding;
    private RecyclerView rvBarbers, rvHairStylist, rvInteriorDeco, rvItems;
    // private AllServicesAdapter allServicesAdapter, allServicesAdapter1, allServicesAdapter2;
    private DatabaseReference dbRef;
    private ActivityItemAdapter activityItemAdapter;
    private LinearLayoutManager layoutManager;
    private Parcelable mState;

    public ActivitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentActivitiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false);

        return fragmentActivitiesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*rvBarbers = fragmentActivitiesBinding.rvBarbers;
        rvHairStylist = fragmentActivitiesBinding.rvHairStylist;
        rvInteriorDeco = fragmentActivitiesBinding.rvInteriorDeco;

        rvBarbers.setHasFixedSize(true);
        rvBarbers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvHairStylist.setHasFixedSize(true);
        rvHairStylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvInteriorDeco.setHasFixedSize(true);
        rvInteriorDeco.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        loadData();
        loadData1();
        loadData2();*/

        loadActivityData();

    }

    private void loadActivityData() {
        rvItems = fragmentActivitiesBinding.rvItems;
        rvItems.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        rvItems.setLayoutManager(layoutManager);

        dbRef = FirebaseDatabase.getInstance().getReference()
                .child("Activity");
        dbRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = dbRef.orderByValue();

        FirebaseRecyclerOptions<StylesItemModel> options =
                new FirebaseRecyclerOptions.Builder<StylesItemModel>().setQuery(query,
                        StylesItemModel.class)
                        .build();

        activityItemAdapter = new ActivityItemAdapter(options);
        rvItems.setAdapter(activityItemAdapter);
    }


  /*  private void loadData1() {
        dbBarbersRef = FirebaseDatabase.getInstance().getReference()
                .child(MyConstants.SERVICES)
                .child(MyConstants.WOMEN_HAIR_STYLIST);
        dbBarbersRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = dbBarbersRef.orderByChild("name");

        FirebaseRecyclerOptions<ServicePerson> options =
                new FirebaseRecyclerOptions.Builder<ServicePerson>().setQuery(query,
                        ServicePerson.class)
                        .build();

        allServicesAdapter1 = new AllServicesAdapter(options);
        rvHairStylist.setAdapter(allServicesAdapter1);
    }

    private void loadData2() {
        dbBarbersRef = FirebaseDatabase.getInstance().getReference()
                .child(MyConstants.SERVICES)
                .child(MyConstants.INTERIOR_DERCORATOR);
        dbBarbersRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = dbBarbersRef.orderByChild("name");

        FirebaseRecyclerOptions<ServicePerson> options =
                new FirebaseRecyclerOptions.Builder<ServicePerson>().setQuery(query,
                        ServicePerson.class)
                        .build();

        allServicesAdapter2 = new AllServicesAdapter(options);
        rvInteriorDeco.setAdapter(allServicesAdapter2);
    }

    private void loadData() {

        dbBarbersRef = FirebaseDatabase.getInstance().getReference()
                .child(MyConstants.SERVICES)
                .child(MyConstants.BARBERS);
        dbBarbersRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = dbBarbersRef.orderByChild("name");

        FirebaseRecyclerOptions<ServicePerson> options =
                new FirebaseRecyclerOptions.Builder<ServicePerson>().setQuery(query,
                        ServicePerson.class)
                        .build();

        allServicesAdapter = new AllServicesAdapter(options);
        rvBarbers.setAdapter(allServicesAdapter);
    }
*/

    @Override
    public void onStart() {
        super.onStart();
        activityItemAdapter.startListening();
        /*allServicesAdapter.startListening();
        allServicesAdapter1.startListening();
        allServicesAdapter2.startListening();
*/

    }


    @Override
    public void onStop() {
        super.onStop();
        activityItemAdapter.stopListening();
       /* allServicesAdapter.stopListening();
        allServicesAdapter1.stopListening();
        allServicesAdapter2.stopListening();*/
    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mState = layoutManager.onSaveInstanceState();
        mBundleState.putParcelable(KEY, mState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBundleState != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mState = mBundleState.getParcelable(KEY);
                    layoutManager.onRestoreInstanceState(mState);
                }
            }, 50);
        }

        rvItems.setLayoutManager(layoutManager);
    }


}
