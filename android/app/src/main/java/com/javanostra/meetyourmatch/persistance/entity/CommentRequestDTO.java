package com.javanostra.meetyourmatch.persistance.entity;

public class CommentRequestDTO {
    private String content;

    public CommentRequestDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
