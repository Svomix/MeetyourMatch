package com.javanostra.meetyourmatch.persistance.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Date;

public class ChatNotificationDTO {

    @SerializedName("id")
    private Long id;
    @SerializedName("senderId")
    private String senderId;
    @SerializedName("recipientId")
    private String recipientId;
    @SerializedName("content")
    private String content;
    @SerializedName("timestamp")
    private Timestamp timestamp;

    public ChatNotificationDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public ChatMessage toChatMessage() {
        ChatMessage msg = new ChatMessage(this.senderId, this.recipientId, this.content, null);
        msg.setId(this.id);
        msg.setTimestamp(this.timestamp != null ? this.timestamp : new Timestamp(new Date().getTime()));
        //msg.setTimestamp(this.timestamp);
        return msg;
    }

    @Override
    public String toString() {
        return "ChatNotificationDTO{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}