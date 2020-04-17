package com.artisans.qwikhomeservices.activities.customeractivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.HandyManTypesAdapter;
import com.artisans.qwikhomeservices.directionhelpers.FetchURL;
import com.artisans.qwikhomeservices.directionhelpers.TaskLoadedCallback;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.artisans.qwikhomeservices.activities.customeractivity.CustomerRegister.PLAYSERVICES_RESOLUTION;

public class HandyManMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, TaskLoadedCallback {
    public static final int MYPERMISSIONREQUEST = 100;
    public static final int UPDATE_INTERVAL = 1000;
    public static final int FAST_INTERVAL = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "HandyManMapsActivity";
    DatabaseReference carpenterDbRef, mUserDbRef;
    Button btnRequest;
    ProgressDialog loading;
    double myLat, myLng, handyManLat, HandyManLng;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String uid;
    HandyManTypesAdapter adapter;
    RecyclerView recyclerView;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double latitude, longitude;
    private Location mLastLocation;
    private Marker marker;
    private DatabaseReference CarpenterDbRef;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handy_man_maps);
        place1 = new MarkerOptions().position(new LatLng(5.637242, -0.3197683)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(5.637491, -0.3108583)).title("Location 2");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        CarpenterDbRef = FirebaseDatabase.getInstance().getReference().child("HandyMen").child("Mechanic");
        CarpenterDbRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();


        mUserDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


        mUserDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    myLat = (double) dataSnapshot.child("latitude").getValue();
                    myLng = (double) dataSnapshot.child("longitude").getValue();


                    Log.i(TAG, "My latitude = " + myLat + " My Longitude " + myLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        loading = new ProgressDialog(this);
        btnRequest = findViewById(R.id.btnRequest);

        btnRequest.setOnClickListener(v -> {
            loading.setMessage("Searching for nearest mechanic ..");
            loading.show();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                loading.dismiss();
                new FetchURL(HandyManMapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

                //   recyclerView.setVisibility(View.VISIBLE);
            }, 2000);


        });


        //build the google client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        setUpLocation();
        initViews();
        setUpRecycler();

    }

    private void searchNearestPlumber() {


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(place1);
        mMap.addMarker(place2);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.isIndoorLevelPickerEnabled();
        uiSettings.isRotateGesturesEnabled();
        uiSettings.isIndoorLevelPickerEnabled();
        uiSettings.isTiltGesturesEnabled();
        uiSettings.setIndoorLevelPickerEnabled(true);


        Log.d(TAG, "onMapReady: Successful");


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void initViews() {

        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }


    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);


        } else {
            if (checkPlayServices()) {
                buildGoogleClient();
                createLocationRequest();
                displayLocation();
            }
        }

    }

    private synchronized void buildGoogleClient() {
        //build the google client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAYSERVICES_RESOLUTION).show();
            else {
                makeToast("This device is not supported");
                finish();
            }
            return false;

        }
        return true;
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);
            if (marker != null) marker.remove();
//            marker = mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(latitude, longitude))
//                    .title("name"));
            //move camera to position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                    16f));


            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

        }
    }

    private void createLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();


        // displayLocation();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MYPERMISSIONREQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPlayServices()) {
                    buildGoogleClient();
                    createLocationRequest();
                    displayLocation();
                }
            }
        }


    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void setUpRecycler() {
        recyclerView = findViewById(R.id.recyclerCpta);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        //now set the drawable of the item decorator
        try {
            itemDecoration.setDrawable(
                    ContextCompat.getDrawable(HandyManMapsActivity.this, R.drawable.recycler_divider)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }


        FirebaseRecyclerOptions<ServicePerson> options = new FirebaseRecyclerOptions.Builder<ServicePerson>().
                setQuery(CarpenterDbRef, ServicePerson.class).build();

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
        googleApiClient.connect();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocationServices.FusedLocationApi
//                .removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        adapter.stopListening();
    }


}
