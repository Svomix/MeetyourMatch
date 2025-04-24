package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.LocationDAO;
import com.javanostra.spring.core.entities.Location;
import com.javanostra.spring.core.exceptions.NoSuchLocationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class LocationService {
    private LocationDAO locationDAO;
    public List<Location> getAllLocations() {
        return locationDAO.findAll();
    }
    public Location getLocationById(Long id) throws NoSuchLocationException {
        return locationDAO.findById(id).orElseThrow(NoSuchLocationException::new);
    }
}
