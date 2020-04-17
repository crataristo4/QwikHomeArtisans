package com.artisans.qwikhomeservices.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.serviceTypes.DetailsScrollingActivity;
import com.artisans.qwikhomeservices.databinding.LayoutListServicePersonsBinding;
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


    public AllServicesAdapter(@NonNull FirebaseRecyclerOptions<ServicePerson> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AllServiceViewHolder allServiceViewHolder,
                                    int i,
                                    @NonNull ServicePerson singlePerson) {

        allServiceViewHolder.layoutListServicePersonsBinding.setUser(singlePerson);

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
                        allServiceViewHolder.layoutListServicePersonsBinding.progressBar.setVisibility(View.VISIBLE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        allServiceViewHolder.layoutListServicePersonsBinding.progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(allServiceViewHolder.layoutListServicePersonsBinding.imgUserPhoto);

        allServiceViewHolder.layoutListServicePersonsBinding.mCardViewItem.setOnClickListener(v -> {

            String position = getRef(i).getKey();
            Intent gotoDetailsIntent = new Intent(allServiceViewHolder.itemView.getContext(),
                    DetailsScrollingActivity.class);
            gotoDetailsIntent.putExtra("position", position);
            gotoDetailsIntent.putExtra("name", singlePerson.getName());
            gotoDetailsIntent.putExtra("about", singlePerson.getAbout());
            gotoDetailsIntent.putExtra("image", singlePerson.getImage());
            gotoDetailsIntent.putExtra("userId", singlePerson.getUserId());

            allServiceViewHolder.itemView.getContext().startActivity(gotoDetailsIntent);

        });


    }

    @NonNull
    @Override
    public AllServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutListServicePersonsBinding layoutListServicePersonsBinding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.layout_list_service_persons, viewGroup, false);

        return new AllServiceViewHolder(layoutListServicePersonsBinding);


    }

    static class AllServiceViewHolder extends RecyclerView.ViewHolder {
        LayoutListServicePersonsBinding layoutListServicePersonsBinding;

        AllServiceViewHolder(@NonNull LayoutListServicePersonsBinding itemView) {
            super(itemView.getRoot());
            layoutListServicePersonsBinding = itemView;
        }


    }


}
