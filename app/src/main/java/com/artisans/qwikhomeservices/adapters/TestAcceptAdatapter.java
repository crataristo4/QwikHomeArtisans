package com.artisans.qwikhomeservices.adapters;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.AcceptOrRejectBtSheet;
import com.artisans.qwikhomeservices.models.RequestModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TestAcceptAdatapter extends FirebaseRecyclerAdapter<RequestModel, TestAcceptAdatapter.TestAcceptAdatapterViewHolder> {
    private FragmentManager fragmentManager;

    public TestAcceptAdatapter(@NonNull FirebaseRecyclerOptions<RequestModel> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;

    }

    @Override
    protected void onBindViewHolder(@NonNull TestAcceptAdatapterViewHolder testAcceptAdatapterViewHolder, int i, @NonNull RequestModel requestModel) {
        testAcceptAdatapterViewHolder.showName(requestModel.getSenderName());
        testAcceptAdatapterViewHolder.showUserPhoto(requestModel.getSenderPhoto());

        final String getAdapterPosition = getRef(i).getKey();
        testAcceptAdatapterViewHolder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptOrRejectBtSheet acceptOrRejectBtSheet = new AcceptOrRejectBtSheet();
                Bundle bundle = new Bundle();
                bundle.putString("position", getAdapterPosition);
                bundle.putString("name", requestModel.getSenderName());
                bundle.putString("photo", requestModel.getSenderPhoto());
                acceptOrRejectBtSheet.setArguments(bundle);
                acceptOrRejectBtSheet.show(fragmentManager, "show");
            }
        });


    }

    @NonNull
    @Override
    public TestAcceptAdatapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TestAcceptAdatapterViewHolder((LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_handyman_request_received, viewGroup, false)));
    }

    //an inner class to hold the views to be inflated
    public static class TestAcceptAdatapterViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout viewForeground;
        FrameLayout frameLayout;
        private View view;
        private ImageButton btnView, btnChat, btnRateHandyMan, btnShowRoute;
        private RelativeLayout viewBackground;
        private RatingBar ratingBar;

        TestAcceptAdatapterViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            frameLayout = view.findViewById(R.id.parentLayout);
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
            AppCompatImageView profile = view.findViewById(R.id.imgItemPhoto);

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
