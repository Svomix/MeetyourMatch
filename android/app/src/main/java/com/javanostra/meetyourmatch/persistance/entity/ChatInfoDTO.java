package com.javanostra.meetyourmatch.persistance.entity;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Objects;

public class ChatInfoDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("avatarPath")
    private String avatarPath;

    @SerializedName("isGroup")
    private Boolean isGroup;

    @SerializedName("lastMessage")
    private String lastMessage;

    @SerializedName("lastMessageTime")
    private Timestamp lastMessageTime;

    public ChatInfoDTO() {
    }

    public ChatInfoDTO(Long id, String username, String avatarPath, Boolean isGroup, String lastMessage, Timestamp lastMessageTime) {
        this.id = id;
        this.username = username;
        this.avatarPath = avatarPath;
        this.isGroup = isGroup;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public Boolean getIsGroup() { return isGroup; } // или isIsGroup() если так принято
    public void setIsGroup(Boolean group) { isGroup = group; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Timestamp getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Timestamp lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatInfoDTO that = (ChatInfoDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(avatarPath, that.avatarPath) &&
                Objects.equals(isGroup, that.isGroup) &&
                Objects.equals(lastMessage, that.lastMessage) &&
                Objects.equals(lastMessageTime, that.lastMessageTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, avatarPath, isGroup, lastMessage, lastMessageTime);
    }

    @Override
    public String toString() {
        return "ChatInfoDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", isGroup=" + isGroup +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }
}