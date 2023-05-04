package com.example.memorylane.Classes;

import java.util.UUID;

public class User {

    private String username;
    private String signatureUrl;
    private int age;
    private String imageUrl;
    private String UserID;

    public User() {

    }

    public User(String username, String signatureUrl, int age, String imageUrl) {
        this.UserID = UUID.randomUUID().toString();
        this.username = username;
        this.signatureUrl = signatureUrl;
        this.age = age;
        this.imageUrl = imageUrl;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserID() {
        return UserID;
    }

    public int getAge() {
        return age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }
}
