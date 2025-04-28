package com.javanostra.meetyourmatch.persistance.entity;
import com.google.gson.annotations.SerializedName;

public class UserRelationDTO extends UserProfileDTO {
    @SerializedName("myRelation")
    private Relation myRelation;
    @SerializedName("userRelation")
    private Relation userRelation;

    public UserRelationDTO(String username, String email) {
        super(username, email);
    }

    public Relation getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(Relation userRelation) {
        this.userRelation = userRelation;
    }

    public Relation getMyRelation() {
        return myRelation;
    }

    public void setMyRelation(Relation myRelation) {
        this.myRelation = myRelation;
    }
}
