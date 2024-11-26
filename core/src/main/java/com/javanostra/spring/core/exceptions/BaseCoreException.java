package com.javanostra.spring.core.exceptions;

import jakarta.servlet.ServletException;

public abstract class BaseCoreException extends ServletException {
    public BaseCoreException(String message){
        super(message);
    }
}
