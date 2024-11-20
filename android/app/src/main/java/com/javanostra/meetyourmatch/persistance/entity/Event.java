package com.javanostra.meetyourmatch.persistance.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Event implements Serializable {
    private Long id;

    private String title;

    private String description;

    private Double price;

    private Timestamp date;

    private Location location;

    private String coverImgUrl;

    private String sourceUrl;

    public Event(Long id, String title, String description, Double price, Timestamp date, Location location, String coverImgUrl, String sourceUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.location = location;
        this.coverImgUrl = coverImgUrl;
        this.sourceUrl = sourceUrl;
    }

    public Event(String title, String description, Double price, Timestamp date, Location location, String coverImgUrl, String sourceUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.location = location;
        this.coverImgUrl = coverImgUrl;
        this.sourceUrl = sourceUrl;
    }

    public Event(String title, String description, Double price, Timestamp date, Location location) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.location = location;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}

