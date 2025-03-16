package com.javanostra.spring.core.exceptions;

public class NoSuchEventException extends BaseCoreException {
    public NoSuchEventException() {
        super("Такого мероприятия не существует");
    }
}
