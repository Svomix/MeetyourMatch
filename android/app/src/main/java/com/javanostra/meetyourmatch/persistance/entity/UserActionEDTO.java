package com.javanostra.meetyourmatch.persistance.entity; // Or your actual package

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class UserActionEDTO implements Parcelable {
    private Boolean isLiked;
    private Boolean isDisliked;
    private Boolean inCalendar;

    public UserActionEDTO(Boolean isLiked, Boolean isDisliked, Boolean inCalendar) {
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
        this.inCalendar = inCalendar;
    }

    public UserActionEDTO() {
        this.isLiked = false;
        this.isDisliked = false;
        this.inCalendar = false;
    }

    protected UserActionEDTO(Parcel in) {
        byte tmpIsLiked = in.readByte();
        isLiked = tmpIsLiked == -1 ? null : tmpIsLiked == 1;
        byte tmpIsDisliked = in.readByte();
        isDisliked = tmpIsDisliked == -1 ? null : tmpIsDisliked == 1;
        byte tmpInCalendar = in.readByte();
        inCalendar = tmpInCalendar == -1 ? null : tmpInCalendar == 1;
    }

    public static final Creator<UserActionEDTO> CREATOR = new Creator<UserActionEDTO>() {
        @Override
        public UserActionEDTO createFromParcel(Parcel in) {
            return new UserActionEDTO(in);
        }

        @Override
        public UserActionEDTO[] newArray(int size) {
            return new UserActionEDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (isLiked == null ? -1 : (isLiked ? 1 : 0)));
        dest.writeByte((byte) (isDisliked == null ? -1 : (isDisliked ? 1 : 0)));
        dest.writeByte((byte) (inCalendar == null ? -1 : (inCalendar ? 1 : 0)));
    }

    public Boolean getLiked() { return isLiked; }
    public void setLiked(Boolean liked) { isLiked = liked; }
    public Boolean getDisliked() { return isDisliked; }
    public void setDisliked(Boolean disliked) { isDisliked = disliked; }
    public Boolean getInCalendar() { return inCalendar; }
    public void setInCalendar(Boolean inCalendar) { this.inCalendar = inCalendar; }
}