package com.artisans.qwikhomeservices.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class RequestModel extends BaseObservable {
    public float rating;
    public String senderId;
    public String receiverId;
    public String name;
    public String reason;
    public int price;
    public String itemName;
    public double latitude;
    public double longitude;
    public String response;
    public String location;
    public String mobileNumber;
    public String itemImage;
    public String distanceBetween;
    public String senderPhoto;
    public String senderName;
    public String servicePersonName;
    public String servicePersonPhoto;
    public String dateRequested;
    public String paymentStatus;
    public String paymentAmount;


    public RequestModel() {
    }

    public RequestModel(float rating, String senderId, String receiverId,
                        String name, String reason, int price, String itemName,
                        String response, String itemImage, String senderPhoto, String senderName,
                        String servicePersonName, String servicePersonPhoto, String dateRequested) {
        this.rating = rating;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.name = name;
        this.reason = reason;
        this.price = price;
        this.itemName = itemName;
        this.response = response;
        this.itemImage = itemImage;
        this.senderPhoto = senderPhoto;
        this.senderName = senderName;
        this.servicePersonName = servicePersonName;
        this.servicePersonPhoto = servicePersonPhoto;
        this.dateRequested = dateRequested;
    }

    @BindingAdapter("imageItemUrl")
    public static void loadImages(AppCompatImageView imageView, String imageUrl) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {


                        return false;
                    }
                })
                .into(imageView);
    }

    @Bindable
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Bindable
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Bindable
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Bindable
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Bindable
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Bindable
    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getDistanceBetween() {
        return distanceBetween;
    }

    public void setDistanceBetween(String distanceBetween) {
        this.distanceBetween = distanceBetween;
    }

    public String getSenderPhoto() {
        return senderPhoto;
    }

    public void setSenderPhoto(String senderPhoto) {
        this.senderPhoto = senderPhoto;
    }

    @Bindable
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Bindable
    public String getServicePersonName() {
        return servicePersonName;
    }

    public void setServicePersonName(String servicePersonName) {
        this.servicePersonName = servicePersonName;
    }

    public String getServicePersonPhoto() {
        return servicePersonPhoto;
    }

    public void setServicePersonPhoto(String servicePersonPhoto) {
        this.servicePersonPhoto = servicePersonPhoto;
    }

    @Bindable
    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    @Bindable
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Bindable
    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

}
