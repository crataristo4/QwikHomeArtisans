package com.artisans.qwikhomeservices.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.database.Exclude;

public class StylesItemModel extends BaseObservable {

    public String price;
    public String styleItem;
    public String itemImage;
    public double rating;
    public String userPhoto;
    public String userName;
    public Object timeStamp;
    public String accountType;


    public StylesItemModel() {
    }

    public StylesItemModel(String price, String styleItem, String itemImage, String userImage, String userName,
                           Object timestamp, String accountType) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
        this.userPhoto = userImage;
        this.userName = userName;
        this.accountType = accountType;
        this.timeStamp = timestamp;

    }

    public StylesItemModel(String price, String styleItem, String itemImage, double rating) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
        this.rating = rating;
    }

    public StylesItemModel(String price, String styleItem, String itemImage,
                           String userPhoto, String userName, Object timeStamp) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.timeStamp = timeStamp;
    }


    @Bindable
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Bindable
    public String getStyleItem() {
        return styleItem;
    }

    public void setStyleItem(String styleItem) {
        this.styleItem = styleItem;
    }

    @Bindable
    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }


    @Bindable
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Exclude
    public long getTimeStamp() {
        return (long) timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
