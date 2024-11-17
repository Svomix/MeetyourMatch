package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityDAO extends JpaRepository<City, Long>, PagingAndSortingRepository<City, Long> {
}
