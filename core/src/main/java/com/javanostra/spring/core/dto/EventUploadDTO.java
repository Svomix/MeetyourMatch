package com.javanostra.spring.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUploadDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private Timestamp date;
    private String coverFileId;
    private String price;
    private String sourceUrl;
    private List<Long> tags;
    private String locationId;
}
