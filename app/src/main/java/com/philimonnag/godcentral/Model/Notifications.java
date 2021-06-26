package com.philimonnag.godcentral.Model;

public class Notifications {
    String userId,PostId,Text;

    public Notifications() {
    }

    public Notifications(String userId, String postId, String text) {
        this.userId = userId;
        PostId = postId;
        Text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
