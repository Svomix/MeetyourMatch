package com.javanostra.meetyourmatch.persistance.entity;

import java.util.Set;

public class UserAuthority {
    private Integer id;

    private String authority;

    public UserAuthority(Integer id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

