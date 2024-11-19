package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.City;
import com.javanostra.spring.core.services.CityService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@AllArgsConstructor
public class CityController {

    private CityService cityService;

//    @GetMapping
//    public Page<City> findAllCities(
//            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//            @RequestParam(value = "limit", defaultValue = "5") Integer limit
//    ) {
//        return cityService.findAllCities(PageRequest.of(offset, limit));
//    }

    @Cacheable
    @GetMapping
    public List<City> findAllCities() {
        return cityService.findAllCities(Pageable.unpaged()).toList();
    }

    @GetMapping("/{city_id}")
    public City findCityById(@PathVariable("city_id") Long cityId) {
        return cityService.findCityById(cityId);
    }

    @PostMapping
    public void saveCity(@RequestBody City city) {
        cityService.saveCity(city);
    }

    @PutMapping
    public void updateCity(@RequestBody City city) {
        cityService.updateCity(city);
    }

    @DeleteMapping("/{city_id}")
    public void deleteCity(@PathVariable("city_id") Long cityId) {
        cityService.deleteCity(cityId);
    }
}
