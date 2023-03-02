package com.example.memorylane.Database;

public class User {

    private String username;
    private String signatureUrl;
    private int age;
    private String imageUrl;

    public User() {

    }

    public User(String username, String signatureUrl, int age, String imageUrl) {
        this.username = username;
        this.signatureUrl = signatureUrl;
        this.age = age;
        this.imageUrl = imageUrl;
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
