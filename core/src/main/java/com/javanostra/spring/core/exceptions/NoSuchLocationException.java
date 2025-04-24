package com.javanostra.spring.core.exceptions;

public class NoSuchLocationException extends BaseCoreException {
    public NoSuchLocationException() {
        super("Такого места не существует");
    }
}
