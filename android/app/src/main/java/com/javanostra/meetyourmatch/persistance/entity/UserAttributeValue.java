package com.javanostra.meetyourmatch.persistance.entity;

public class UserAttributeValue {
    private Long id;

    private User user;

    private Attribute attribute;

    private String value;

    public UserAttributeValue(Long id, User user, Attribute attribute, String value) {
        this.id = id;
        this.user = user;
        this.attribute = attribute;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

