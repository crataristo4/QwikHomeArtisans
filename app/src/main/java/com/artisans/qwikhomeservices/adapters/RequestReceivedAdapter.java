package com.artisans.qwikhomeservices.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.AcceptOrRejectBtSheet;
import com.artisans.qwikhomeservices.databinding.LayoutRequestReceivedBinding;
import com.artisans.qwikhomeservices.models.RequestModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RequestReceivedAdapter extends FirebaseRecyclerAdapter<RequestModel, RequestReceivedAdapter.RequestReceivedAdapterViewHolder> {

    private FragmentManager fragmentManager;

    public RequestReceivedAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestReceivedAdapterViewHolder requestReceivedAdapterViewHolder, int i, @NonNull RequestModel requestModel) {


        final String getAdapterPosition = getRef(i).getKey();
        requestReceivedAdapterViewHolder.btnView.setOnClickListener(v -> {
            AcceptOrRejectBtSheet acceptOrRejectBtSheet = new AcceptOrRejectBtSheet();
            Bundle bundle = new Bundle();
            bundle.putString("position", getAdapterPosition);
            bundle.putString("senderName", requestModel.getSenderName());
            bundle.putString("senderPhoto", requestModel.getSenderPhoto());
            bundle.putString("itemName", requestModel.getItemName());
            bundle.putString("itemPhoto", requestModel.getItemImage());
            bundle.putString("itemPrice", String.valueOf(requestModel.getPrice()));
            acceptOrRejectBtSheet.setArguments(bundle);
            acceptOrRejectBtSheet.show(fragmentManager, "show");
        });

    }

    @NonNull
    @Override
    public RequestReceivedAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRequestReceivedBinding layoutRequestReceivedBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.layout_request_received, parent, false);

        return new RequestReceivedAdapterViewHolder(layoutRequestReceivedBinding);

    }

    //an inner class to hold the views to be inflated
    public static class RequestReceivedAdapterViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout viewForeground;
        FrameLayout frameLayout;
        private ImageButton btnView, btnChat;
        private RelativeLayout viewBackground;
        private RatingBar ratingBar;

        private LayoutRequestReceivedBinding layoutRequestReceivedBinding;

        RequestReceivedAdapterViewHolder(@NonNull LayoutRequestReceivedBinding layoutRequestReceivedBinding) {
            super(layoutRequestReceivedBinding.getRoot());
            this.layoutRequestReceivedBinding = layoutRequestReceivedBinding;
            frameLayout = layoutRequestReceivedBinding.parentLayout;
            btnView = layoutRequestReceivedBinding.btnView;
            btnChat = layoutRequestReceivedBinding.btnChat;
            viewBackground = layoutRequestReceivedBinding.viewBackground;
            viewForeground = layoutRequestReceivedBinding.viewForeground;
            ratingBar = layoutRequestReceivedBinding.ratedResults;
        }

    }
}
