package com.javanostra.spring.core.exceptions;

public class UserAlreadyExistsException extends BaseCoreException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
