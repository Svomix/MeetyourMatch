package com.javanostra.spring.core.exceptions;

public class UserIsNotSameException extends BaseCoreException {
    public UserIsNotSameException() {
        super("Переданный пользователь и текущий не совпадают");
    }
}
