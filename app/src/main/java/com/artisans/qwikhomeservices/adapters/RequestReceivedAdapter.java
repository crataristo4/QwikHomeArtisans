package com.artisans.qwikhomeservices.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.AcceptOrRejectBtSheet;
import com.artisans.qwikhomeservices.databinding.LayoutRequestReceivedBinding;
import com.artisans.qwikhomeservices.models.RequestModel;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RequestReceivedAdapter extends FirebaseRecyclerAdapter<RequestModel, RequestReceivedAdapter.RequestReceivedAdapterViewHolder> {
    private FragmentManager fragmentManager;

    public RequestReceivedAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options, FragmentManager fragmentManager) {
        super(options);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestReceivedAdapterViewHolder requestReceivedAdapterViewHolder,
                                    int i, @NonNull RequestModel requestModel) {

        requestReceivedAdapterViewHolder.layoutRequestReceivedBinding.setRequest(requestModel);

        final String getAdapterPosition = getRef(i).getKey();
        requestReceivedAdapterViewHolder.btnView.setOnClickListener(v -> {
            AcceptOrRejectBtSheet acceptOrRejectBtSheet = new AcceptOrRejectBtSheet();
            Bundle bundle = new Bundle();
            bundle.putString("position", getAdapterPosition);
            bundle.putString(MyConstants.FULL_NAME, requestModel.getSenderName());
            bundle.putString(MyConstants.USER_IMAGE_URL, requestModel.getSenderPhoto());
            bundle.putString(MyConstants.ITEM_NAME, requestModel.getItemName());
            bundle.putString(MyConstants.ITEM_PHOTO, requestModel.getItemImage());
            bundle.putString(MyConstants.ITEM_PRICE, requestModel.getPrice());
            bundle.putString(MyConstants.ITEM_REASON, requestModel.getReason());
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

        private ImageButton btnView, btnChat;
        private RatingBar ratingBar;

        private LayoutRequestReceivedBinding layoutRequestReceivedBinding;

        public RequestReceivedAdapterViewHolder(@NonNull LayoutRequestReceivedBinding layoutRequestReceivedBinding) {
            super(layoutRequestReceivedBinding.getRoot());
            this.layoutRequestReceivedBinding = layoutRequestReceivedBinding;
            btnView = layoutRequestReceivedBinding.btnView;
            btnChat = layoutRequestReceivedBinding.btnChat;
            ratingBar = layoutRequestReceivedBinding.ratedResults;
        }

    }
}
