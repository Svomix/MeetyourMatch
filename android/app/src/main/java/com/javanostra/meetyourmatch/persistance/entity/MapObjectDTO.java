package com.javanostra.meetyourmatch.persistance.entity; 

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList; 

public class MapObjectDTO implements Parcelable {

    
    private String id; 
    private String title; 
    private String address;
    private Double latitude;
    private Double longitude;
    private List<Event> events; 

    
    public MapObjectDTO() {
    }

    
    public MapObjectDTO(String id, String title, String address, Double latitude, Double longitude, List<Event> events) {
        this.id = id;
        this.title = title; 
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.events = events;
    }

    

    protected MapObjectDTO(Parcel in) {
        id = in.readString();
        title = in.readString();
        address = in.readString();
        
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        
        events = in.createTypedArrayList(Event.CREATOR); 
    }

    public static final Creator<MapObjectDTO> CREATOR = new Creator<MapObjectDTO>() {
        @Override
        public MapObjectDTO createFromParcel(Parcel in) {
            return new MapObjectDTO(in);
        }

        @Override
        public MapObjectDTO[] newArray(int size) {
            return new MapObjectDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(address);
        
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        
        dest.writeTypedList(events);
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapObjectDTO that = (MapObjectDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(address, that.address) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(events, that.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, address, latitude, longitude, events);
    }

    @Override
    public String toString() {
        return "MapObjectDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", eventsCount=" + (events != null ? events.size() : 0) +
                '}';
    }
}