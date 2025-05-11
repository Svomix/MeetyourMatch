package com.javanostra.meetyourmatch.persistance.entity; 

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects; 

public class EventUploadDTO {

    private String title;
    private String description;
    private Timestamp date; 
    private String coverFileId; 
    private String price;
    private String sourceUrl;
    private List<Long> tags; 
    private String locationId; 

    
    public EventUploadDTO() {
    }

    
    public EventUploadDTO(String title, String description, Timestamp date, String coverFileId, String price, String sourceUrl, List<Long> tags, String locationId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.coverFileId = coverFileId;
        this.price = price;
        this.sourceUrl = sourceUrl;
        this.tags = tags;
        this.locationId = locationId;
    }

    

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getCoverFileId() {
        return coverFileId;
    }

    public String getPrice() {
        return price;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public List<Long> getTags() {
        return tags;
    }

    public String getLocationId() {
        return locationId;
    }

    

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setCoverFileId(String coverFileId) {
        this.coverFileId = coverFileId;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    

    @Override
    public String toString() {
        return "EventUploadDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", coverFileId='" + coverFileId + '\'' +
                ", price='" + price + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", tags=" + tags +
                ", locationId='" + locationId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventUploadDTO that = (EventUploadDTO) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                Objects.equals(coverFileId, that.coverFileId) &&
                Objects.equals(price, that.price) &&
                Objects.equals(sourceUrl, that.sourceUrl) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, date, coverFileId, price, sourceUrl, tags, locationId);
    }
}