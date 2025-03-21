package com.javanostra.meetyourmatch.persistance.entity;

public class UserEvent {
    private Long user_id;

    private Long event_id;

    private Boolean isLiked = false;

    private Boolean isDisliked = false;

    private Boolean inCalendar = false;

    public UserEvent(Long user_id, Long event_id, Boolean isLiked, Boolean isDisliked, Boolean inCalendar) {
        this.user_id = user_id;
        this.event_id = event_id;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
        this.inCalendar = inCalendar;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getEvent_id() {
        return event_id;
    }

    public void setEvent_id(Long event_id) {
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

