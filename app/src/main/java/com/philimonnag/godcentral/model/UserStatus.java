package com.philimonnag.godcentral.model;

import java.util.ArrayList;

public class UserStatus {
   private String uName,url;
   private long lastUpdated;
   private ArrayList<Status>statuses;

    public UserStatus(String uName, String url, long lastUpdated, ArrayList<Status> statuses) {
        this.uName = uName;
        this.url = url;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public UserStatus() {

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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
