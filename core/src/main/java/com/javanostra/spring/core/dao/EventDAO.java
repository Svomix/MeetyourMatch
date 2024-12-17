package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDAO extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {
    Event findEventById(long id);
    List<Event> findEventsByLocation(Location location);
}
