package com.javanostra.meetyourmatch.persistance.entity;

public class UserActionDTO {
    private Event event;

    private Boolean isLiked = false;
    private Boolean isDisliked = false;
    private Boolean inCalendar = false;

    public UserActionDTO(Event event, Boolean isLiked, Boolean isDisliked, Boolean inCalendar) {
        this.event = event;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
        this.inCalendar = inCalendar;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
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
