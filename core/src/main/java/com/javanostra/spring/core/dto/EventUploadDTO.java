package com.javanostra.spring.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUploadDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private Timestamp date;
    //private String location;
    private String coverFileId;

}
