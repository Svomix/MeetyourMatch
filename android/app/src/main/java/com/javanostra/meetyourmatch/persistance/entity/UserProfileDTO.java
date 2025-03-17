package com.javanostra.meetyourmatch.persistance.entity;

import java.io.Serializable;
import java.util.Set;

public class UserProfileDTO {
    private Long id;
    private Set<UserAuthority> authorities;
    private String username;
    private String email;
    private City city;
    private String gender;
    private String description;
    private String avatarPath;

    public Long getId() {
        return id;
    }

    public Set<UserAuthority> getAuthorities() {
        return authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public City getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatarPath() {
        return avatarPath;
    }
}
