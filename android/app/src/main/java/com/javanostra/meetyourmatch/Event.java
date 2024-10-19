package com.javanostra.meetyourmatch;

public class Event {
    private String title;
    private String description;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }

    private int date, month, year;

    public Event(String title, String description) {
        this(title, description, 0, 0 ,0);
    }

    public Event(String title, String description, int date, int month, int year) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.month = month - 1;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}