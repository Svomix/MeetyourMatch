package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO {
    HttpStatus code = HttpStatus.OK;
    String message;
}
