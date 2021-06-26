package com.philimonnag.godcentral.Model;

public class Post {
    String userId,postId,timeStamp,privacy,postImg,postTitle;
    int clr;

    public Post() {
    }

    public Post(String userId, String postId, String timeStamp, String privacy, String postImg, String postTitle, int clr) {
        this.userId = userId;
        this.postId = postId;
        this.timeStamp = timeStamp;
        this.privacy = privacy;
        this.postImg = postImg;
        this.postTitle = postTitle;
        this.clr = clr;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public int getClr() {
        return clr;
    }

    public void setClr(int clr) {
        this.clr = clr;
    }
}
