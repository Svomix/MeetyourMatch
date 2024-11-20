package com.javanostra.meetyourmatch.persistance.entity;

import java.util.Set;

public class User {
    private Long id;

    private Set<UserAuthority> authorities;

    private String username;

    private String email;

    private String password;

    private City city;

    private String gender;

    private String description;

    private String avatarPath;

    public User(Long id, Set<UserAuthority> authorities, String username, String email, String password, City city, String gender, String description, String avatarPath) {
        this.id = id;
        this.authorities = authorities;
        this.username = username;
        this.email = email;
        this.password = password;
        this.city = city;
        this.gender = gender;
        this.description = description;
        this.avatarPath = avatarPath;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<UserAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<UserAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}

