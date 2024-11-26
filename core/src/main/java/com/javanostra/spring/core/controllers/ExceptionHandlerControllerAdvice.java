package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.ExceptionDTO;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler(BaseCoreException.class)
    public ResponseEntity<ExceptionDTO> coreException(BaseCoreException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionDTO(exception.getMessage(), exception.getClass().getSimpleName(), HttpStatus.BAD_REQUEST.value())
        );
    }
}
