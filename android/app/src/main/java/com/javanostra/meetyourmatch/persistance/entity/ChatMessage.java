package com.javanostra.meetyourmatch.persistance.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class ChatMessage {

    @SerializedName("id")
    private String id;

    @SerializedName("chatId")
    private String chatId;

    @SerializedName("senderId")
    private String senderId;

    @SerializedName("recipientId")
    private String recipientId;

    @SerializedName("content")
    private String content;

    @SerializedName("timestamp")
    private Date timestamp;

    public ChatMessage(String senderId, String recipientId, String content) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        //this.timestamp = new Date();
    }

    public ChatMessage() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}