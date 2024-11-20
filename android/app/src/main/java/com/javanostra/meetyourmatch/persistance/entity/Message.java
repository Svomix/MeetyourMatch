package com.javanostra.meetyourmatch.persistance.entity;

public class Message {
    private Long id;

    private String content;

    private Integer chat_id;

    private Integer user_id;

    //private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private String timestamp;

    public Message(String content, Integer chat_id, Integer user_id, String timestamp) {
        this.content = content;
        this.chat_id = chat_id;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getChat_id() {
        return chat_id;
    }

    public void setChat_id(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

