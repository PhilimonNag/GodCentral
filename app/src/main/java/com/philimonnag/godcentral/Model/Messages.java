package com.philimonnag.godcentral.Model;

public class Messages {
    String msgId,userId,timeStamp,TextMsg;

    public Messages() {
    }

    public Messages(String msgId, String userId, String timeStamp, String textMsg) {
        this.msgId = msgId;
        this.userId = userId;
        this.timeStamp = timeStamp;
        TextMsg = textMsg;
    }

//    public Messages(String userId, String timeStamp, String textMsg) {
//        this.userId = userId;
//        this.timeStamp = timeStamp;
//        TextMsg = textMsg;
//    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTextMsg() {
        return TextMsg;
    }

    public void setTextMsg(String textMsg) {
        TextMsg = textMsg;
    }
}
