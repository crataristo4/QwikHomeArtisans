package com.artisans.qwikhomeservices.adapters;

import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.handymanactivity.RequestHandyManActivity;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class HandyManTypesAdapter extends FirebaseRecyclerAdapter<ServicePerson, HandyManTypesAdapter.MechanicViewHolder> {
    private Intent intent;
    private double lat, lng;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HandyManTypesAdapter(@NonNull FirebaseRecyclerOptions<ServicePerson> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MechanicViewHolder holder,
                                    int position, @NonNull final ServicePerson model) {
        double distance;
        Location locationA = new Location("Point A");
        locationA.setLatitude(lat);
        locationA.setLongitude(lng);

        Location locationB = new Location("Point B");
        locationB.setLatitude(model.getLatitude());
        locationB.setLongitude(model.getLongitude());
//
//        distance = locationA.distanceTo(locationB);   // in meters
//        distance = locationA.distanceTo(locationB) / 1000;   // in km
        distance = locationA.distanceTo(locationB) / 1609.344;   // in miles


        float[] results = new float[1];
        Location.distanceBetween(lat, lng, model.getLatitude(), model.getLongitude(), results);

        float distanceInMeters = results[0];

        boolean isWithinRange = distanceInMeters < 1000;

        if (isWithinRange) {
            holder.txtDistance.setText(MessageFormat.format("{0} m ", String.format("%.2f", distanceInMeters)));
        } else if (distanceInMeters >= 1000) {
            distance = locationA.distanceTo(locationB) / 1000;   // in km
            // toKilometers = (distanceInMeters / mToKm);
            holder.txtDistance.setText(MessageFormat.format("{0} km ", String.format("%.2f", distance)));

        } else if (distance >= 1609.34) {
            distance = locationA.distanceTo(locationB) / 1609.344;   // in miles
            holder.txtDistance.setText(MessageFormat.format("{0} miles ", String.format("%.2f", distance)));
        }


        holder.showName(model.getName());
        holder.showOccupation(model.getOccupation());
        holder.showUserPhoto(model.getImage());
        // holder.txtDistance.setText(String.valueOf(distance));


        final String getAdapterPosition = getRef(position).getKey();

        holder.btnView.setOnClickListener(v -> {


            intent = new Intent(v.getContext(), RequestHandyManActivity.class);
            intent.putExtra("position", getAdapterPosition);
            intent.putExtra("name", model.getName());
            intent.putExtra("image", model.getImage());
            intent.putExtra("occupation", model.getOccupation());
            intent.putExtra("details", model.getAbout());
            intent.putExtra("location", model.getLocation());
            intent.putExtra("handyManId", model.getServicePersonId());
            intent.putExtra("latitude", model.getLatitude());
            intent.putExtra("longitude", model.getLongitude());

            v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


        });
    }

    @NonNull
    @Override
    public MechanicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MechanicViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_view_all_handy_men, viewGroup, false));
    }

    //an inner class to hold the views to be inflated
    public class MechanicViewHolder extends RecyclerView.ViewHolder {
        TextView txtDistance;
        FirebaseUser firebaseUser;
        DatabaseReference mUser;
        private View view;
        private ImageButton btnView;
        private FirebaseAuth mAuth;


        MechanicViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnView = view.findViewById(R.id.btnView);
            txtDistance = view.findViewById(R.id.txtDistance);
            mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();
            String uid = firebaseUser.getUid();

            mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        lat = (double) dataSnapshot.child("latitude").getValue();
                        lng = (double) dataSnapshot.child("longitude").getValue();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        //display the user photo
        void showUserPhoto(String urlOfImage) {
            CircleImageView profile = view.findViewById(R.id.imgViewHandyMan);

            Glide.with(view).load(urlOfImage).into(profile);
        }

        //display the Name
        void showName(String s) {
            TextView name = view.findViewById(R.id.txtNameOfHandyMan);
            name.setText(s);
        }


        //display the occupation
        void showOccupation(String s) {
            TextView occ = view.findViewById(R.id.txtOccupationOfHandyMan);
            occ.setText(s);
        }


    }

}
