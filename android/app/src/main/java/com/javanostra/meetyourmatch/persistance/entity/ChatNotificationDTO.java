package com.javanostra.meetyourmatch.persistance.entity;

import com.google.gson.annotations.SerializedName;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;
import java.util.Date;

public class ChatNotificationDTO {

    @SerializedName("id")
    private String id;
    @SerializedName("senderId")
    private String senderId;
    @SerializedName("recipientId")
    private String recipientId;
    @SerializedName("content")
    private String content;
    @SerializedName("timestamp")
    private Date timestamp;

    public ChatNotificationDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public ChatMessage toChatMessage() {
        ChatMessage msg = new ChatMessage(this.senderId, this.recipientId, this.content);
        msg.setId(this.id);
        msg.setTimestamp(this.timestamp != null ? this.timestamp : new Date()); // Handle null timestamp
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