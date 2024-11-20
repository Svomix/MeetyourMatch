package com.javanostra.meetyourmatch.persistance.entity;

import java.io.Serializable;

public class UserEventId implements Serializable {
    private Long user;
    private Long event;

    public UserEventId(Long user, Long event) {
        this.user = user;
        this.event = event;
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
}
