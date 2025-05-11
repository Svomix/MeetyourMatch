package com.javanostra.meetyourmatch.persistance.entity;

import java.sql.Timestamp;

public class GroupChatMessageNotification {
    private Long id;
    private Long groupChatId;
    private Long senderId;
    private String senderUsername;
    private String senderAvatarPath;
    private String content;
    private Timestamp timestamp;

    public GroupChatMessageNotification() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGroupChatId() { return groupChatId; }
    public void setGroupChatId(Long groupChatId) { this.groupChatId = groupChatId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    public String getSenderAvatarPath() { return senderAvatarPath; }
    public void setSenderAvatarPath(String senderAvatarPath) { this.senderAvatarPath = senderAvatarPath; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public GroupChatMessage toGroupChatMessage() {
        GroupChatMessage.GroupChatInfo chatInfo = new GroupChatMessage.GroupChatInfo();
        chatInfo.setId(this.groupChatId);

        GroupChatMessage.SenderInfo senderInfo = new GroupChatMessage.SenderInfo();
        senderInfo.setId(this.senderId);
        senderInfo.setUsername(this.senderUsername);
        senderInfo.setAvatarPath(this.senderAvatarPath);

        return new GroupChatMessage(
                this.id,
                chatInfo,
                senderInfo,
                this.content,
                this.timestamp
        );
    }
}