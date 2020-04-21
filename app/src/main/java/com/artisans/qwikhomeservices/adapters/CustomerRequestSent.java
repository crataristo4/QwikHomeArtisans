package com.artisans.qwikhomeservices.adapters;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.customeractivity.ChatActivity;
import com.artisans.qwikhomeservices.activities.customeractivity.RatingActivity;
import com.artisans.qwikhomeservices.activities.customeractivity.ShowRouteActivity;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerRequestSent extends FirebaseRecyclerAdapter<ServicePerson, CustomerRequestSent.HandyManRequest> {
    FragmentManager fragmentManager;
    private Intent intent;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CustomerRequestSent(@NonNull FirebaseRecyclerOptions<ServicePerson> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final HandyManRequest holder, int position,
                                    @NonNull final ServicePerson model) {

        holder.showName(model.getSenderName());
        holder.showUserPhoto(model.getSenderPhoto());
        // holder.showResponse(model.getResponse());
        // holder.showDate(model.getDateRequested());
        // holder.showRating(model.getRating());


        final String getAdapterPosition = getRef(position).getKey();

        holder.btnView.setOnClickListener(v -> {
            if (model.getRating() == 0.0) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(v.getResources().getDrawable(R.drawable.request))
                        .setTitle("Your request to " + model.getServicePersonName())
                        .setMessage(model.getReason())
                        .setPositiveButton("ok", (dialog, which) -> dialog.dismiss()).create().show();

            } else if (model.getRating() > 0) {

                new AlertDialog.Builder(v.getContext())
                        .setIcon(v.getResources().getDrawable(R.drawable.request))
                        .setTitle("Your request to " + model.getServicePersonName())
                        .setMessage(model.getReason() + "\n\n\n" +
                                "You rated " + model.getServicePersonName() + " "
                                + model.getRating() + " stars on the work done")
                        .setPositiveButton("ok", (dialog, which) -> dialog.dismiss()).create().show();
            }

        });

        holder.btnChat.setOnClickListener(v -> {
            intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("position", getAdapterPosition);
            intent.putExtra("photo", model.getServicePersonPhoto());
            intent.putExtra("name", model.getServicePersonName());
            intent.putExtra("content", model.getReason());
            intent.putExtra("senderName", model.getSenderName());
            intent.putExtra("senderPhoto", model.getSenderPhoto());
            v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        });


        holder.btnRateHandyMan.setOnClickListener(view -> new AlertDialog.Builder(view.getContext())
                .setIcon(view.getResources().getDrawable(R.drawable.handshake))
                .setTitle("Request Accepted")
                .setMessage("Is the job done?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();


                    new AlertDialog.Builder(view.getContext())
                            .setIcon(view.getResources().getDrawable(R.drawable.handshake))
                            .setTitle("Rate " + model.getServicePersonName())
                            .setMessage("Would you like to rate " + model.getServicePersonName() + " to "
                                    + "improve working  experience?")
                            .setPositiveButton("Yes", (dialog12, which12) -> {
                                dialog12.dismiss();

                                intent = new Intent(view.getContext(), RatingActivity.class);
                                intent.putExtra("position", getAdapterPosition);
                                intent.putExtra("servicePersonPhoto", model.getServicePersonPhoto());
                                intent.putExtra("servicePersonName", model.getServicePersonName());
                                view.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


                            }).setNegativeButton("No", (dialog1, which1) -> {

                    }).create().show();

                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create().show());


        holder.btnShowRoute.setOnClickListener(v -> {
            //todo create a map to show the route from customer to handy man
            holder.makeToast("Working on maps");
            intent = new Intent(v.getContext(), ShowRouteActivity.class);
            intent.putExtra("position", getAdapterPosition);
            intent.putExtra("latitude", model.getLatitude());
            intent.putExtra("longitude", model.getLongitude());
            v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        });

    }

    @NonNull
    @Override
    public HandyManRequest onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HandyManRequest((LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_handyman_request_received, viewGroup, false)));
    }


    //an inner class to hold the views to be inflated
    public class HandyManRequest extends RecyclerView.ViewHolder {
        public ConstraintLayout viewForeground;
        private View view;
        private ImageButton btnView, btnChat, btnRateHandyMan, btnShowRoute;
        private RelativeLayout viewBackground;
        private RatingBar ratingBar;

        HandyManRequest(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnView = view.findViewById(R.id.btnView);
            btnChat = view.findViewById(R.id.btnChat);
            btnRateHandyMan = view.findViewById(R.id.btnRateHandyMan);
            btnShowRoute = view.findViewById(R.id.imgBtnRouteOnMap);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            ratingBar = view.findViewById(R.id.ratedResults);
        }


        void showDate(String date) {

            TextView txtDate = view.findViewById(R.id.txtRequestDate);
            txtDate.setText(String.format("Requested on %s", date));
        }


        //display the user photo
        void showUserPhoto(String urlOfImage) {
            CircleImageView profile = view.findViewById(R.id.imgItemPhoto);

            Glide.with(view).load(urlOfImage).into(profile);
        }


        //display the Name
        void showName(String s) {
            TextView name = view.findViewById(R.id.txtName);
            name.setText(s);
        }

        //display the rating
        void showRating(float rating) {
            if (!String.valueOf(rating).isEmpty() && rating > 0) {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(rating);
                btnRateHandyMan.setEnabled(false);

            } else if (rating == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            }

        }

        //display the details
        void showResponse(String s) {
            TextView loc = view.findViewById(R.id.txtResponse);
            //customer can only chat , rate and view the route only when their request are accepted
            if (s.equals("Request Accepted")) {
                btnRateHandyMan.setVisibility(View.VISIBLE);
                btnChat.setVisibility(View.VISIBLE);
                btnShowRoute.setVisibility(View.VISIBLE);
                loc.setTextColor(view.getResources().getColor(R.color.colorGreen));

            } else if (s.equals("Request Rejected")) {
                btnChat.setVisibility(View.INVISIBLE);
                btnRateHandyMan.setVisibility(View.INVISIBLE);
                btnShowRoute.setVisibility(View.INVISIBLE);
                loc.setTextColor(view.getResources().getColor(R.color.colorRed));
            }


            loc.setText(s);
        }

        void makeToast(String text) {
            Toast toast = Toast.makeText(view.getContext(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


    }


}
