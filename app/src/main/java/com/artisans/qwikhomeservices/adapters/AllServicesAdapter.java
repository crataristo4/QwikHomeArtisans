package com.artisans.qwikhomeservices.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.serviceTypes.DetailsScrollingActivity;
import com.artisans.qwikhomeservices.databinding.LayoutListItemsBinding;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AllServicesAdapter extends FirebaseRecyclerAdapter<ServicePerson,
        AllServicesAdapter.AllServiceViewHolder> {
    private Context mContext;


    public AllServicesAdapter(@NonNull FirebaseRecyclerOptions<ServicePerson> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AllServiceViewHolder allServiceViewHolder,
                                    int i,
                                    @NonNull ServicePerson singlePerson) {

        allServiceViewHolder.listItemsServicesBinding.setServiceType(singlePerson);
        allServiceViewHolder.cardView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        Glide.with(allServiceViewHolder.itemView.getContext())
                .load(singlePerson.image)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        allServiceViewHolder.listItemsServicesBinding.pbLoading.setVisibility(View.VISIBLE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        allServiceViewHolder.listItemsServicesBinding.pbLoading.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(allServiceViewHolder.listItemsServicesBinding.imgUserPhoto);

        allServiceViewHolder.listItemsServicesBinding.mMaterialCard.setOnClickListener(v -> {

            String position = getRef(i).getKey();
            Intent gotoDetailsIntent = new Intent(allServiceViewHolder.itemView.getContext(),
                    DetailsScrollingActivity.class);
            gotoDetailsIntent.putExtra("position", position);
            gotoDetailsIntent.putExtra("fullName", singlePerson.getFullName());
            gotoDetailsIntent.putExtra("about", singlePerson.getAbout());
            gotoDetailsIntent.putExtra("image", singlePerson.getImage());
            gotoDetailsIntent.putExtra("servicePersonId", singlePerson.getServicePersonId());
            gotoDetailsIntent.putExtra("mobileNumber", singlePerson.getMobileNumber());
            allServiceViewHolder.itemView.getContext().startActivity(gotoDetailsIntent);

        });


    }

    @NonNull
    @Override
    public AllServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutListItemsBinding listItemsServicesBinding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.layout_list_items, viewGroup, false);

        return new AllServiceViewHolder(listItemsServicesBinding);


    }

    static class AllServiceViewHolder extends RecyclerView.ViewHolder {
        LayoutListItemsBinding listItemsServicesBinding;
        CardView cardView;

        AllServiceViewHolder(@NonNull LayoutListItemsBinding listItemsServicesBinding) {
            super(listItemsServicesBinding.getRoot());
            this.listItemsServicesBinding = listItemsServicesBinding;
            cardView = listItemsServicesBinding.mMaterialCard;

        }


    }


}
