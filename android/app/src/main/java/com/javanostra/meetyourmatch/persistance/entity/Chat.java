package com.javanostra.meetyourmatch.persistance.entity;

public class Chat {
    private Long id;

    private String name;

    private Boolean isGroup;

    public Chat(Long id, String name, Boolean isGroup) {
        this.id = id;
        this.name = name;
        this.isGroup = isGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }
}

