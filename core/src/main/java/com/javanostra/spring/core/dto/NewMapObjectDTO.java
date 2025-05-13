package com.javanostra.spring.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMapObjectDTO {
    @NotNull
    private String title;
    @NotNull
    private String address;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
