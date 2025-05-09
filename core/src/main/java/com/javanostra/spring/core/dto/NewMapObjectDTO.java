package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMapObjectDTO {
    @NonNull
    private String title;
    @NonNull
    private String address;
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;
}
