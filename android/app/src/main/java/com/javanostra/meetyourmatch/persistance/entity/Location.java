package com.javanostra.meetyourmatch.persistance.entity;

import java.io.Serializable;

public class Location implements Serializable {
    private Long id;
    private String title;
    private String address;
    private Double latitude;
    private Double longitude;

    public Location(Long id, String title, String address, Double latitude, Double longitude) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}