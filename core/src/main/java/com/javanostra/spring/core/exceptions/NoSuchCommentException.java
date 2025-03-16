package com.javanostra.spring.core.exceptions;

public class NoSuchCommentException extends BaseCoreException {
    public NoSuchCommentException() {
        super("Такого комментария не существует");
    }
}
