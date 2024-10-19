package com.javanostra.meetyourmatch;

import java.util.ArrayList;
import java.util.List;

public class Day {
    private int date;
    private List<Event> events;

    public Day(int date) {
        this(date, new ArrayList<>());
    }

    public Day(int date, ArrayList<Event> events) {
        this.date = date;
        this.events = events;
    }

    public int getDate() {
        return date;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public int getEventCount() {
        return events.size();
    }

    public List<Event> getEvents() {
        return events;
    }
}