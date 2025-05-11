package com.javanostra.meetyourmatch.persistance.entity;

public class GroupChatMessageRequest {
    private Long groupChatId;
    private Long senderId;
    private String content;

    public GroupChatMessageRequest(Long groupChatId, Long senderId, String content) {
        this.groupChatId = groupChatId;
        this.senderId = senderId;
        this.content = content;
    }

    public Long getGroupChatId() { return groupChatId; }
    public Long getSenderId() { return senderId; }
    public String getContent() { return content; }
}