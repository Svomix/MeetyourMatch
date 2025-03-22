package com.javanostra.meetyourmatch.persistance.entity;

public class UserEventDTO {
    private Long user;
    private Long event;

    private Boolean isLiked = false;
    private Boolean isDisliked = false;
    private Boolean inCalendar = false;

    public UserEventDTO(Long user, Long event, Boolean isLiked, Boolean isDisliked, Boolean inCalendar) {
        this.user = user;
        this.event = event;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
        this.inCalendar = inCalendar;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
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
