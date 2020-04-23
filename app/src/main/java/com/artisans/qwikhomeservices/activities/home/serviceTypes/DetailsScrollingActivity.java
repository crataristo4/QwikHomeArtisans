package com.artisans.qwikhomeservices.activities.home.serviceTypes;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.SendRequestBottomSheet;
import com.artisans.qwikhomeservices.adapters.StylesAdapter;
import com.artisans.qwikhomeservices.databinding.ActivityDetailsScrollingBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DetailsScrollingActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    int numberOfItems = 0;
    private ActivityDetailsScrollingBinding activityDetailsScrollingBinding;
    private DatabaseReference databaseReference;
    private StylesAdapter adapter;
    private String name, about, image, userId, mobileNumber;
    private long mLastClickTime = 0;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);


        activityDetailsScrollingBinding = DataBindingUtil.setContentView(this, R.layout.activity_details_scrolling);

        setSupportActionBar(activityDetailsScrollingBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        if (intent != null) {
            String position = intent.getStringExtra("position");
            assert position != null;
            name = intent.getStringExtra("fullName");
            about = intent.getStringExtra("about");
            image = intent.getStringExtra("image");
            userId = intent.getStringExtra("servicePersonId");
            mobileNumber = intent.getStringExtra("mobileNumber");
        }

        activityDetailsScrollingBinding.fabCall.setOnClickListener(view -> Snackbar.make(view,
                "Call ".concat(name),
                Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.WHITE)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(getColor(R.color.purple))
                .setDuration(8000)
                .setAction("CALL NOW", v -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.fromParts("tel", mobileNumber, null));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);

                }).show());


        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Styles")
                .child(userId);
        databaseReference.keepSynced(true);
        //get number of items in database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    numberOfItems = (int) ds.getChildrenCount();

                }

                //check if the adapter is empty and notify users
                if (numberOfItems == 0) {

                    activityDetailsScrollingBinding.contentDetails.txtStyleLabel.setText(getResources().getString(R.string.noStyles));


                } else {

                    activityDetailsScrollingBinding.contentDetails.txtStyleLabel.setText(getString(R.string.styles_offered));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        activityDetailsScrollingBinding.collapsingToolBar.setTitle(name);
        activityDetailsScrollingBinding.contentDetails.txtAbout.setText(about);
        //activityDetailsScrollingBinding.userImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        Glide.with(this)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(activityDetailsScrollingBinding.userImage);


        loadStyleItems();


    }

    private void loadStyleItems() {

        RecyclerView recyclerView = activityDetailsScrollingBinding.contentDetails.rvStylesItem;
        recyclerView.setHasFixedSize(true);

        //querying the database BY NAME
        Query query = databaseReference.orderByChild("price");


        // TODO: 09-Apr-20 load more items on refresh and on recycler view scrolled to bottom


  /*      PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(3)
                .build();

        DatabasePagingOptions<StylesItemModel> databasePagingOptions = new DatabasePagingOptions.Builder<StylesItemModel>()
                .setLifecycleOwner(this)
                .setQuery(databaseReference, config, StylesItemModel.class)
                .build();
*/


        FirebaseRecyclerOptions<StylesItemModel> options =
                new FirebaseRecyclerOptions.Builder<StylesItemModel>().setQuery(query,
                        StylesItemModel.class)
                        .build();
        adapter = new StylesAdapter(options);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        }

        //on item click
        adapter.setOnItemClickListener((view, position) -> {
            String price = String.valueOf(adapter.getItem(position).getPrice());
            String itemStyleName = String.valueOf(adapter.getItem(position).getStyleItem());
            String imageItem = String.valueOf(adapter.getItem(position).getItemImage());

            //scroll app bar to state collapsed when item is clicked
            activityDetailsScrollingBinding.appBar.setExpanded(false, true);

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();

            Bundle bundle = new Bundle();
            bundle.putString(MyConstants.PRICE, price);
            bundle.putString(MyConstants.STYLE, itemStyleName);
            bundle.putString(MyConstants.IMAGE_URL, imageItem);

            //pass details of service person to bottom sheet
            bundle.putString(MyConstants.SERVICE_PERSON_NAME, name);

            //pass users name , user photo , user id to bundle
            String userName = MainActivity.fullName;
            String userId = MainActivity.uid;
            String userPhoto = MainActivity.imageUrl;

            bundle.putString(MyConstants.NAME, userName);
            bundle.putString(MyConstants.UID, userId);
            bundle.putString(MyConstants.USER_IMAGE_URL, userPhoto);


            SendRequestBottomSheet sendRequestBottomSheet = new SendRequestBottomSheet();
            sendRequestBottomSheet.setCancelable(false);
            sendRequestBottomSheet.setArguments(bundle);
            sendRequestBottomSheet.show(getSupportFragmentManager(), MyConstants.SEND_REQUEST_TAG);


        });

        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
