package com.javanostra.meetyourmatch.persistance.entity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

public class ChatUserDTO {

    private ChatInfoDTO userProfile;
    private RelationStatus relationStatus;
    private Timestamp lastSeenAt;

    public enum RelationStatus {
        NONE, FRIEND, BLOCKED
    }

    public ChatUserDTO(ChatInfoDTO userProfile, RelationStatus relationStatus) {
        this.userProfile = userProfile;
        this.relationStatus = relationStatus;
        this.lastSeenAt = null;
    }

    public ChatInfoDTO getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(ChatInfoDTO userProfile) {
        this.userProfile = userProfile;
    }

    public RelationStatus getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(RelationStatus relationStatus) {
        this.relationStatus = relationStatus;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUserDTO that = (ChatUserDTO) o;
        return Objects.equals(userProfile, that.userProfile) &&
                relationStatus == that.relationStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfile, relationStatus);
    }
}