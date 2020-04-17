package com.artisans.qwikhomeservices.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.LayoutStylesListItemBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
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


public class StylesAdapter extends FirebaseRecyclerAdapter<StylesItemModel, StylesAdapter.StylesViewHolder> {
    private static onItemClickListener onItemClickListener;

    public StylesAdapter(@NonNull FirebaseRecyclerOptions<StylesItemModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public StylesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutStylesListItemBinding layoutStylesListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.layout_styles_list_item, viewGroup, false);

        return new StylesViewHolder(layoutStylesListItemBinding);
    }


    @Override
    protected void onBindViewHolder(@NonNull StylesViewHolder stylesViewHolder,
                                    int i, @NonNull StylesItemModel itemModel) {

        stylesViewHolder.layoutStylesListItemBinding.setItem(itemModel);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        Glide.with(stylesViewHolder.layoutStylesListItemBinding.getRoot().getContext())
                .load(itemModel.itemImage)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        if (isFirstResource) {
                            stylesViewHolder.layoutStylesListItemBinding.progressBar.setVisibility(View.INVISIBLE);

                        }
                        stylesViewHolder.layoutStylesListItemBinding.progressBar.setVisibility(View.VISIBLE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        stylesViewHolder.layoutStylesListItemBinding.progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(stylesViewHolder.layoutStylesListItemBinding.imgStylePhoto);

        stylesViewHolder.layoutStylesListItemBinding.mCardViewItem.startAnimation(AnimationUtils.loadAnimation(stylesViewHolder.layoutStylesListItemBinding.getRoot().getContext()
                , R.anim.fade_scale_animation));


    }


    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        StylesAdapter.onItemClickListener = onItemClickListener;

    }

    public interface onItemClickListener {
        void onClick(View view, int position);
    }

    public static class StylesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LayoutStylesListItemBinding layoutStylesListItemBinding;

        public StylesViewHolder(@NonNull LayoutStylesListItemBinding layoutStylesListItemBinding) {
            super(layoutStylesListItemBinding.getRoot());
            this.layoutStylesListItemBinding = layoutStylesListItemBinding;
            layoutStylesListItemBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(layoutStylesListItemBinding.getRoot(), getAdapterPosition());

        }
    }
}
