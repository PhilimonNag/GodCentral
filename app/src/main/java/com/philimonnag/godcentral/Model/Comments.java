package com.philimonnag.godcentral.Model;

public class Comments {
    String postId,userId,prayer;

    public Comments(String postId, String userId, String prayer) {
        this.postId = postId;
        this.userId = userId;
        this.prayer = prayer;
    }

    public Comments() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrayer() {
        return prayer;
    }

    public void setPrayer(String prayer) {
        this.prayer = prayer;
    }
}
