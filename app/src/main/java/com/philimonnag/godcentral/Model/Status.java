package com.philimonnag.godcentral.Model;

public class Status {
    private String imageUrl,privacy,caption;
    private long timeStamp;

    public Status() {

    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Status(String imageUrl, String privacy, String caption, long timeStamp) {
        this.imageUrl = imageUrl;
        this.privacy = privacy;
        this.caption = caption;
        this.timeStamp = timeStamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}