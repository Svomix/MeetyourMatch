package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationDAO extends JpaRepository<Location, Long> {

}
