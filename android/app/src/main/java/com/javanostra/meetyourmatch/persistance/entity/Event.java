package com.javanostra.meetyourmatch.persistance.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.sql.Timestamp;

public class Event implements Parcelable {
    private Long id;
    private String title;
    private String description;
    private String price;
    private Timestamp date;
    private Location location;
    private String coverImgUrl;
    private String sourceUrl;
    private UserActionEDTO userAction;
    private UserActionCountersDTO userActionCounters;

    public Event() {}

    public Event(Long id, String title, String description, String price, Timestamp date, Location location, String coverImgUrl, String sourceUrl, UserActionEDTO userAction, UserActionCountersDTO userActionCounters) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.location = location;
        this.coverImgUrl = coverImgUrl;
        this.sourceUrl = sourceUrl;
        this.userAction = userAction;
        this.userActionCounters = userActionCounters;
    }

    protected Event(Parcel in) {
        if (in.readByte() == 0) { id = null; } else { id = in.readLong(); }
        title = in.readString();
        description = in.readString();
        price = in.readString();
        long tmpDate = in.readLong();
        date = (tmpDate == -1) ? null : new Timestamp(tmpDate);

        location = in.readParcelable(Location.class.getClassLoader());

        coverImgUrl = in.readString();
        sourceUrl = in.readString();

        if (in.readByte() == 1) {
            userAction = in.readParcelable(UserActionEDTO.class.getClassLoader());
        } else {
            userAction = null;
        }

        if (in.readByte() == 1) {
            userActionCounters = in.readParcelable(UserActionCountersDTO.class.getClassLoader());
        } else {
            userActionCounters = null;
        }
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) { dest.writeByte((byte) 0); } else { dest.writeByte((byte) 1); dest.writeLong(id); }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeLong(date == null ? -1 : date.getTime());

        dest.writeParcelable(location, flags);

        dest.writeString(coverImgUrl);
        dest.writeString(sourceUrl);

        if (userAction == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeParcelable(userAction, flags);
        }

        if (userActionCounters == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeParcelable(userActionCounters, flags);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public String getCoverImgUrl() { return coverImgUrl; }
    public void setCoverImgUrl(String coverImgUrl) { this.coverImgUrl = coverImgUrl; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public UserActionEDTO getUserAction() { return userAction; }
    public void setUserAction(UserActionEDTO userAction) { this.userAction = userAction; }
    public UserActionCountersDTO getUserActionCounters() { return userActionCounters; }
    public void setUserActionCounters(UserActionCountersDTO userActionCounters) { this.userActionCounters = userActionCounters; }
}