package com.artisans.qwikhomeservices.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.bottomsheets.SendRequestBottomSheet;
import com.artisans.qwikhomeservices.databinding.LayoutStylesListItemBinding;
import com.artisans.qwikhomeservices.models.StylesItemModel;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ItemStyleAdapter extends RecyclerView.Adapter<ItemStyleAdapter.ItemStyleAdapterViewHolder> {
    private static onItemClickListener onItemClickListener;
    private Context context;
    private List<StylesItemModel> stylesItemModelList;
    private long mLastClickTime = 0;

    public ItemStyleAdapter(Context context, List<StylesItemModel> stylesItemModels) {
        this.context = context;
        this.stylesItemModelList = stylesItemModels;
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        ItemStyleAdapter.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public ItemStyleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutStylesListItemBinding layoutStylesListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.layout_styles_list_item, viewGroup, false);

        return new ItemStyleAdapterViewHolder(layoutStylesListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemStyleAdapterViewHolder itemStyleAdapterViewHolder, int i) {

        StylesItemModel stylesItemModel = stylesItemModelList.get(i);
        itemStyleAdapterViewHolder.layoutStylesListItemBinding.setItem(stylesItemModel);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        Glide.with(itemStyleAdapterViewHolder.layoutStylesListItemBinding.getRoot().getContext())
                .load(stylesItemModel.itemImage)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        itemStyleAdapterViewHolder.layoutStylesListItemBinding.progressBar.setVisibility(View.VISIBLE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        itemStyleAdapterViewHolder.layoutStylesListItemBinding.progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemStyleAdapterViewHolder.layoutStylesListItemBinding.imgStylePhoto);

        itemStyleAdapterViewHolder.layoutStylesListItemBinding.mCardViewItem.startAnimation(AnimationUtils.loadAnimation(itemStyleAdapterViewHolder.layoutStylesListItemBinding.getRoot().getContext()
                , R.anim.fade_scale_animation));

        //on item click
        itemStyleAdapterViewHolder.layoutStylesListItemBinding.mCardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                int adapterPosition = itemStyleAdapterViewHolder.getAdapterPosition();
                String price = String.valueOf(stylesItemModel.getPrice());
                String itemStyleName = String.valueOf(stylesItemModel.getStyleItem());
                String itemImage = String.valueOf(stylesItemModel.getItemImage());


                Bundle bundle = new Bundle();
                bundle.putString(MyConstants.PRICE, price);
                bundle.putString(MyConstants.STYLE, itemStyleName);
                bundle.putString(MyConstants.IMAGE_URL, itemImage);

                AppCompatActivity appCompatActivity = new AppCompatActivity();
                FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();


                SendRequestBottomSheet sendRequestBottomSheet = new SendRequestBottomSheet();
                sendRequestBottomSheet.setArguments(bundle);
                sendRequestBottomSheet.show(fragmentManager, "sendRequest");

            }
        });


    }

    @Override
    public int getItemCount() {
        return stylesItemModelList == null ? 0 : stylesItemModelList.size();
    }

    public void clear() {
        stylesItemModelList.clear();
    }

    public void addAll(List<StylesItemModel> stylesItemModels) {
        int initialSize = stylesItemModelList.size();
        stylesItemModelList.addAll(stylesItemModels);
        notifyItemRangeChanged(initialSize, stylesItemModels.size());
    }



    public void removeLastItemId() {
        stylesItemModelList.remove(stylesItemModelList.size() - 1);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public interface onItemClickListener {
        void onClick(View view, int position);
    }

    public static class ItemStyleAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LayoutStylesListItemBinding layoutStylesListItemBinding;

        ItemStyleAdapterViewHolder(@NonNull LayoutStylesListItemBinding layoutStylesListItemBinding) {
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
