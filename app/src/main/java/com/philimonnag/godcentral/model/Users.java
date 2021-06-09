package com.philimonnag.godcentral.model;

public class Users {
    String uName,url,gender,email;

    public Users() {
    }

    public Users(String uName, String url, String gender, String email) {
        this.uName = uName;
        this.url = url;
        this.gender = gender;
        this.email = email;
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
}
