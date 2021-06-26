package com.philimonnag.godcentral.Model;

public class User {
    String uName,url,gender,email,uid,bio;

    public User() {
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User(String uName, String url, String gender, String email, String uid, String bio) {
        this.uName = uName;
        this.url = url;
        this.gender = gender;
        this.email = email;
        this.uid = uid;
        this.bio = bio;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
