package com.artisans.qwikhomeservices.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.handymanactivity.AcceptOrRejectActivity;
import com.artisans.qwikhomeservices.models.Customer;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HandyManRequestReceived extends FirebaseRecyclerAdapter<Customer, HandyManRequestReceived.HandyManRequestViewHolder> {
    private Intent intent;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HandyManRequestReceived(@NonNull FirebaseRecyclerOptions<Customer> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HandyManRequestViewHolder holder, int position, @NonNull final Customer model) {
        holder.showName(model.getFullName());
        holder.showUserPhoto(model.getImage());
        holder.showResponse(model.getResponse());
        holder.showDate(model.getDate());
        holder.showReason(model.getReason());

        final String getAdapterPosition = getRef(position).getKey();

        holder.btnView.setOnClickListener(v -> {

            intent = new Intent(v.getContext(), AcceptOrRejectActivity.class);
            intent.putExtra("position", getAdapterPosition);
            intent.putExtra("name", model.getFullName());
            intent.putExtra("image", model.getImage());
            intent.putExtra("date", model.getDate());
            intent.putExtra("reason", model.getReason());

            v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


        });
    }


    @NonNull
    @Override
    public HandyManRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HandyManRequestViewHolder((LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_handyman_request_received, viewGroup, false)));
    }


    //an inner class to hold the views to be inflated
    public static class HandyManRequestViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private Button btnView;


        HandyManRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnView = view.findViewById(R.id.btnView);
        }


        void showDate(Long date) {

            TextView txtDate = view.findViewById(R.id.txtRequestDate);
            SimpleDateFormat sfd = new SimpleDateFormat("'Requested on ' dd-MM-yyyy '@' hh:mm aa",
                    Locale.US);

            try {
                txtDate.setText(sfd.format(date));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //display the user photo
        void showUserPhoto(String urlOfImage) {
            CircleImageView profile = view.findViewById(R.id.imgItemPhoto);

            Glide.with(view).load(urlOfImage).into(profile);
        }


        //display the Name
        void showName(String s) {
            TextView name = view.findViewById(R.id.txtNameOfCustomer);
            name.setText(s);
        }


        //display the details
        void showResponse(String s) {
            TextView loc = view.findViewById(R.id.txtResponse);
            loc.setText(s);
        }

        //display the reason
        void showReason(String s) {
            TextView reason = view.findViewById(R.id.txtReason);
            reason.setText(s);
        }


    }

}
