package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.CityDAO;
import com.javanostra.spring.core.entities.City;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CityService {
    private CityDAO cityDAO;

    public Page<City> findAllCities(Pageable pageable) {
        return cityDAO.findAll(pageable);
    }

    public City findCityById(Long cityId) {
        return cityDAO.findById(cityId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void saveCity(City city) {
        cityDAO.save(city);
    }

    @Transactional
    public void updateCity(City city) {
        cityDAO.save(city);
    }

    @Transactional
    public void deleteCity(Long cityId) {
        cityDAO.deleteById(cityId);
    }
}
