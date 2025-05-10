package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.Location;
import com.javanostra.spring.core.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDAO extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Event findEventById(long id);

    List<Event> findEventsByLocation(Location location);

    Page<Event> findEventsByCreatedBy(User user, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t.id = :tagId")
    List<Event> findAllByTagId(@Param("tagId") Long tagId);
}
