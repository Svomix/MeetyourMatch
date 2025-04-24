package com.javanostra.spring.core.exceptions;

public class NoSuchTagException extends BaseCoreException {
    public NoSuchTagException() {
        super("Такого тега не существует");
    }
}
