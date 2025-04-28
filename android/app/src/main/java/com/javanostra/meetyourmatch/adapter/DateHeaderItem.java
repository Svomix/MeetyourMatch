package com.javanostra.meetyourmatch.adapter;

import java.util.Objects;

public class DateHeaderItem {
    private final long timestamp; 

    public DateHeaderItem(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateHeaderItem that = (DateHeaderItem) o;
        return timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }
}