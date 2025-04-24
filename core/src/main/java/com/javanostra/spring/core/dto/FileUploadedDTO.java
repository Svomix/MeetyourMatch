package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadedDTO {
    private String fileId;
    private String path;
}
