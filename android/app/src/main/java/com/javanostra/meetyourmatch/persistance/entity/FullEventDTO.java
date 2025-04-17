package com.javanostra.meetyourmatch.persistance.entity;

import java.util.List;

public class FullEventDTO extends Event {
    private List<CommentDTO> comments;

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
