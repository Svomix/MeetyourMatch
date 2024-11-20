package com.javanostra.meetyourmatch.persistance.entity;

public class EventAttributeValue {
    private Long id;

    private Event event;

    private Attribute attribute;

    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EventAttributeValue(Long id, Event event, Attribute attribute, String value) {
        this.id = id;
        this.event = event;
        this.attribute = attribute;
        this.value = value;
    }
}
