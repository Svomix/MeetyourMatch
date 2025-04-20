package com.javanostra.spring.core.exceptions;

public class InvalidVKAccessTokenException extends BaseCoreException {
    public InvalidVKAccessTokenException() {
        super("Неправильный VK access token");
    }
}
