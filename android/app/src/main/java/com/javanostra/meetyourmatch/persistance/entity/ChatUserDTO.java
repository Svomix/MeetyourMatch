package com.javanostra.meetyourmatch.persistance.entity;

import java.util.Objects;

public class ChatUserDTO {

    private UserProfileDTO userProfile;
    private RelationStatus relationStatus;

    public enum RelationStatus {
        FRIEND, BLOCKED, NONE
    }

    public ChatUserDTO(UserProfileDTO userProfile, RelationStatus relationStatus) {
        this.userProfile = userProfile;
        this.relationStatus = relationStatus;
    }

    public ChatUserDTO() {}

    public UserProfileDTO getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfileDTO userProfile) { this.userProfile = userProfile; }
    public RelationStatus getRelationStatus() { return relationStatus; }
    public void setRelationStatus(RelationStatus relationStatus) { this.relationStatus = relationStatus; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUserDTO that = (ChatUserDTO) o;
        return Objects.equals(userProfile != null ? userProfile.getId() : null,
                that.userProfile != null ? that.userProfile.getId() : null) &&
                relationStatus == that.relationStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfile != null ? userProfile.getId() : null, relationStatus);
    }
}