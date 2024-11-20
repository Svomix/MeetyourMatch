package com.javanostra.meetyourmatch.persistance.entity;

import java.io.Serializable;

public class ChatMemberId implements Serializable {
    private Long chat;
    private Long user;

    public ChatMemberId(Long user, Long chat) {
        this.user = user;
        this.chat = chat;
    }

    public Long getChat() {
        return chat;
    }

    public void setChat(Long chat) {
        this.chat = chat;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
