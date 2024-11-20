package com.javanostra.meetyourmatch.persistance.entity;

public class ChatMember {
    private Chat chat;

    private User user;

    private UserRole role;

    public ChatMember(Chat chat, User user, UserRole role) {
        this.chat = chat;
        this.user = user;
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}

