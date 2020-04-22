package com.artisans.qwikhomeservices.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServicePerson extends BaseObservable {

    public float rating;
    public String servicePersonId;
    public String firstName, lastName, fullName, name;
    public String email;
    public String reason;
    public int price;
    public String styleItem;
    public double latitude;
    public double longitude;
    public String occupation;
    public String response;
    public String location;
    public String date;
    public String about;
    public String mobileNumber;
    public String accountType;
    public String image;
    public String distanceBetween;
    public String senderPhoto;
    public String senderName;
    public String servicePersonName;
    public String servicePersonPhoto;
    public String dateRequested;
    public long timeStamp;
    public String joinedDate;
    private String userId;


    public ServicePerson() {
    }

    public ServicePerson(String servicePersonId, String firstName, String lastName, String fullName,
                         String about, String mobileNumber, String accountType, String image, String joinedDate) {
        this.servicePersonId = servicePersonId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.about = about;
        this.mobileNumber = mobileNumber;
        this.accountType = accountType;
        this.image = image;
        this.joinedDate = joinedDate;
    }

    public ServicePerson(String servicePersonId, String name, String email, String accountType) {
        this.servicePersonId = servicePersonId;
        this.name = name;
        this.email = email;
        this.accountType = accountType;
    }


    public ServicePerson(int price, String styleItem, String image) {
        this.price = price;
        this.styleItem = styleItem;
        this.image = image;
    }

    @BindingAdapter("imageUrl")
    public static void loadImages(CircleImageView imageView, String imageUrl) {
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

    public String getServicePersonId() {
        return servicePersonId;
    }

    public void setServicePersonId(String servicePersonId) {
        this.servicePersonId = servicePersonId;
    }

    @Bindable
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Bindable
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Bindable
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Bindable
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Bindable
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Bindable
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getServicePersonPhoto() {
        return servicePersonPhoto;
    }

    public void setServicePersonPhoto(String servicePersonPhoto) {
        this.servicePersonPhoto = servicePersonPhoto;
    }

    @Bindable
    public String getServicePersonName() {
        return servicePersonName;
    }

    public void setServicePersonName(String servicePersonName) {
        this.servicePersonName = servicePersonName;
    }

    @Bindable
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Bindable
    public String getStyleItem() {
        return styleItem;
    }

    public void setStyleItem(String styleItem) {
        this.styleItem = styleItem;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    @Bindable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Bindable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Bindable
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Bindable
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
