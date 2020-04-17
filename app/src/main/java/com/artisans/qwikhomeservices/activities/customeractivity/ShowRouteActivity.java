package com.artisans.qwikhomeservices.activities.customeractivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowRouteActivity extends AppCompatActivity
        implements OnMapReadyCallback, TaskLoadedCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final String TAG = "ShowRouteActivity";
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;

    private void makeLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);

// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapRoute);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        place1 = new MarkerOptions().position(new LatLng(5.637242, -0.3147683)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(5.637391, -0.3108583)).title("Location 2");
        String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");

        // new FetchURL(ShowRouteActivity.this).execute(url, "driving");

    }


    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                makeLocationPermissionRequest();
            } else {
                showPolyline();
            }
        } else {
            showPolyline();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPolyline();
                } else {
                }
                return;
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

    private void showPolyline() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            makeLocationPermissionRequest();
            return;
        }
        mMap.setMyLocationEnabled(true);

        Polyline polyline1 =

                mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(
                                new LatLng(7.437242, -0.3147683),
                                new LatLng(5.637391, -0.3108583))
                        .color(Color.GREEN)
                        .width(3));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(5.034063, -0.567006), 12));
    }

}
