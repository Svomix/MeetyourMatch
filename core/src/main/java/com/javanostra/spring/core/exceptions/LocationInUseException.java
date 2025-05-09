package com.javanostra.spring.core.exceptions;

public class LocationInUseException extends BaseCoreException {
    public LocationInUseException() {
        super("Это место уже используется");
    }
}
