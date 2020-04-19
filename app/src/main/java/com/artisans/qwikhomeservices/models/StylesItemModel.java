package com.artisans.qwikhomeservices.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.database.Exclude;

public class StylesItemModel extends BaseObservable {

    public int price;
    public String styleItem;
    public String itemImage;
    public double rating;
    public String userPhoto;
    public String userName;
    public Object timeStamp;
    public String accountType;


    public StylesItemModel() {
    }

    public StylesItemModel(int price, String styleItem, String itemImage) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
    }

    public StylesItemModel(int price, String styleItem, String itemImage, double rating) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
        this.rating = rating;
    }

    public StylesItemModel(int price, String styleItem, String itemImage, String userPhoto, String userName, String timeStamp) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.timeStamp = timeStamp;
    }



    public StylesItemModel(int price, String styleItem, String itemImage, String userPhoto, String userName, Object timeStamp, String accountType) {
        this.price = price;
        this.styleItem = styleItem;
        this.itemImage = itemImage;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.accountType = accountType;
    }

    @Bindable
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

    @Bindable
    @Exclude
    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }


}
