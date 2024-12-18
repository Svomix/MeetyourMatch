package com.javanostra.spring.core.exceptions;

public class UserDoesNotExistException extends BaseCoreException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
