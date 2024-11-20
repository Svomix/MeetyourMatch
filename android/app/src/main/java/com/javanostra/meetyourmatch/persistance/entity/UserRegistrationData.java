package com.javanostra.meetyourmatch.persistance.entity;

import java.io.Serializable;

public class UserRegistrationData implements Serializable {
    private String username;
    private String email;
    private String password;
    private int cityId;

    public UserRegistrationData(String username, String email, String password, int city) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.cityId = city;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getCityId() {
        return cityId;
    }
}