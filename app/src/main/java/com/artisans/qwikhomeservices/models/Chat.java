package com.artisans.qwikhomeservices.models;

public class Chat {
    private String userId;
    private String fullName;
    private String image;
    private Long timeStamp;
    private String title;
    private String chats;

    public Chat() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChats() {
        return chats;
    }

    public void setChats(String chats) {
        this.chats = chats;
    }
}
