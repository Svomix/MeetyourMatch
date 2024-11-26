package com.javanostra.spring.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ExceptionDTO {
    @NonNull
    String error;
    @NonNull
    String exception;
    @NonNull
    Integer status;
    Timestamp timestamp = Timestamp.from(Instant.now());
}
