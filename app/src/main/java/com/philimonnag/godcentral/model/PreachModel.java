package com.philimonnag.godcentral.model;


public class PreachModel {

    // variables for storing our image and name.
    private String preach;
    private String url;
    private String uName;


    public PreachModel(String preach, String url, String uName) {
        this.preach = preach;
        this.url = url;
        this.uName = uName;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public PreachModel() {
        // empty constructor required for firebase.
    }


    // getter and setter methods
    public String getpreach() {
        return preach;
    }

    public void setpreach(String preach) {
        this.preach = preach;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }
}