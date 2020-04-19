package com.artisans.qwikhomeservices.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.LayoutActivityItemsBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.GetTimeAgo;
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

public class ActivityItemAdapter extends FirebaseRecyclerAdapter<StylesItemModel, ActivityItemAdapter.ActivityItemAdapterViewHolder> {


    public ActivityItemAdapter(@NonNull FirebaseRecyclerOptions<StylesItemModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ActivityItemAdapterViewHolder activityItemAdapterViewHolder,
                                    int i, @NonNull StylesItemModel itemModel) {

        activityItemAdapterViewHolder.activityItemsBinding.setItems(itemModel);
        activityItemAdapterViewHolder.activityItemsBinding.txtTime.setText(GetTimeAgo.getTimeAgo(itemModel.getTimeStamp()));
        // activityItemAdapterViewHolder.activityItemsBinding.txtPubDate.setText(GetDateTime.DateToTimeFormat(itemModel.timeStamp));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        Glide.with(activityItemAdapterViewHolder.activityItemsBinding.getRoot().getContext())
                .load(itemModel.itemImage)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        if (isFirstResource) {
                            activityItemAdapterViewHolder.activityItemsBinding.progressBar.setVisibility(View.INVISIBLE);

                        }
                        activityItemAdapterViewHolder.activityItemsBinding.progressBar.setVisibility(View.VISIBLE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        activityItemAdapterViewHolder.activityItemsBinding.progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(activityItemAdapterViewHolder.activityItemsBinding.imgItemPhoto);

    }

    @NonNull
    @Override
    public ActivityItemAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutActivityItemsBinding layoutActivityItemsBinding = DataBindingUtil
                .inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.layout_activity_items, viewGroup, false);

        return new ActivityItemAdapterViewHolder(layoutActivityItemsBinding);

    }

    class ActivityItemAdapterViewHolder extends RecyclerView.ViewHolder {

        LayoutActivityItemsBinding activityItemsBinding;

        ActivityItemAdapterViewHolder(@NonNull LayoutActivityItemsBinding activityItemsBinding) {
            super(activityItemsBinding.getRoot());
            this.activityItemsBinding = activityItemsBinding;
        }
    }
}
