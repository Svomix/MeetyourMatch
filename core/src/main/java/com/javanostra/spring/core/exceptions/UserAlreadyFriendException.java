package com.javanostra.spring.core.exceptions;

public class UserAlreadyFriendException extends BaseCoreException {
    public UserAlreadyFriendException() {
        super("Вы уже добавили этого пользователя в друзья");
    }
}
