package com.javanostra.meetyourmatch.persistance.entity;

public class UserEvent {
    private Integer user_id;

    private Integer event_id;

    private Boolean isLiked = false;

    private Boolean isDisliked = false;

    private Boolean inCalendar = false;

    public UserEvent(Integer user_id, Integer event_id, Boolean isLiked, Boolean isDisliked, Boolean inCalendar) {
        this.user_id = user_id;
        this.event_id = event_id;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
        this.inCalendar = inCalendar;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getEvent_id() {
        return event_id;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Boolean getDisliked() {
        return isDisliked;
    }

    public void setDisliked(Boolean disliked) {
        isDisliked = disliked;
    }

    public Boolean getInCalendar() {
        return inCalendar;
    }

    public void setInCalendar(Boolean inCalendar) {
        this.inCalendar = inCalendar;
    }
}

