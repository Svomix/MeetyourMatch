package com.javanostra.spring.core.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double latitude;
    private Double longitude;
}


@Converter
class LocationConverter implements AttributeConverter<Location, String> {
    @Override
    public String convertToDatabaseColumn(Location location) {
        return "latitude=" + location.getLatitude() + ";longitude=" + location.getLongitude();
    }

    @Override
    public Location convertToEntityAttribute(String s) {
        if(Objects.isNull(s)) return null;
        String[] fields = s.split(";");
        return new Location(Double.parseDouble(fields[0].substring(9)), Double.parseDouble(fields[1].substring(10)));
    }
}