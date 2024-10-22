package com.javanostra.meetyourmatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    private static long newElementId = 0;

    private final long id;
    private final String title;
    private final String description;
    private List<String> tags;
    private int date, month, year;

    public Event(String title, String description, int date, int month, int year) {
        this(title, description, new ArrayList<>(), date, month, year);
    }

    public Event(String title, String description, List<String> tags, int date, int month, int year) {
        this.id = newElementId;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.date = date;
        this.month = month - 1;
        this.year = year;
        ++newElementId;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getYear() {
        return year;
    }

    public int getMonthAsIndex() {
        return month;
    }

    public int getDate() {
        return date;
    }
}