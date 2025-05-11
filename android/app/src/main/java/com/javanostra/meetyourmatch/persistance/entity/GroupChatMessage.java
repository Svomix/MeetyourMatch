package com.javanostra.meetyourmatch.persistance.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Objects;

public class GroupChatMessage {

    @SerializedName("id")
    private Long id;

    @SerializedName("chat")
    private GroupChatInfo chatInfo;

    @SerializedName("user")
    private SenderInfo senderInfo;

    @SerializedName("content")
    private String content;

    @SerializedName("timestamp")
    private Timestamp timestamp;

    public GroupChatMessage(Long id, GroupChatInfo chatInfo, SenderInfo senderInfo, String content, Timestamp timestamp) {
        this.id = id;
        this.chatInfo = chatInfo;
        this.senderInfo = senderInfo;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static class GroupChatInfo {
        @SerializedName("id")
        private Long id;
        @SerializedName("name")
        private String name;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class SenderInfo {
        @SerializedName("id")
        private Long id;
        @SerializedName("username")
        private String username;
        @SerializedName("avatarPath")
        private String avatarPath;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatarPath() {
            return avatarPath;
        }

        public void setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
        }
    }

    public GroupChatMessage() {}

    public Long getId() { return id; }
    public GroupChatInfo getChatInfo() { return chatInfo; }
    public SenderInfo getSenderInfo() { return senderInfo; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }

    public Long getGroupChatId() {
        return (chatInfo != null) ? chatInfo.getId() : null;
    }
    public Long getSenderId() {
        return (senderInfo != null) ? senderInfo.getId() : null;
    }
    public String getSenderUsername() {
        return (senderInfo != null) ? senderInfo.getUsername() : null;
    }
    public String getSenderAvatarPath() {
        return (senderInfo != null) ? senderInfo.getAvatarPath() : null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChatMessage that = (GroupChatMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}