package com.javanostra.spring.core.exceptions;

public class UserIsSameException extends BaseCoreException {
    public UserIsSameException() {
        super("Нельзя сделать это действие с собой");
    }
}
