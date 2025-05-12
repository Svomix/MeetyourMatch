package com.javanostra.meetyourmatch.persistance.entity;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private Timestamp lastSeenAt;

    public UserProfileDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthorities(Set<UserAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Timestamp getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Timestamp lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public boolean isOnline() {
        long milliseconds = Timestamp.from(Instant.now()).getTime() - lastSeenAt.getTime();
        return milliseconds < 35000;
    }
}
