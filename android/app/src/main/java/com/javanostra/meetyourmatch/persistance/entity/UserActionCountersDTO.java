package com.javanostra.meetyourmatch.persistance.entity; // Or your actual package

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class UserActionCountersDTO implements Parcelable {
    private Long likedCounter;
    private Long dislikedCounter;
    private Long calendarCounter;

    public UserActionCountersDTO(Long likedCounter, Long dislikedCounter, Long calendarCounter) {
        this.likedCounter = likedCounter;
        this.dislikedCounter = dislikedCounter;
        this.calendarCounter = calendarCounter;
    }

    public UserActionCountersDTO() {}

    protected UserActionCountersDTO(Parcel in) {
        if (in.readByte() == 0) { likedCounter = null; } else { likedCounter = in.readLong(); }
        if (in.readByte() == 0) { dislikedCounter = null; } else { dislikedCounter = in.readLong(); }
        if (in.readByte() == 0) { calendarCounter = null; } else { calendarCounter = in.readLong(); }
    }

    public static final Creator<UserActionCountersDTO> CREATOR = new Creator<UserActionCountersDTO>() {
        @Override
        public UserActionCountersDTO createFromParcel(Parcel in) {
            return new UserActionCountersDTO(in);
        }

        @Override
        public UserActionCountersDTO[] newArray(int size) {
            return new UserActionCountersDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (likedCounter == null) { dest.writeByte((byte) 0); } else { dest.writeByte((byte) 1); dest.writeLong(likedCounter); }
        if (dislikedCounter == null) { dest.writeByte((byte) 0); } else { dest.writeByte((byte) 1); dest.writeLong(dislikedCounter); }
        if (calendarCounter == null) { dest.writeByte((byte) 0); } else { dest.writeByte((byte) 1); dest.writeLong(calendarCounter); }
    }

    public Long getLikedCounter() { return likedCounter; }
    public void setLikedCounter(Long likedCounter) { this.likedCounter = likedCounter; }
    public Long getDislikedCounter() { return dislikedCounter; }
    public void setDislikedCounter(Long dislikedCounter) { this.dislikedCounter = dislikedCounter; }
    public Long getCalendarCounter() { return calendarCounter; }
    public void setCalendarCounter(Long calendarCounter) { this.calendarCounter = calendarCounter; }
}